package drkodi.data.movie;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class MovieTitleListener implements ChangeListener<String> {

    private final MovieData movie;

    @Override
    public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
        log.debug("Movie title has changed from {} to {}", oldValue, newValue);
        movie.updateTitleWarnings(newValue);
    }
}
