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


import drrename.SearchResultDtoMapper;
import drrename.commons.RenamingPath;
import drrename.kodi.KodiUtil;
import drrename.kodi.data.*;
import drrename.kodi.data.json.SearchResultDto;
import drrename.kodi.data.json.TranslationDto;
import drrename.kodi.data.json.WebSearchResults;
import drrename.kodi.nfo.NfoUtil;
import javafx.beans.value.ObservableValue;
import javafx.scene.image.Image;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.nio.file.Path;
import java.util.Optional;

@Slf4j
public class DynamicMovieData extends StaticMovieData {

    public DynamicMovieData(RenamingPath renamingPath, SearchResultDtoMapper mapper, FolderNameCompareNormalizer folderNameCompareNormalizer, SearchResultToMovieMapper searchResultToMovieMapper) {
        super(renamingPath, mapper, searchResultToMovieMapper, folderNameCompareNormalizer);
        registerDefaultListeners();
        registerListeners();
    }

    protected void registerListeners() {

    }

    private void registerDefaultListeners() {
        registerRenamingPathListeners();
        registerMovieTitleListeners();
        registerMovieYearListeners();
        registerMovieTitleFromFolderListeners();
        registerMovieYearFromFolderListeners();
        registerFolderNameListener();
        registerNfoDataListener();
        registerWebSearchResultsListener();

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

    private void registerFolderNameListener() {
        // listen for renaming changes
        getRenamingPath().fileNameProperty().addListener(this::folderNameListener);
    }

    private void registerNfoDataListener() {

        // NFO data changed, update all properties
        nfoDataProperty().addListener(this::nfoDataListener);
    }

    private void registerWebSearchResultsListener() {
        // Web data changed, update all properties
        webSearchResultProperty().addListener(this::webSearchResultListener);
    }

    private void webSearchResultListener(ObservableValue<? extends WebSearchResults> observable, WebSearchResults oldValue, WebSearchResults newValue) {
        if (newValue != null) {
            for (Number id : newValue.getSearchResults().keySet()) {
                SearchResultDto searchResultDto = newValue.getSearchResults().get(id);
                TranslationDto translationDto = newValue.getTranslations().get(id);
                Image image = newValue.getImages().get(id);
                byte[] imageData = newValue.getImageData().get(id);

                SearchResult searchResult = getMapper().map(searchResultDto, imageData, image);
                getSearchResults().add(searchResult);

                if (StringUtils.isNotBlank(searchResultDto.getOriginalTitle()) && !searchResultDto.getOriginalTitle().equalsIgnoreCase(searchResult.getTitle())) {
                    SearchResult originalTitleCopy = getMapper().map(searchResultDto, imageData, image);
                    originalTitleCopy.setTitle(searchResultDto.getOriginalTitle());
                    getSearchResults().add(originalTitleCopy);
                }

                if (translationDto != null && StringUtils.isNotBlank(translationDto.getData().getTitle())) {
                    searchResult = new SearchResult(searchResult);
                    searchResult.setTitle(translationDto.getData().getTitle());
                    searchResult.setPlot(translationDto.getData().getOverview());
                    searchResult.setTagline(translationDto.getData().getTagline());
                    getSearchResults().add(searchResult);
                }
            }
        } else {
            getSearchResults().clear();
        }

    }


    private void movieTitleListener(ObservableValue<? extends String> observable, String oldValue, String newValue) {
        log.debug("Movie title has changed from {} to {}", oldValue, newValue);
        updateTitleWarnings();
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
        Optional.ofNullable(NfoUtil.getGenres(newValue.getElement())).ifPresent(g -> getGenres().setAll(g));
        setPlot(NfoUtil.getPlot(newValue.getElement()));
        setTagline(NfoUtil.getTagline(newValue.getElement()));
        Path path = NfoUtil.getImagePath(getRenamingPath().getOldPath(), newValue.getElement());
        setImagePath(QualifiedPath.from(path));
        setMovieDbId(NfoUtil.getId2(newValue.getElement()));
    }


}
