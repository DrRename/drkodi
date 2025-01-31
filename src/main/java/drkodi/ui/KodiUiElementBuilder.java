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

package drkodi.ui;

import drkodi.config.AppConfig;
import drkodi.data.movie.MovieData;
import drkodi.data.movie.Movie;
import drkodi.ui.config.KodiUiConfig;
import drkodi.ui.control.ImageBox;
import drkodi.ui.control.KodiMovieInfoBox;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class KodiUiElementBuilder {

    public static String buildMovieYearString(MovieData item) {
        return buildMovieYearString(item.getMovieYear());
    }

    public static String buildMovieYearString(Integer year) {
        if (year == null) {
            return "";
        }
        return "(" + year + ")";
    }

    public static String buildTitleAndYearString(MovieData item) {
        return buildTitleAndYearString(item.getMovieTitle(), item.getMovieYear());
    }

    public static String buildTitleAndYearString(String movieTitle, Integer movieYear) {
        return movieTitle + " " + buildMovieYearString(movieYear);
    }

    private final AppConfig appConfig;

    private final KodiUiConfig kodiUiConfig;

    private Node applyDebug(Node node) {
        return UiUtil.applyDebug(node, appConfig);
    }


}
