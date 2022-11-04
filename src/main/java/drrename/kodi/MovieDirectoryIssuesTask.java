package drrename.kodi;

import drrename.kodi.nfo.MovieDbLookupTreeItemValue;
import drrename.kodi.nfo.NfoFileNameTreeItemValue;
import drrename.model.RenamingPath;
import javafx.application.Platform;
import javafx.concurrent.Task;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.stream.Stream;

@RequiredArgsConstructor
@Getter
@Slf4j
class MovieDirectoryIssuesTask extends Task<Void> {
    private final Path directory;

    private final KodiRootTreeItem rootTreeItem;

    private final Executor executor;

    private final MovieDbClientFactory movieDbClientFactory;

    private void verifyState() {
        Objects.requireNonNull(directory);
        if (!Files.isDirectory(directory)) {
            throw new IllegalArgumentException(directory.getFileName() + " is not a directory");
        }
        Objects.requireNonNull(rootTreeItem);
        Objects.requireNonNull(executor);
    }

    @Override
    protected Void call() throws Exception {
        verifyState();
        try (DirectoryStream<Path> ds = Files.newDirectoryStream(directory)) {
            for (Path path : ds) {
                if (Thread.interrupted()) {
                    log.info("Cancelling");
                    break;
                }
                if (Files.isDirectory(path)) {
                    if (path.getFileName().toString().startsWith("@")) {
                        log.debug("Ignoring {}", path);
                        continue;
                    }

                    var renamingPath = new RenamingPath(path);
                    var item = new MovieTreeItem(new MovieTreeItemValue(renamingPath, executor));
                    List<KodiTreeItem> childItems = buildChildItems(renamingPath);
                    childItems.forEach(childItem -> Platform.runLater(() -> item.getSourceChildren().add(childItem)));
                    Platform.runLater(() -> getRootTreeItem().getSourceChildren().add(item));
                }
            }
        }
        return null;
    }

    List<KodiTreeItem> buildChildItems(RenamingPath renamingPath){
        List<KodiTreeItem> result = new ArrayList<>();
        result.add(new KodiTreeItem(new MovieDbLookupTreeItemValue(renamingPath, executor, movieDbClientFactory)));
        result.add(new KodiTreeItem(new NfoFileNameTreeItemValue(renamingPath, executor)));
        result.add(new KodiTreeItem(new MediaFileNameTreeItemValue(renamingPath, executor)));
        result.add(new KodiTreeItem(new NfoFileContentMovieNameTreeItemValue(renamingPath, executor)));
        return result;
    }

    private void executeTask(MovieTreeItem item, Task<KodiTreeItem> task) {
        task.setOnFailed(event -> log.error(task + " failed", task.getException()));
        task.setOnCancelled(event -> log.debug("{} cancelled", task));
        task.setOnSucceeded(event -> item.getSourceChildren().add(task.getValue()));
        executor.execute(task);
    }
}
