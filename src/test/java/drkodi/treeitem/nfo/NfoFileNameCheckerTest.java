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

package drkodi.treeitem.nfo;

import drkodi.nfo.NfoFileCheckResultType;
import drkodi.nfo.NfoFileNameChecker;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;

class NfoFileNameCheckerTest {

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void checkNoFile() {
        var result = new NfoFileNameChecker().checkDir(Paths.get("src/test/resources/kodi/Maleficent 2 (2019)"));
        assertEquals(NfoFileCheckResultType.NO_FILE, result.getType());
    }

    @Test
    void checkMovieName() {
        var result = new NfoFileNameChecker().checkDir(Paths.get("src/test/resources/kodi/test01"));
        assertEquals(NfoFileCheckResultType.MOVIE_NAME, result.getType());
    }

    @Test
    void checkDefaultName() {
        var result = new NfoFileNameChecker().checkDir(Paths.get("src/test/resources/kodi/test02"));
        assertEquals(NfoFileCheckResultType.DEFAULT_NAME, result.getType());
    }
}