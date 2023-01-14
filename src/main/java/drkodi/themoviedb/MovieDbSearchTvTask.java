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

package drkodi.themoviedb;

import drkodi.data.json.WebSearchResults;
import drkodi.data.movie.Movie;
import drkodi.normalization.TitleSearchNormalizer;
import javafx.concurrent.Task;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class MovieDbSearchTvTask extends Task<WebSearchResults> {

    private final MovieDbSearcher movieDbSearcher;

    private final TitleSearchNormalizer normalizer;

    private final Movie movie;

    @Override
    protected WebSearchResults call() throws Exception {
        var searchString = normalizer.normalize(movie.getMovieTitle());
        log.debug("Starting query for {}", searchString);
        WebSearchResults movieDbMovieSearchResult = movieDbSearcher.searchTv(searchString, null);
        log.debug("Found {} TV matches for '{}'", movieDbMovieSearchResult.getSearchResults().keySet().size(), searchString);
        return movieDbMovieSearchResult;
    }


}
