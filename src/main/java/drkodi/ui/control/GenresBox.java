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

import drkodi.MovieDbGenre;
import drkodi.data.dynamic.Movie;
import javafx.collections.ListChangeListener;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GenresBox extends FlowPane {
    public GenresBox(Movie kodiMovie) {

        setContent(kodiMovie);

        kodiMovie.genresProperty().addListener(new ListChangeListener<MovieDbGenre>() {
            @Override
            public void onChanged(Change<? extends MovieDbGenre> c) {
                while (c.next()) {
                    log.debug("Genres changed: {}", c);
                }
                setContent(kodiMovie);
            }
        });

        setVgap(4);
        setHgap(4);
        setPadding(new Insets(4, 4, 4, 4));

//        visibleProperty().bind(kodiMovie.genresProperty().isNotNull().and(kodiMovie.genresProperty().emptyProperty().not()));
//        managedProperty().bind(visibleProperty());
    }

    private void setContent(Movie genres) {
        if(genres.getGenres() == null){
            return;
        }
        getChildren().clear();
        for(MovieDbGenre genre : genres.getGenres()){
            getChildren().add(buildGenreNode(genre));
        }
        getChildren().add(buildAddGenreButton(genres));
    }

    private Label buildGenreNode(MovieDbGenre genre) {
        Label label = new Label(genre.getName());
        label.getStyleClass().add("kodi-genre");
        return label;
    }

    private Node buildAddGenreButton(Movie genres) {
        HBox result = new HBox();
        result.setAlignment(Pos.CENTER);
        TextField input = new TextField();
        input.getStyleClass().add("kodi-genre");
        input.setPrefWidth(100);

        Button button = new Button("Add");
        button.setOnAction(event -> {
           genres.getGenres().add(new MovieDbGenre(null, input.getText()));
           genres.writeNfoDataAndImage();
        });
        result.getChildren().add(input);
        result.getChildren().add(button);
        return result;
    }
}
