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
import drkodi.ui.UiUtil;
import drkodi.ui.config.KodiUiConfig;
import javafx.collections.ListChangeListener;
import javafx.scene.layout.HBox;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SearchResultsBox extends HBox {

    public SearchResultsBox(MovieData item, AppConfig appConfig, KodiUiConfig kodiUiConfig){



        // content
        // Set initial value
        setSearchResults(item, appConfig, kodiUiConfig);
        // Add listener
        item.searchResultsProperty().addListener(new ListChangeListener<SearchResult>() {
            @Override
            public void onChanged(Change<? extends SearchResult> c) {
                while (c.next()){
                    log.debug("Search results changed, updating content");
                }
                setSearchResults(item, appConfig, kodiUiConfig);
            }
        });






//       setStyle("-fx-background-color: #da1919;");



        // behaviour
//        visibleProperty().bind(item.searchResultsProperty().emptyProperty().not());
//        managedProperty().bind(visibleProperty());


    }

    private void setSearchResults(MovieData item, AppConfig appConfig, KodiUiConfig kodiUiConfig) {
        getChildren().clear();
        if(item.getSearchResults() == null){
            return;
        }
        for(SearchResult searchResult : item.getSearchResults()){
            if(searchResult.getImage() != null) {
                var sr = new SearchResultBox(item, searchResult, appConfig, kodiUiConfig);
                getChildren().add(UiUtil.applyDebug(sr, appConfig));
            }
        }
    }
}
