package drkodi.data;

import drkodi.MovieDbGenre;
import drkodi.NfoMovie;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class ToNfoCopier {

    private final StaticMovieData movie;

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
        if (movie.getNfoData().getElement().getMovie() == null) {
            movie.getNfoData().getElement().setMovie(new NfoMovie());
        }
        if (movie.getNfoData().getElement().getMovie().getArt() == null) {
            movie.getNfoData().getElement().getMovie().setArt(new NfoMovie.Art());
        }
        movie.getNfoData().getElement().getMovie().setUniqueid(new NfoMovie.UniqueId(movie.getMovieDbId(), "tmdb"));
        movie.getNfoData().getElement().getMovie().setTitle(movie.getMovieTitle());
        movie.getNfoData().getElement().getMovie().setOriginaltitle(movie.getMovieOriginalTitle());
        if(movie.getMovieYear() != null)
            movie.getNfoData().getElement().getMovie().setYear(movie.getMovieYear().toString());
        movie.getNfoData().getElement().getMovie().setPlot(movie.getPlot());
        movie.getNfoData().getElement().getMovie().setGenre(movie.getGenres().stream().map(MovieDbGenre::getName).toList());
        movie.getNfoData().getElement().getMovie().setTagline(movie.getTagline());
        movie.getNfoData().getElement().setUrl(movie.getUrl());
        movie.getNfoData().getElement().getMovie().getArt().setPoster(movie.getImagePath().getElement().getFileName().toString());
    }
}
