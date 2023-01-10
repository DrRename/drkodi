package drkodi.data.movie;

import javafx.concurrent.Task;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class MediaFilesPresentTask extends Task<Boolean> {

    private final Movie movie;

    @Override
    protected Boolean call() throws Exception {
        return new MediaFilesCollectTask(movie).call().isEmpty();

    }

}
