package com.github.drrename;

import javafx.concurrent.Service;
import javafx.concurrent.Task;

import java.nio.file.Path;
import java.nio.file.WatchEvent;

public abstract class DirectoryWatcherService extends Service<Void> {

    private Path dir;

    public DirectoryWatcherService(Path dir) {
        this.dir = dir;
    }

    public DirectoryWatcherService() {

    }

    @Override
    protected Task<Void> createTask() {
        return new DirectoryWatcherTask(dir) {
            @Override
            public void onModified(WatchEvent.Kind<?> oldFile, Path newFile) {
                DirectoryWatcherService.this.onModified(oldFile, newFile);
            }
        };
    }

    abstract void onModified(WatchEvent.Kind<?> oldFile, Path newFile);

    public Path getDir() {
        return dir;
    }

    public void setDir(Path dir) {
        this.dir = dir;
    }
}
