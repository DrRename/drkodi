package drrename.ui.service;

import drrename.RenamingControl;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

@Slf4j
public class ListDirectoryTask extends Task<ObservableList<RenamingControl>> {

    private final Path dir;

    public ListDirectoryTask(final Path dir) {
        this.dir = Objects.requireNonNull(dir);
        if (!Files.isDirectory(dir))
            throw new IllegalArgumentException(dir + " not a directory");
    }

    @Override
    protected ObservableList<RenamingControl> call() throws IOException {
        ObservableList<RenamingControl> result = FXCollections.observableArrayList();

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir)) {
            for (Path path : stream) {
                if (isCancelled()) {
                    break;
                }
                RenamingControl newEntry = new RenamingControl(path);
                result.add(newEntry);
                Platform.runLater(new Runnable() {
                    @Override public void run() {
                        updateValue(result);
                    }
                });

            }
        }
        return result;
    }
}

