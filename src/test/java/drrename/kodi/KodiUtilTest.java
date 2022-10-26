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

package drrename.kodi;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class KodiUtilTest {

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void testHappyCaseName(){
        var directoryString = "Frozen (2011)";
        var expectedString = "Frozen";
        assertEquals(expectedString, KodiUtil.getMovieNameFromDirectoryName(directoryString));
    }

    @Test
    void testHappyCaseYear(){
        var directoryString = "Frozen (2011)";
        var expectedNumber = 2011;
        assertEquals(expectedNumber, KodiUtil.getMovieYearFromDirectoryName(directoryString));
    }

    @Test
    void testMissingYear(){
        var directoryString = "Frozen";
        var expectedString = "Frozen";
        assertEquals(expectedString, KodiUtil.getMovieNameFromDirectoryName(directoryString));
    }

    @Test
    void testMissingYear2(){
        var directoryString = "Frozen";
        assertNull(KodiUtil.getMovieYearFromDirectoryName(directoryString));
    }

    @Test
    void testNoYearInBrackets(){
        var directoryString = "Frozen (cold)";
        assertNull(KodiUtil.getMovieYearFromDirectoryName(directoryString));
    }
}