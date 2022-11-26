package drrename.ui.service;

import drrename.DrRenameTask;
import drrename.Entries;
import drrename.RenamingControl;
import drrename.Tasks;
import drrename.config.AppConfig;
import javafx.application.Platform;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.nio.file.Path;
import java.util.Collection;
import java.util.ResourceBundle;

@Slf4j
public class ListFilesTask extends DrRenameTask<Void> {

    private final Collection<Path> files;

    private final Entries entries;

    public ListFilesTask(AppConfig config, ResourceBundle resourceBundle, Collection<Path> files, Entries entries) {
        super(config, resourceBundle);
        this.files = files;
        this.entries = entries;
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
            handleNewEntry(++cnt, new RenamingControl(f));
            if (getConfig().isDebug()) {
                Thread.sleep(getConfig().getLoopDelayMs());
            }
        }
        log.debug("Finished");
        updateMessage(null);
        return null;
    }

    private void handleNewEntry(int progress, RenamingControl renamingControl) {
        Platform.runLater(() -> entries.getEntries().add(renamingControl));
        updateProgress(progress, files.size());
    }
}

