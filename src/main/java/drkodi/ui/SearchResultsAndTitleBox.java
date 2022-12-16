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

import drkodi.config.AppConfig;
import drkodi.data.StaticMovieData;
import drkodi.ui.config.KodiUiConfig;
import drkodi.ui.control.SearchResultsBox;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class SearchResultsAndTitleBox extends VBox {

    public SearchResultsAndTitleBox(StaticMovieData staticMovieData, AppConfig appConfig, KodiUiConfig kodiUiConfig){

        // content

        var srb = new SearchResultsBox(staticMovieData, appConfig, kodiUiConfig);
        getChildren().add(UiUtil.applyDebug(new Label("MovieDB search results"), appConfig));
        getChildren().add(UiUtil.applyDebug(srb, appConfig));

       // formatting

//        setPadding(new Insets(4,4,4,4));



        // behaviour

        visibleProperty().bind(staticMovieData.searchResultsProperty().emptyProperty().not());
        managedProperty().bind(visibleProperty());
    }
}
