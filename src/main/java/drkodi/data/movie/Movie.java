/*
 *     Dr.Rename - A Minimalistic Batch Renamer
 *
 *     Copyright (C) 2022
 *
 *     This file is part of Dr.Rename.
 *
 *     You can redistribute it and/or modify it under the terms of the GNU Affero
 *     General Public License as published by the Free Software Foundation, either
 *     version 3 of the License, or (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful, but WITHOUT
 *     ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 *     FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License
 *     for more details.
 *
 *     You should have received a copy of the GNU Affero General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package drkodi.data.movie;


import drkodi.*;
import drkodi.data.*;
import drkodi.data.json.MovieWebSearchResults;
import drkodi.data.json.TvWebSearchResults;
import drkodi.normalization.FolderNameWarningNormalizer;
import drkodi.normalization.TitleSearchNormalizer;
import drkodi.normalization.MovieTitleWriteNormalizer;
import drkodi.task.RenameFolderToMovieTitleTask;
import drkodi.themoviedb.*;
import drrename.commons.RenamingPath;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.concurrent.Worker;
import javafx.concurrent.WorkerStateEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.layout.Region;
import javafx.stage.Modality;
import javafx.stage.StageStyle;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.nio.file.Path;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executor;

@Setter
@Getter
@Slf4j
public class Movie extends DynamicMovieData {

    // Inner classes //

    class MovieTitleListener implements ChangeListener<String> {

        Timer timer = new Timer();

        @Override
        public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
            log.debug("Movie title has changed from {} to {}", oldValue, newValue);
            timer.cancel();
            timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    triggerWebSearch();
                }
            }, 1000);
        }
    }
    private final MovieTitleListener movieTitleListener;

    class NfoPathListener implements ChangeListener<QualifiedPath> {

        @Override
        public void changed(ObservableValue<? extends QualifiedPath> observable, QualifiedPath oldValue, QualifiedPath newValue) {
            if (Qualified.isOk(newValue)) {
                log.debug("Got NFO path: {}", newValue);
                loadNfoData(newValue.getElement());
            } else {
                log.debug("Got invalid NFO path: {}", newValue);
                // we are not loading NFO data since path is invalid.
                // To signal data loading is complete, we need to set NFO data to INVALID here.
                setNfoData(new QualifiedNfoData(null, Qualified.Type.INVALID));
            }
        }
    }

    private final NfoPathListener nfoPathListener;

    class ImagePathListener implements ChangeListener<QualifiedPath> {

        @Override
        public void changed(ObservableValue<? extends QualifiedPath> observable, QualifiedPath oldValue, QualifiedPath newValue) {
            if (Qualified.isOk(newValue)) {
                log.debug("Got new image path: {}", newValue);
                loadImageData(newValue.getElement());
            } else {
                log.debug("Invalid image path: {}", newValue);
            }
        }
    }
    private final ImagePathListener imagePathListener;

    class TypeListener implements ChangeListener<Type> {

        @Override
        public void changed(ObservableValue<? extends Type> observable, Type oldValue, Type newValue) {
            if(newValue != null) {
                new CollectMediaFilesMovieTaskExecutor(Movie.this, executor, runningTasksList, Type.TV_SERIES.equals(newValue) ? 1 : 0).execute();
            }
        }
    }
    private final TypeListener typeListener;

    class CheckMediaFilesListener implements ListChangeListener<RenamingPath> {

        @Override
        public void onChanged(Change<? extends RenamingPath> c) {
            while (c.next()) {
            }
            log.debug("Media files changed, now {} registered", c.getList().size());

        }
    }
    private final CheckMediaFilesListener checkMediaFilesListener;

    //

    private final MovieDbSearcher movieDbSearcher;

    private final TitleSearchNormalizer titleSearchNormalizer;

    private final MovieTitleWriteNormalizer movieTitleWriteNormalizer;

    private final Executor executor;

    private final BooleanProperty running;

    private final ObservableList<Task<?>> runningTasksList;

    public Movie(RenamingPath renamingPath, MovieSearchResultDtoMapper mapper, Executor executor, MovieDbSearcher movieDbSearcher, FolderNameWarningNormalizer folderNameWarningNormalizer, TitleSearchNormalizer titleSearchNormalizer, SearchResultToMovieMapper searchResultToMovieMapper, MovieTitleWriteNormalizer movieTitleWriteNormalizer, TvSearchResultDtoMapper tvSearchResultDtoMapper) {
        super(renamingPath, mapper, folderNameWarningNormalizer, searchResultToMovieMapper, tvSearchResultDtoMapper);
        this.executor = executor;
        this.movieDbSearcher = movieDbSearcher;
        this.titleSearchNormalizer = titleSearchNormalizer;
        this.movieTitleWriteNormalizer = movieTitleWriteNormalizer;
        this.movieTitleListener = new MovieTitleListener();
        this.nfoPathListener = new NfoPathListener();
        this.imagePathListener = new ImagePathListener();
        this.typeListener = new TypeListener();
        this.checkMediaFilesListener = new CheckMediaFilesListener();
        this.running = new SimpleBooleanProperty();
        this.runningTasksList = FXCollections.synchronizedObservableList(FXCollections.observableArrayList());
        init();
    }

    private void init() {

        runningTasksList.addListener((ListChangeListener<Task<?>>) c -> {
            while(c.next()){

            }
            running.set(!c.getList().isEmpty());
        });

        mediaFilesProperty().addListener(checkMediaFilesListener);
        typeProperty().addListener(typeListener);
        movieTitleProperty().addListener(movieTitleListener);
        nfoPathProperty().addListener(nfoPathListener);
        imagePathProperty().addListener(imagePathListener);

        initWebSearchListener();
        initImageDataListener();
        initIdListener();

    }

    public void loadExternalData(){
        executeReadNfoTask();
        triggerWebSearch();
    }

    public void triggerChecks(){
        getWarnings().clear();

        // simple folder name checks
        updateTitleWarnings(getMovieTitle());
        updateYearWarnings();

        //
        new IsDirectoryMovieTaskExecutor(this, executor, runningTasksList).execute();

    }

    // Register Listeners //

    private void initWebSearchListener() {
        // nfo data loaded, so movie title might have changed. run a search
        nfoDataProperty().addListener(this::webSearchListener);
        // nfo path loaded, this might be unsuccessful if no file has been found. Run a search.
        nfoPathProperty().addListener(this::webSearchListener);
    }



    private void webSearchListener(ObservableValue<? extends Qualified<?>> observable, Qualified<?> oldValue, Qualified<?> newValue) {
        if (newValue != null) {
            log.debug("Data OK, triggering web search");
            triggerWebSearch();

        }
    }

    private void initImageDataListener() {
        if (Qualified.isOk(getImageData())) {
            loadImage(getImageData().getElement());
        }
        imageDataProperty().addListener(this::imageDataListener);
    }


    @Override
    protected void setDefaultNfoPath() {
        nfoPathProperty().removeListener(nfoPathListener);
        super.setDefaultNfoPath();
        nfoPathProperty().addListener(nfoPathListener);
    }

    private void imageDataListener(ObservableValue<? extends ImageData> observable, ImageData oldValue, ImageData newValue) {

        if (Qualified.isOk(newValue)) {
            log.debug("Got new image data");
            loadImage(newValue.getElement());
        } else {
            log.debug("Image data invalid: {}", newValue);
        }

    }

    private void triggerWebSearch() {
        if (movieDbSearcher == null) {
            log.warn("Cannot perform web query");
            return;
        }
        if (!isDataLoadingComplete()) {
            log.debug("Data loading incomplete, will not query TheMovieDb");
            return;
        }
        if (isDataComplete()) {
            log.debug("Data complete, will not query TheMovieDb");
            return;
        }
        var moviesTask = new MovieDbSearchMoviesTask(movieDbSearcher, getTitleSearchNormalizer(), this);
        moviesTask.setOnSucceeded(event -> {
            new MovieSearchResultProcessor(this, (MovieWebSearchResults) event.getSource().getValue()).process();
        });
        executeTask(moviesTask);
        var tvTask = new MovieDbSearchTvTask(movieDbSearcher, getTitleSearchNormalizer(), this);
        tvTask.setOnSucceeded(event -> {
            new TvSearchResultProcessor(this, (TvWebSearchResults) event.getSource().getValue()).process();
        });
        executeTask(tvTask);
    }

    private void initIdListener() {
        movieDbIdProperty().addListener(this::idListener);

//        if (getMovieDbId() != null && !isDetailsComplete()) {
//            createAndRunMovieDbDetailsTask(getMovieDbId());
//        }
    }

    private void idListener(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
//        log.debug("Got new TheMovieDB ID {}", newValue);
//        if (getMovieDbId() != null && !isDetailsComplete()) {
//            createAndRunMovieDbDetailsTask(getMovieDbId());
//        }
    }

    private void createAndRunMovieDbMovieDetailsTask(Number newValue) {
        var task = new MovieDbMovieDetailsTask(newValue, movieDbSearcher);
        task.setOnSucceeded(event -> {
            takeOverMovieDetails(task.getValue());
        });
        executeTask(task);
    }

    private void createAndRunMovieDbTvDetailsTask(Number newValue) {
        var task = new MovieDbTvDetailsTask(newValue, movieDbSearcher);
        task.setOnSucceeded(event -> {
            takeOverTvDetails(task.getValue());
        });
        executeTask(task);
    }

    //

    public void takeOverMovieDetails(MovieDbMovieDetails movieDbMovieDetails) {

        setType(Type.MOVIE);

        // Only genres are updated here.
        // The rest is already updated from the search result.

        Set<MovieDbGenre> genreSet = new LinkedHashSet<>(getGenres());
        genreSet.addAll(movieDbMovieDetails.getGenres());
        getGenres().setAll(genreSet);

        if (StringUtils.isBlank(getTagline()))
            setTagline(movieDbMovieDetails.getTagline());

        writeNfoDataAndImage();
        triggerChecks();
    }

    public void takeOverTvDetails(MovieDbTvDetails movieDbMovieDetails) {

        setType(Type.TV_SERIES);

        // Only genres are updated here.
        // The rest is already updated from the search result.

        Set<MovieDbGenre> genreSet = new LinkedHashSet<>(getGenres());
        genreSet.addAll(movieDbMovieDetails.getGenres());
        getGenres().setAll(genreSet);

        if (StringUtils.isBlank(getTagline()))
            setTagline(movieDbMovieDetails.getTagline());

        writeNfoDataAndImage();
        triggerChecks();
    }

    private void taskFailed(WorkerStateEvent event) {
        log.error("Task {} failed: ", event.getSource(), event.getSource().getException());
    }

    private void executeReadNfoTask() {
        new LoadNfoMovieTaskExecutor(this, executor, runningTasksList).execute();
    }


    void loadNfoData(Path path) {
        var task = new ReadNfoTask(path);
        task.setOnSucceeded(event -> {
            setNfoData(QualifiedNfoData.from((NfoRoot) task.getValue().nfoRoot()));
            setType(task.getValue().type());
        });
        task.setOnFailed(event -> {
            log.warn("Task failed: ", task.getException());
            setNfoData(QualifiedNfoData.from(null));
            getWarnings().add(new KodiWarning(KodiWarning.Type.NFO_NOT_READABLE));
        });
        executeTask2(task);
    }

    void loadImageData(Path imagePath) {
        var task = new LoadImageDataTask(imagePath);
        task.setOnSucceeded(event -> setImageData(ImageData.from((byte[]) event.getSource().getValue())));
        executeTask(task);
    }

    void loadImage(byte[] imageData) {
        log.debug("Loading image from image data");
        var task = new LoadImageTask(imageData);
        task.setOnSucceeded(event -> setImage((Image) event.getSource().getValue()));
        executeTask(task);
    }

    private void executeNfoFileWriterTask() {
        if (!Qualified.isOk(getNfoPath())) {
            log.warn("Cannot write NFO file to path {}", getNfoPath());
            return;
        }
        var task = new NfoFileWriterTask(getNfoData().getElement(), getNfoPath().getElement());
        executeTask(task);
    }

    private void executeImageWriterTask() {
        if (!Qualified.isOk(getImagePath())) {
            log.warn("Cannot write image to path {}", getImagePath());
            return;
        }
        var task = new ImageWriterTask(getImageData().getElement(), getImagePath().getElement());
        executeTask(task);
    }


    @Deprecated
    protected void executeTask(Task<?> task) {
        task.setOnFailed(this::taskFailed);
        executeTask2(task);
    }

    @Deprecated
    protected void executeTask2(Task<?> task) {
        log.debug("Adding task {} to queue", task);
        runningTasksList.add(task);
        task.stateProperty().addListener((obs, oldState, newState) -> {
            if (newState == Worker.State.SUCCEEDED || newState == Worker.State.FAILED || newState == Worker.State.CANCELLED) {
                log.debug("Removing task {} from queue", task);
                runningTasksList.remove(task);
            }
        });

        if (executor == null) {
            log.debug("No executor set, executing on current thread");
            task.run();
        } else
            executor.execute(task);
    }

    public void writeNfoDataAndImage() {

        copyToNfo();
        writeImageToFile();
        executeNfoFileWriterTask();
    }

    private void writeImageToFile() {
        if (!Qualified.isOk(getImageData())) {
            log.warn("No image to write");
            return;
        }
        if (getImagePath() == null) {
            setDefaultImagePath();
        }
        executeImageWriterTask();
    }

    public void renameFolder() {
        log.debug("Renaming folder");
        var task = new RenameFolderToMovieTitleTask(this, movieTitleWriteNormalizer);
        task.setOnSucceeded(event -> {
            Renamer.commitRename(this.getRenamingPath(), task.getValue());
        });
        task.setOnFailed(event -> {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Rename failed with \n" + event.getSource().getException().getClass().getSimpleName() + " for\n" + event.getSource().getException().getLocalizedMessage(), ButtonType.OK);
            alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
            alert.initStyle(StageStyle.UTILITY);
            alert.showAndWait();
        });
        executeTask2(task);
    }

    public void clearData() {
        if(getType() != null)
            initEmptyNfoData();
        if(getType() != null)
            executeNfoFileWriterTask();
        setMovieTitle(null);
        setMovieDbId(null);
        setNfoData(null);
        setImageData(null);
        getGenres().clear();
        setImage(null);
        setMovieYear(null);
        setImagePath(null);
        setMovieOriginalTitle(null);
        setMovieTitleFromWeb(null);
        setMovieYearFromWeb(null);
        setPlot(null);
        getMovieSearchResults().clear();
        setTagline(null);
        getWarnings().clear();
        initMovieTitleAndMovieYear();
    }

    @Override
    public void takeOverSearchResultData(SearchResult searchResult) {
        super.takeOverSearchResultData(searchResult);
        if(searchResult instanceof MovieSearchResult) {
            createAndRunMovieDbMovieDetailsTask(getMovieDbId());
        }
        else if(searchResult instanceof TvSearchResult) {
            createAndRunMovieDbTvDetailsTask(getMovieDbId());
        } else {
            log.warn("Search result not recognized {}", searchResult);
        }
    }

    @Override
    protected void setDefaultImagePath() {
        imagePathProperty().removeListener(imagePathListener);
        super.setDefaultImagePath();
        imagePathProperty().addListener(imagePathListener);
    }



    // FX Getter / Setter //


    public boolean isRunning() {
        return running.get();
    }

    public BooleanProperty runningProperty() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running.set(running);
    }
}
