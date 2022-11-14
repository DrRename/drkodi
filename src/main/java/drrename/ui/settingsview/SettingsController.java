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

package drrename.ui.settingsview;

import drrename.Settings;
import drrename.SettingsProvider;
import drrename.ui.FxApplicationStyle;
import drrename.ui.UiTheme;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ResourceBundle;

@RequiredArgsConstructor
@Slf4j
@Component
@FxmlView("/fxml/SettingsView.fxml")
public class SettingsController implements Initializable {

    private static class UiThemeConverter extends StringConverter<UiTheme> {

        private final ResourceBundle resourceBundle;

        UiThemeConverter(ResourceBundle resourceBundle) {
            this.resourceBundle = resourceBundle;
        }

        @Override
        public String toString(UiTheme impl) {
            return resourceBundle.getString(impl.getDisplayName());
        }

        @Override
        public UiTheme fromString(String string) {
            throw new UnsupportedOperationException();
        }

    }

    private final Settings settings;

    private final SettingsProvider settingsProvider;

    private final FxApplicationStyle applicationStyle;

    public VBox root;

    public ChoiceBox<UiTheme> themeChoiceBox;

    private Stage mainStage;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        mainStage = new Stage();
        mainStage.setScene(new Scene(root));
        mainStage.setTitle("Settings");
        log.debug("Working with settings {}", settings);
        // Settings::Theme
        settings.themeProperty().addListener(this::saveSettings);
        themeChoiceBox.getItems().addAll(UiTheme.applicableValues());
        if (!themeChoiceBox.getItems().contains(settings.getTheme())) {
            settings.setTheme(UiTheme.LIGHT);
        }
        themeChoiceBox.valueProperty().bindBidirectional(settings.themeProperty());
        themeChoiceBox.setConverter(new UiThemeConverter(resourceBundle));
        //
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

    private void saveSettings(@SuppressWarnings("unused") ObservableValue<? extends UiTheme> observable, @SuppressWarnings("unused") UiTheme oldValue, UiTheme newValue) {
        settingsProvider.save(settings);
    }

    public void show() {
        mainStage.show();
    }
}
