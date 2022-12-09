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
import drrename.kodi.ui.SearchResultsAndTitleBox;
import drrename.kodi.ui.config.KodiUiConfig;
import javafx.scene.layout.VBox;

import java.util.concurrent.Executor;

public class HansBox extends VBox {

    public HansBox(Movie kodiMovie, Executor executor, AppConfig appConfig, KodiUiConfig kodiUiConfig){

        // content
        getChildren().add(UiUtil.applyDebug(new KodiMovieAndImageBox(kodiMovie, executor, appConfig, kodiUiConfig), appConfig));
        getChildren().add(UiUtil.applyDebug(new SearchResultsAndTitleBox(kodiMovie, appConfig, kodiUiConfig), appConfig));

        // layout
        getStyleClass().add("kodi-box");


    }
}
