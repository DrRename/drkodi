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
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import lombok.extern.slf4j.Slf4j;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

@Slf4j
public class CollectMediaFilesMovieTaskExecutor extends MovieTaskExecutor<List<RenamingPath>> {

    public CollectMediaFilesMovieTaskExecutor(Movie movie, Executor executor, ObservableList<Task<?>> runningTasksList, int maxDepth) {
        super(movie, new RecursiveMediaFilesCollectTask(movie, maxDepth), executor, runningTasksList);
    }

    @Override
    protected void initTask() {
        super.initTask();
        task.setOnSucceeded(event -> {
                    log.debug("Found {} media files:\n{}", task.getValue().size(), task.getValue().stream().map(RenamingPath::getOldPath).map(Path::toString).collect(Collectors.joining("\n")));
                    movie.getMediaFiles().addAll(task.getValue());
                }
        );
    }

    @Override
    public void execute() {

        if (Files.isDirectory(movie.getRenamingPath().getOldPath())) {
            super.execute();
        } else {
            // ignore for file
        }
    }
}
