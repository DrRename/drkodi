package drrename.ui.service;

import drrename.RenamingControl;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;

@Setter
@RequiredArgsConstructor
@org.springframework.stereotype.Service
public class LoadPathsService extends Service<ObservableList<RenamingControl>> {

    private Collection<Path> files;

    @Override
    protected Task<ObservableList<RenamingControl>> createTask(){
        // If 'files' is one entry only, and it's a directory, use ListDirectoryTask, otherwise use ListFilesTask.
        if(files != null && files.size() == 1 && Files.isDirectory(files.iterator().next())){
            return new ListDirectoryTask(files.iterator().next());
        }
        return new ListFilesTask(files);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " file cnt: " + (files == null ? 0 : files.size());
    }


}

