package com.github.drrename;

import javafx.concurrent.Task;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.nio.file.*;
import java.util.*;

import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;

@Slf4j
public abstract class FileWatcherTask extends Task<Void> {

    static class Entry {
        private final Path watchFile;

        Entry(Path watchFile) {
            this.watchFile = watchFile;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Entry entry = (Entry) o;
            return Objects.equals(watchFile, entry.watchFile);
        }

        @Override
        public int hashCode() {
            return Objects.hash(watchFile);
        }
    }

    private final List<Entry> entryList;

    private final Map<WatchKey, Entry> watchKeyEntryMap;

    public FileWatcherTask(Iterable<Path> watchFiles) {
        this.entryList = new ArrayList<>();
        this.watchKeyEntryMap = new LinkedHashMap<>();
        for (Path watchFile : watchFiles) {
            if (!Files.isRegularFile(watchFile)) {
                // Do not allow this to be a folder since we want to watch files
                throw new IllegalArgumentException(watchFile + " is not a regular file");
            }
            Entry entry = new Entry(watchFile);
            entryList.add(entry);
            log.debug("Watcher initialized for {} entries. ({})", entryList.size(), entryList.stream().map(e -> e.watchFile).findFirst().orElse(null));
        }
    }

    public FileWatcherTask(Path... watchFiles) {
        this(Arrays.asList(watchFiles));
    }

    public void watchFile() throws Exception {
        // We obtain the file system of the Path
        // FileSystem fileSystem = folderPath.getFileSystem();
        // TODO: use the actual file system instead of default
        FileSystem fileSystem = FileSystems.getDefault();

        // We create the new WatchService using the try-with-resources block
        try (WatchService service = fileSystem.newWatchService()) {
            log.debug("Watching filesystem {}", fileSystem);
            for (Entry e : entryList) {
                // We watch for modification events
                WatchKey key = e.watchFile.getParent().register(service, ENTRY_MODIFY);
                watchKeyEntryMap.put(key, e);
            }

            // Start the infinite polling loop
            while (true) {
                // Wait for the next event
                WatchKey watchKey = service.take();
                for (Entry e : entryList) {
                    // Call this if the right file is involved
                    if (watchKeyEntryMap.get(watchKey) != null) {
                        for (WatchEvent<?> watchEvent : watchKey.pollEvents()) {
                            // Get the type of the event
                            WatchEvent.Kind<?> kind = watchEvent.kind();

                            if (kind == ENTRY_MODIFY) {
                                Path watchEventPath = (Path) watchEvent.context();
                                onModified(e.watchFile, watchEventPath);
                            }
                            if (!watchKey.reset()) {
                                // Exit if no longer valid
                                log.debug("Watch key {} was reset", watchKey);
                                break;
                            }
                        }
                    } else {
                        log.debug("Ignoring file change {}", watchKey);
                    }
                }
            }
        }
    }

    @Override
    protected Void call() throws Exception {
        watchFile();
        return null;
    }

    public abstract void onModified(Path oldFile, Path newFile);
}
