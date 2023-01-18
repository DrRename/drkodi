package drkodi.data.movie;

import drkodi.util.DrRenameUtil;
import drrename.commons.RenamingPath;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.scene.control.Label;
import lombok.extern.slf4j.Slf4j;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

@Slf4j
public class MediaFilesFileSizeTaskExecutor extends TaskExecutor<List<Long>> {

    private final Label fileSizeLabel;

    public MediaFilesFileSizeTaskExecutor(Movie movie, Collection<Path> mediaFiles, Executor executor, ObservableList<Task<?>> runningTasksList, Label fileSizeLabel) {
        super(movie, new MediaFilesSizeTask(mediaFiles), executor, runningTasksList);
        this.fileSizeLabel = fileSizeLabel;
    }

    @Override
    protected void initTask() {
        super.initTask();
        task.setOnSucceeded(event -> {
            fileSizeLabel.setText(DrRenameUtil.formatSize(task.getValue().stream().mapToLong(e -> e).sum()));
        });
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
