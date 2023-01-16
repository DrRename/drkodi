/*
 *     Dr.Kodi - A Minimalistic Batch Renamer
 *
 *     Copyright (C) 2023
 *
 *     This file is part of Dr.Kodi.
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
import drkodi.data.movie.Movie;
import drkodi.data.movie.MovieData;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GenresBox2 extends GenresBox {

    private final Movie movie;

    public GenresBox2(Movie kodiMovie) {
        super(kodiMovie);
        this.movie = kodiMovie;
    }

    @Override
    public void setContent(Movie genres) {
        super.setContent(genres);
        getChildren().add(buildAddGenreButton(genres));
    }

    @Override
    protected Control buildGenreNode(MovieDbGenre genre) {
        Control result = super.buildGenreNode(genre);

        result.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                log.debug("Genre {} clicked", genre);

                Button button = new Button("Remove");
                Button button2 = new Button("Cancel");
                button.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        log.debug("Remove {}", genre);
                        movie.getGenres().remove(genre);
                        movie.writeNfoDataAndImage();
                    }
                });
                button2.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        log.debug("Cancel {}", genre);
                        Pane parent = ((Pane)button.getParent());
                        parent.getChildren().removeAll(button, button2);
                    }
                });
                Pane parent = ((Pane)result.getParent());
                parent.getChildren().addAll(button, button2);

            }
        });



        return result;
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
