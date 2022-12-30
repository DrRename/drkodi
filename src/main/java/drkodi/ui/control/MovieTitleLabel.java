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

import drkodi.data.movie.MovieData;
import javafx.scene.control.Label;

public class MovieTitleLabel extends Label {

    public MovieTitleLabel(MovieData element){
        // initially set
        setText(element.getMovieTitle());
        // bind
        textProperty().bind(element.movieTitleProperty());
        // set style
        getStyleClass().add("kodi-movie-title");
        setWrapText(true);
    }
}
