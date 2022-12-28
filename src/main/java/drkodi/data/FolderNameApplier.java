package drkodi.data;

import drkodi.KodiUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.nio.file.Path;

@RequiredArgsConstructor
@Slf4j
public class FolderNameApplier {

    private final MovieData movie;

    private final String folderName;

    public void apply(){
        log.debug("Setting properties from new folder name {}", folderName);
        movie.setMovieTitleFromFolder(KodiUtil.getMovieNameFromDirectoryName(folderName));
        movie.setMovieYearFromFolder(KodiUtil.getMovieYearFromDirectoryName(folderName));
        movie.setMovieTitle(movie.getMovieTitleFromFolder());
        movie.setMovieYear(movie.getMovieYearFromFolder());
        movie.updateTitleWarnings(movie.getMovieTitleFromFolder());
        movie.updateYearWarnings();
        if (Qualified.isOk(movie.getNfoPath())) {
            String currentNfoFileName = movie.getNfoPath().getElement().getFileName().toString();
            Path path = Path.of(movie.getRenamingPath().getOldPath().toString(), currentNfoFileName);
            log.debug("Updating NFO path to {}", path);
            movie.setNfoPath(QualifiedPath.from(path));

        } else {
            log.debug("NFO path invalid, will not update ({})", movie.getNfoPath());
        }
    }
}
