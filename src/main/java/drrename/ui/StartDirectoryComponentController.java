/*
 *     Dr.Rename - A Minimalistic Batch Renamer
 *
 *     Copyright (C) 2022
 *
 *     This file is part of Dr.Rename.
 *
 *     You can redistribute it and/or modify it under the terms of the GNU Affero
 *     General Public License as published by the Free Software Foundation, either
 *     version 3 of the License, or (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful, but WITHOUT
 *     ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 *     FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License
 *     for more details.
 *
 *     You should have received a copy of the GNU Affero General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package drrename.ui;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import lombok.extern.slf4j.Slf4j;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.File;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Component
@Scope("prototype")
@FxmlView("/fxml/StartDirectoryComponent.fxml")
public class StartDirectoryComponentController implements Initializable, ApplicationListener<ApplicationEvent> {

    public TextField textFieldDirectory;

    private ObjectProperty<Path> inputPath;

    private BooleanProperty ready;

    private ChangeListener<? super String> textFieldChangeListener;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ready = new SimpleBooleanProperty(false);
        inputPath = new SimpleObjectProperty<>();
        textFieldChangeListener = (e, o, n) -> updateInput(n);
        textFieldDirectory.textProperty().addListener(textFieldChangeListener);
        textFieldDirectory.setOnDragOver(event -> {
            if ((event.getGestureSource() != textFieldDirectory) && event.getDragboard().hasFiles()) {
                /* allow for both copying and moving, whatever user chooses */
                event.acceptTransferModes(TransferMode.ANY);
            }
            event.consume();
        });
        textFieldDirectory.setOnDragDropped(event -> {
            final Dragboard db = event.getDragboard();
            boolean success = false;
            if (db.hasFiles()) {
                if (db.getFiles().iterator().next().isDirectory())
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
        if (inputPath != null) {
            updateInput(Path.of(inputPath));
        }
    }

    private void updateInput(Path inputPath) {
        if (Files.isReadable(inputPath)) {
            if (Files.isDirectory(inputPath)) {
                if (Files.isWritable(inputPath)) {
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
        this.inputPath.set(inputPath);
        log.debug("Ready-state now {}", ready.get());
    }

    @Override
    public void onApplicationEvent(ApplicationEvent event) {

    }

    public Path getInputPath() {
        return inputPath.get();
    }

    public ObjectProperty<Path> inputPathProperty() {
        return inputPath;
    }

    @SuppressWarnings("unused")
    public void setInputPath(Path inputPath) {
        this.inputPath.set(inputPath);
    }

    @SuppressWarnings("unused")
    public boolean isReady() {
        return ready.get();
    }

    public BooleanProperty readyProperty() {
        return ready;
    }
}
