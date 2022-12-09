package drrename.kodi;

import drrename.DrRenameService;
import drrename.Entries;
import drrename.SearchResultMapper;
import drrename.config.AppConfig;
import drrename.kodi.data.StaticMovieData;
import drrename.kodi.ui.FilterableKodiRootTreeItem;
import javafx.beans.Observable;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ResourceBundle;

@Slf4j
@Setter
@Component
public class KodiCollectService extends DrRenameService<ObservableList<StaticMovieData>> {

    static final String MESSAGE = "kodi.collect";

    private final Entries directory;

    private final SearchResultMapper mapper;

    private FilterableKodiRootTreeItem rootTreeItem;

//    private final MovieDbClientFactory movieDbClientFactory;

    private final MovieDbQuerier2 movieDbQuerier2;

    private WarningsConfig warningsConfig;

    private Observable[] extractor;

    public KodiCollectService(AppConfig appConfig, ResourceBundle resourceBundle, Entries directory, SearchResultMapper mapper/*, MovieDbClientFactory movieDbClientFactory*/, MovieDbQuerier2 movieDbQuerier2) {
        super(appConfig, resourceBundle);
        this.directory = directory;
        this.mapper = mapper;
//        this.movieDbClientFactory = movieDbClientFactory;
        this.movieDbQuerier2 = movieDbQuerier2;
    }


    @Override
    protected Task<ObservableList<StaticMovieData>> createTask() {
        return new KodiToolsCollectTask(getAppConfig(),getResourceBundle(),directory,rootTreeItem,getExecutor()/*,movieDbClientFactory*/,warningsConfig,extractor, mapper, movieDbQuerier2);
    }

}
