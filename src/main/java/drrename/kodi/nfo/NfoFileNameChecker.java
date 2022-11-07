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

package drrename.kodi.nfo;

import drrename.kodi.NfoFileNameCheckResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;


@Slf4j
public class NfoFileNameChecker extends AbstractNfoFileChecker {

    protected NfoFileContentType checkFile(String movieName, Path nfoFile) {
        if (!Files.isRegularFile(nfoFile)) {
            throw new IllegalArgumentException(nfoFile.getFileName().toString() + " is not a file");
        }
        String nfoFileName = nfoFile.getFileName().toString();
        if (NfoFiles.DEFAULT_NAME.equals(nfoFileName)) {
            return NfoFileContentType.DEFAULT_NAME;
        } else if (FilenameUtils.getBaseName(nfoFileName).equals(movieName)) {
            return NfoFileContentType.MOVIE_NAME;
        } else {
            return NfoFileContentType.INVALID_NAME;
        }
    }


}
