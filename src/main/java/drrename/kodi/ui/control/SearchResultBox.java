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
import drrename.kodi.data.SearchResult;
import drrename.kodi.ui.KodiUiElementBuilder;
import drrename.kodi.ui.config.KodiUiConfig;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class SearchResultBox extends VBox {



    public SearchResultBox(StaticMovieData kodieMovie, SearchResult searchResult, KodiUiConfig kodiUiConfig) {

        addImage(searchResult, kodiUiConfig);
        addMovieTitle(searchResult, kodiUiConfig);
        addMovieYear(searchResult);
        addTakeOverButton(kodieMovie, searchResult);

        setPadding(new Insets(8,0,8,0));
    }

    private void addImage(SearchResult searchResult, KodiUiConfig kodiUiConfig) {
        if(searchResult.getImage() != null){
            var imageBox = new ImageBox(searchResult.getImage(), kodiUiConfig.getSearchImageHeight());
            getChildren().add(imageBox);
            setMaxWidth(Math.max(imageBox.getWidth() + 4, 100));
        }
    }

    private void addMovieTitle(SearchResult searchResult, KodiUiConfig kodiUiConfig) {
        if(searchResult.getTitle() != null){
            Label labelTitle = new Label(searchResult.getTitle());
            labelTitle.setWrapText(true);
            VBox.setVgrow(labelTitle, Priority.ALWAYS);
            getChildren().add(labelTitle);
        }
    }

    private void addMovieYear(SearchResult searchResult){
        if (searchResult.getReleaseDate() != null) {
            Label labelYear = new Label(KodiUiElementBuilder.buildMovieYearString(searchResult.getReleaseDate()));
            getChildren().add(labelYear);
        }
    }

    private void addTakeOverButton(StaticMovieData kodieMovie, SearchResult searchResult){
        Hyperlink button = new Hyperlink("Take over data");
        button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                kodieMovie.takeOverSearchResultData(searchResult);
            }
        });
        button.getStyleClass().add("kodi-link");
        getChildren().add(button);
    }
}
