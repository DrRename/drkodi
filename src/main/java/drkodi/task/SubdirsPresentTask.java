package drkodi.task;

import drkodi.KodiUtil;
import drkodi.data.movie.Movie;
import javafx.concurrent.Task;
import lombok.RequiredArgsConstructor;

import java.nio.file.Files;
import java.nio.file.Path;

@RequiredArgsConstructor
public class SubdirsPresentTask extends Task<Boolean> {

    private final Movie movie;

    @Override
    protected Boolean call() throws Exception {
        Path path = movie.getRenamingPath().getOldPath();
        return Files.isDirectory(movie.getRenamingPath().getOldPath()) && !KodiUtil.getSubdirs(path).isEmpty();
    }

}
