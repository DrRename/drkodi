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


import drkodi.KodiUtil;
import drkodi.SearchResultDtoMapper;
import drkodi.data.QualifiedNfoData;
import drkodi.data.QualifiedPath;
import drkodi.data.SearchResultToMovieMapper;
import drkodi.nfo.NfoUtil;
import drkodi.normalization.FolderNameWarningNormalizer;
import drrename.commons.RenamingPath;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.nio.file.Path;
import java.util.Optional;

@Slf4j
public class DynamicMovieData extends MovieData {

    // Inner classes //

    class MovieTitleListener implements ChangeListener<String> {

        @Override
        public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
            log.debug("Movie title has changed from {} to {}", oldValue, newValue);
            updateTitleWarnings(newValue);
        }
    }

    private final MovieTitleListener movieTitleListener;

    class NfoDataListener implements ChangeListener<QualifiedNfoData> {

        @Override
        public void changed(ObservableValue<? extends QualifiedNfoData> observable, QualifiedNfoData oldValue, QualifiedNfoData newValue) {
            if(newValue == null){
                log.debug("New NFO data null");
                return;
            }
            log.debug("Setting NFO data to {}", newValue);
            setMovieTitleFromNfo(NfoUtil.getMovieTitle(newValue.getElement()));
            setMovieYearFromNfo(NfoUtil.getMovieYear(newValue.getElement()));
            Optional.ofNullable(NfoUtil.getGenres(newValue.getElement())).ifPresent(g -> getGenres().setAll(g));
            setPlot(NfoUtil.getPlot(newValue.getElement()));
            setTagline(NfoUtil.getTagline(newValue.getElement()));
            setImagePath(QualifiedPath.from(NfoUtil.getImagePath(getRenamingPath().getOldPath(), newValue.getElement())));
            setMovieDbId(NfoUtil.getId2(newValue.getElement()));
            setMovieOriginalTitle(NfoUtil.getMovieOriginalTitle(newValue.getElement()));
        }
    }

    private final NfoDataListener nfoDataListener;

    class MoviePathChangeListener implements ChangeListener<Path> {

        @Override
        public void changed(ObservableValue<? extends Path> observable, Path oldValue, Path newValue) {
            applyNewFolderName(newValue.getFileName().toString());
        }
    }

    private final MoviePathChangeListener moviePathChangeListener;

    //


    public DynamicMovieData(RenamingPath renamingPath, SearchResultDtoMapper mapper, FolderNameWarningNormalizer folderNameWarningNormalizer, SearchResultToMovieMapper searchResultToMovieMapper) {
        super(renamingPath, mapper, searchResultToMovieMapper, folderNameWarningNormalizer);
        this.nfoDataListener = new NfoDataListener();
        this.moviePathChangeListener = new MoviePathChangeListener();
        this.movieTitleListener = new MovieTitleListener();
        registerDefaultListeners();
        registerListeners();
    }

    protected void registerListeners() {

    }

    private void registerDefaultListeners() {
        getRenamingPath().oldPathProperty().addListener(moviePathChangeListener);
        movieTitleProperty().addListener(movieTitleListener);
        movieOriginalTitleProperty().addListener(this::movieOriginalTitleListener);
        registerMovieYearListeners();
        registerMovieTitleFromFolderListeners();
        registerMovieYearFromFolderListeners();
        getRenamingPath().fileNameProperty().addListener(this::folderNameListener);
        nfoDataProperty().addListener(nfoDataListener);

    }



    private void registerMovieYearListeners() {
        movieYearProperty().addListener(this::movieYearListener);
    }

    private void registerMovieTitleFromFolderListeners() {
        // listen for changes from folder, nfo, web
        movieTitleFromFolderProperty().addListener(this::movieTitleFromFolderListener);
        movieTitleFromNfoProperty().addListener(this::movieTitleFromNfoListener);
        movieTitleFromWebProperty().addListener(this::movieTitleFromWebListener);
    }

    private void registerMovieYearFromFolderListeners() {
        // listen for changes from folder, nfo, web
        movieYearFromFolderProperty().addListener(this::movieYearFromFolderListener);
        movieYearFromNfoProperty().addListener(this::movieYearFromNfoListener);
        movieYearFromWebProperty().addListener(this::movieYearFromWebListener);
    }

    private void movieOriginalTitleListener(ObservableValue<? extends String> observable, String oldValue, String newValue) {
        log.debug("Movie original title has changed from {} to {}", oldValue, newValue);
        updateOriginalTitleWarnings(newValue);
    }

    private void movieYearListener(ObservableValue<? extends Integer> observable, Integer oldValue, Integer newValue) {
        log.debug("Movie year has changed from {} to {}", oldValue, newValue);
        updateYearWarnings();
    }

    private void movieTitleFromFolderListener(ObservableValue<? extends String> observable, String oldValue, String newValue) {
        log.debug("Movie title from folder has changed from {} to {}", oldValue, newValue);
        if (newValue != null) {
            if (getMovieTitle() == null || getMovieTitle() != null && getMovieTitle().equals(oldValue)) {
                // no generic movie name set, set it || old value was from folder name, update to new value
                setMovieTitle(newValue);
            }
        }
    }

    private void movieTitleFromNfoListener(ObservableValue<? extends String> observable, String oldValue, String newValue) {
        log.debug("Movie title from NFO has changed from {} to {}", oldValue, newValue);
        if (StringUtils.isNotBlank(newValue)) {
            // NFO name has priority, set it in any case
            setMovieTitle(newValue);
        }
    }

    private void movieTitleFromWebListener(ObservableValue<? extends String> observable, String oldValue, String newValue) {
        log.debug("Movie name from Web has changed from {} to {}", oldValue, newValue);
        if (newValue != null) {
            // This is set manually
            setMovieTitle(newValue);
        }
    }

    private void movieYearFromFolderListener(ObservableValue<? extends Integer> observable, Integer oldValue, Integer newValue) {
        log.debug("Movie year from Web has changed from {} to {}", oldValue, newValue);
        if (newValue != null) {
            if (getMovieYear() == null || getMovieYear() != null && getMovieYear().equals(oldValue)) {
                // no generic movie name set, set it || old value was from folder name, update to new value
                setMovieYear(newValue);
            }
        }
    }

    private void movieYearFromNfoListener(ObservableValue<? extends Integer> observable, Integer oldValue, Integer newValue) {
        log.debug("Movie year from NFO has changed from {} to {}", oldValue, newValue);
        if (newValue != null) {
            // NFO year has priority, set it in any case
            setMovieYear(newValue);
        }
    }

    private void movieYearFromWebListener(ObservableValue<? extends Integer> observable, Integer oldValue, Integer newValue) {
        log.debug("Movie year from Web has changed from {} to {}", oldValue, newValue);
        if (newValue != null) {
            // this is set manually
            setMovieYear(newValue);
        }
    }

    private void folderNameListener(ObservableValue<? extends String> observable, String oldValue, String newValue) {
        // update name and year, they are both coming from the folder name
        movieTitleFromFolderProperty().set(KodiUtil.getMovieNameFromDirectoryName(newValue));
        movieYearFromFolderProperty().set(KodiUtil.getMovieYearFromDirectoryName(newValue));
    }

    @Override
    protected void initEmptyNfoData() {
        // we are writing properties to NFO. the nfoDataListener copies NFO data to the properties.
        nfoDataProperty().removeListener(nfoDataListener);
        super.initEmptyNfoData();
        nfoDataProperty().addListener(nfoDataListener);
    }

    @Override
    public void copyToNfo() {
        // we are writing properties to NFO. the nfoDataListener copies NFO data to the properties.
        nfoDataProperty().removeListener(nfoDataListener);
        super.copyToNfo();
        nfoDataProperty().addListener(nfoDataListener);
    }
}
