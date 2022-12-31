package drkodi;

import drkodi.config.AppConfig;
import drkodi.data.movie.Movie;
import drkodi.data.SearchResultToMovieMapper;
import drkodi.normalization.FolderNameWarningNormalizer;
import drkodi.normalization.MovieTitleSearchNormalizer;
import drkodi.normalization.MovieTitleWriteNormalizer;
import drkodi.themoviedb.MovieDbSearcher;
import drkodi.ui.config.KodiUiConfig;
import drkodi.ui.control.KodiMoviePathEntryBox;
import drrename.commons.RenamingPath;
import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.scene.control.ListView;
import javafx.scene.layout.Pane;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.nio.file.Files;
import java.util.Comparator;
import java.util.ResourceBundle;
import java.util.concurrent.Executor;

@Getter
@Slf4j
class KodiToolsCollectTask extends PrototypeTask<Void> {

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

    private final FolderNameWarningNormalizer folderNameWarningNormalizer;

    private final ListView<KodiMoviePathEntryBox> listView;

    public KodiToolsCollectTask(AppConfig config, ResourceBundle resourceBundle, Entries entries, Executor executor, WarningsConfig warningsConfig, KodiUiConfig kodiUiConfig, Observable[] extractor, SearchResultToMovieMapper searchResultToMovieMapper, SearchResultDtoMapper mapper, MovieDbSearcher movieDbSearcher, FolderNameWarningNormalizer folderNameWarningNormalizer, MovieTitleSearchNormalizer movieTitleSearchNormalizer, MovieTitleWriteNormalizer movieTitleWriteNormalizer, ListView<KodiMoviePathEntryBox> listView) {
        super(config, resourceBundle);
        this.entries = entries;
        this.executor = executor;
        this.warningsConfig = warningsConfig;
        this.kodiUiConfig = kodiUiConfig;
        this.extractor = extractor;
        this.searchResultToMovieMapper = searchResultToMovieMapper;
        this.mapper = mapper;
        this.movieDbSearcher = movieDbSearcher;
        this.folderNameWarningNormalizer = folderNameWarningNormalizer;
        this.movieTitleSearchNormalizer = movieTitleSearchNormalizer;
        this.movieTitleWriteNormalizer = movieTitleWriteNormalizer;
        this.listView = listView;
    }

    @Override
    protected Void call() throws Exception {
        log.debug("Starting");
        updateMessage(String.format(getResourceBundle().getString(KodiCollectService.MESSAGE)));

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
            Platform.runLater(() -> {
                listView.getItems().add(buildUiData(buildData(renamingPath)));
                listView.getItems().sort(Comparator.comparing(kodiElement -> kodiElement.getMovie().getRenamingPath().getOldPath()));
            });
            updateProgress(++cnt, listView.getItems().size());
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
        log.debug("Finished");
        updateMessage(null);
        return null;
    }

    private KodiMoviePathEntryBox buildUiData(Movie data) {
      return new KodiMoviePathEntryBox(data, getAppConfig());
     }

    private Movie buildData(RenamingPath renamingPath) {
        return new Movie(renamingPath, mapper, executor, movieDbSearcher, folderNameWarningNormalizer, movieTitleSearchNormalizer, searchResultToMovieMapper, movieTitleWriteNormalizer);
    }
}
