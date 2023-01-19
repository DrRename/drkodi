package drkodi.data.movie;

import drkodi.data.LoadNfoPathTask;
import drkodi.data.QualifiedPath;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import lombok.extern.slf4j.Slf4j;

import java.nio.file.Path;
import java.util.concurrent.Executor;

@Slf4j
public class LoadNfoMovieTaskExecutor extends MovieTaskExecutor<Path> {

    public LoadNfoMovieTaskExecutor(Movie movie, Executor executor, ObservableList<Task<?>> runningTasksList) {
        super(movie, new LoadNfoPathTask(movie.getRenamingPath().getOldPath()), executor, runningTasksList);
    }

    @Override
    protected void initTask() {
        super.initTask();
        task.setOnSucceeded(event -> movie.setNfoPath(QualifiedPath.from(task.getValue())));
    }
}
