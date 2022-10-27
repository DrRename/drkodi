package drrename.kodi;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.nio.file.Path;
import java.util.List;

@Slf4j
@Getter
@Setter
@org.springframework.stereotype.Service
public class MovieDirectoryCollectorService extends Service<List<Path>> {

    private Path directory;

    @Override
    protected Task<List<Path>> createTask() {
        return new MovieDirectoryCollectorTask().setDirectory(directory);
    }

}
