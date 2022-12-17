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

package drkodi.ui;

import drkodi.Entries;
import drkodi.config.AppConfig;
import drkodi.ui.service.LoadPathsServicePrototype;
import drkodi.util.FXUtil;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.MenuBar;
import javafx.scene.layout.Pane;
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

    private final FxApplicationStyle applicationStyle;

    private final LoadPathsServicePrototype loadPathsService;

    private final Executor executor;

    private final Entries entries;

    private final ResourceBundle resourceBundle;


    //

    // FXML injected //

    // Controller

    @FXML
    public StartDirectoryComponentController startDirectoryController;

    @FXML
    public MainViewController mainViewController;



    //


    @FXML
    public Pane root;

    @FXML
    public ProgressAndStatusGridPane progressAndStatusGrid;

    @FXML
    public Pane startDirectory;

    @FXML
    public Pane mainView;

    @FXML
    MenuBar menuBar;

    //

    // Default fields //

    private final LoadServiceStarter loadServiceStarter;

    private final Label progressLabel;

    // Nested classes //

    private class LoadServiceStarter extends ServiceStarter<LoadPathsServicePrototype> {

        public LoadServiceStarter(LoadPathsServicePrototype service) {
            super(service);
        }

        @Override
        protected void prepareUi() {
            super.prepareUi();
            entries.getEntries().clear();
            mainViewController.clearView();

        }

        @Override
        protected void onSucceeded(WorkerStateEvent workerStateEvent) {
            mainViewController.updateInputView();
            log.debug("Service {} finished", workerStateEvent.getSource());
        }

        @Override
        protected void doInitService(LoadPathsServicePrototype service) {
            progressAndStatusGrid.getProgressBar().progressProperty().bind(service.progressProperty());
            progressLabel.textProperty().bind(loadPathsService.messageProperty());
        }
    }

    //

    public TabController(FxWeaver fxWeaver, FxApplicationStyle applicationStyle, Entries entries, LoadPathsServicePrototype loadPathsService, Executor executor, AppConfig appConfig, ResourceBundle resourceBundle) {
        super(appConfig);
        this.fxWeaver = fxWeaver;
        this.applicationStyle = applicationStyle;
        this.loadPathsService = loadPathsService;
        this.executor = executor;
        this.entries = entries;
        this.resourceBundle = resourceBundle;
        this.loadServiceStarter = new LoadServiceStarter(this.loadPathsService);
        this.progressLabel = new Label();

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        super.initialize(url, resourceBundle);

        FXUtil.initAppMenu(menuBar);

        startDirectoryController.inputPathProperty().addListener(this::getNewInputPathChangeListener);
        startDirectoryController.readyProperty().addListener(this::readyChangeListener);

        if (!getAppConfig().isDebug())
        progressAndStatusGrid.visibleProperty().bind(loadPathsService.runningProperty());

        loadPathsService.setExecutor(executor);

        progressAndStatusGrid.getProgressStatusBox().getChildren().add(progressLabel);

        applicationStyle.currentStyleSheetProperty().addListener(this::themeChanged);
        Platform.runLater(() -> applyTheme(null, applicationStyle.getCurrentStyleSheet()));

    }

    @Override
    protected Parent[] getUiElementsForRandomColor() {
        return new Parent[]{progressAndStatusGrid, progressAndStatusGrid.getProgressStatusBox(), root};
    }

    private void readyChangeListener(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
        if (newValue != null && newValue) {
            // we are ready. Starting is triggered by input change listener
        } else {
            mainViewController.cancelCurrentOperationAndClearView();
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
