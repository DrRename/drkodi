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

import drrename.mime.FileTypeByMimeProvider;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Slf4j
public class MovieFileNameChecker {

    private List<Path> mediaFiles;

    public MovieFileNameType checkDir(Path directory) {
        String movieName = directory.getFileName().toString();
        try {
            setMediaFiles(findAllMediaFiles(directory));
            for(Path mediaFile : mediaFiles){
                String baseName = FilenameUtils.getBaseName(mediaFile.getFileName().toString());
                if(baseName.startsWith(movieName)){
                    return MovieFileNameType.MATCHES_DIR_NAME;
                }
            }
            if(!mediaFiles.isEmpty()){
                return MovieFileNameType.INVALID_MEDIA_FILE_NAME;
            }
        } catch (IOException e) {
            log.error(e.getLocalizedMessage(), e);
            return MovieFileNameType.EXCEPTION;

        }
        return MovieFileNameType.NO_MEDIA_FILES_FOUND;
    }

    private List<Path> findAllMediaFiles(Path directory) throws IOException {
        List<Path> result = new ArrayList<>();
        try (DirectoryStream<Path> ds = Files.newDirectoryStream(directory)) {
            for (Path child : ds) {
                if(Files.isRegularFile(child)){
                    var mediaType = new FileTypeByMimeProvider().getFileType(child);
                    if(mediaType.startsWith("video")){
                        result.add(child.getFileName());
                    }
                }
            }
        }
        return result;
    }
}
