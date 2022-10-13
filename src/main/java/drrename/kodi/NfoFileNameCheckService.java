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

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class NfoFileNameCheckService implements CheckService {

    public CheckResult calculate(Path path){
        try {
            return checkDir(path);
        } catch (IOException e) {
            log.error(e.getLocalizedMessage(), e);
        }
        return null;
    }

    static NfoFileCheckResult checkDir(Path path) throws IOException {
        if(!Files.isDirectory(path)){
            throw new IllegalArgumentException(path.getFileName().toString() + " is not a directory");
        }
        String movieName = path.getFileName().toString();
        List<Path> childString = new ArrayList<>();
        NfoFileNameType type = NfoFileNameType.NO_FILE;
        try (DirectoryStream<Path> ds = Files.newDirectoryStream(path)) {
            for (Path child : ds) {
                String extension = FilenameUtils.getExtension(child.getFileName().toString());
                if (Files.isRegularFile(child) && "nfo".equalsIgnoreCase(extension)) {
                    childString.add(child);
                    if(childString.size() > 1){
                        type = NfoFileNameType.MULTIPLE_FILES;
                    } else {
                        type = checkFile(movieName, child);
                    }
                }
            }
        }
        return new NfoFileCheckResult(type.toString(), !type.equals(NfoFileNameType.DEFAULT_NAME), childString);
    }

     static NfoFileNameType checkFile(String movieName, Path child) {
         if(!Files.isRegularFile(child)){
             throw new IllegalArgumentException(child.getFileName().toString() + " is not a directory");
         }
        String childName = child.getFileName().toString();
        if ("movie.nfo".equalsIgnoreCase(childName)) {
            return NfoFileNameType.DEFAULT_NAME;
        } else if (FilenameUtils.getBaseName(childName).equalsIgnoreCase(movieName)) {
            return NfoFileNameType.MOVIE_NAME;
        } else {
            return NfoFileNameType.INVALID_NAME;
        }
    }
}
