package drrename.kodi;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.nio.file.Path;

@Slf4j
@Getter
@Setter
@org.springframework.stereotype.Service
public class MovieDirectoryCollectorService extends Service<Void> {

    private Path directory;

    private KodiRootTreeItem rootTreeItem;

    private MovieDbClientFactory movieDbClientFactory;

    @Override
    protected Task<Void> createTask() {
        return new MovieDirectoryCollectorTask(directory, rootTreeItem, getExecutor(), movieDbClientFactory);
    }

}
