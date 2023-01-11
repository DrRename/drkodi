package drkodi.data.movie;

import javafx.concurrent.Task;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.nio.file.Files;

@Slf4j
@RequiredArgsConstructor
public class IsDirectoryTask extends Task<Boolean> {

    private final Movie movie;

    @Override
    protected Boolean call() throws Exception {
        boolean isDirectory = Files.isDirectory(movie.getRenamingPath().getOldPath());
        if(!isDirectory){
            log.debug("Not a directory: {}", movie.getRenamingPath().getOldPath());
        }
        return isDirectory;
    }
}
