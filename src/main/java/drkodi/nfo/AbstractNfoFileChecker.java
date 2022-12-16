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

package drkodi.nfo;

import drkodi.NfoFileCheckResult;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collections;

@Slf4j
public abstract class AbstractNfoFileChecker {

    public NfoFileCheckResult checkDir(Path directory) {
        String movieName = directory.getFileName().toString();
        try {
            var nfoFiles = new NfoFileCollector().collectNfoFiles(directory);
            if (nfoFiles.isEmpty()) {
                return new NfoFileCheckResult(NfoFileCheckResultType.NO_FILE, Collections.emptyList());
            }
            if (nfoFiles.size() > 1) {
                return new NfoFileCheckResult(NfoFileCheckResultType.MULTIPLE_FILES, nfoFiles);
            } else {
                return new NfoFileCheckResult(checkFile(movieName, nfoFiles.get(0)), nfoFiles.get(0));
            }
        } catch (IOException e) {
            log.error(e.getLocalizedMessage(), e);
            return new NfoFileCheckResult(NfoFileCheckResultType.EXCEPTION, Collections.emptyList());
        }
    }

    protected abstract NfoFileCheckResultType checkFile(String movieName, Path nfoFile);
}
