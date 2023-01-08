package drkodi.data.movie;

import drkodi.data.KodiWarning;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import lombok.extern.slf4j.Slf4j;

import java.nio.file.Files;
import java.util.concurrent.Executor;

@Slf4j
public class MediaFilesPresentTaskExecutor extends TaskExecutor<Boolean> {
    public MediaFilesPresentTaskExecutor(Movie movie, Executor executor, ObservableList<Task<?>> runningTasksList) {
        super(movie, new MediaFilesPresentTask(movie), executor, runningTasksList);
    }

    @Override
    protected void initTask() {
        super.initTask();
        task.setOnSucceeded(event -> {
                    if (task.getValue()) {
                        log.debug("No media files found: {}", movie.getRenamingPath().getOldPath());
                        movie.getWarnings().add(new KodiWarning(KodiWarning.Type.EMPTY_FOLDER));
                    }
                }
        );
    }

    @Override
    public void execute() {

        if(Files.isDirectory(movie.getRenamingPath().getOldPath())) {
            super.execute();
        } else {
            // ignore file
        }
    }
}
