package drkodi;

import drkodi.config.AppConfig;
import drkodi.data.SearchResultToMovieMapper;
import drkodi.normalization.FolderNameWarningNormalizer;
import drkodi.normalization.MovieTitleSearchNormalizer;
import drkodi.normalization.MovieTitleWriteNormalizer;
import drkodi.themoviedb.MovieDbSearcher;
import drkodi.ui.config.KodiUiConfig;
import drkodi.ui.control.KodiMoviePathEntryBox;
import javafx.beans.Observable;
import javafx.concurrent.Task;
import javafx.scene.control.ListView;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ResourceBundle;

@Slf4j
@Setter
@Component
public class KodiCollectService extends ServicePrototype<Void> {

    static final String MESSAGE = "kodi.collect";

    private final Entries directory;

    private final SearchResultToMovieMapper searchResultToMovieMapper;

    private final SearchResultDtoMapper mapper;

    private final KodiUiConfig kodiUiConfig;


    private final MovieDbSearcher movieDbSearcher;

    private WarningsConfig warningsConfig;

    private final FolderNameWarningNormalizer folderNameWarningNormalizer;

    private final MovieTitleWriteNormalizer movieTitleWriteNormalizer;

    private final MovieTitleSearchNormalizer normalizer;

    private Observable[] extractor;

    private ListView<KodiMoviePathEntryBox> listView;

    public KodiCollectService(AppConfig appConfig, ResourceBundle resourceBundle, Entries directory, SearchResultToMovieMapper searchResultToMovieMapper, SearchResultDtoMapper mapper, KodiUiConfig kodiUiConfig, MovieDbSearcher movieDbSearcher, FolderNameWarningNormalizer folderNameWarningNormalizer, MovieTitleWriteNormalizer movieTitleWriteNormalizer, MovieTitleSearchNormalizer normalizer) {
        super(appConfig, resourceBundle);
        this.directory = directory;
        this.searchResultToMovieMapper = searchResultToMovieMapper;
        this.mapper = mapper;
        this.kodiUiConfig = kodiUiConfig;
        this.movieDbSearcher = movieDbSearcher;
        this.folderNameWarningNormalizer = folderNameWarningNormalizer;
        this.movieTitleWriteNormalizer = movieTitleWriteNormalizer;
        this.normalizer = normalizer;
    }


    @Override
    protected Task<Void> createTask() {
        return new KodiToolsCollectTask(getAppConfig(),getResourceBundle(),directory,getExecutor(),warningsConfig, kodiUiConfig, extractor, searchResultToMovieMapper, mapper, movieDbSearcher, folderNameWarningNormalizer, normalizer, movieTitleWriteNormalizer, listView);
    }

}
