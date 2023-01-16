package drkodi.data.movie;

import drkodi.MovieDbGenre;
import drkodi.NfoMovie;
import drkodi.data.Qualified;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class ToNfoCopier {

    private final MovieData movie;

    public void apply(){
        if (!Qualified.isOk(movie.getNfoData())) {
            movie.initEmptyNfoData();
        }
        if (!Qualified.isOk(movie.getImagePath())) {
            movie.setDefaultImagePath();
        }
        if (!Qualified.isOk(movie.getNfoPath())) {
            movie.setDefaultNfoPath();
        }
        if (movie.getNfoData().getElement().getElement() == null) {
//            movie.getNfoData().getElement().setElement(new NfoMovie());
        }
        if (movie.getNfoData().getElement().getElement().getArt() == null) {
            movie.getNfoData().getElement().getElement().setArt(new NfoMovie.Art());
        }
        movie.getNfoData().getElement().getElement().setUniqueid(new NfoMovie.UniqueId(movie.getMovieDbId(), "tmdb"));
        movie.getNfoData().getElement().getElement().setTitle(movie.getMovieTitle());
        movie.getNfoData().getElement().getElement().setOriginaltitle(movie.getMovieOriginalTitle());
        if(movie.getMovieYear() != null)
            movie.getNfoData().getElement().getElement().setYear(movie.getMovieYear().toString());
        movie.getNfoData().getElement().getElement().setPlot(movie.getPlot());
        movie.getNfoData().getElement().getElement().setGenre(movie.getGenres().stream().map(MovieDbGenre::getName).toList());
        movie.getNfoData().getElement().getElement().setTagline(movie.getTagline());
        movie.getNfoData().getElement().setUrl(getUrl(movie));
        movie.getNfoData().getElement().getElement().getArt().setPoster(movie.getImagePath().getElement().getFileName().toString());
    }

    String getUrl(MovieData movie) {
        // for now, URL is always TheMovieDB
        return "https://www.themoviedb.org/movie/" + movie.getMovieDbId();
    }
}
