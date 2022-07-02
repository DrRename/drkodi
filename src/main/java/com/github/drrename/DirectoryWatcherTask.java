package com.github.drrename;

import javafx.concurrent.Task;
import lombok.extern.slf4j.Slf4j;

import java.nio.file.*;

import static java.nio.file.StandardWatchEventKinds.*;

@Slf4j
public abstract class DirectoryWatcherTask extends Task<Void> {

    private final Path path;

    public DirectoryWatcherTask(Path path) {
        this.path = path;
    }

    public void watchFile() throws Exception {
        // We obtain the file system of the Path
        // FileSystem fileSystem = folderPath.getFileSystem();
        // TODO: use the actual file system instead of default
        FileSystem fileSystem = FileSystems.getDefault();
        // We create the new WatchService using the try-with-resources block
        try (WatchService service = fileSystem.newWatchService()) {
            log.debug("Watching filesystem {}", fileSystem);
            WatchKey key = path.register(service, ENTRY_MODIFY, ENTRY_CREATE, ENTRY_DELETE);
            // Start the infinite polling loop
            while (true) {
                // Wait for the next event
                WatchKey watchKey = service.take();
                for (WatchEvent<?> watchEvent : watchKey.pollEvents()) {
                    // Get the type of the event
                    WatchEvent.Kind<?> kind = watchEvent.kind();
                    Path watchEventPath = (Path) watchEvent.context();
                    onModified(kind, watchEventPath);
                    if (!watchKey.reset()) {
                        // Exit if no longer valid
                        log.debug("Watch key {} was reset", watchKey);
                        break;
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

    public abstract void onModified(WatchEvent.Kind<?> oldFile, Path newFile);
}
