package drrename.kodi;

import drrename.ServicePrototype;
import drrename.Entries;
import drrename.SearchResultDtoMapper;
import drrename.config.AppConfig;
import drrename.kodi.data.FolderNameCompareNormalizer;
import drrename.kodi.data.MovieTitleSearchNormalizer;
import drrename.kodi.data.SearchResultToMovieMapper;
import drrename.kodi.ui.config.KodiUiConfig;
import drrename.kodi.ui.control.KodiBox;
import javafx.beans.Observable;
import javafx.concurrent.Task;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.ResourceBundle;

@Slf4j
@Setter
@Component
public class KodiCollectService extends ServicePrototype<List<KodiBox>> {

    static final String MESSAGE = "kodi.collect";

    private final Entries directory;

    private final SearchResultToMovieMapper searchResultToMovieMapper;

    private final SearchResultDtoMapper mapper;

    private final KodiUiConfig kodiUiConfig;


    private final MovieDbQuerier2 movieDbQuerier2;

    private WarningsConfig warningsConfig;

    private final FolderNameCompareNormalizer folderNameCompareNormalizer;

    private final MovieTitleSearchNormalizer normalizer;

    private Observable[] extractor;

    public KodiCollectService(AppConfig appConfig, ResourceBundle resourceBundle, Entries directory, SearchResultToMovieMapper searchResultToMovieMapper, SearchResultDtoMapper mapper, KodiUiConfig kodiUiConfig, MovieDbQuerier2 movieDbQuerier2, FolderNameCompareNormalizer folderNameCompareNormalizer, MovieTitleSearchNormalizer normalizer) {
        super(appConfig, resourceBundle);
        this.directory = directory;
        this.searchResultToMovieMapper = searchResultToMovieMapper;
        this.mapper = mapper;
        this.kodiUiConfig = kodiUiConfig;
        this.movieDbQuerier2 = movieDbQuerier2;
        this.folderNameCompareNormalizer = folderNameCompareNormalizer;
        this.normalizer = normalizer;
    }


    @Override
    protected Task<List<KodiBox>> createTask() {
        return new KodiToolsCollectTask(getAppConfig(),getResourceBundle(),directory,getExecutor(),warningsConfig, kodiUiConfig, extractor, searchResultToMovieMapper, mapper, movieDbQuerier2, folderNameCompareNormalizer, normalizer);
    }

}
