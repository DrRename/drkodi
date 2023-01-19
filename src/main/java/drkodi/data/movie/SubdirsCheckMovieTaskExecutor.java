package drkodi.data.movie;

import drkodi.data.KodiWarning;
import drkodi.task.SubdirsCheckTask;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Executor;

@Slf4j
public class SubdirsCheckMovieTaskExecutor extends MovieTaskExecutor<Boolean> {

    public SubdirsCheckMovieTaskExecutor(Movie movie, Executor executor, ObservableList<Task<?>> runningTasksList) {
        super(movie, new SubdirsCheckTask(movie), executor, runningTasksList);
    }

    @Override
    protected void initTask() {
        super.initTask();
        task.setOnSucceeded(event -> {
                    if (task.getValue()) {
                        log.debug("Subdirectories found in {}", movie.getRenamingPath().getOldPath());
                        movie.getWarnings().add(new KodiWarning(KodiWarning.Type.SUBDIRS));
                    }
                }
        );
    }
}
