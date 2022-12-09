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

import drrename.config.AppConfig;
import drrename.kodi.data.StaticMovieData;
import drrename.kodi.data.SearchResult;
import drrename.ui.UiUtil;
import drrename.kodi.ui.config.KodiUiConfig;
import javafx.collections.ListChangeListener;
import javafx.geometry.Orientation;
import javafx.scene.layout.FlowPane;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SearchResultsBox extends FlowPane {

    public SearchResultsBox(StaticMovieData item, AppConfig appConfig, KodiUiConfig kodiUiConfig){



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

        // layout
        setOrientation(Orientation.HORIZONTAL);


        // behaviour
        visibleProperty().bind(item.searchResultsProperty().emptyProperty().not());
        managedProperty().bind(visibleProperty());


    }

    private void setSearchResults(StaticMovieData item, AppConfig appConfig, KodiUiConfig kodiUiConfig) {
        getChildren().clear();
        for(SearchResult searchResult : item.getSearchResults()){
            var sr = new SearchResultBox(item, searchResult, kodiUiConfig);
//            VBox.setVgrow(sr, Priority.ALWAYS);
//            sr.setMaxHeight(200);
            getChildren().add(UiUtil.applyDebug(sr, appConfig));
        }
    }
}
