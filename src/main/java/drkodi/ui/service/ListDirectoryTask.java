package drkodi.ui.service;

import drkodi.RenamingPathEntries;
import drkodi.PrototypeTask;
import drkodi.Tasks;
import drkodi.config.AppConfig;
import drrename.commons.RenamingPath;
import javafx.application.Platform;
import javafx.concurrent.Task;
import lombok.extern.slf4j.Slf4j;

import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ResourceBundle;

/**
 * A {@link Task} that will iterate over all children of given {@link Path} and create a new instance of
 * {@link RenamingPath} from every child. Every {@code RenamingPath} is immediately added to given instance of
 * {@code Entries}.
 */
@Slf4j
public class ListDirectoryTask extends PrototypeTask<Void> {

    private final Path dir;

    private final RenamingPathEntries renamingPathEntries;

    public ListDirectoryTask(AppConfig config, ResourceBundle resourceBundle, Path dir, RenamingPathEntries renamingPathEntries) {
        super(config, resourceBundle);
        this.dir = dir;
        this.renamingPathEntries = renamingPathEntries;
    }

    @Override
    protected Void call() throws Exception {
        checkState();
        log.debug("Starting");
        updateMessage(String.format(getResourceBundle().getString(LoadPathsService.LOADING_FILES)));
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir)) {
            for (Path path : stream) {
                if (isCancelled()) {
                    log.debug("Cancelled");
                    updateMessage(String.format(getResourceBundle().getString(Tasks.MESSAGE_CANCELLED)));
                    break;
                }
                var entry = new RenamingPath(path);
                handleNewEntry(entry);
                if (getAppConfig().isDebug()) {
                    try {
                        Thread.sleep(getAppConfig().getLoopDelayMs());
                    } catch (InterruptedException e) {
                        if (isCancelled()) {
                            log.debug("Cancelled");
                            updateMessage(String.format(getResourceBundle().getString(Tasks.MESSAGE_CANCELLED)));
                            break;
                        }
                    }
                }
            }
        }
        log.debug("Finished");
        updateMessage(null);
        return null;
    }

    protected void handleNewEntry(RenamingPath element) {
        Platform.runLater(() -> {
            renamingPathEntries.getEntries().add(element);
        });
    }

    private void checkState() {
        if (dir == null || !Files.isDirectory(dir))
            throw new IllegalArgumentException(dir + " not a directory");
    }
}

