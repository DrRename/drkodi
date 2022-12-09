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

package drrename.kodi.ui;

import drrename.config.AppConfig;
import drrename.kodi.data.KodiWarning;
import drrename.kodi.data.StaticMovieData;
import drrename.kodi.ui.control.KodiTitleWarningBox;
import drrename.kodi.ui.control.KodiYearWarningBox;
import drrename.ui.UiUtil;
import javafx.collections.ListChangeListener;
import javafx.scene.layout.VBox;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class KodiWarningBox extends VBox {

    public KodiWarningBox(StaticMovieData element, AppConfig appConfig){



        // set initial value
        addWarnings(element, appConfig);

// register listener

        element.warningsProperty().addListener(new ListChangeListener<KodiWarning>() {
            @Override
            public void onChanged(Change<? extends KodiWarning> c) {
                while(c.next()){
                    log.debug("Warnings changed, re-creating content");
                }
                addWarnings(element, appConfig);
            }
        });


        // set style


        getStyleClass().add("kodi-warning-box");



        visibleProperty().bind(element.warningsProperty().emptyProperty().not());
        managedProperty().bind(visibleProperty());

    }


    private void addWarnings(StaticMovieData element, AppConfig appConfig){
        getChildren().clear();
        for (KodiWarning warning : element.getWarnings()) {
            switch (warning.getType()) {
                case TITLE_MISMATCH ->   getChildren().add(UiUtil.applyDebug(new KodiTitleWarningBox(element, appConfig), appConfig));
                case YEAR_MISMATCH -> getChildren().add(UiUtil.applyDebug(new KodiYearWarningBox(element, appConfig), appConfig));
            }
        }
    }


}
