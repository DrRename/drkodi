package drkodi.data.movie;

import drkodi.data.QualifiedNfoData;
import drkodi.data.QualifiedPath;
import drkodi.nfo.NfoUtil;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
class NfoDataListener implements ChangeListener<QualifiedNfoData> {

    private final DynamicMovieData movie;

    @Override
    public void changed(ObservableValue<? extends QualifiedNfoData> observable, QualifiedNfoData oldValue, QualifiedNfoData newValue) {
        if(newValue == null){
            log.debug("New NFO data null");
            return;
        }
        log.debug("Setting NFO data to {}", newValue);
        movie.setMovieTitleFromNfo(NfoUtil.getMovieTitle(newValue.getElement()));
        movie.setMovieYearFromNfo(NfoUtil.getMovieYear(newValue.getElement()));
        Optional.ofNullable(NfoUtil.getGenres(newValue.getElement())).ifPresent(g -> movie.getGenres().setAll(g));
        movie.setPlot(NfoUtil.getPlot(newValue.getElement()));
        movie.setTagline(NfoUtil.getTagline(newValue.getElement()));
        movie.setImagePath(QualifiedPath.from(NfoUtil.getImagePath(movie.getRenamingPath().getOldPath(), newValue.getElement())));
        movie.setMovieDbId(NfoUtil.getId2(newValue.getElement()));
        movie.setMovieOriginalTitle(NfoUtil.getMovieOriginalTitle(newValue.getElement()));
    }
}
