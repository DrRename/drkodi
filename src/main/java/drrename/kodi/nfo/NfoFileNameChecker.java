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

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Slf4j
public class NfoFileNameChecker extends NfoChecker {

    public NfoFileNameType checkDir(Path directory) {
        String movieName = directory.getFileName().toString();
        try {
            setNfoFiles(new NfoFileCollector().collectNfoFiles(directory));
            if (getNfoFiles().isEmpty()) {
                return NfoFileNameType.NO_FILE;
            }
            if (getNfoFiles().size() > 1) {
                return NfoFileNameType.MULTIPLE_FILES;
            } else {
                return checkFile(movieName, getNfoFiles().get(0));
            }
        } catch (IOException e) {
            log.error(e.getLocalizedMessage(), e);
            return NfoFileNameType.ERROR;
        }
    }

    protected NfoFileNameType checkFile(String movieName, Path nfoFile) {
        if (!Files.isRegularFile(nfoFile)) {
            throw new IllegalArgumentException(nfoFile.getFileName().toString() + " is not a file");
        }
        String nfoFileName = nfoFile.getFileName().toString();
        if (NfoFiles.DEFAULT_NAME.equalsIgnoreCase(nfoFileName)) {
            return NfoFileNameType.DEFAULT_NAME;
        } else if (FilenameUtils.getBaseName(nfoFileName).equalsIgnoreCase(movieName)) {
            return NfoFileNameType.MOVIE_NAME;
        } else {
            return NfoFileNameType.INVALID_NAME;
        }
    }


}
