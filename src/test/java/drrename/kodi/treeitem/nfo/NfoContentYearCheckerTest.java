/*
 *     Dr.Rename - A Minimalistic Batch Renamer
 *
 *     Copyright (C) 2022
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Affero General Public License as
 *     published by the Free Software Foundation, either version 3 of the
 *     License, or (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Affero General Public License for more details.
 *
 *     You should have received a copy of the GNU Affero General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package drrename.kodi.treeitem.nfo;

import drrename.kodi.nfo.NfoContentYearChecker;
import drrename.kodi.nfo.NfoFileContentType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;

class NfoContentYearCheckerTest {

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void checkValidFile() {
        var result = new NfoContentYearChecker().checkNfoFile(Paths.get("src/test/resources/kodi/Some Movie (2000)/movie.nfo"));
        assertEquals(NfoFileContentType.VALID_YEAR, result);
    }

    @Test
    void checkInValidFile() {
        var result = new NfoContentYearChecker().checkFile(null,Paths.get("src/test/resources/kodi/Some Movie (2000)/wrong-format.nfo"));
        assertEquals(NfoFileContentType.INVALID_FILE, result);
    }

    @Test
    void checkUrlOnlyFile() {
        var result = new NfoContentYearChecker().checkFile(null,Paths.get("src/test/resources/kodi/UrlOnlyMovie/movie.nfo"));
        assertEquals(NfoFileContentType.URL_ONLY_FILE, result);
    }

    @Test
    void checkYearlessFile() {
        var result = new NfoContentYearChecker().checkFile(null,Paths.get("src/test/resources/kodi/Yearless Movie/movie.nfo"));
        assertEquals(NfoFileContentType.MISSING_YEAR, result);
    }
}