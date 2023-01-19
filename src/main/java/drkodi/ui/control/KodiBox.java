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
import drkodi.ui.MovieSearchResultsAndTitleBox;
import drkodi.ui.TvSearchResultsAndTitleBox;
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

        addMovieSearchResultScrollPane(kodiMovie, appConfig, kodiUiConfig);

        addTvSearchResultScrollPane(kodiMovie, appConfig, kodiUiConfig);

        Pane spacer = new Pane();
        VBox.setVgrow(spacer, Priority.ALWAYS);


        getChildren().add(UiUtil.applyDebug(spacer, appConfig));

        getChildren().add(UiUtil.applyDebug(detailsView, appConfig));

        // layout
        getStyleClass().add("kodi-box");


//        setMaxHeight(Double.MAX_VALUE);

        setVgrow(this, Priority.ALWAYS);




    }


    private void addMovieSearchResultScrollPane(Movie kodiMovie, AppConfig appConfig, KodiUiConfig kodiUiConfig) {
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(UiUtil.applyDebug(new MovieSearchResultsAndTitleBox(kodiMovie, appConfig, kodiUiConfig), appConfig));
        scrollPane.setFitToHeight(true);

        scrollPane.visibleProperty().bind(kodiMovie.movieSearchResultsProperty().emptyProperty().not());
        scrollPane.managedProperty().bind(scrollPane.visibleProperty());

//        scrollPane.getStyleClass().add("search-result-box");

        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        getChildren().add(UiUtil.applyDebug(scrollPane, appConfig));
    }

    private void addTvSearchResultScrollPane(Movie kodiMovie, AppConfig appConfig, KodiUiConfig kodiUiConfig) {
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(UiUtil.applyDebug(new TvSearchResultsAndTitleBox(kodiMovie, appConfig, kodiUiConfig), appConfig));
        scrollPane.setFitToHeight(true);

        scrollPane.visibleProperty().bind(kodiMovie.tvSearchResultsProperty().emptyProperty().not());
        scrollPane.managedProperty().bind(scrollPane.visibleProperty());

//        scrollPane.getStyleClass().add("search-result-box");

        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        getChildren().add(UiUtil.applyDebug(scrollPane, appConfig));
    }

    // Getter / Setter //

    public Movie getMovie() {
        return movie;
    }
}
