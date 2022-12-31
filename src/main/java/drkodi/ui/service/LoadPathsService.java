package drkodi.ui.service;

import drkodi.RenamingPathEntries;
import drkodi.ServicePrototype;
import drkodi.config.AppConfig;
import javafx.concurrent.Task;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.ResourceBundle;

/**
 * The 'initial' service. It loads a given set of {@link Path}, converts all child items to instances of
 * {@link drrename.commons.RenamingPath} and adds those to given instance of {@link RenamingPathEntries}.
 */
@Setter
@Component
public class LoadPathsService extends ServicePrototype<Void> {

    static final String LOADING_FILES = "mainview.status.loading_files";

    private Collection<Path> files;

    private final RenamingPathEntries renamingPathEntries;

    public LoadPathsService(AppConfig appConfig, ResourceBundle resourceBundle, RenamingPathEntries renamingPathEntries) {
        super(appConfig, resourceBundle);
        this.renamingPathEntries = renamingPathEntries;
    }


    @Override
    protected Task<Void> createTask() {
        // If 'files' is one entry only, and it's a directory, use ListDirectoryTask, otherwise use ListFilesTask.
        if (files != null && files.size() == 1 && Files.isDirectory(files.iterator().next())) {
            return new ListDirectoryTask(getAppConfig(), getResourceBundle(),files.iterator().next(), renamingPathEntries);
        }
        return new ListFilesTask(getAppConfig(), getResourceBundle(),files, renamingPathEntries);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " file cnt: " + (files == null ? 0 : files.size());
    }


}

