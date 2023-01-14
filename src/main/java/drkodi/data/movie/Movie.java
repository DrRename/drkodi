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
import drkodi.data.json.WebSearchResults;
import drkodi.normalization.FolderNameWarningNormalizer;
import drkodi.normalization.TitleSearchNormalizer;
import drkodi.normalization.MovieTitleWriteNormalizer;
import drkodi.task.RenameFolderToMovieTitleTask;
import drkodi.themoviedb.MovieDbDetails;
import drkodi.themoviedb.MovieDbSearchMoviesTask;
import drkodi.themoviedb.MovieDbSearcher;
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
import javafx.scene.image.Image;
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

    //

    private final MovieDbSearcher movieDbSearcher;

    private final TitleSearchNormalizer titleSearchNormalizer;

    private final MovieTitleWriteNormalizer movieTitleWriteNormalizer;

    private final Executor executor;

    private final BooleanProperty running;

    private final ObservableList<Task<?>> runningTasksList;

    public Movie(RenamingPath renamingPath, SearchResultDtoMapper mapper, Executor executor, MovieDbSearcher movieDbSearcher, FolderNameWarningNormalizer folderNameWarningNormalizer, TitleSearchNormalizer titleSearchNormalizer, SearchResultToMovieMapper searchResultToMovieMapper, MovieTitleWriteNormalizer movieTitleWriteNormalizer) {
        super(renamingPath, mapper, folderNameWarningNormalizer, searchResultToMovieMapper);
        this.executor = executor;
        this.movieDbSearcher = movieDbSearcher;
        this.titleSearchNormalizer = titleSearchNormalizer;
        this.movieTitleWriteNormalizer = movieTitleWriteNormalizer;
        this.movieTitleListener = new MovieTitleListener();
        this.nfoPathListener = new NfoPathListener();
        this.imagePathListener = new ImagePathListener();
        this.running = new SimpleBooleanProperty();
        this.runningTasksList = FXCollections.synchronizedObservableList(FXCollections.observableArrayList());
        init();
    }

    private void init() {

        runningTasksList.addListener((ListChangeListener<Task<?>>) c -> {
            while(c.next()){

            }
            log.debug("Running tasks now {}", c.getList().size());
            running.set(!c.getList().isEmpty());
        });

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
        updateTitleWarnings(getMovieTitle());
        updateYearWarnings();
        new MediaFilesPresentTaskExecutor(this, executor, runningTasksList).execute();
        new SubdirsCheckTaskExecutor(this, executor, runningTasksList).execute();
        new IsDirectoryTaskExecutor(this, executor, runningTasksList).execute();
    }

    // Register Listeners //

    private void initWebSearchListener() {
        // nfo data loaded, so movie title might have changed. run a search
        nfoDataProperty().addListener(this::webSearchListener);
        // nfo path loaded, this might be unsuccessful if no file has been found. Run a search.
        nfoPathProperty().addListener(this::webSearchListener);
    }

    @Deprecated
    private boolean searchingWeb;

    private void webSearchListener(ObservableValue<? extends Qualified<?>> observable, Qualified<?> oldValue, Qualified<?> newValue) {
        if (searchingWeb) {
            log.debug("Already searching, skipping change event");
            return;
        }
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
        searchingWeb = true;
        var task = new MovieDbSearchMoviesTask(movieDbSearcher, getTitleSearchNormalizer(), this);
        task.setOnSucceeded(event -> {
            new MovieDbSearchResultProcessor(this, (WebSearchResults) event.getSource().getValue()).process();
            searchingWeb = false;
        });
        executeTask(task);
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

    private void createAndRunMovieDbDetailsTask(Number newValue) {
        var task = new MovieDbDetailsTask(newValue, movieDbSearcher);
        task.setOnSucceeded(event -> {
            takeOverMovieDetails(task.getValue());
        });
        executeTask(task);
    }

    //

    public void takeOverMovieDetails(MovieDbDetails movieDbDetails) {
        // Only genres are updated here.
        // The rest is already updated from the search result.

        Set<MovieDbGenre> genreSet = new LinkedHashSet<>(getGenres());
        genreSet.addAll(movieDbDetails.getGenres());
        getGenres().setAll(genreSet);

        if (StringUtils.isBlank(getTagline()))
            setTagline(movieDbDetails.getTaline());

        writeNfoDataAndImage();
        triggerChecks();
    }

    private void taskFailed(WorkerStateEvent event) {
        log.error("Task {} failed: ", event.getSource(), event.getSource().getException());
    }

    void executeReadNfoTask() {
        Path path = getRenamingPath().getOldPath();
        var task = new LoadNfoPathTask(path);
        task.setOnSucceeded(event -> setNfoPath(QualifiedPath.from((Path) event.getSource().getValue())));
        executeTask(task);
    }


    void loadNfoData(Path path) {
        var task = new ReadNfoTask(path);
        task.setOnSucceeded(event -> setNfoData(QualifiedNfoData.from((NfoRoot) event.getSource().getValue())));
        task.setOnFailed(event -> {
            log.warn("Task failed", task.getException());
            setNfoData(QualifiedNfoData.from(null));
            getWarnings().add(new KodiWarning(KodiWarning.Type.NFO_NOT_READABLE));
//            triggerWebSearch();
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
        task.setOnSucceeded(event -> Renamer.commitRename(this.getRenamingPath(), task.getValue()));
        executeTask(task);
    }

    public void clearData() {
        initEmptyNfoData();
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
        getSearchResults().clear();
        setTagline(null);
        getWarnings().clear();
        initMovieTitleAndMovieYear();
    }

    @Override
    public void takeOverSearchResultData(SearchResult searchResult) {
        super.takeOverSearchResultData(searchResult);
        createAndRunMovieDbDetailsTask(getMovieDbId());
    }

    private void triggerEmptyFolderCheck() {
        log.debug("Trigger media files check");
        var task = new MediaFilesPresentTask(this);
        task.setOnSucceeded(event -> {
                    if (task.getValue()) {
                        log.debug("No media files found: {}", getRenamingPath().getOldPath());
                        getWarnings().add(new KodiWarning(KodiWarning.Type.EMPTY_FOLDER));
                    }
                }
        );
        executeTask(task);
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
