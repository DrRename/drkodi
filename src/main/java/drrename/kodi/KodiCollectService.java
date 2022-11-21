package drrename.kodi;

import drrename.kodi.ui.FilterableKodiRootTreeItem;
import javafx.beans.Observable;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.Executor;

@RequiredArgsConstructor
@Slf4j
@Setter
@org.springframework.stereotype.Service
public class KodiCollectService extends Service<List<MovieTreeItemFilterable>> {

    private Path directory;

    private FilterableKodiRootTreeItem rootTreeItem;

    private final MovieDbClientFactory movieDbClientFactory;

    private final Executor executor;

    private WarningsConfig warningsConfig;

    private Observable[] extractor;

    @Override
    protected Task<List<MovieTreeItemFilterable>> createTask() {
        return new KodiToolsCollectTask(directory, rootTreeItem, executor, movieDbClientFactory, warningsConfig, extractor);
    }

}
