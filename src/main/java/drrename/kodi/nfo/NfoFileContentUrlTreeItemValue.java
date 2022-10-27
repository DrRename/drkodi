/*
 *     Dr.Rename - A Minimalistic Batch Renamer
 *
 *     Copyright (C) 2022
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Affero General Public License as
 *     published by the Free Software Foundation, either version 3 of the
 *     License, or (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Affero General Public License for more details.
 *
 *     You should have received a copy of the GNU Affero General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package drrename.kodi.nfo;

import drrename.MovieDbQuerier;
import drrename.MovieDbImagesClient;
import drrename.RenameUtil;
import drrename.config.TheMovieDbConfig;
import drrename.kodi.FixFailedException;
import drrename.kodi.KodiTreeItemValue;
import drrename.kodi.KodiUtil;
import drrename.kodi.MovieDbClientFactory;
import drrename.model.RenamingPath;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.*;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

@Slf4j
public class NfoFileContentUrlTreeItemValue extends KodiTreeItemValue {

    @AllArgsConstructor
    static
    class FixConfig {
        Button button;
        String newName;
    }

    private final ObjectProperty<MovieDbCheckType> type;

    private final MovieDbQuerier checker;

    private final MovieDbImagesClient imagesClient;

    private final TheMovieDbConfig config;

    private FixConfig fixConfig;

    public NfoFileContentUrlTreeItemValue(RenamingPath moviePath, Executor executor, MovieDbClientFactory factory) {
        super(moviePath, false, executor);
        this.type = new SimpleObjectProperty<>();
        this.checker = factory.getNewMovieDbChecker();
        this.imagesClient = factory.getImagesClient();
        this.config = factory.getConfig();
        updateStatus();
    }

    @Override
    protected String updateMessage(Boolean newValue) {
        return newValue ? "Movie name could not be found online.\n" + getAdditionalMessageString() : "Movie name found online:\n" + ": " + getType();
    }

    private String getAdditionalMessageString() {
        return checker.getOnlineTitles().isEmpty() ? "" : "Best matches:\n" + checker.getOnlineTitles().stream().map(Object::toString).collect(Collectors.joining("\n"));
    }

    @Override
    public void fix() throws FixFailedException {
        try {
            var renameResult = RenameUtil.rename(getRenamingPath().getOldPath(), fixConfig.newName);
            Platform.runLater(() -> getRenamingPath().commitRename(renameResult));
        } catch (IOException e) {
            throw new FixFailedException(e);
        }
    }

    @Override
    protected String updateIdentifier() {
        return "theMovieDB lookup";
    }


    @Override
    protected void updateStatus() {
        MovieDbCheckType newType = null;
        try {
            newType = checker.query(KodiUtil.getMovieNameFromDirectoryName(getRenamingPath().getMovieName()), KodiUtil.getMovieYearFromDirectoryName(getRenamingPath().getMovieName()));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        setType(newType);
        setWarning(getType().isWarning());
        updateMessage(isWarning());
        setCanFix(!checker.getOnlineTitles().isEmpty() && isWarning());
        if (!checker.getOnlineTitles().isEmpty() && isWarning()) {
            Platform.runLater(() -> setGraphic(buildGraphic2()));
        } else {
            Platform.runLater(() -> setGraphic(super.buildGraphic()));
        }
        if (checker.getTheMovieDbId() != null) {
            var image = imagesClient.searchMovie("ca540140c89af81851d4026286942896", null, true, checker.getTheMovieDbId());
            try {
                if (image.getBody() != null) {
                    Path tempFile = Files.createTempFile("tmp", ".jpg");
                    Path outputFile = getRenamingPath().getOldPath().resolve(Paths.get("folder.jpg"));
                    try (FileOutputStream outputStream = new FileOutputStream(tempFile.toFile())) {
                        outputStream.write(image.getBody());
                        log.debug("Wrote image to {}", tempFile);
                    }
                    if (Files.exists(outputFile)) {
                        FileChannel imageFileChannel = FileChannel.open(outputFile);
                        long imageFileSize = imageFileChannel.size();
                        imageFileChannel.close();
                        FileChannel imageFileChannel2 = FileChannel.open(tempFile);
                        long imageFileSize2 = imageFileChannel2.size();
                        imageFileChannel2.close();
                        if(imageFileSize2 > imageFileSize){
                            log.debug("Downloaded file is larger in size, replacing ours with download");
                            Files.move(tempFile, outputFile, StandardCopyOption.REPLACE_EXISTING);
                        } else {
                            log.debug("Our image is larger in size, keeping it");
                        }
                    } else {
                        log.debug("{} does not exist, using download", outputFile);
                        Files.move(tempFile, outputFile);
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private Node buildGraphic2() {
        VBox box = new VBox(4);
        for (String name : checker.getOnlineTitles()) {
            Button button = new Button("Fix to \"" + name + "\"");
            VBox.setVgrow(button, Priority.ALWAYS);
            button.setMaxWidth(500);
            button.setOnAction(event -> button.setOnAction(actionEvent -> {
                fixConfig = new FixConfig(button, name);
                performFix();
            }));
            box.getChildren().add(button);
        }
        return box;
    }

    // FX Getter / Setter //


    public MovieDbCheckType getType() {
        return type.get();
    }

    public ObjectProperty<MovieDbCheckType> typeProperty() {
        return type;
    }

    public void setType(MovieDbCheckType type) {
        this.type.set(type);
    }
}
