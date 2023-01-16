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

package drkodi;

import drkodi.config.AppConfig;
import drkodi.data.SearchResultToMovieMapper;
import drkodi.data.movie.Movie;
import drkodi.normalization.FolderNameWarningNormalizer;
import drkodi.normalization.TitleSearchNormalizer;
import drkodi.normalization.MovieTitleWriteNormalizer;
import drkodi.themoviedb.MovieDbSearcher;
import drkodi.ui.control.KodiMoviePathEntryBox;
import drrename.commons.RenamingPath;
import javafx.concurrent.Task;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.ResourceBundle;
import java.util.concurrent.Executor;

@Getter
@RequiredArgsConstructor
public abstract class PrototypeTask<T> extends Task<T> {

    protected final AppConfig appConfig;

    protected final ResourceBundle resourceBundle;

    protected final MovieEntries movieEntries;

    protected final Executor executor;

    protected final SearchResultToMovieMapper searchResultToMovieMapper;

    protected final MovieSearchResultDtoMapper movieSearchResultDtoMapper;

    protected final TvSearchResultDtoMapper tvSearchResultDtoMapper;

    protected final MovieDbSearcher movieDbSearcher;

    protected final TitleSearchNormalizer titleSearchNormalizer;

    protected final MovieTitleWriteNormalizer movieTitleWriteNormalizer;

    protected final FolderNameWarningNormalizer folderNameWarningNormalizer;

    protected Movie buildData(RenamingPath renamingPath) {
        return new Movie(renamingPath, movieSearchResultDtoMapper, executor, movieDbSearcher, folderNameWarningNormalizer, titleSearchNormalizer, searchResultToMovieMapper, movieTitleWriteNormalizer, tvSearchResultDtoMapper);
    }

    protected KodiMoviePathEntryBox buildUiData(Movie data) {
        return new KodiMoviePathEntryBox(data, getAppConfig());
    }


}
