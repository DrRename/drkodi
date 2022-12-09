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

package drrename.kodi.data.dynamic;

import drrename.RenamingPath;
import drrename.kodi.KodiUtil;
import drrename.kodi.NfoRoot;
import drrename.kodi.data.QualifiedNfoData;
import drrename.kodi.data.QualifiedPath;
import drrename.kodi.data.KodiWarning;
import drrename.SearchResultMapper;
import drrename.kodi.data.StaticMovieData;
import drrename.kodi.nfo.NfoUtil;
import javafx.beans.value.ObservableValue;
import lombok.extern.slf4j.Slf4j;

import java.nio.file.Path;
import java.util.Optional;

@Slf4j
public class DynamicMovieData extends StaticMovieData {

    public DynamicMovieData(RenamingPath renamingPath, SearchResultMapper mapper) {
        super(renamingPath, mapper);
        registerDefaultListeners();
        registerListeners();
    }

    protected void registerListeners() {

    }

    private void registerDefaultListeners() {
        registerMovieTitleListeners();
        registerMovieYearListeners();
        registerMovieTitleFromFolderListeners();
        registerMovieYearFromFolderListeners();
        registerFolderNameListener();
        initNfoDataListener();

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

    private void registerFolderNameListener() {
        // listen for renaming changes
        getRenamingPath().fileNameProperty().addListener(this::folderNameListener);
    }

    private void initNfoDataListener() {

        // NFO data changed, update all properties
        nfoDataProperty().addListener(this::nfoDataListener);
    }



    private void movieTitleListener(ObservableValue<? extends String> observable, String oldValue, String newValue) {
        log.debug("Movie title has changed from {} to {}", oldValue, newValue);
        updateTitleWarnings(newValue);
    }

    private void movieYearListener(ObservableValue<? extends Integer> observable, Integer oldValue, Integer newValue) {
        log.debug("Movie year has changed from {} to {}", oldValue, newValue);
        updateYearWarnings(newValue);
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
        if (newValue != null) {
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

    private void nfoDataListener(ObservableValue<? extends QualifiedNfoData> observable, QualifiedNfoData oldValue, QualifiedNfoData newValue){
        if (writingToNfo) {
            log.debug("Writing data, will not load data from NFO");
            return;
        }
        log.debug("Setting NFO data to {}", newValue);
        setMovieTitleFromNfo(NfoUtil.getMovieTitle(newValue.getElement()));
        setMovieYearFromNfo(NfoUtil.getMovieYear(newValue.getElement()));
        Optional.ofNullable(NfoUtil.getGenres(newValue.getElement())).ifPresent(g -> getGenres().addAll(g));
        setPlot(NfoUtil.getPlot(newValue.getElement()));
        setTagline(NfoUtil.getTagline(newValue.getElement()));
        Path path = NfoUtil.getImagePath(getRenamingPath().getOldPath(), newValue.getElement());
        setImagePath(QualifiedPath.from(path));
    }



    private void updateTitleWarnings(String newMovieTitle) {
        log.debug("Recreating title warnings");
        // first, clear/ filter out title warnings
        getWarnings().removeIf(w -> KodiWarning.Type.TITLE_MISMATCH.equals(w.getType()));
        // next, create new warnings if necessary
        String folderValue = getMovieTitleFromFolder();
        String currentValue = getMovieTitle();
        if (folderValue != null && currentValue != null && !folderValue.equals(currentValue)) {
            getWarnings().add(new KodiWarning(KodiWarning.Type.TITLE_MISMATCH));
        }
    }

    private void updateYearWarnings(Integer newMovieYear) {
        log.debug("Recreating year warnings");
        // filter out year warnings
        getWarnings().removeIf(w -> KodiWarning.Type.YEAR_MISMATCH.equals(w.getType()));
        // next, create new warnings if necessary
        Integer folderValue = getMovieYearFromFolder();
        Integer currentValue = getMovieYear();
        if (folderValue != null && currentValue != null && !folderValue.equals(currentValue)) {
            getWarnings().add(new KodiWarning(KodiWarning.Type.YEAR_MISMATCH));
        }
    }

}
