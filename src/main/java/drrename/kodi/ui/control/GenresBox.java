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

import drrename.kodi.MovieDbGenre;
import drrename.kodi.data.Movie;
import javafx.collections.ListChangeListener;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import lombok.extern.slf4j.Slf4j;

import java.util.Collection;

@Slf4j
public class GenresBox extends HBox {
    public GenresBox(Movie kodiMovie) {
        setContent(kodiMovie.getGenres());

        kodiMovie.genresProperty().addListener(new ListChangeListener<MovieDbGenre>() {
            @Override
            public void onChanged(Change<? extends MovieDbGenre> c) {
                while(c.next()){
                    log.debug("Genres changed: {}", c);
                }
                setContent(c.getList());
            }
        });

        setSpacing(4);
        setPadding(new Insets(4,4,4,4));
        visibleProperty().bind(kodiMovie.genresProperty().isNotNull());
        managedProperty().bind(visibleProperty());
    }

    private void setContent(Collection<? extends MovieDbGenre> genres) {
        getChildren().clear();
        for(MovieDbGenre genre : genres){
            getChildren().add(buildGenreNode(genre));
        }
    }

    private Label buildGenreNode(MovieDbGenre genre) {
        Label label = new Label(genre.getName());
        label.getStyleClass().add("kodi-genre");
        return label;
    }
}
