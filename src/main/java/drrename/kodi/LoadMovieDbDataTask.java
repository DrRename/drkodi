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

package drrename.kodi;

import drrename.config.AppConfig;
import drrename.kodi.data.Movie;
import drrename.kodi.data.json.WebSearchResults;
import javafx.application.Platform;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.ResourceBundle;

@Slf4j
public class LoadMovieDbDataTask extends KodiTask {

    private final MovieDbQuerier2 querier2;

    public LoadMovieDbDataTask(AppConfig appConfig, ResourceBundle resourceBundle, List<? extends Movie> elements, int indexOfAppearance, MovieDbQuerier2 querier2) {
        super(appConfig, resourceBundle, elements, indexOfAppearance);
        this.querier2 = querier2;
    }

    public LoadMovieDbDataTask(AppConfig appConfig, ResourceBundle resourceBundle, Movie element, int indexOfAppearance, MovieDbQuerier2 querier2) {
        super(appConfig, resourceBundle, element, indexOfAppearance);
        this.querier2 = querier2;
    }

    @Override
    protected void handleElement(Movie element) throws Exception {
        if (element.isDataComplete()) {
            log.info("Data complete for {}, will not query TheMovieDb", element);
            return;
        }
        log.info("Starting query for {}", element);
        WebSearchResults movieDbMovieSearchResult = querier2.query(element.getMovieTitle(), null);
        log.debug("Found {} matches for {}", movieDbMovieSearchResult.getSearchResults().keySet().size(), element.getMovieTitle());
        Platform.runLater(() -> element.setWebSearchResult(movieDbMovieSearchResult));
    }
}
