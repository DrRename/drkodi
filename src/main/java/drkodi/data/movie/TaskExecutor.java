package drkodi.data.movie;

import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.concurrent.Worker;
import javafx.concurrent.WorkerStateEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Executor;

@Slf4j
@RequiredArgsConstructor
public class TaskExecutor<T> {

    protected final Movie movie;

    protected final Task<T> task;

    protected final Executor executor;

    protected final ObservableList<Task<?>> runningTasksList;

    protected void initTaskCounter(){
        runningTasksList.add(task);
        task.stateProperty().addListener((obs, oldState, newState) -> {
            if (newState == Worker.State.SUCCEEDED || newState == Worker.State.FAILED || newState == Worker.State.CANCELLED) {
                runningTasksList.remove(task);
            }
        });
    }

    protected void initTask(){
        task.setOnFailed(this::logTaskFailed);
    }

    protected void logTaskFailed(WorkerStateEvent event) {
        log.error("Task {} failed: ", event.getSource(), event.getSource().getException());
    }

    public void execute() {

        initTaskCounter();

        initTask();

        if (executor == null) {
            log.debug("No executor set, executing on current thread");
            task.run();
        } else
            executor.execute(task);
    }


}
