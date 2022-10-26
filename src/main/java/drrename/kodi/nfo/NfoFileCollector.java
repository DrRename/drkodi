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
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class NfoFileCollector {

    public List<Path> collectNfoFiles(Path directory) throws IOException {
        List<Path> result = new ArrayList<>();
        try (DirectoryStream<Path> ds = Files.newDirectoryStream(directory)) {
            for (Path child : ds) {
                String extension = FilenameUtils.getExtension(child.getFileName().toString());
                if(Files.isHidden(child)){
                    log.debug("Ignoring hidden NFO file {}", child);
                    continue;
                }
                if (Files.isRegularFile(child) && NfoFiles.DEFAULT_EXTENSION.equalsIgnoreCase(extension)) {
                    result.add(child);
                }
            }
        }
        return result;
    }
}
