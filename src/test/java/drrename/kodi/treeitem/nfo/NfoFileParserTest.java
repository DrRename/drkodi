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

import drrename.kodi.nfo.NfoFileParser;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class NfoFileParserTest {

    @BeforeEach
    void setUp() {

    }

    @AfterEach
    void tearDown() {

    }

    @Test
    void parse01() throws IOException {
        var result = new NfoFileParser().parse(Paths.get("src/test/resources/kodi/test-nfo-01.nfo"));
        assertNotNull(result.getMovie().getTitle());
        assertNotNull(result.getUrl());
    }
    @Test
    void parse02() throws IOException {
        var result = new NfoFileParser().parse(Paths.get("src/test/resources/kodi/test-nfo-02.nfo"));
        assertNotNull(result.getUrl());
        assertNotNull(result.getMovie());
    }
    @Test
    void parse03() throws IOException {
        var result = new NfoFileParser().parse(Paths.get("src/test/resources/kodi/url-only.nfo"));
        assertNotNull(result.getUrl());
    }

    @Test
    void emptyLine() throws IOException {
        var result = new NfoFileParser().parse(Paths.get("src/test/resources/kodi/empty-line.nfo"));
        System.out.println(result);
    }
}