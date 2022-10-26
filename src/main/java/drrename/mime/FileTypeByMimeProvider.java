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

package drrename.mime;

import drrename.FileTypeProvider;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.Tika;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

@Slf4j
public class FileTypeByMimeProvider implements FileTypeProvider {

    public static String UNKNOWN = "n/a";

    public static String DIRECTORY = "directory";

    @Override
    public String getFileType(Path path) {
        final File file = path.toFile();
        if(Files.isDirectory(path)){
            return DIRECTORY;
        }
        try {
            Tika tika = new Tika();
            @SuppressWarnings("")
            var result = tika.detect(file);
            return result;
        } catch (Exception e) {
            log.error(e.getLocalizedMessage(), e);
            return UNKNOWN;
        }
    }
}
