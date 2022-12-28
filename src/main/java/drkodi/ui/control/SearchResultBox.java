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
import drkodi.data.SearchResult;
import drkodi.data.MovieData;
import drkodi.ui.KodiUiElementBuilder;
import drkodi.ui.UiUtil;
import drkodi.ui.config.KodiUiConfig;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class SearchResultBox extends VBox {


    public SearchResultBox(MovieData kodieMovie, SearchResult searchResult, AppConfig appConfig, KodiUiConfig kodiUiConfig) {


//        setMaxWidth(searchResult.getImage().getWidth());
        addImage(searchResult, appConfig, kodiUiConfig);
        addMovieTitle(searchResult, appConfig, kodiUiConfig);
        addMovieYear(searchResult, appConfig);
        addTakeOverButton(kodieMovie, appConfig, searchResult);

        setPadding(new Insets(4,4,4,4));

//        visibleProperty().bind(kodieMovie.searchResultsProperty().emptyProperty().not());
//        managedProperty().bind(visibleProperty());


    }

    private void addImage(SearchResult searchResult, AppConfig appConfig, KodiUiConfig kodiUiConfig) {
        if (searchResult.getImage() != null) {
            var imageBox = new ImageBox(searchResult.getImage(), kodiUiConfig.getSearchImageHeight());
            getChildren().add(UiUtil.applyDebug(imageBox, appConfig));
            maxWidthProperty().bind(imageBox.widthProperty());

        }
    }

    private void addMovieTitle(SearchResult searchResult, AppConfig appConfig, KodiUiConfig kodiUiConfig) {
        if (searchResult.getTitle() != null) {
            Label labelTitle = new Label(searchResult.getTitle());
            labelTitle.setWrapText(true);
            getChildren().add(UiUtil.applyDebug(labelTitle, appConfig));

        }
    }

    private void addMovieYear(SearchResult searchResult, AppConfig appConfig) {
        if (searchResult.getReleaseDate() != null) {
            Label labelYear = new Label(KodiUiElementBuilder.buildMovieYearString(searchResult.getReleaseDate()));
            getChildren().add(UiUtil.applyDebug(labelYear, appConfig));
        }
    }

    private void addTakeOverButton(MovieData kodieMovie, AppConfig appConfig, SearchResult searchResult) {
        Hyperlink button = new Hyperlink("Take over data");
        button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                kodieMovie.takeOverSearchResultData(searchResult);
            }
        });
        button.getStyleClass().add("kodi-link");
        getChildren().add(UiUtil.applyDebug(button, appConfig));
    }
}
