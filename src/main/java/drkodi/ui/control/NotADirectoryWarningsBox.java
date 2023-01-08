package drkodi.ui.control;

import drkodi.config.AppConfig;
import drkodi.data.movie.Movie;
import drkodi.ui.UiUtil;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
public class NotADirectoryWarningsBox extends HBox {

    private final Movie movie;

    public NotADirectoryWarningsBox(Movie element, AppConfig appConfig) {

        this.movie = element;

        Label label1 = new KodiWarningKeyLabel("Not a directory");

        getChildren().add(UiUtil.applyDebug(label1, appConfig));
        setAlignment(Pos.CENTER_LEFT);
        setSpacing(4);
        getStyleClass().add("kodi-warning-box-element");
        final Pane spacer = new Pane();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        getChildren().add(UiUtil.applyDebug(spacer, appConfig));
        getChildren().add(UiUtil.applyDebug(buildFixButton(element), appConfig));
    }

    private Control buildFixButton(Movie element) {
        Button button = new Button("Fix");
        button.setOnAction(event -> {
            // create directory of same name
            Path parent = element.getRenamingPath().getOldPath().getParent();
            Path directoryPath = Paths.get(parent.toString(), FilenameUtils.getBaseName(element.getRenamingPath().getOldPath().getFileName().toString()));
            log.debug("Directory name is {}", directoryPath);
            try {
                Path directory = Files.createDirectories(directoryPath);
                Path target = Paths.get(directory.toString(), element.getRenamingPath().getOldPath().getFileName().toString());
                log.debug("Target is {}", target);
                Path newPath = Files.move(element.getRenamingPath().getOldPath(), target);
                log.debug("Moved {} to {}", element.getRenamingPath().getOldPath(), newPath);
                element.getRenamingPath().setOldPath(directory);
            } catch (IOException e) {
                log.error("Failed to fix: ", e);
            }
            // re-check after fixing the issue
            movie.triggerChecks();
        });
        button.setMinWidth(40);
        button.setAlignment(Pos.CENTER_RIGHT);
        return button;
    }
}
