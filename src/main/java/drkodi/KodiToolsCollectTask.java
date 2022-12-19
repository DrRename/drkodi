package drkodi;

import drkodi.config.AppConfig;
import drkodi.data.dynamic.Movie;
import drkodi.data.SearchResultToMovieMapper;
import drkodi.normalization.FolderNameCompareNormalizer;
import drkodi.normalization.MovieTitleSearchNormalizer;
import drkodi.normalization.MovieTitleWriteNormalizer;
import drkodi.themoviedb.MovieDbSearcher;
import drkodi.ui.config.KodiUiConfig;
import drkodi.ui.control.KodiBox;
import drrename.commons.RenamingPath;
import javafx.beans.Observable;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.Executor;

@Getter
@Slf4j
class KodiToolsCollectTask extends PrototypeTask<List<KodiBox>> {

    private final Entries entries;

    private final Executor executor;

    private final WarningsConfig warningsConfig;

    private final KodiUiConfig kodiUiConfig;

    private final Observable[] extractor;

    private final SearchResultToMovieMapper searchResultToMovieMapper;

    private final SearchResultDtoMapper mapper;

    private final MovieDbSearcher movieDbSearcher;

    private final MovieTitleSearchNormalizer movieTitleSearchNormalizer;

    private final MovieTitleWriteNormalizer movieTitleWriteNormalizer;

    private final FolderNameCompareNormalizer folderNameCompareNormalizer;

    public KodiToolsCollectTask(AppConfig config, ResourceBundle resourceBundle, Entries entries, Executor executor, WarningsConfig warningsConfig, KodiUiConfig kodiUiConfig, Observable[] extractor, SearchResultToMovieMapper searchResultToMovieMapper, SearchResultDtoMapper mapper, MovieDbSearcher movieDbSearcher, FolderNameCompareNormalizer folderNameCompareNormalizer, MovieTitleSearchNormalizer movieTitleSearchNormalizer, MovieTitleWriteNormalizer movieTitleWriteNormalizer) {
        super(config, resourceBundle);
        this.entries = entries;
        this.executor = executor;
        this.warningsConfig = warningsConfig;
        this.kodiUiConfig = kodiUiConfig;
        this.extractor = extractor;
        this.searchResultToMovieMapper = searchResultToMovieMapper;
        this.mapper = mapper;
        this.movieDbSearcher = movieDbSearcher;
        this.folderNameCompareNormalizer = folderNameCompareNormalizer;
        this.movieTitleSearchNormalizer = movieTitleSearchNormalizer;
        this.movieTitleWriteNormalizer = movieTitleWriteNormalizer;
    }

    @Override
    protected List<KodiBox> call() throws Exception {
        log.debug("Starting");
        updateMessage(String.format(getResourceBundle().getString(KodiCollectService.MESSAGE)));
        List<KodiBox> result = new ArrayList<>();

        int cnt = 0;
        for (RenamingPath renamingPath : entries.getEntriesFiltered()) {
            if (isCancelled()) {
                log.debug("Cancelled");
                updateMessage("Cancelled.");
                break;
            }
            if (!Files.isDirectory(renamingPath.getOldPath())) {
                continue;
            }
            result.add(buildUiData(buildDataType(renamingPath)));
            updateProgress(++cnt, result.size());
            if (getAppConfig().isDebug()) {
                try {
                    Thread.sleep(getAppConfig().getLoopDelayMs());
                } catch (InterruptedException e) {
                    if (isCancelled()) {
                        log.debug("Cancelled");
                        updateMessage(String.format(getResourceBundle().getString(Tasks.MESSAGE_CANCELLED)));
                        break;
                    }
                }
            }
        }

        result.sort(Comparator.comparing(kodiElement -> kodiElement.getMovie().getRenamingPath().getOldPath()));
        log.debug("Finished");
        updateMessage(null);
        return result;
    }

    private KodiBox buildUiData(Movie data) {
      return new KodiBox(data, getAppConfig(), kodiUiConfig);
     }

    private Movie buildDataType(RenamingPath renamingPath) {
        return new Movie(renamingPath, mapper, executor, movieDbSearcher, folderNameCompareNormalizer, movieTitleSearchNormalizer, searchResultToMovieMapper, movieTitleWriteNormalizer);
    }
}
