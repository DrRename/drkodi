package drrename.ui.service;

import drrename.DrRenameTask;
import drrename.Entries;
import drrename.RenamingControl;
import drrename.Tasks;
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
public class ListDirectoryTask extends DrRenameTask<Void> {

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
        updateMessage(String.format(getResourceBundle().getString(LoadPathsService.LOADING_FILES)));
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir)) {
            for (Path path : stream) {
                if (isCancelled()) {
                    log.debug("Cancelled");
                    updateMessage(String.format(getResourceBundle().getString(Tasks.MESSAGE_CANCELLED)));
                    break;
                }
                var entry = new RenamingControl(path);
                handleNewEntry(entry);
                if (getConfig().isDebug()) {
                    Thread.sleep(getConfig().getLoopDelayMs());
                }
            }
        }
        log.debug("Finished");
        updateMessage(null);
        return null;
    }

    protected void handleNewEntry(RenamingControl renamingControl) {
        Platform.runLater(() -> {
            entries.getEntries().add(renamingControl);
        });
    }

    private void checkState() {
        if (dir == null || !Files.isDirectory(dir))
            throw new IllegalArgumentException(dir + " not a directory");
    }
}

