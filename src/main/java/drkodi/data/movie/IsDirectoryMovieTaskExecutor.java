package drkodi.data.movie;

import drkodi.data.KodiWarning;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Executor;

@Slf4j
public class IsDirectoryMovieTaskExecutor extends MovieTaskExecutor<Boolean> {
    public IsDirectoryMovieTaskExecutor(Movie movie, Executor executor, ObservableList<Task<?>> runningTasksList) {
        super(movie, new IsDirectoryTask(movie), executor, runningTasksList);
    }

    @Override
    protected void initTask() {
        super.initTask();
        task.setOnSucceeded(event -> {
                    if (!task.getValue()) {
                        log.debug("{} is not a directory", movie.getRenamingPath().getOldPath());
                        movie.getWarnings().add(new KodiWarning(KodiWarning.Type.NOT_A_DIRECTORY));
                    }
                }
        );
    }
}
