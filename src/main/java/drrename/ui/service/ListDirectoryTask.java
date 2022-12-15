package drrename.ui.service;

import drrename.PrototypeTask;
import drrename.Entries;
import drrename.RenamingControl;
import drrename.Tasks;
import drrename.commons.RenamingPath;
import drrename.config.AppConfig;
import javafx.application.Platform;
import javafx.concurrent.Task;
import lombok.extern.slf4j.Slf4j;

import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ResourceBundle;

/**
 * A {@link Task} that will iterate over all children of given {@link Path} and create a new instance of
 * {@link RenamingControl} from every child. Every {@code RenamingControl} is immediately added to given instance of
 * {@code Entries}.
 */
@Slf4j
public class ListDirectoryTask extends PrototypeTask<Void> {

    private final Path dir;

    private final Entries entries;

    public ListDirectoryTask(AppConfig config, ResourceBundle resourceBundle, Path dir, Entries entries) {
        super(config, resourceBundle);
        this.dir = dir;
        this.entries = entries;
    }

    @Override
    protected Void call() throws Exception {
        checkState();
        log.debug("Starting");
        updateMessage(String.format(getResourceBundle().getString(LoadPathsServicePrototype.LOADING_FILES)));
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
            entries.getEntries().add(element);
        });
    }

    private void checkState() {
        if (dir == null || !Files.isDirectory(dir))
            throw new IllegalArgumentException(dir + " not a directory");
    }
}

