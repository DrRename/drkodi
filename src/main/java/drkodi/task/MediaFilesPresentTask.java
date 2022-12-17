package drkodi.task;

import drkodi.KodiUtil;
import drkodi.data.Movie;
import javafx.concurrent.Task;
import lombok.RequiredArgsConstructor;

import java.nio.file.Path;

@RequiredArgsConstructor
public class MediaFilesPresentTask extends Task<Boolean> {

    private final Movie movie;

    @Override
    protected Boolean call() throws Exception {
        Path path = movie.getRenamingPath().getOldPath();
        return KodiUtil.findAllVideoFiles(path).isEmpty();

    }

}
