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

import drkodi.data.MovieData;
import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import org.apache.commons.lang3.StringUtils;

public class MovieOriginalTitleLabel extends Label {

    public MovieOriginalTitleLabel(MovieData element){

        // bind
        textProperty().bind(Bindings.createStringBinding(() ->getString(element.getMovieOriginalTitle()), element.movieOriginalTitleProperty()));
        // set style
        getStyleClass().add("kodi-movie-original-title");
        setWrapText(true);

        setPadding(new Insets(0,0,0,0));
        setAlignment(Pos.CENTER_LEFT);

        visibleProperty().bind(Bindings.isNotNull(element.movieOriginalTitleProperty()).and(Bindings.equal(element.movieTitleProperty(), element.movieOriginalTitleProperty()).not()));
        managedProperty().bind(visibleProperty());


    }



    private String getString(String movieOriginalTitle) {
        if(StringUtils.isBlank(movieOriginalTitle)){
            return null;
        }
        return "(" + movieOriginalTitle + ")";
    }
}
