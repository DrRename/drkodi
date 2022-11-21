package drrename.ui.service;

import drrename.RenamingControl;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import lombok.extern.slf4j.Slf4j;

import java.nio.file.Path;
import java.util.Collection;
import java.util.Objects;


@Slf4j
public class ListFilesTask extends Task<ObservableList<RenamingControl>> {

    private final Collection<Path> files;

    public ListFilesTask(Collection<Path> files) {
        this.files = Objects.requireNonNull(files);
    }


    @Override
    protected ObservableList<RenamingControl> call() {
        ObservableList<RenamingControl> result = FXCollections.observableArrayList();
        for (final Path f : files) {
            if (isCancelled()) {
                break;
            }
            var newEntry = new RenamingControl(f);
            result.add(newEntry);
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    updateValue(result);
                }
            });
            updateProgress(result.size(), files.size());
        }
        return result;
    }


}

