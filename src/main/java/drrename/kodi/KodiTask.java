package drrename.kodi;

import javafx.concurrent.Task;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@Slf4j
class KodiTask extends Task<List<Path>> {
    private Path directory;

    public KodiTask(Path directory) {
        this.directory = directory;
    }

    public static boolean isEmpty(Path path) throws IOException {
        if (Files.isDirectory(path)) {
            try (DirectoryStream<Path> directory = Files.newDirectoryStream(path)) {
                return !directory.iterator().hasNext();
            }
        }
        return false;
    }

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
                    String movieName = path.getFileName().toString();
                    if (movieName.startsWith("@")) {
                        log.info("Ignoring {}", path);
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

    public void setDirectory(Path directory) {
        this.directory = directory;
    }

}
