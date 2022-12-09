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

package drrename.kodi.data;

import drrename.kodi.MovieDbQuerier2;
import drrename.kodi.data.json.WebSearchResults;
import javafx.concurrent.Task;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class WebQuerierTask extends Task<WebSearchResults> {

    private final MovieDbQuerier2 querier2;

    private final Movie movie;

    @Override
    protected WebSearchResults call() throws Exception {
        log.info("Starting query for {}", movie);
        var searchString = prepareSearchString(movie.getMovieTitle());
        WebSearchResults movieDbMovieSearchResult = querier2.query(searchString, null);
        log.debug("Found {} matches for '{}'", movieDbMovieSearchResult.getSearchResults().keySet().size(), searchString);
        return movieDbMovieSearchResult;
    }

    private String prepareSearchString(String movieTitle) {
        String s = movieTitle.replaceAll("\\.+", "");
        return s;
    }
}
