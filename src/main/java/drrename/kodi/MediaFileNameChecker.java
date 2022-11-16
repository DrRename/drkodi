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
import java.util.Collections;
import java.util.List;

@Getter
@Setter
@Slf4j
public class MediaFileNameChecker {

    public MediaFileNameCheckResult checkStatus(Path directory) {
        String movieName = directory.getFileName().toString();
        try {
            List<Path> mediaFiles = findAllVideoFiles(directory);
            for(Path mediaFile : mediaFiles){
                String baseName = FilenameUtils.getBaseName(mediaFile.getFileName().toString());
                if(!baseName.equals(movieName)){
                    return new MediaFileNameCheckResult(MovieFileNameType.INVALID_MEDIA_FILE_NAME, mediaFiles);
                }
            }
            if(!mediaFiles.isEmpty()){
                return new MediaFileNameCheckResult(MovieFileNameType.MATCHES_DIR_NAME, mediaFiles);
            }
        } catch (IOException e) {
            log.error(e.getLocalizedMessage(), e);
            return new MediaFileNameCheckResult(MovieFileNameType.EXCEPTION, Collections.emptyList());

        }
        return new MediaFileNameCheckResult(MovieFileNameType.NO_MEDIA_FILES_FOUND, Collections.emptyList());
    }

    static List<Path> findAllVideoFiles(Path directory) throws IOException {
        List<Path> result = new ArrayList<>();
        try (DirectoryStream<Path> ds = Files.newDirectoryStream(directory)) {
            for (Path child : ds) {
                if(Files.isRegularFile(child)){
                    var mediaType = new FileTypeByMimeProvider().getFileType(child);
                    if(mediaType.startsWith("video")){
                        result.add(child);
                    }
                }
            }
        }
        return result;
    }
}
