package drrename.kodi;

import drrename.DrRenameService;
import drrename.Entries;
import drrename.config.AppConfig;
import drrename.kodi.ui.FilterableKodiRootTreeItem;
import javafx.beans.Observable;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ResourceBundle;
import java.util.concurrent.Executor;

@Slf4j
@Setter
@Component
public class KodiCollectService extends DrRenameService<ObservableList<MovieTreeItemFilterable>> {

    static final String MESSAGE = "kodi.collect";

    private final Entries directory;

    private FilterableKodiRootTreeItem rootTreeItem;

    private final MovieDbClientFactory movieDbClientFactory;

    private WarningsConfig warningsConfig;

    private Observable[] extractor;

    public KodiCollectService(AppConfig appConfig, ResourceBundle resourceBundle, Entries directory, MovieDbClientFactory movieDbClientFactory) {
        super(appConfig, resourceBundle);
        this.directory = directory;
        this.movieDbClientFactory = movieDbClientFactory;
    }


    @Override
    protected Task<ObservableList<MovieTreeItemFilterable>> createTask() {
        return new KodiToolsCollectTask(getAppConfig(),getResourceBundle(),directory,rootTreeItem,getExecutor(),movieDbClientFactory,warningsConfig,extractor);
    }

}
