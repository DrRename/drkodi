package com.kerner1000.drrename;

import org.springframework.context.ApplicationEvent;

import java.nio.file.Path;
import java.nio.file.WatchEvent;

class FileModifiedEvent extends ApplicationEvent {

    static class Entry {
        private final WatchEvent.Kind<?> oldFile;
        private final Path newFile;

        Entry(WatchEvent.Kind<?> oldFile, Path newFile) {
            this.oldFile = oldFile;
            this.newFile = newFile;
        }

        @Override
        public String toString() {
            return "Entry{" +
                    "oldFile=" + oldFile +
                    ", newFile=" + newFile +
                    '}';
        }

        public WatchEvent.Kind<?> getOldFile() {
            return oldFile;
        }

        public Path getNewFile() {
            return newFile;
        }
    }

    public FileModifiedEvent(WatchEvent.Kind<?> oldFile, Path newFile) {
        super(new Entry(oldFile, newFile));
    }

    public Entry getFiles() {
        return ((Entry) getSource());
    }
}
