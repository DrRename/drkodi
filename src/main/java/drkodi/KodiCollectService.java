package drkodi;

import drkodi.config.AppConfig;
import drkodi.data.SearchResultToMovieMapper;
import drkodi.normalization.FolderNameCompareNormalizer;
import drkodi.normalization.MovieTitleSearchNormalizer;
import drkodi.normalization.MovieTitleWriteNormalizer;
import drkodi.themoviedb.MovieDbSearcher;
import drkodi.ui.config.KodiUiConfig;
import drkodi.ui.control.KodiBox;
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


    private final MovieDbSearcher movieDbSearcher;

    private WarningsConfig warningsConfig;

    private final FolderNameCompareNormalizer folderNameCompareNormalizer;

    private final MovieTitleWriteNormalizer movieTitleWriteNormalizer;

    private final MovieTitleSearchNormalizer normalizer;

    private Observable[] extractor;

    public KodiCollectService(AppConfig appConfig, ResourceBundle resourceBundle, Entries directory, SearchResultToMovieMapper searchResultToMovieMapper, SearchResultDtoMapper mapper, KodiUiConfig kodiUiConfig, MovieDbSearcher movieDbSearcher, FolderNameCompareNormalizer folderNameCompareNormalizer, MovieTitleWriteNormalizer movieTitleWriteNormalizer, MovieTitleSearchNormalizer normalizer) {
        super(appConfig, resourceBundle);
        this.directory = directory;
        this.searchResultToMovieMapper = searchResultToMovieMapper;
        this.mapper = mapper;
        this.kodiUiConfig = kodiUiConfig;
        this.movieDbSearcher = movieDbSearcher;
        this.folderNameCompareNormalizer = folderNameCompareNormalizer;
        this.movieTitleWriteNormalizer = movieTitleWriteNormalizer;
        this.normalizer = normalizer;
    }


    @Override
    protected Task<List<KodiBox>> createTask() {
        return new KodiToolsCollectTask(getAppConfig(),getResourceBundle(),directory,getExecutor(),warningsConfig, kodiUiConfig, extractor, searchResultToMovieMapper, mapper, movieDbSearcher, folderNameCompareNormalizer, normalizer, movieTitleWriteNormalizer);
    }

}
