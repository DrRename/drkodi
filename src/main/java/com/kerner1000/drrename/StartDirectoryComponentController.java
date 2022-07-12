package com.kerner1000.drrename;

import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import lombok.extern.slf4j.Slf4j;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ResourceBundle;

@Slf4j
@Component
@Scope("prototype")
@FxmlView("/fxml/StartDirectoryComponent.fxml")
public class StartDirectoryComponentController implements Initializable, ApplicationListener<ApplicationEvent> {

    public TextField textFieldDirectory;

    private Path inputPath;

    private BooleanProperty ready;

    private ChangeListener<? super String> textFieldChangeListener;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ready = new SimpleBooleanProperty(false);
        textFieldChangeListener = (e, o, n) -> Platform.runLater(() -> updateInput(n));
        textFieldDirectory.textProperty().addListener(textFieldChangeListener);
        textFieldDirectory.setOnDragOver(event -> {
            if ((event.getGestureSource() != textFieldDirectory) && event.getDragboard().hasFiles()) {
                /* allow for both copying and moving, whatever user chooses */
                event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
            }
            event.consume();
        });
        textFieldDirectory.setOnDragDropped(event -> {
            final Dragboard db = event.getDragboard();
            boolean success = false;
            if (db.hasFiles()) {
                if (!db.getFiles().isEmpty() && db.getFiles().iterator().next().isDirectory())
                    textFieldDirectory.setText(db.getFiles().iterator().next().getPath());
                else textFieldDirectory.setText(null);
                success = true;
            }
            /*
             * let the source know whether the string was successfully transferred and used
             */
            event.setDropCompleted(success);
            event.consume();
        });
    }

    private void updateInput(String inputPath) {
        if(inputPath != null){
            updateInput(Path.of(inputPath));
        }
    }

    private void updateInput(Path inputPath) {
        if (Files.isReadable(inputPath)) {
            if (Files.isDirectory(inputPath)) {
                if (Files.isWritable(inputPath)) {
                    this.inputPath = inputPath;
                    ready.set(true);
                } else {
                    log.debug("cannot write to {}", inputPath);
                    ready.set(false);
                }
            } else {
                log.debug("{} is not a directory", inputPath);
                ready.set(false);
            }
        } else {
            log.debug("cannot read {}", inputPath);
            ready.set(false);
        }
        log.debug("Ready-state now {}", ready.get());
    }

    @Override
    public void onApplicationEvent(ApplicationEvent event) {

    }

    public Path getInputPath() {
        return inputPath;
    }

    public boolean isReady() {
        return ready.get();
    }

    public BooleanProperty readyProperty() {
        return ready;
    }
}
