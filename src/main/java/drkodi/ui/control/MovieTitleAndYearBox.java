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
import drkodi.data.StaticMovieData;
import drkodi.ui.UiUtil;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;

public class MovieTitleAndYearBox extends HBox {

    public MovieTitleAndYearBox(StaticMovieData element, AppConfig appConfig) {

        // content
        getChildren().add(UiUtil.applyDebug(new MovieTitleLabel(element), appConfig));
        getChildren().add(UiUtil.applyDebug(new MovieYearLabel(element), appConfig));

        // layout
        getStyleClass().add("kodi-movie-title-year");
        setSpacing(4);
        setAlignment(Pos.TOP_LEFT);
        setPadding(new Insets(8,8,8,0));
    }
}
