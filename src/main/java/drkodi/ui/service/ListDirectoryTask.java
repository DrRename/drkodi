package drkodi.ui.service;

import drkodi.*;
import drkodi.config.AppConfig;
import drkodi.data.SearchResultToMovieMapper;
import drkodi.data.movie.Movie;
import drkodi.normalization.FolderNameWarningNormalizer;
import drkodi.normalization.MovieTitleSearchNormalizer;
import drkodi.normalization.MovieTitleWriteNormalizer;
import drkodi.themoviedb.MovieDbSearcher;
import drkodi.ui.control.KodiMoviePathEntryBox;
import drrename.commons.RenamingPath;
import javafx.application.Platform;
import javafx.concurrent.Task;
import lombok.extern.slf4j.Slf4j;

import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ResourceBundle;
import java.util.concurrent.Executor;

/**
 * A {@link Task} that will iterate over all children of given {@link Path} and create a new instance of
 * {@link RenamingPath} from every child. Every {@code RenamingPath} is immediately added to given instance of
 * {@code Entries}.
 */
@Slf4j
public class ListDirectoryTask extends PrototypeTask<Void> {

    private final Path dir;

    public ListDirectoryTask(AppConfig appConfig, ResourceBundle resourceBundle, MovieEntries movieEntries, Executor executor, SearchResultToMovieMapper searchResultToMovieMapper, SearchResultDtoMapper mapper, MovieDbSearcher movieDbSearcher, MovieTitleSearchNormalizer movieTitleSearchNormalizer, MovieTitleWriteNormalizer movieTitleWriteNormalizer, FolderNameWarningNormalizer folderNameWarningNormalizer, Path dir) {
        super(appConfig, resourceBundle, movieEntries, executor, searchResultToMovieMapper, mapper, movieDbSearcher, movieTitleSearchNormalizer, movieTitleWriteNormalizer, folderNameWarningNormalizer);
        this.dir = dir;
    }

    @Override
    protected Void call() throws Exception {
        checkState();
        log.debug("Starting");
        updateMessage(String.format(getResourceBundle().getString(LoadPathsService.LOADING_FILES)));
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir)) {
            for (Path path : stream) {
                if (isCancelled()) {
                    log.debug("Cancelled");
                    updateMessage(String.format(getResourceBundle().getString(Tasks.MESSAGE_CANCELLED)));
                    break;
                }
                var entry = buildUiData(buildData(new RenamingPath(path)));
                handleNewEntry(entry);
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
        }
        log.debug("Finished");
        updateMessage(null);
        return null;
    }

    protected void handleNewEntry(KodiMoviePathEntryBox element) {
        Platform.runLater(() -> {
            movieEntries.getEntries().add(element);
        });
    }

    private void checkState() {
        if (dir == null || !Files.isDirectory(dir))
            throw new IllegalArgumentException(dir + " not a directory");
    }
}

