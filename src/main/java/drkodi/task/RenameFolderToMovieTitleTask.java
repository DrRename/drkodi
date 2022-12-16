package drkodi.task;

import drkodi.KodiUtil;
import drkodi.data.Movie;
import drkodi.normalization.MovieTitleWriteNormalizer;
import drkodi.util.DrRenameUtil;
import javafx.concurrent.Task;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.nio.file.Path;


@Slf4j
@RequiredArgsConstructor
public class RenameFolderToMovieTitleTask extends Task<Path> {

    private final Movie movie;

    private final MovieTitleWriteNormalizer normalizer;

    @Override
    protected Path call() throws Exception {
        Path currentPath = movie.getRenamingPath().getOldPath();
        String normalizedMovieTitle = normalizer.normalize(movie.getMovieTitle());
        String newName = KodiUtil.buildFolderNameString(normalizedMovieTitle, movie.getMovieYear());
        log.debug("Renaming from {} to {}", currentPath.getFileName(), newName);
        return DrRenameUtil.rename(currentPath, newName);
    }
}
