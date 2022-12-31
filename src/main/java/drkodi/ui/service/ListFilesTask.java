package drkodi.ui.service;

import drkodi.*;
import drkodi.config.AppConfig;
import drkodi.data.SearchResultToMovieMapper;
import drkodi.normalization.FolderNameWarningNormalizer;
import drkodi.normalization.MovieTitleSearchNormalizer;
import drkodi.normalization.MovieTitleWriteNormalizer;
import drkodi.themoviedb.MovieDbSearcher;
import drkodi.ui.control.KodiMoviePathEntryBox;
import drrename.commons.RenamingPath;
import javafx.application.Platform;
import lombok.extern.slf4j.Slf4j;

import java.nio.file.Path;
import java.util.Collection;
import java.util.ResourceBundle;
import java.util.concurrent.Executor;

/**
 * Iterates over a given collection of {@code Path}, transforms all entries into {@code RenamingPath} and stores them
 * into given instance of {@code Entries}.
 */
@Slf4j
public class ListFilesTask extends PrototypeTask<Void> {

    private final Collection<Path> files;

    public ListFilesTask(AppConfig appConfig, ResourceBundle resourceBundle, MovieEntries movieEntries, Executor executor, SearchResultToMovieMapper searchResultToMovieMapper, SearchResultDtoMapper mapper, MovieDbSearcher movieDbSearcher, MovieTitleSearchNormalizer movieTitleSearchNormalizer, MovieTitleWriteNormalizer movieTitleWriteNormalizer, FolderNameWarningNormalizer folderNameWarningNormalizer, Collection<Path> files) {
        super(appConfig, resourceBundle, movieEntries, executor, searchResultToMovieMapper, mapper, movieDbSearcher, movieTitleSearchNormalizer, movieTitleWriteNormalizer, folderNameWarningNormalizer);
        this.files = files;
    }

    @Override
    protected Void call() throws InterruptedException {
        log.debug("Starting");
        updateMessage(String.format(getResourceBundle().getString(LoadPathsService.LOADING_FILES)));
        int cnt = 0;
        for (final Path f : files) {
            if (isCancelled()) {
                log.debug("Cancelled");
                updateMessage(String.format(getResourceBundle().getString(Tasks.MESSAGE_CANCELLED)));
                break;
            }
            handleNewEntry(++cnt,  buildUiData(buildData(new RenamingPath(f))));
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

    private void handleNewEntry(int progress, KodiMoviePathEntryBox element) {
        Platform.runLater(() -> {
            movieEntries.getEntries().add(element);
        });
        updateProgress(progress, files.size());
    }
}

