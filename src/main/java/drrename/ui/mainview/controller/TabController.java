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

package drrename.ui.mainview.controller;

import drrename.ui.FxApplicationStyle;
import drrename.ui.mainview.StartDirectoryComponentController;
import drrename.ui.settingsview.SettingsController;
import drrename.util.FXUtil;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.MenuBar;
import javafx.scene.control.Tab;
import javafx.scene.layout.BorderPane;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.rgielen.fxweaver.core.FxWeaver;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ResourceBundle;

@RequiredArgsConstructor
@Slf4j
@Component
@FxmlView("/fxml/TabView.fxml")
public class TabController implements Initializable {

    public Parent startDirectoryComponent;

    public StartDirectoryComponentController startDirectoryComponentController;

    private final FxWeaver fxWeaver;

    private final ResourceBundle resourceBundle;

    private final FxApplicationStyle applicationStyle;

    public Parent mainController;

    public BorderPane kodiToolsController;

    public Tab dummyFileTab;

    public Tab kodiToolsTab;

    public Tab renameTab;

    public Parent dummyFileCreatorController;

    public Parent root;

    @FXML
    MenuBar menuBar;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        FXUtil.initAppMenu(menuBar);
        applicationStyle.currentStyleSheetProperty().addListener(this::themeChanged);
        Platform.runLater(() -> applyTheme(null, applicationStyle.getCurrentStyleSheet()));
    }

    private void themeChanged(ObservableValue<? extends URL> observable, URL oldValue, URL newValue) {
        applyTheme(oldValue, newValue);
    }

    private void applyTheme(URL oldSheet, URL newSheet) {
        if(oldSheet != null)
            root.getScene().getStylesheets().remove(oldSheet.toString());
        if(newSheet != null)
            root.getScene().getStylesheets().add(newSheet.toString());
    }

    public void handleMenuItemSettings(ActionEvent actionEvent) {
        SettingsController controller = fxWeaver.loadController(SettingsController.class, resourceBundle);
        controller.show();
    }
}
