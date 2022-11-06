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

import drrename.MovieDbImagesClient;
import drrename.RenameUtil;
import drrename.config.TheMovieDbConfig;
import drrename.kodi.*;
import drrename.model.RenamingPath;
import javafx.application.Platform;
import javafx.concurrent.WorkerStateEvent;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

@Slf4j
public class MovieDbLookupTreeItemValue extends KodiTreeItemValue<MovieDbLookupCheckResult> {

    private final MovieDbClientFactory factory;

    @AllArgsConstructor
    static
    class FixConfig {
        Button button;
        String newName;
    }



    private final MovieDbImagesClient imagesClient;

    private final TheMovieDbConfig config;

    private FixConfig fixConfig;

    public MovieDbLookupTreeItemValue(RenamingPath moviePath, Executor executor, MovieDbClientFactory factory, WarningsConfig warningsConfig) {
        super(moviePath, executor, warningsConfig);
        this.factory = factory;
        this.imagesClient = factory.getImagesClient();
        this.config = factory.getConfig();
        triggerStatusCheck();

    }

    @Override
    public String getIdentifier() {
        return "theMovieDB Lookup";
    }

    @Override
    public String getHelpText() {
        return null;
    }

    @Override
    public MovieDbLookupCheckResult checkStatus() {
//        log.debug("Triggering check status on thread {}", Thread.currentThread());
        try {
            return factory.getNewMovieDbChecker().query(KodiUtil.getMovieNameFromDirectoryName(getRenamingPath().getMovieName()), KodiUtil.getMovieYearFromDirectoryName(getRenamingPath().getMovieName()));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void updateStatus(MovieDbLookupCheckResult result) {
        if(result == null)
            return;
//        log.debug("Updating status");
        setCheckResult(result);
        setWarning(getCheckResult().getType().isWarning());
        buildNewMessage(isWarning());
        setFixable(!getCheckResult().getOnlineTitles().isEmpty() && isWarning());
        if (!getCheckResult().getOnlineTitles().isEmpty() && isWarning()) {
            setGraphic(buildGraphic2());
        } else {
            setGraphic(super.buildGraphic());
        }
//        if (querier.getTheMovieDbId() != null) {
//            var image = imagesClient.searchMovie("ca540140c89af81851d4026286942896", null, config.isIncludeAdult(), querier.getTheMovieDbId());
//            try {
//                if (image.getBody() != null) {
//                    Path tempFile = Files.createTempFile("tmp", ".jpg");
//                    Path outputFile = getRenamingPath().getOldPath().resolve(Paths.get("folder.jpg"));
//                    try (FileOutputStream outputStream = new FileOutputStream(tempFile.toFile())) {
//                        outputStream.write(image.getBody());
//                        log.debug("Wrote image to {}", tempFile);
//                    }
//                    if (Files.exists(outputFile)) {
//                        FileChannel imageFileChannel = FileChannel.open(outputFile);
//                        long imageFileSize = imageFileChannel.size();
//                        imageFileChannel.close();
//                        FileChannel imageFileChannel2 = FileChannel.open(tempFile);
//                        long imageFileSize2 = imageFileChannel2.size();
//                        imageFileChannel2.close();
//                        if(imageFileSize2 > imageFileSize){
//                            log.debug("Downloaded file is larger in size, replacing ours with downloaded");
//                            Files.move(tempFile, outputFile, StandardCopyOption.REPLACE_EXISTING);
//                        } else {
//                            log.debug("Our image is larger in size, keeping it");
//                        }
//                    } else {
//                        log.debug("{} does not exist, using download", outputFile);
//                        Files.move(tempFile, outputFile);
//                    }
//                }
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            }
//        }
    }

    @Override
    protected String buildNewMessage(Boolean newValue) {
        if(getAdditionalMessageString() == null){
            return null;
        }
        return newValue ? "Movie name could not be found online.\n" + getAdditionalMessageString() : "Movie name found online: " + getCheckResult().getType() +  getAdditionalMessageString();
    }

    private String getAdditionalMessageString() {
        return ". " + (getCheckResult().getOnlineTitles().isEmpty() ? "" : "Best matches:\n" + getCheckResult().getOnlineTitles().stream().map(Object::toString).collect(Collectors.joining("\n")));
    }

    @Override
    public void fix(MovieDbLookupCheckResult result) throws FixFailedException {
        log.debug("Triggering fixing on thread {}", Thread.currentThread());
        try {
            var renameResult = RenameUtil.rename(getRenamingPath().getOldPath(), fixConfig.newName);
            Platform.runLater(() -> getRenamingPath().commitRename(renameResult));
        } catch (IOException e) {
            throw new FixFailedException(e);
        }
    }

    private Node buildGraphic2() {
        VBox box = new VBox(4);
        for (String name : getCheckResult().getOnlineTitles()) {
            Button button = new Button("Fix to \"" + name + "\"");
//            VBox.setVgrow(button, Priority.ALWAYS);
            button.setMaxWidth(250);
            button.wrapTextProperty().setValue(true);
            button.setOnAction(actionEvent -> {
                fixConfig = new FixConfig(button, name);
                bla2();
            });
            box.getChildren().add(button);
        }
        return box;
    }

    private void bla2(){
        var fixableFixer = new IssueFixer<>(this, getCheckResult());
        fixableFixer.setOnFailed(this::defaultTaskFailed);
        fixableFixer.setOnSucceeded(this::fixSucceeded);
        getExecutor().execute(fixableFixer);
    }

    private void fixSucceeded(WorkerStateEvent workerStateEvent) {
        updateAllStatus();
    }

}
