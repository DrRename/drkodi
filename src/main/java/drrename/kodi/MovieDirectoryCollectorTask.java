package drrename.kodi;

import javafx.concurrent.Task;
import lombok.extern.slf4j.Slf4j;

import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@Slf4j
class MovieDirectoryCollectorTask extends Task<List<Path>> {
    private Path directory;

    @Override
    protected List<Path> call() throws Exception {
        List<Path> result = new ArrayList<>();
        try (DirectoryStream<Path> ds = Files.newDirectoryStream(directory)) {
            for (Path path : ds) {
                if (Thread.interrupted()) {
                    log.info("Cancelling");
                    break;
                }
                if (Files.isDirectory(path)) {
                    if (path.getFileName().toString().startsWith("@")) {
                        log.debug("Ignoring {}", path);
                        continue;
                    }
                    result.add(path);
                }
            }
        }
        return result;
    }

    public Path getDirectory() {
        return directory;
    }

    public MovieDirectoryCollectorTask setDirectory(Path directory) {
        if(!Files.isDirectory(directory)){
            throw new IllegalArgumentException( directory.getFileName() + " is not a directory");
        }
        this.directory = directory;
        return this;
    }

}
