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
import drrename.kodi.data.StaticMovieData;
import drrename.kodi.ui.config.KodiUiConfig;
import drrename.kodi.ui.control.SearchResultsBox;
import drrename.ui.UiUtil;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class SearchResultsAndTitleBox extends VBox {

    public SearchResultsAndTitleBox(StaticMovieData staticMovieData, AppConfig appConfig, KodiUiConfig kodiUiConfig){

        // content

        getChildren().add(UiUtil.applyDebug(new Label("MovieDB search results"), appConfig));
        getChildren().add(UiUtil.applyDebug(new SearchResultsBox(staticMovieData, appConfig, kodiUiConfig), appConfig));

       // formatting

        setPadding(new Insets(4,4,4,4));

        // behaviour

        visibleProperty().bind(staticMovieData.searchResultsProperty().emptyProperty().not());
        managedProperty().bind(visibleProperty());
    }
}
