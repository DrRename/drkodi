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

package drrename.kodi.data;

import drrename.kodi.nfo.NfoFileCollector;
import javafx.concurrent.Task;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.nio.file.Path;

@Slf4j
@RequiredArgsConstructor
public class LoadNfoPathTask extends Task<Path> {

    private final static NfoFileCollector nfoFileCollector = new NfoFileCollector();

    private final Path movieDirectory;

    @Override
    protected Path call() throws Exception {
        log.debug("Looking for NFO files in {}", movieDirectory);
        var nfoFiles = nfoFileCollector.collectNfoFiles(movieDirectory);
        log.debug("Found {} NFO files", nfoFiles.size());
        if(!nfoFiles.isEmpty()){
            return nfoFiles.get(0);
        }
        return null;
    }
}
