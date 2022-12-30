package drkodi.data.movie;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import lombok.RequiredArgsConstructor;

import java.nio.file.Path;

@RequiredArgsConstructor
class MoviePathChangeListener implements ChangeListener<Path> {

    private final MovieData movie;

    @Override
    public void changed(ObservableValue<? extends Path> observable, Path oldValue, Path newValue) {
        movie.applyNewFolderName(newValue.getFileName().toString());
    }
}
