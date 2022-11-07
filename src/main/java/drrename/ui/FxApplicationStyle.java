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

import drrename.Settings;
import javafx.application.Application;
import javafx.beans.value.ObservableValue;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Optional;

@RequiredArgsConstructor
@Slf4j
@Component
public class FxApplicationStyle {

    private final Settings settings;

    @PostConstruct
    public void init() {
        settings.themeProperty().addListener(this::appThemeChanged);
        loadSelectedStyleSheet(settings.getTheme());
    }


    private void appThemeChanged(@SuppressWarnings("unused") ObservableValue<? extends UiTheme> observable, @SuppressWarnings("unused") UiTheme oldValue, UiTheme newValue) {
        loadSelectedStyleSheet(newValue);
    }

    private void loadSelectedStyleSheet(UiTheme desiredTheme) {
        UiTheme theme = desiredTheme;
        switch (theme) {
            case LIGHT -> applyLightTheme();
            case DARK -> applyDarkTheme();
            case AUTOMATIC -> {


            }
        }
    }

    private void applyLightTheme() {
        var stylesheet = Optional //
                .ofNullable(getClass().getResource("/css/light-theme.bss")) //
                .orElse(getClass().getResource("/css/light-theme.css"));
        if (stylesheet == null) {
            log.warn("Failed to load light_theme stylesheet");
        } else {
            Application.setUserAgentStylesheet(stylesheet.toString());
        }
    }

    private void applyDarkTheme() {
        var stylesheet = Optional //
                .ofNullable(getClass().getResource("/css/dark-theme.bss")) //
                .orElse(getClass().getResource("/css/dark-theme.css"));
        if (stylesheet == null) {
            log.warn("Failed to load dark_theme stylesheet");
        } else {
            Application.setUserAgentStylesheet(stylesheet.toString());
        }
    }


}
