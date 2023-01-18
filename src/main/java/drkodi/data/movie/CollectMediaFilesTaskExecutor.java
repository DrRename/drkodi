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
public class CollectMediaFilesTaskExecutor extends TaskExecutor<List<RenamingPath>> {

    public CollectMediaFilesTaskExecutor(Movie movie, Executor executor, ObservableList<Task<?>> runningTasksList, int maxDepth) {
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
