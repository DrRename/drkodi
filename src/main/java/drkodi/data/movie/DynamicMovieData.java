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
import drkodi.MovieSearchResultDtoMapper;
import drkodi.TvSearchResultDtoMapper;
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

    class MovieTitleFromFolderListener implements ChangeListener<String> {

        @Override
        public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
            log.debug("Movie title from folder has changed from {} to {}", oldValue, newValue);
            if (newValue != null) {
                if (getMovieTitle() == null || getMovieTitle() != null && getMovieTitle().equals(oldValue)) {
                    // no generic movie name set, set it || old value was from folder name, update to new value
                    setMovieTitle(newValue);
                }
            }
        }
    }
    private final MovieTitleFromFolderListener movieTitleFromFolderListener;

    class MovieTitleFromNfoListener implements ChangeListener<String> {

        @Override
        public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
            if (StringUtils.isNotBlank(newValue))
                // NFO name has priority, set it in any case
                setMovieTitle(newValue);
        }
    }
    private final MovieTitleFromNfoListener movieTitleFromNfoListener;

    class MovieOriginalTitleListener implements ChangeListener<String> {
        @Override
        public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
            log.debug("Movie original title has changed from {} to {}", oldValue, newValue);
            if(StringUtils.isNotBlank(newValue))
                updateOriginalTitleWarnings(newValue);
        }
    }
    private final MovieOriginalTitleListener movieOriginalTitleListener;

    class MovieTitleFromWebListener implements ChangeListener<String> {
        @Override
        public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
            log.debug("Movie title from web has changed from {} to {}", oldValue, newValue);
            if (StringUtils.isNotBlank(newValue)) {
                // NFO name has priority, set it in any case
                setMovieTitle(newValue);
            }
        }
    }
    private final MovieTitleFromWebListener movieTitleFromWebListener;

    class MovieYearListener implements ChangeListener<Integer> {
        @Override
        public void changed(ObservableValue<? extends Integer> observable, Integer oldValue, Integer newValue) {
            log.debug("Movie year has changed from {} to {}", oldValue, newValue);
            updateYearWarnings();
        }
    }
    private final MovieYearListener movieYearListener;

    class MovieYearFromFolderListener implements ChangeListener<Integer> {
        @Override
        public void changed(ObservableValue<? extends Integer> observable, Integer oldValue, Integer newValue) {
            log.debug("Movie year from Folder has changed from {} to {}", oldValue, newValue);
            // NFO year has priority, set it in any case
            setMovieYear(newValue);
        }
    }
    private final MovieYearFromFolderListener movieYearFromFolderListener;

    class MovieYearFromNfoListener implements ChangeListener<Integer> {
        @Override
        public void changed(ObservableValue<? extends Integer> observable, Integer oldValue, Integer newValue) {
            log.debug("Movie year from NFO has changed from {} to {}", oldValue, newValue);
            if (newValue != null)
                // NFO year has priority, set it in any case
                setMovieYear(newValue);
        }
    }
    private final MovieYearFromNfoListener movieYearFromNfoListener;

    class MovieYearFromWebListener implements ChangeListener<Integer> {
        @Override
        public void changed(ObservableValue<? extends Integer> observable, Integer oldValue, Integer newValue) {
            log.debug("Movie year from web has changed from {} to {}", oldValue, newValue);
            setMovieYear(newValue);
        }
    }
    private final MovieYearFromWebListener movieYearFromWebListener;

    class FolderNameListener implements ChangeListener<String> {

        @Override
        public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
            // update name and year, they are both coming from the folder name
            movieTitleFromFolderProperty().set(KodiUtil.getMovieNameFromDirectoryName(newValue));
            movieYearFromFolderProperty().set(KodiUtil.getMovieYearFromDirectoryName(newValue));
        }
    }
    private final FolderNameListener folderNameListener;

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


    public DynamicMovieData(RenamingPath renamingPath, MovieSearchResultDtoMapper mapper, FolderNameWarningNormalizer folderNameWarningNormalizer, SearchResultToMovieMapper searchResultToMovieMapper, TvSearchResultDtoMapper tvSearchResultDtoMapper) {
        super(renamingPath, mapper, tvSearchResultDtoMapper, searchResultToMovieMapper, folderNameWarningNormalizer);
        this.folderNameListener = new FolderNameListener();
        this.nfoDataListener = new NfoDataListener();
        this.moviePathChangeListener = new MoviePathChangeListener();
        this.movieTitleListener = new MovieTitleListener();
        this.movieTitleFromFolderListener = new MovieTitleFromFolderListener();
        this.movieTitleFromNfoListener = new MovieTitleFromNfoListener();
        this.movieTitleFromWebListener = new MovieTitleFromWebListener();
        this.movieOriginalTitleListener = new MovieOriginalTitleListener();
        this.movieYearListener = new MovieYearListener();
        this.movieYearFromNfoListener = new MovieYearFromNfoListener();
        this.movieYearFromFolderListener = new MovieYearFromFolderListener();
        this.movieYearFromWebListener = new MovieYearFromWebListener();
        registerDefaultListeners();
        registerListeners();
    }

    protected void registerListeners() {

    }

    private void registerDefaultListeners() {
        getRenamingPath().fileNameProperty().addListener(folderNameListener);
        nfoDataProperty().addListener(nfoDataListener);
        getRenamingPath().oldPathProperty().addListener(moviePathChangeListener);
        movieTitleProperty().addListener(movieTitleListener);
        movieTitleFromFolderProperty().addListener(movieTitleFromFolderListener);
        movieTitleFromNfoProperty().addListener(movieTitleFromNfoListener);
        movieTitleFromWebProperty().addListener(movieTitleFromWebListener);
        movieOriginalTitleProperty().addListener(movieOriginalTitleListener);
        movieYearProperty().addListener(movieYearListener);
        movieYearFromNfoProperty().addListener(movieYearFromNfoListener);
        movieYearFromFolderProperty().addListener(movieYearFromFolderListener);
        movieYearFromWebProperty().addListener(movieYearFromWebListener);
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
