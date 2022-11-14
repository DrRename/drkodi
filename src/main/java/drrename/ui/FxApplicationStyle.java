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

import com.jthemedetecor.OsThemeDetector;
import drrename.Settings;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.net.URL;
import java.util.function.Consumer;

@Slf4j
@Component
public class FxApplicationStyle {

    private final Settings settings;

    private final OsThemeDetector detector;

    private final Consumer<Boolean> systemInterfaceThemeListener = this::systemInterfaceThemeChanged;

    private final ObjectProperty<URL> currentStyleSheet;

    FxApplicationStyle(Settings settings) {
        this.settings = settings;
        detector = OsThemeDetector.getDetector();
        this.currentStyleSheet = new SimpleObjectProperty<>();
    }

    @PostConstruct
    public void init() {
        settings.themeProperty().addListener(this::appThemeChanged);
        log.debug("Loading theme {}", settings.getTheme());
        Platform.runLater(()-> loadSelectedStyleSheet(settings.getTheme()));
    }

    private void appThemeChanged(@SuppressWarnings("unused") ObservableValue<? extends UiTheme> observable, @SuppressWarnings("unused") UiTheme oldValue, UiTheme newValue) {
        if (oldValue == UiTheme.AUTOMATIC && newValue != UiTheme.AUTOMATIC) {
                detector.removeListener(systemInterfaceThemeListener);
        }
        loadSelectedStyleSheet(newValue);
    }

    private void systemInterfaceThemeChanged(Boolean aBoolean) {
    }

    private void loadSelectedStyleSheet(UiTheme desiredTheme) {
        switch (desiredTheme) {
            case LIGHT -> applyLightTheme();
            case DARK -> applyDarkTheme();
            case AUTOMATIC -> {
                detector.registerListener(systemInterfaceThemeListener);
                applySystemTheme();
            }
        }
    }

    private void applySystemTheme() {
            if(detector.isDark()){
                applyDarkTheme();
            } else
                applyLightTheme();
    }

    private void applyLightTheme() {
        var stylesheet = getClass().getResource("/css/light-theme.css");
        log.debug("Setting style sheet from {} to {}", currentStyleSheet.get(), stylesheet);
        currentStyleSheet.set(stylesheet);
    }

    private void applyDarkTheme() {
        var stylesheet = getClass().getResource("/css/dark-theme.css");
        log.debug("Setting style sheet from {} to {}", currentStyleSheet.get(), stylesheet);
        currentStyleSheet.set(stylesheet);
    }

    // FX Getter / Setter //


    public URL getCurrentStyleSheet() {
        return currentStyleSheet.get();
    }

    public ObjectProperty<URL> currentStyleSheetProperty() {
        return currentStyleSheet;
    }

    public void setCurrentStyleSheet(URL currentStyleSheet) {
        this.currentStyleSheet.set(currentStyleSheet);
    }
}
