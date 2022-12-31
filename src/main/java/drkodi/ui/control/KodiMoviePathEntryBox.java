package drkodi.ui.control;

import drkodi.config.AppConfig;
import drkodi.data.movie.Movie;
import drkodi.data.movie.MovieData;
import drkodi.ui.UiUtil;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import lombok.Getter;


@Getter
public class KodiMoviePathEntryBox extends HBox {

    private final Movie movie;

    private final AppConfig appConfig;

    public KodiMoviePathEntryBox(Movie movie, AppConfig appConfig){

        this.movie = movie;
        this.appConfig = appConfig;

        Label titleLabel = new Label();
        titleLabel.textProperty().bind(movie.movieTitleProperty());

        getChildren().add(UiUtil.applyDebug(titleLabel, appConfig));
    }


}
