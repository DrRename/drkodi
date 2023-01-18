/*
 *     Dr.Kodi - A Minimalistic Batch Renamer
 *
 *     Copyright (C) 2023
 *
 *     This file is part of Dr.Kodi.
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

package drkodi.data.movie;

import drrename.commons.RenamingPath;
import javafx.concurrent.Task;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


public class MediaFilesSizeTask extends Task<List<Long>> {

    private final Collection<Path> mediaFiles;

    public MediaFilesSizeTask(Collection<Path> mediaFiles) {
        this.mediaFiles = mediaFiles;
    }

    @Override
    protected List<Long> call() throws Exception {
        List<Long> result = new ArrayList<>();
        for(Path path : mediaFiles){
            result.add(Files.size(path));
        }
        return result;
    }
}
