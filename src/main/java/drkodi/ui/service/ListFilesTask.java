package drkodi.ui.service;

import drkodi.RenamingPathEntries;
import drkodi.PrototypeTask;
import drkodi.Tasks;
import drkodi.config.AppConfig;
import drrename.commons.RenamingPath;
import javafx.application.Platform;
import lombok.extern.slf4j.Slf4j;

import java.nio.file.Path;
import java.util.Collection;
import java.util.ResourceBundle;

/**
 * Iterates over a given collection of {@code Path}, transforms all entries into {@code RenamingPath} and stores them
 * into given instance of {@code Entries}.
 */
@Slf4j
public class ListFilesTask extends PrototypeTask<Void> {

    private final Collection<Path> files;

    private final RenamingPathEntries renamingPathEntries;

    public ListFilesTask(AppConfig config, ResourceBundle resourceBundle, Collection<Path> files, RenamingPathEntries renamingPathEntries) {
        super(config, resourceBundle);
        this.files = files;
        this.renamingPathEntries = renamingPathEntries;
    }

    @Override
    protected Void call() throws InterruptedException {
        log.debug("Starting");
        updateMessage(String.format(getResourceBundle().getString(LoadPathsService.LOADING_FILES)));
        int cnt = 0;
        for (final Path f : files) {
            if (isCancelled()) {
                log.debug("Cancelled");
                updateMessage(String.format(getResourceBundle().getString(Tasks.MESSAGE_CANCELLED)));
                break;
            }
            handleNewEntry(++cnt, new RenamingPath(f));
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
        log.debug("Finished");
        updateMessage(null);
        return null;
    }

    private void handleNewEntry(int progress, RenamingPath renamingControl) {
        Platform.runLater(() -> renamingPathEntries.getEntries().add(renamingControl));
        updateProgress(progress, files.size());
    }
}

