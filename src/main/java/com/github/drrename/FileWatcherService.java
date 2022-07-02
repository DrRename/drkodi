package com.github.drrename;

import javafx.concurrent.Service;
import javafx.concurrent.Task;

import java.nio.file.Path;
import java.util.ArrayList;

public abstract class FileWatcherService extends Service<Void> {

    private Iterable<Path> files;

    public FileWatcherService(Iterable<Path> files) {
        this.files = files;
    }

    public FileWatcherService() {
        this(new ArrayList<>());
    }

    @Override
    protected Task<Void> createTask() {
        return new FileWatcherTask(files) {
            @Override
            public void onModified(Path oldFile, Path newFile) {
                FileWatcherService.this.onModified(oldFile, newFile);
            }
        };
    }

    abstract void onModified(Path oldFile, Path newFile);

    public Iterable<Path> getFiles() {
        return files;
    }

    public void setFiles(Iterable<Path> files) {
        this.files = files;
    }
}
