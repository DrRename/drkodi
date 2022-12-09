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

package drrename.kodi;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

class NfoFileWriterTest {

    NfoFileWriter writer = new NfoFileWriter();

    Path outPathWithoutUrl = Paths.get("src/test/resources/kodi/writer/movie-no-url.nfo");

    Path outPathWithUrl = Paths.get("src/test/resources/kodi/writer/movie-url.nfo");

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void writeNoUrl() throws IOException {

        Files.deleteIfExists(outPathWithoutUrl);
        Files.createDirectories(outPathWithoutUrl.getParent());
        NfoRoot nfoData = new NfoRoot();
        nfoData.movie = new NfoMovie();
        nfoData.movie.title = "title";
        nfoData.movie.year = "1111";

        writer.write(nfoData, outPathWithoutUrl);
    }

    @Test
    void writeWithUrl() throws IOException {

        Files.deleteIfExists(outPathWithoutUrl);
        Files.createDirectories(outPathWithoutUrl.getParent());
        NfoRoot nfoData = new NfoRoot();
        nfoData.movie = new NfoMovie();
        nfoData.movie.title = "title";
        nfoData.movie.year = "1111";
        nfoData.url = "url";

        writer.write(nfoData, outPathWithUrl);
    }

    @Test
    void writeFileDoesntExist() throws IOException {


        NfoRoot nfoData = new NfoRoot();
        nfoData.movie = new NfoMovie();
        nfoData.movie.title = "title";
        nfoData.movie.year = "1111";
        nfoData.url = "url";


        FileNotFoundException thrown = Assertions.assertThrows(FileNotFoundException.class, () -> {
            writer.write(nfoData, Paths.get("file/does/not/exist"));
        }, "FileNotFoundException was expected");

        Assertions.assertEquals("file/does/not/exist.tmp (No such file or directory)", thrown.getMessage());


    }
}