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

package drkodi.data;


import drkodi.*;
import drkodi.data.dynamic.DynamicMovieData;
import drkodi.data.json.WebSearchResults;
import drkodi.normalization.FolderNameCompareNormalizer;
import drkodi.normalization.MovieTitleSearchNormalizer;
import drkodi.normalization.MovieTitleWriteNormalizer;
import drkodi.task.MediaFilesPresentTask;
import drkodi.task.RenameFolderToMovieTitleTask;
import drkodi.task.SubdirsPresentTask;
import drkodi.themoviedb.MovieDbSearchTask;
import drrename.commons.RenamingPath;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.scene.image.Image;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.nio.file.Path;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.Executor;

@Setter
@Getter
@Slf4j
public class Movie extends DynamicMovieData {

    private final MovieDbSearcher movieDbSearcher;

    private final MovieTitleSearchNormalizer movieTitleSearchNormalizer;

    private final MovieTitleWriteNormalizer movieTitleWriteNormalizer;

    private Executor executor;

    public Movie(RenamingPath renamingPath, SearchResultDtoMapper mapper, Executor executor, MovieDbSearcher movieDbSearcher, FolderNameCompareNormalizer folderNameCompareNormalizer, MovieTitleSearchNormalizer movieTitleSearchNormalizer, SearchResultToMovieMapper searchResultToMovieMapper, MovieTitleWriteNormalizer movieTitleWriteNormalizer) {
        super(renamingPath, mapper, folderNameCompareNormalizer, searchResultToMovieMapper);
        this.executor = executor;
        this.movieDbSearcher = movieDbSearcher;
        this.movieTitleSearchNormalizer = movieTitleSearchNormalizer;
        this.movieTitleWriteNormalizer = movieTitleWriteNormalizer;
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
        // trigger checks
        triggerEmptyFolderCheck();
        triggerSubdirsCheck();
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
        var task = new MovieDbSearchTask(movieDbSearcher, getMovieTitleSearchNormalizer(), this);
        task.setOnSucceeded(event -> {
            setWebSearchResult((WebSearchResults) event.getSource().getValue());
            searchingWeb = false;
        });
        executor.execute(task);
    }

    private void initIdListener() {
        movieDbIdProperty().addListener(this::idListener);

//        if (getMovieDbId() != null && !isDetailsComplete()) {
//            createAndRunMovieDbDetailsTask(getMovieDbId());
//        }
    }

    private void idListener(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
        log.debug("Got new TheMovieDB ID {}", newValue);
        if (getMovieDbId() != null && !isDetailsComplete()) {
            createAndRunMovieDbDetailsTask(getMovieDbId());
        }
    }

    private void createAndRunMovieDbDetailsTask(Number newValue) {
        var task = new MovieDbDetailsTask(newValue, movieDbSearcher);
        task.setOnSucceeded(event -> {
            takeOverMovieDetails(task.getValue());
        });
        executeTask(task);
    }

    //

    public void takeOverMovieDetails(MovieDbDetails movieDbDetails){
        // Only genres are updated here.
        // The rest is already updated from the search result.

        Set<MovieDbGenre> genreSet = new LinkedHashSet<>(getGenres());
        genreSet.addAll(movieDbDetails.getGenres());
        getGenres().setAll(genreSet);
    }

    private void taskFailed(WorkerStateEvent event) {
        log.error("Task failed: ", event.getSource().getException());
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

    private void triggerEmptyFolderCheck() {
        log.debug("Trigger media files check");
        var task = new MediaFilesPresentTask(this);
        task.setOnSucceeded(event -> {
                    if (task.getValue()) {
                        log.debug("No media files found: {}", getRenamingPath().getOldPath());
                        getWarnings().add(new KodiWarning(KodiWarning.Type.EMTPY_FOLDER));
                    }
                }
        );
        executeTask(task);
    }

    private void triggerSubdirsCheck() {
        log.debug("Trigger subdirs check");
        var task = new SubdirsPresentTask(this);
        task.setOnSucceeded(event -> {
                    if (task.getValue()) {
                        log.debug("Subdirectories found in {}", getRenamingPath().getOldPath());
                        getWarnings().add(new KodiWarning(KodiWarning.Type.SUBDIRS));
                    }
                }
        );
        executeTask(task);
    }
}
