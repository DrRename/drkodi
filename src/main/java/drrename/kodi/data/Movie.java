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

package drrename.kodi.data;

import drrename.RenamingPath;
import drrename.SearchResultMapper;
import drrename.kodi.*;
import drrename.kodi.data.dynamic.DynamicMovieData;
import drrename.kodi.data.json.WebSearchResults;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.scene.image.Image;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.nio.file.Path;
import java.util.concurrent.Executor;

@Setter
@Getter
@Slf4j
public class Movie extends DynamicMovieData {

    private final MovieDbQuerier2 movieDbQuerier2;

    private Executor executor;

    public Movie(RenamingPath renamingPath, SearchResultMapper mapper, Executor executor, MovieDbQuerier2 movieDbQuerier2) {
        super(renamingPath, mapper);
        this.executor = executor;
        this.movieDbQuerier2 = movieDbQuerier2;
        init();
    }

    private void init() {
        initNfoPathListener();
        initWebSearchListener();
        initImagePathListener();
        initImageDataListener();
        initIdListener();
        // find and load NFO path
        executeNfoLoadTask();
        triggerWebSearch();

    }

    // Register Listeners //

    private void initNfoPathListener() {
        nfoPathProperty().addListener(this::nfoPathListener);
    }

    private void initWebSearchListener() {
        // nfo data loaded, so movie title might have changed. run a search
        nfoDataProperty().addListener(this::webSearchListener);
        // nfo path loaded, this might be unsuccessful if no file has been found. Run a search.
        nfoPathProperty().addListener(this::webSearchListener);
    }

    private void webSearchListener(ObservableValue<? extends Qualified<?>> observable, Qualified<?> oldValue, Qualified<?> newValue) {
        if (newValue != null) {
            // we need to wait for other listeners to complete the data loading, otherwise we might run the search too early.
            Platform.runLater(() -> {
                log.debug("Data OK, triggering web search");
                triggerWebSearch();
            });
        }
    }

    private void initImageDataListener() {
        if (Qualified.isOk(getImageData())) {
            loadImage(getImageData().getElement());
        }
        imageDataProperty().addListener(this::imageDataListener);
    }

    private void initImagePathListener() {
        imagePathProperty().addListener(this::imagePathListener);
    }

    private void nfoPathListener(ObservableValue<? extends QualifiedPath> observable, QualifiedPath oldValue, QualifiedPath newValue) {

        if (writingToNfo) {
            log.debug("Writing data, will not load NFO data");
            return;
        }

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

    private void imagePathListener(ObservableValue<? extends QualifiedPath> observable, QualifiedPath oldValue, QualifiedPath newValue) {
        if (writingToNfo) {
            log.debug("Writing data, will not load image data");
            return;
        }
        if (Qualified.isOk(newValue)) {
            log.debug("Got new image path: {}", newValue);
            loadImageData(newValue.getElement());
        } else {
            log.debug("Invalid image path: {}", newValue);
        }

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
        if (movieDbQuerier2 == null) {
            log.warn("Cannot perform web query");
            return;
        }
        if (!isDataLoadingComplete()) {
            log.info("Data loading incomplete, will not query TheMovieDb");
            return;
        }
        if (isDataComplete()) {
            log.info("Data complete, will not query TheMovieDb");
            return;
        }
        var task = new WebQuerierTask(movieDbQuerier2, this);
        task.setOnSucceeded(event -> setWebSearchResult((WebSearchResults) event.getSource().getValue()));
        executor.execute(task);
    }

    private void initIdListener() {
        movieDbIdProperty().addListener(this::idListener);

        if (getMovieDbId() != null && !isDetailsComplete()) {
            createAndRunMovieDbDetailsTask(getMovieDbId());
        }
    }

    private void idListener(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
        log.debug("Got new TheMovieDB ID {}", newValue);
        if (getMovieDbId() != null && !isDetailsComplete()) {
            createAndRunMovieDbDetailsTask(getMovieDbId());
        }
    }

    private void createAndRunMovieDbDetailsTask(Number newValue) {
        var task = new MovieDbDetailsTask(newValue, movieDbQuerier2);
        task.setOnSucceeded(event -> {
            MovieDbDetails movieDbDetails = (MovieDbDetails) event.getSource().getValue();
            // TODO: use mapper
            getGenres().setAll(movieDbDetails.getGenres());
            setTagline(movieDbDetails.getTaline());
            setPlot(movieDbDetails.getOverview());
            setMovieTitleFromWeb(movieDbDetails.getTitle());
            setMovieYearFromWeb(movieDbDetails.getReleaseDate());
            setImage(movieDbDetails.getImage());
            setImageData(ImageData.from(movieDbDetails.getImageData()));
        });
        executeTask(task);
    }

    //

    private void taskFailed(WorkerStateEvent event) {
        log.error("Task failed", event.getSource().getException());
    }

    void executeNfoLoadTask() {
        Path path = getRenamingPath().getOldPath();
        var task = new LoadNfoPathTask(path);
        task.setOnSucceeded(event -> setNfoPath(QualifiedPath.from((Path) event.getSource().getValue())));
        executeTask(task);
    }

    void loadNfoData(Path path) {
        var task = new LoadNfoTask2(path);
        task.setOnSucceeded(event -> setNfoData(QualifiedNfoData.from((NfoRoot) event.getSource().getValue())));
        executeTask(task);
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
        var task = new NfoFileWriterTask(getNfoData().getElement(), getNfoPath().getElement());
        executeTask(task);
    }

    private void executeImageWriterTask() {
        var task = new ImageWriterTask(getImageData().getElement(), getImagePath().getElement());
        executeTask(task);
    }

    protected void executeTask(Task<?> task) {
        task.setOnFailed(this::taskFailed);
        if (executor == null) {
            task.run();
        } else
            executor.execute(task);
    }

    public void writeNfoDataAndImage(Executor executor) {

        copyToNfo();
        writeImageToFile(executor);
        executeNfoFileWriterTask();
    }

    private void writeImageToFile(Executor executor) {
        if (!Qualified.isOk(getImageData())) {
            log.warn("No image to write");
            return;
        }
        if (getImagePath() == null) {
            setDefaultImagePath();
        }
        executeImageWriterTask();
    }


}
