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

package drrename.kodi.ui;

import drrename.RenamingPath;
import drrename.SearchResultMapperImpl;
import drrename.kodi.NfoMovie;
import drrename.kodi.NfoRoot;
import drrename.kodi.data.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.nio.file.Path;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class KodiDataBuilder {

    static Executor executor = Executors.newSingleThreadExecutor();

    static StaticMovieData build(String title, Integer year){
        StaticMovieData result = new Movie(new RenamingPath(Path.of("hans/dampf")), new SearchResultMapperImpl(), executor, null);
        result.setMovieTitle(title);
        result.setMovieYear(year);
        result.setNfoData(QualifiedNfoData.from(buildNfoData()));
        result.setSearchResults(buildSearchResults());
        return result;
    }

    static ObservableList<SearchResult> buildSearchResults() {
        ObservableList<SearchResult> result = FXCollections.observableArrayList();
        result.add(buildSearchResult(1));
        result.add(buildSearchResult(2));
        result.add(buildSearchResult(3));
        return result;
    }

    static SearchResult buildSearchResult(int cnt) {
        SearchResult result = new SearchResult();
        result.setTitle("Search Result " + cnt);
        result.setReleaseDate((2000 + cnt));
        return result;
    }

    private static NfoRoot buildNfoData() {
        NfoRoot nfoData = new NfoRoot();
        nfoData.setMovie(new NfoMovie());
        nfoData.getMovie().setTitle("NFO Title");
        nfoData.getMovie().setYear("2022");
        NfoMovie.Art art = new NfoMovie.Art();
        art.setPoster("movie.jpg");
        nfoData.getMovie().setArt(art);
        return nfoData;
    }
}
