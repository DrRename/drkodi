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

package drkodi.data.dynamic;


import drkodi.KodiUtil;
import drkodi.SearchResultDtoMapper;
import drkodi.data.MovieData;
import drkodi.data.SearchResult;
import drkodi.data.SearchResultToMovieMapper;
import drkodi.data.json.SearchResultDto;
import drkodi.data.json.TranslationDto;
import drkodi.data.json.WebSearchResults;
import drkodi.normalization.FolderNameWarningNormalizer;
import drrename.commons.RenamingPath;
import javafx.beans.value.ObservableValue;
import javafx.scene.image.Image;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.nio.file.Path;

@Slf4j
public class DynamicMovieData extends MovieData {

    private NfoDataListener nfoDataListener;

    public DynamicMovieData(RenamingPath renamingPath, SearchResultDtoMapper mapper, FolderNameWarningNormalizer folderNameWarningNormalizer, SearchResultToMovieMapper searchResultToMovieMapper) {
        super(renamingPath, mapper, searchResultToMovieMapper, folderNameWarningNormalizer);
        this.nfoDataListener = new NfoDataListener(this);
        registerDefaultListeners();
        registerListeners();
    }

    protected void registerListeners() {

    }

    private void registerDefaultListeners() {
        registerRenamingPathListeners();
        registerMovieTitleListeners();
        movieOriginalTitleProperty().addListener(this::movieOriginalTitleListener);
        registerMovieYearListeners();
        registerMovieTitleFromFolderListeners();
        registerMovieYearFromFolderListeners();
        getRenamingPath().fileNameProperty().addListener(this::folderNameListener);
        nfoDataProperty().addListener(nfoDataListener);

    }

    private void registerRenamingPathListeners() {
        getRenamingPath().oldPathProperty().addListener(this::oldPathListener);
    }

    private void oldPathListener(ObservableValue<? extends Path> observable, Path oldValue, Path newValue) {
        applyNewFolderName(newValue.getFileName().toString());
    }

    private void registerMovieTitleListeners() {
        movieTitleProperty().addListener(this::movieTitleListener);
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


    private void movieTitleListener(ObservableValue<? extends String> observable, String oldValue, String newValue) {
        log.debug("Movie title has changed from {} to {}", oldValue, newValue);
        updateTitleWarnings(newValue);
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
