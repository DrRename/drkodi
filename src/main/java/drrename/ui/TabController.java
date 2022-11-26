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

import drrename.Entries;
import drrename.config.AppConfig;
import drrename.filecreator.DummyFileCreatorController;
import drrename.kodi.ui.KodiToolsController;
import drrename.kodi.ui.ServiceStarter;
import drrename.ui.service.LoadPathsService;
import drrename.util.FXUtil;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.MenuBar;
import lombok.extern.slf4j.Slf4j;
import net.rgielen.fxweaver.core.FxWeaver;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.nio.file.Path;
import java.util.Collections;
import java.util.ResourceBundle;
import java.util.concurrent.Executor;

@Slf4j
@Component
@FxmlView("/fxml/TabView.fxml")
public class TabController extends DebuggableController implements Initializable {


    // Spring Injected //

    private final FxWeaver fxWeaver;

    private final ResourceBundle resourceBundle;

    private final FxApplicationStyle applicationStyle;

    private final Entries entries;

    private final LoadPathsService loadPathsService;

    private final Executor executor;


    //

    // FXML injected //

    // Controller

    @FXML
    public StartDirectoryComponentController startDirectoryController;

    @FXML
    public RenameController renameController;

    @FXML
    public KodiToolsController kodiToolsController;

    @FXML
    public DummyFileCreatorController dummyFileCreatorController;

    //


    @FXML
    public Parent root;

    @FXML
    public Label loadingFilesStatusLabel;

    @FXML
    public ProgressAndStatusGridPane progressAndStatusGrid;

    @FXML
    MenuBar menuBar;

    //

    // Default fields //

    private final LoadServiceStarter loadServiceStarter;

    private final Label progressLabel;

    // Nested classes //

    private class LoadServiceStarter extends ServiceStarter<LoadPathsService> {

        public LoadServiceStarter(LoadPathsService service) {
            super(service);
        }

        @Override
        protected void onSucceeded(WorkerStateEvent workerStateEvent) {
            renameController.updateFileTypeInfo();
            renameController.updatePreview();
            kodiToolsController.updateInputView();
            log.debug("Service {} finished", workerStateEvent.getSource());
        }

        @Override
        protected void doInitService(LoadPathsService service) {
            progressAndStatusGrid.getProgressBar().progressProperty().bind(service.progressProperty());
            progressLabel.textProperty().bind(loadPathsService.messageProperty());
        }
    }

    //

    public TabController(FxWeaver fxWeaver, ResourceBundle resourceBundle, FxApplicationStyle applicationStyle, Entries entries, LoadPathsService loadPathsService, Executor executor, AppConfig appConfig) {
        super(appConfig);
        this.fxWeaver = fxWeaver;
        this.resourceBundle = resourceBundle;
        this.applicationStyle = applicationStyle;
        this.entries = entries;
        this.loadPathsService = loadPathsService;
        this.executor = executor;
        this.loadServiceStarter = new LoadServiceStarter(this.loadPathsService);
        this.progressLabel = new Label();

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        super.initialize(url, resourceBundle);

        log.debug("FXML injected controllers: {}, {}, {}, {}", startDirectoryController, renameController, kodiToolsController, dummyFileCreatorController);
        FXUtil.initAppMenu(menuBar);
        applicationStyle.currentStyleSheetProperty().addListener(this::themeChanged);
        Platform.runLater(() -> applyTheme(null, applicationStyle.getCurrentStyleSheet()));
        startDirectoryController.inputPathProperty().addListener(this::getNewInputPathChangeListener);
        startDirectoryController.readyProperty().addListener(this::getNewReadyChangeListener);

        /*if (!appConfig.isDebug()) */
        progressAndStatusGrid.visibleProperty().bind(loadPathsService.runningProperty());

        loadPathsService.setExecutor(executor);

        progressAndStatusGrid.getProgressStatusBox().getChildren().add(progressLabel);

    }

    @Override
    protected Parent[] getUiElementsForRandomColor() {
        return new Parent[]{progressAndStatusGrid, progressAndStatusGrid.getProgressStatusBox(), root};
    }

    private void getNewReadyChangeListener(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
        if (newValue != null && newValue) {
            // we are ready. Starting is triggered by input change listener
        } else {
            renameController.cancelCurrentOperationAndClearView();
            kodiToolsController.cancelCurrentOperationAndClearView();
        }
    }

    private void getNewInputPathChangeListener(ObservableValue<? extends Path> observable, Path oldValue, Path newValue) {
        if (startDirectoryController.isReady()) {
            loadPathsService.setFiles(Collections.singleton(newValue));
            loadServiceStarter.startService();
        }
    }

    private void themeChanged(ObservableValue<? extends URL> observable, URL oldValue, URL newValue) {
        applyTheme(oldValue, newValue);
    }

    private void applyTheme(URL oldSheet, URL newSheet) {
        if (oldSheet != null)
            root.getScene().getStylesheets().remove(oldSheet.toString());
        if (newSheet != null)
            root.getScene().getStylesheets().add(newSheet.toString());
    }

    public void handleMenuItemSettings(ActionEvent actionEvent) {
        SettingsController controller = fxWeaver.loadController(SettingsController.class, resourceBundle);
        controller.show();
    }
}
