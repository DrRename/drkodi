package drrename.kodi;

import drrename.*;
import drrename.commons.RenamingPath;
import drrename.config.AppConfig;
import drrename.kodi.data.FolderNameCompareNormalizer;
import drrename.kodi.data.Movie;
import drrename.kodi.data.MovieTitleSearchNormalizer;
import drrename.kodi.data.SearchResultToMovieMapper;
import drrename.kodi.ui.config.KodiUiConfig;
import drrename.kodi.ui.control.KodiBox;
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

    private final MovieDbQuerier2 movieDbQuerier2;

    private final MovieTitleSearchNormalizer movieTitleSearchNormalizer;

    private final FolderNameCompareNormalizer folderNameCompareNormalizer;

    public KodiToolsCollectTask(AppConfig config, ResourceBundle resourceBundle, Entries entries, Executor executor, WarningsConfig warningsConfig, KodiUiConfig kodiUiConfig, Observable[] extractor, SearchResultToMovieMapper searchResultToMovieMapper, SearchResultDtoMapper mapper, MovieDbQuerier2 movieDbQuerier2, FolderNameCompareNormalizer folderNameCompareNormalizer, MovieTitleSearchNormalizer movieTitleSearchNormalizer) {
        super(config, resourceBundle);
        this.entries = entries;
        this.executor = executor;
        this.warningsConfig = warningsConfig;
        this.kodiUiConfig = kodiUiConfig;
        this.extractor = extractor;
        this.searchResultToMovieMapper = searchResultToMovieMapper;
        this.mapper = mapper;
        this.movieDbQuerier2 = movieDbQuerier2;
        this.folderNameCompareNormalizer = folderNameCompareNormalizer;
        this.movieTitleSearchNormalizer = movieTitleSearchNormalizer;
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
        return new Movie(renamingPath, mapper, executor, movieDbQuerier2, folderNameCompareNormalizer, movieTitleSearchNormalizer, searchResultToMovieMapper);
    }
}
