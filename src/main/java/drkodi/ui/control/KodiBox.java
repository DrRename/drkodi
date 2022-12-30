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

package drkodi.ui.control;

import drkodi.config.AppConfig;
import drkodi.data.movie.Movie;
import drkodi.ui.SearchResultsAndTitleBox;
import drkodi.ui.UiUtil;
import drkodi.ui.config.KodiUiConfig;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import lombok.Getter;

@Getter
public class KodiBox extends VBox {

    private final Movie movie;

    public KodiBox(Movie kodiMovie, AppConfig appConfig, KodiUiConfig kodiUiConfig){

        this.movie = kodiMovie;

        // content
        getChildren().add(UiUtil.applyDebug(new KodiMovieAndImageBox(kodiMovie, appConfig, kodiUiConfig), appConfig));


        var john = new SearchResultsAndTitleBox(kodiMovie, appConfig, kodiUiConfig);

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(UiUtil.applyDebug(john, appConfig));
        scrollPane.setFitToHeight(true);
//        scrollPane.setFitToWidth(true);

        scrollPane.visibleProperty().bind(kodiMovie.searchResultsProperty().emptyProperty().not());
        scrollPane.managedProperty().bind(scrollPane.visibleProperty());

        scrollPane.getStyleClass().add("search-result-box");

        scrollPane.minHeightProperty().bind(john.prefHeightProperty());

        getChildren().add(UiUtil.applyDebug(scrollPane, appConfig));
//        VBox.setVgrow(scrollPane, Priority.ALWAYS);
//        VBox.setVgrow(john, Priority.ALWAYS);

        // layout
        getStyleClass().add("kodi-box");



//        setVgrow(scrollPane, Priority.ALWAYS);


    }


}
