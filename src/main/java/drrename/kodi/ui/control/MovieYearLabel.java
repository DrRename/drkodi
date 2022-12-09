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

import drrename.kodi.data.StaticMovieData;
import javafx.scene.control.Label;

import static drrename.kodi.ui.KodiUiElementBuilder.buildMovieYearString;

public class MovieYearLabel extends Label {

    public MovieYearLabel(StaticMovieData element){
        // initially set
        setText(buildMovieYearString(element));
        // bind
        element.movieYearProperty().addListener((observable, oldValue, newValue) -> setText(buildMovieYearString(newValue)));
        // set style
        getStyleClass().add("kodi-movie-year");
    }
}
