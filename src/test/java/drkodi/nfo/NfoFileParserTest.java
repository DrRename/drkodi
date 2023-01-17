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

package drkodi.nfo;

import drkodi.NfoMovieRoot;
import drkodi.data.movie.MovieData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertNull;

class NfoFileParserTest {

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void checkInValidFile() throws Exception {
        var result = new NfoFileParser(MovieData.Type.MOVIE).parse(Paths.get("src/test/resources/kodi/Some Movie (2000)/wrong-format.nfo"));
        assertNull(result.getUrl());
        assertNull(result.getElement());
    }

    @Test
    void parse01() throws Exception {
        NfoMovieRoot result = (NfoMovieRoot) new NfoFileParser(MovieData.Type.MOVIE).parse(Paths.get("src/test/resources/kodi/nfo/complete.nfo"));
        System.out.println(result);
    }
}