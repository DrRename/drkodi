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

package drrename.kodi.nfo;

import drrename.kodi.MovieDbGenre;
import drrename.kodi.NfoMovie;
import drrename.kodi.NfoRoot;
import drrename.kodi.data.Movie;
import drrename.kodi.data.Qualified;
import org.apache.commons.lang3.StringUtils;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class NfoUtil {

    public static String getPlot(NfoRoot item) {
        var nfoMovie = getNfoMovie(item);
        return nfoMovie == null ? null : nfoMovie.getPlot();
    }

    public static NfoMovie getNfoMovie(NfoRoot nfoRoot) {
        return nfoRoot == null ? null : nfoRoot.getMovie();
    }

    public static NfoRoot getNfoRoot(Movie item) {
        return Qualified.isOk(item.getNfoData()) ? null : item.getNfoData().getElement();
    }

    public static NfoMovie.UniqueId getId(NfoRoot item) {
        var nfoMovie = getNfoMovie(item);
        return nfoMovie == null ? null : nfoMovie.getUniqueid();
    }

    public static Number getId2(NfoRoot item) {
        var id = getId(item);
        return id == null ? null : id.getUniqueid();
    }


    public static String getMovieTitle(NfoRoot item) {
        var nfoMovie = getNfoMovie(item);
        return nfoMovie == null ? null : nfoMovie.getTitle();
    }

    public static Integer getMovieYear(NfoRoot item) {
        var nfoMovie = getNfoMovie(item);
        return nfoMovie == null ? null : Integer.parseInt(nfoMovie.getYear());
    }

    public static List<MovieDbGenre> getGenres(NfoRoot item) {
        var nfoMovie = getNfoMovie(item);
        return nfoMovie == null ? null : nfoMovie.getGenre().stream().map(s -> new MovieDbGenre(null, s)).toList();
    }

    public static String getTagline(NfoRoot item) {
        var nfoMovie = getNfoMovie(item);
        return nfoMovie == null ? null : nfoMovie.getTagline();
    }

    public static Path getImagePath(Path nfoParent, NfoRoot item) {
        var nfoMovie = getNfoMovie(item);
        if(nfoMovie == null){
            return null;
        }
        String art = getNfoArtPoster(nfoMovie);
        Path result = StringUtils.isBlank(art) ? null : nfoParent.resolve(Paths.get(art));
        return result;
    }

    private static String getNfoArtPoster(NfoMovie item) {
        var art = item.getArt();
        return art == null ? null : art.getPoster();
    }
}
