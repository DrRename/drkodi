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

import drrename.config.AppConfig;
import drrename.kodi.data.Movie;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import lombok.extern.slf4j.Slf4j;

import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.Executor;

@Slf4j
public class KodiSuggestionsTask extends KodiTask {

    private final Executor executor;

    private final List<KodiTask> subTasks;

    private final MovieDbQuerier2 querier2;

    public KodiSuggestionsTask(AppConfig appConfig, ResourceBundle resourceBundle, List<? extends Movie> elements, Executor executor, MovieDbQuerier2 querier2) {
        super(appConfig, resourceBundle, elements, -1);
        this.executor = executor;
        this.querier2 = querier2;
        this.subTasks = new ArrayList<>();
        setOnCancelled(this::mainTaskCancelled);
    }

    public KodiSuggestionsTask(AppConfig appConfig, ResourceBundle resourceBundle, Movie element, Executor executor, MovieDbQuerier2 querier2) {
        super(appConfig, resourceBundle, element, -1);
        this.executor = executor;
        this.querier2 = querier2;
        this.subTasks = new ArrayList<>();
        setOnCancelled(this::mainTaskCancelled);
        setOnSucceeded(this::mainTaskSucceeded);
    }

    @Override
    protected void handleElement(Movie element) throws Exception {

//        element.initIdListener();

        if(Files.isDirectory(element.getRenamingPath().getOldPath())) {
//            new LoadNfoTask(getAppConfig(), getResourceBundle(), element, -1).call();
//            new LoadMovieDbDataTask(getAppConfig(), getResourceBundle(), element, -1, querier2).call();
        }




//        int index = 0;
//        executor.execute(initSubTask(new LoadMovieDbDataTask(getAppConfig(), getResourceBundle(), element, index++, querier2)));
//        executor.execute(initSubTask(new NfoPresentTask(getAppConfig(), getResourceBundle(), element, index++)));
//        executor.execute(initSubTask(new NfoTitleTask(getAppConfig(), getResourceBundle(), element, index++)));
//        executor.execute(initSubTask(new NfoYearTask(getAppConfig(), getResourceBundle(), element, index++)));

    }

    private void mainTaskCancelled(WorkerStateEvent workerStateEvent) {
        subTasks.forEach(Task::cancel);
        subTasks.clear();
    }

    private void mainTaskSucceeded(WorkerStateEvent workerStateEvent) {
        subTasks.clear();
    }

    private Runnable initSubTask(KodiTask task) {
        task.setOnFailed(this::subTaskFailed);
        task.setOnSucceeded(this::subTaskSucceeded);
        subTasks.add(task);
        return task;
    }

    private void subTaskSucceeded(WorkerStateEvent workerStateEvent) {
//        log.debug("{} finished", workerStateEvent.getSource());
    }

    private void subTaskFailed(WorkerStateEvent workerStateEvent) {
        log.error("{} failed with cause {}", workerStateEvent.getSource(), workerStateEvent.getSource().getException());
    }
}
