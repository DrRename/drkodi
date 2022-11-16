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

import drrename.model.RenamingPath;
import drrename.util.DrRenameUtil;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Getter
@Slf4j
public class MediaFileNameIssue extends FxKodiIssue<MediaFileNameCheckResult> {

    private MediaFileNameCheckResult checkResult;

    public MediaFileNameIssue(RenamingPath moviePath) {
        super(moviePath);
    }

    @Override
    public MediaFileNameCheckResult checkStatus() {
        return new MediaFileNameChecker().checkStatus(getRenamingPath().getOldPath());
    }

    @Override
    public void fix(MediaFileNameCheckResult checkStatusResult) throws FixFailedException {
        log.debug("Fixing following files: {}", checkStatusResult.getMediaFiles());
        for(Path file : checkResult.getMediaFiles()){
            fixFile(file);
        }
    }

    private void fixFile(Path file) throws FixFailedException {
        if(Files.isDirectory(file)){
            throw new IllegalArgumentException(file.getFileName() + " is a directory");
        }
        String fileNameString = file.getFileName().toString();
        var baseName = FilenameUtils.getBaseName(fileNameString);
        var extension = FilenameUtils.getExtension(fileNameString);
        var newPath = file.resolveSibling(getMovieName() + "." + extension);
        log.debug("Renaming from {} to {}", file, newPath);
        try {
            DrRenameUtil.rename(file, newPath.getFileName().toString());
        } catch (IOException e) {
            throw new FixFailedException(e);
        }
    }

    @Override
    public void updateStatus(MediaFileNameCheckResult checkStatusResult) {
        this.checkResult = checkStatusResult;
    }

    @Override
    public String getIdentifier() {
        return "Media File name";
    }

    @Override
    public String getHelpText() {
        return null;
    }
}
