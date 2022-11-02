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

package drrename.kodi.nfo;

import drrename.RenameUtil;
import drrename.kodi.FixFailedException;
import drrename.kodi.NfoFileNameCheckResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Path;

@Slf4j
@RequiredArgsConstructor
public class NfoFileNameFixer {

    private final NfoFileNameCheckResult checkResult;

    public void fix(String movieName) throws FixFailedException {
        final Path nfoFile = checkResult.getNfoFiles().get(0);
        final String nfoFileName = movieName + KodiConstants.NFO_FILE_EXTENSION;
        try {
            Path newPath = RenameUtil.rename(nfoFile, nfoFileName);
            log.info("Renamed {} to {}", nfoFile, newPath);
            if(!newPath.getFileName().toString().equals(nfoFileName)){
                throw new FixFailedException("Rename failed");
            }
        } catch (IOException e) {
            throw new FixFailedException(e);
        }
    }
}
