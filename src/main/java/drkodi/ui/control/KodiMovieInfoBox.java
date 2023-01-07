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
import drkodi.ui.WarningsBox;
import drkodi.ui.UiUtil;
import javafx.geometry.Insets;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class KodiMovieInfoBox extends VBox {

    private final ProgressBar progressBar;

    public KodiMovieInfoBox(Movie kodiMovie, AppConfig appConfig) {

        VBox progressBox = new VBox();
        progressBox.setPadding(new Insets(8,2,8,2));

        this.progressBar = new ProgressBar();
        this.progressBar.setMaxWidth(Double.MAX_VALUE);
        progressBox.managedProperty().bind(kodiMovie.runningProperty());
        progressBox.visibleProperty().bind(progressBox.managedProperty());
        progressBox.getChildren().add(progressBar);


        // content

        getChildren().add(UiUtil.applyDebug(progressBox, appConfig));

//        getChildren().add(UiUtil.applyDebug(new KodiOpenAndSaveButtonsBox(kodiMovie), appConfig));

        getChildren().add(UiUtil.applyDebug(new MovieTitleAndYearBox(kodiMovie, appConfig), appConfig));
        getChildren().add(UiUtil.applyDebug(new MovieOriginalTitleLabel(kodiMovie), appConfig));

        getChildren().add(UiUtil.applyDebug(new TaglineBox(kodiMovie), appConfig));
        getChildren().add(UiUtil.applyDebug(new PlotBox(kodiMovie), appConfig));
        getChildren().add(UiUtil.applyDebug(new GenresBox(kodiMovie), appConfig));
        getChildren().add(UiUtil.applyDebug(UiUtil.applyDebug(new WarningsBox(kodiMovie, appConfig), appConfig), appConfig));

        // layout

//        VBox.setVgrow(this, Priority.ALWAYS);
        setPadding(new Insets(4,4,4,4));

        HBox.setHgrow(this, Priority.ALWAYS);

    }
}
