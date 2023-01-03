package drkodi.data.movie;

import javafx.concurrent.Task;
import lombok.RequiredArgsConstructor;

import java.nio.file.Files;

@RequiredArgsConstructor
public class IsDirectoryTask extends Task<Boolean> {

    private final Movie movie;

    @Override
    protected Boolean call() throws Exception {
        return Files.isDirectory(movie.getRenamingPath().getOldPath());
    }
}
