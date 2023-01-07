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
import javafx.scene.layout.*;


public class KodiBox extends VBox {

    private final Movie movie;

    private final Pane detailsView;

    public KodiBox(Movie kodiMovie, AppConfig appConfig, KodiUiConfig kodiUiConfig, Pane detailsView){

        this.movie = kodiMovie;
        this.detailsView = detailsView;

        getChildren().add(UiUtil.applyDebug(new KodiMovieAndImageBox(kodiMovie, appConfig, kodiUiConfig), appConfig));

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(UiUtil.applyDebug(new SearchResultsAndTitleBox(kodiMovie, appConfig, kodiUiConfig), appConfig));
        scrollPane.setFitToHeight(true);
//        scrollPane.setFitToWidth(true);

        scrollPane.visibleProperty().bind(kodiMovie.searchResultsProperty().emptyProperty().not());
        scrollPane.managedProperty().bind(scrollPane.visibleProperty());

        scrollPane.getStyleClass().add("search-result-box");

//        scrollPane.minHeightProperty().bind(john.prefHeightProperty());

        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        getChildren().add(UiUtil.applyDebug(scrollPane, appConfig));
//        VBox.setVgrow(scrollPane, Priority.ALWAYS);
//        VBox.setVgrow(john, Priority.ALWAYS);

        Pane spacer = new Pane();
//        spacer.setMinHeight(50);
        VBox.setVgrow(spacer, Priority.ALWAYS);



        getChildren().add(UiUtil.applyDebug(spacer, appConfig));

        getChildren().add(UiUtil.applyDebug(detailsView, appConfig));

        // layout
        getStyleClass().add("kodi-box");


//        setMaxHeight(Double.MAX_VALUE);

        setVgrow(this, Priority.ALWAYS);




    }

    // Getter / Setter //

    public Movie getMovie() {
        return movie;
    }
}
