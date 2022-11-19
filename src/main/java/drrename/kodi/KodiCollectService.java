package drrename.kodi;

import drrename.ui.kodi.FilterableKodiRootTreeItem;
import javafx.beans.Observable;
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
public class KodiCollectService extends Service<Void> {

    private Path directory;

    private FilterableKodiRootTreeItem rootTreeItem;

    private MovieDbClientFactory movieDbClientFactory;

    private WarningsConfig warningsConfig;

    private Observable[] extractor;

    @Override
    protected Task<Void> createTask() {
        return new MovieDirectoryIssuesTask(directory, rootTreeItem, getExecutor(), movieDbClientFactory, warningsConfig, extractor);
    }

}
