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

package drrename.kodi.ui.control;

import drrename.config.AppConfig;
import drrename.kodi.data.Movie;
import drrename.ui.UiUtil;
import drrename.kodi.ui.KodiWarningBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.util.concurrent.Executor;

public class KodiMovieInfoBox extends VBox {

    public KodiMovieInfoBox(Movie kodiMovie, AppConfig appConfig, Executor executor) {

        // content

        getChildren().add(UiUtil.applyDebug(new MovieTitleAndYearBox(kodiMovie, appConfig), appConfig));
        getChildren().add(UiUtil.applyDebug(new KodiOpenAndSaveButtonsBox(kodiMovie, executor), appConfig));
        getChildren().add(UiUtil.applyDebug(new TaglineBox(kodiMovie), appConfig));
        getChildren().add(UiUtil.applyDebug(new PlotBox(kodiMovie), appConfig));
        getChildren().add(UiUtil.applyDebug(new GenresBox(kodiMovie), appConfig));
        getChildren().add(UiUtil.applyDebug(UiUtil.applyDebug(new KodiWarningBox(kodiMovie, appConfig), appConfig), appConfig));

        // layout

        VBox.setVgrow(this, Priority.ALWAYS);

    }
}
