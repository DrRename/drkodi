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
import drkodi.data.json.WebSearchResults;
import drkodi.normalization.FolderNameCompareNormalizer;
import drrename.commons.RenamingPath;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.image.Image;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.nio.file.Path;

@Slf4j
public class StaticMovieData {

    private final SearchResultDtoMapper searchResultDtoMapper;

    private final SearchResultToMovieMapper searchResultToMovieMapper;

    private final FolderNameCompareNormalizer folderNameCompareNormalizer;

    // Underlying path

    private final RenamingPath renamingPath;

    //

    // Final properties //

    private final StringProperty movieTitle;

    private final ObjectProperty<Integer> movieYear;

    //

    // Properties from web //

    private final StringProperty movieTitleFromWeb;

    private final StringProperty movieOriginalTitle;

    private final ObjectProperty<Integer> movieYearFromWeb;

    private final ObjectProperty<Image> image;

    private final ObjectProperty<ImageData> imageData;

    private final ObjectProperty<QualifiedPath> imagePath;

    private final ObjectProperty<Number> movieDbId;

    //

    // Properties from NFO //

    private final StringProperty movieTitleFromNfo;

    private final ObjectProperty<Integer> movieYearFromNfo;

    private final StringProperty plot;

    private final StringProperty tagline;

    private final ListProperty<MovieDbGenre> genres;

    //

    // Properties from folder

    private final StringProperty movieTitleFromFolder;

    private final ObjectProperty<Integer> movieYearFromFolder;

    //

    private final ObjectProperty<QualifiedNfoData> nfoData;

    private final ObjectProperty<QualifiedPath> nfoPath;


    /**
     * TODO: Do not store json data here. After receiving it, immediately transform to {@link SearchResult}.
     */
    @Deprecated
    private final ObjectProperty<WebSearchResults> webSearchResult;

    private final ListProperty<KodiWarning> warnings;

    private final ListProperty<SearchResult> searchResults;

    protected boolean writingToNfo = false;

    public StaticMovieData(RenamingPath renamingPath, SearchResultDtoMapper searchResultDtoMapper, SearchResultToMovieMapper searchResultToMovieMapper, FolderNameCompareNormalizer folderNameCompareNormalizer) {
        this.renamingPath = renamingPath;
        this.searchResultDtoMapper = searchResultDtoMapper;
        this.searchResultToMovieMapper = searchResultToMovieMapper;
        this.folderNameCompareNormalizer = folderNameCompareNormalizer;
        this.movieTitleFromFolder = new SimpleStringProperty();
        this.movieTitleFromNfo = new SimpleStringProperty();
        this.nfoPath = new SimpleObjectProperty<>();
        this.nfoData = new SimpleObjectProperty<>();
        this.webSearchResult = new SimpleObjectProperty<>();
        this.movieYearFromFolder = new SimpleObjectProperty<>();
        this.movieYearFromNfo = new SimpleObjectProperty<>();
        this.movieTitle = new SimpleStringProperty();
        this.movieYear = new SimpleObjectProperty<>();
        this.movieTitleFromWeb = new SimpleStringProperty();
        this.movieOriginalTitle = new SimpleStringProperty();
        this.movieYearFromWeb = new SimpleObjectProperty<>();
        this.image = new SimpleObjectProperty<>();
        this.imageData = new SimpleObjectProperty<>();
        this.imagePath = new SimpleObjectProperty<>();
        this.movieDbId = new SimpleObjectProperty<>();
        this.plot = new SimpleStringProperty();
        this.tagline = new SimpleStringProperty();
        this.genres = new SimpleListProperty<>(FXCollections.observableArrayList());
        this.warnings = new SimpleListProperty<>(FXCollections.observableArrayList());
        this.searchResults = new SimpleListProperty<>(FXCollections.observableArrayList());
        init();
    }

    private void init() {
        initMovieTitleAndMovieYear();
    }

    protected void initMovieTitleAndMovieYear() {
        // init from folder name
        applyNewFolderName(getRenamingPath().getFileName());
    }

    public void applyNewFolderName(String folderName) {
        log.debug("Setting properties from new folder name {}", folderName);
        setMovieTitleFromFolder(KodiUtil.getMovieNameFromDirectoryName(folderName));
        setMovieYearFromFolder(KodiUtil.getMovieYearFromDirectoryName(folderName));
        setMovieTitle(getMovieTitleFromFolder());
        setMovieYear(getMovieYearFromFolder());
        updateTitleWarnings(getMovieTitleFromFolder());
        updateYearWarnings();
        if (Qualified.isOk(getNfoPath())) {
            String currentNfoFileName = getNfoPath().getElement().getFileName().toString();
            Path path = Path.of(getRenamingPath().getOldPath().toString(), currentNfoFileName);
            log.debug("Updating NFO path to {}", path);
            setNfoPath(QualifiedPath.from(path));

        } else {
            log.debug("NFO path invalid, will not update");
        }

    }

    protected void updateTitleWarnings(String newValue) {
        log.debug("Recreating title warnings");
        // first, clear/ filter out title warnings
        getWarnings().removeIf(w -> KodiWarning.Type.TITLE_MISMATCH.equals(w.type()));
        // next, create new warnings if necessary
        String normalizedTitle = getFolderNameCompareNormalizer().normalize(newValue);
        String normalizedOriginalTitle = getFolderNameCompareNormalizer().normalize(getMovieOriginalTitle());
        check(newValue, normalizedTitle, getMovieOriginalTitle(), normalizedOriginalTitle);
    }

    protected void updateOriginalTitleWarnings(String newValue) {
        log.debug("Recreating original title warnings");
        // first, clear/ filter out title warnings
        getWarnings().removeIf(w -> KodiWarning.Type.TITLE_MISMATCH.equals(w.type()));
        // next, create new warnings if necessary
        String normalizedTitle = getFolderNameCompareNormalizer().normalize(getMovieTitle());
        String normalizedOriginalTitle = getFolderNameCompareNormalizer().normalize(newValue);
        check(getMovieTitle(), normalizedTitle, newValue, normalizedOriginalTitle);
    }

    private void check(String title, String normalizedTitle, String originalTitle, String normalizedOriginalTitle) {
        String folderName = getMovieTitleFromFolder();
        // TODO: make case sensitive equals configurable
        if (folderName.equalsIgnoreCase(title)
                || folderName.equalsIgnoreCase(normalizedTitle)
                || folderName.equalsIgnoreCase(originalTitle)
                || folderName.equalsIgnoreCase(normalizedOriginalTitle)) {
                // ok
                return;
        }

        log.debug("Title mismatch, folder: {}, title: {}, original title {}", folderName, normalizedTitle, normalizedOriginalTitle);
        getWarnings().add(new KodiWarning(KodiWarning.Type.TITLE_MISMATCH));
    }

    private boolean movieTitleMismatch(String folderName, String normalizedTitle) {
        return !folderName.equalsIgnoreCase(normalizedTitle) && !folderName.equalsIgnoreCase(getMovieTitle());
    }

    protected void updateYearWarnings() {
        log.debug("Recreating year warnings");
        // filter out year warnings
        getWarnings().removeIf(w -> KodiWarning.Type.YEAR_MISMATCH.equals(w.type()));
        // next, create new warnings if necessary
        Integer folderValue = getMovieYearFromFolder();
        Integer currentValue = getMovieYear();
        if (folderValue != null && currentValue != null && !folderValue.equals(currentValue)) {
            getWarnings().add(new KodiWarning(KodiWarning.Type.YEAR_MISMATCH));
        }
    }


    public void takeOverSearchResultData(SearchResult searchResult) {
        log.debug("Taking over data for {} from {}", getMovieTitle(), searchResult);
        searchResultToMovieMapper.map(this, searchResult);

    }


    public void copyToNfo() {
        this.writingToNfo = true;
        if (!Qualified.isOk(getNfoData())) {
            initEmptyNfoData();
        }
        if (!Qualified.isOk(getImagePath())) {
            setDefaultImagePath();
        }
        if (!Qualified.isOk(getNfoPath())) {
            setDefaultNfoPath();
        }
        if (getNfoData().getElement().getMovie() == null) {
            getNfoData().getElement().setMovie(new NfoMovie());
        }
        if (getNfoData().getElement().getMovie().getArt() == null) {
            getNfoData().getElement().getMovie().setArt(new NfoMovie.Art());
        }
        getNfoData().getElement().getMovie().setUniqueid(new NfoMovie.UniqueId(getMovieDbId(), "tmdb"));
        getNfoData().getElement().getMovie().setTitle(getMovieTitle());
        getNfoData().getElement().getMovie().setOriginaltitle(getMovieOriginalTitle());
        if(getMovieYear() != null)
            getNfoData().getElement().getMovie().setYear(getMovieYear().toString());
        getNfoData().getElement().getMovie().setPlot(getPlot());
        getNfoData().getElement().getMovie().setGenre(getGenres().stream().map(MovieDbGenre::getName).toList());
        getNfoData().getElement().getMovie().setTagline(getTagline());
        getNfoData().getElement().setUrl(getUrl());
        getNfoData().getElement().getMovie().getArt().setPoster(getImagePath().getElement().getFileName().toString());

        this.writingToNfo = false;
    }

    private String getUrl() {
        // for now, URL is always TheMovieDB
        return "https://www.themoviedb.org/movie/" + getMovieDbId();
    }

    protected void initEmptyNfoData() {
        writingToNfo = true;
        NfoRoot data = new NfoRoot();
        data.setMovie(new NfoMovie());
        data.getMovie().setArt(new NfoMovie.Art());
        setNfoData(QualifiedNfoData.from(data));
        writingToNfo = false;
    }

    protected void setDefaultImagePath() {
        Path defaultImagePath = KodiUtil.getDefaultImagePath(this);
        log.debug("Setting default image path ({})", defaultImagePath);
        // TODO: set only image path? Update only NFO? Listeners?
        setImagePath(new QualifiedPath(defaultImagePath, QualifiedPath.Type.OK));
//        getNfoData().getMovie().getArt().setPoster(getImagePath().getFileName().toString());
    }

    private void setDefaultNfoPath() {
        Path defaultPath = KodiUtil.getDefaultNfoPath(this);
        log.debug("Setting default NFO path ({})", defaultPath);
        setNfoPath(QualifiedPath.from(defaultPath));
    }

    public boolean isDataComplete() {
        return isDataLoadingComplete() && Qualified.isOk(getImagePath()) && StringUtils.isNotBlank(getMovieOriginalTitle());
    }

    public boolean isDataLoadingComplete(){
        return getNfoData() != null;
    }

    public boolean isDetailsComplete(){
        return !getGenres().isEmpty();
    }


    @Override
    public String toString() {
        return "StaticMovieData{" +
                "renamingPath=" + renamingPath +
                '}';
    }
// Getter / Setter //

    public RenamingPath getRenamingPath() {
        return renamingPath;
    }

    public SearchResultDtoMapper getSearchResultDtoMapper() {
        return searchResultDtoMapper;
    }

    public FolderNameCompareNormalizer getFolderNameCompareNormalizer() {
        return folderNameCompareNormalizer;
    }

    // FX Getter / Setter //


    public String getMovieTitleFromFolder() {
        return movieTitleFromFolder.get();
    }

    public StringProperty movieTitleFromFolderProperty() {
        return movieTitleFromFolder;
    }

    public void setMovieTitleFromFolder(String movieTitleFromFolder) {
        this.movieTitleFromFolder.set(movieTitleFromFolder);
    }

    public String getMovieTitleFromNfo() {
        return movieTitleFromNfo.get();
    }

    public StringProperty movieTitleFromNfoProperty() {
        return movieTitleFromNfo;
    }

    public void setMovieTitleFromNfo(String movieTitleFromNfo) {
        this.movieTitleFromNfo.set(movieTitleFromNfo);
    }

    public QualifiedNfoData getNfoData() {
        return nfoData.get();
    }

    public ObjectProperty<QualifiedNfoData> nfoDataProperty() {
        return nfoData;
    }

    public void setNfoData(QualifiedNfoData nfoData) {
        this.nfoData.set(nfoData);
    }

    public WebSearchResults getWebSearchResult() {
        return webSearchResult.get();
    }

    public ObjectProperty<WebSearchResults> webSearchResultProperty() {
        return webSearchResult;
    }

    public void setWebSearchResult(WebSearchResults webSearchResult) {
        this.webSearchResult.set(webSearchResult);
    }

    public Integer getMovieYearFromFolder() {
        return movieYearFromFolder.get();
    }

    public ObjectProperty<Integer> movieYearFromFolderProperty() {
        return movieYearFromFolder;
    }

    public Integer getMovieYearFromNfo() {
        return movieYearFromNfo.get();
    }

    public ObjectProperty<Integer> movieYearFromNfoProperty() {
        return movieYearFromNfo;
    }

    public void setMovieYearFromFolder(Integer movieYearFromFolder) {
        this.movieYearFromFolder.set(movieYearFromFolder);
    }

    public void setMovieYearFromNfo(Integer movieYearFromNfo) {
        this.movieYearFromNfo.set(movieYearFromNfo);
    }

    public ObservableList<KodiWarning> getWarnings() {
        return warnings.get();
    }

    public ListProperty<KodiWarning> warningsProperty() {
        return warnings;
    }

    public void setWarnings(ObservableList<KodiWarning> warnings) {
        this.warnings.set(warnings);
    }

    public QualifiedPath getNfoPath() {
        return nfoPath.get();
    }

    public ObjectProperty<QualifiedPath> nfoPathProperty() {
        return nfoPath;
    }

    public void setNfoPath(QualifiedPath nfoPath) {
        this.nfoPath.set(nfoPath);
    }

    public String getMovieTitle() {
        return movieTitle.get();
    }

    public StringProperty movieTitleProperty() {
        return movieTitle;
    }

    public void setMovieTitle(String movieTitle) {
        this.movieTitle.set(movieTitle);
    }

    public Integer getMovieYear() {
        return movieYear.get();
    }

    public ObjectProperty<Integer> movieYearProperty() {
        return movieYear;
    }

    public void setMovieYear(Integer movieYear) {
        this.movieYear.set(movieYear);
    }

    public String getMovieTitleFromWeb() {
        return movieTitleFromWeb.get();
    }

    public StringProperty movieTitleFromWebProperty() {
        return movieTitleFromWeb;
    }

    public void setMovieTitleFromWeb(String movieTitleFromWeb) {
        this.movieTitleFromWeb.set(movieTitleFromWeb);
    }

    public Integer getMovieYearFromWeb() {
        return movieYearFromWeb.get();
    }

    public ObjectProperty<Integer> movieYearFromWebProperty() {
        return movieYearFromWeb;
    }

    public void setMovieYearFromWeb(Integer movieYearFromWeb) {
        this.movieYearFromWeb.set(movieYearFromWeb);
    }

    public ObservableList<SearchResult> getSearchResults() {
        return searchResults.get();
    }

    public ListProperty<SearchResult> searchResultsProperty() {
        return searchResults;
    }

    public void setSearchResults(ObservableList<SearchResult> searchResults) {
        this.searchResults.set(searchResults);
    }

    public Image getImage() {
        return image.get();
    }

    public ObjectProperty<Image> imageProperty() {
        return image;
    }

    public void setImage(Image image) {
        this.image.set(image);
    }

    public Number getMovieDbId() {
        return movieDbId.get();
    }

    public ObjectProperty<Number> movieDbIdProperty() {
        return movieDbId;
    }

    public void setMovieDbId(Number movieDbId) {
        this.movieDbId.set(movieDbId);
    }

    public ImageData getImageData() {
        return imageData.get();
    }

    public ObjectProperty<ImageData> imageDataProperty() {
        return imageData;
    }

    public void setImageData(ImageData imageData) {
        this.imageData.set(imageData);
    }

    public QualifiedPath getImagePath() {
        return imagePath.get();
    }

    public ObjectProperty<QualifiedPath> imagePathProperty() {
        return imagePath;
    }

    public void setImagePath(QualifiedPath qualifiedPath) {
        this.imagePath.set(qualifiedPath);
    }

    public String getPlot() {
        return plot.get();
    }

    public StringProperty plotProperty() {
        return plot;
    }

    public void setPlot(String plot) {
        this.plot.set(plot);
    }

    public ObservableList<MovieDbGenre> getGenres() {
        return genres.get();
    }

    public ListProperty<MovieDbGenre> genresProperty() {
        return genres;
    }

    public void setGenres(ObservableList<MovieDbGenre> genres) {
        this.genres.set(genres);
    }

    public String getTagline() {
        return tagline.get();
    }

    public StringProperty taglineProperty() {
        return tagline;
    }

    public void setTagline(String tagline) {
        this.tagline.set(tagline);
    }

    public String getMovieOriginalTitle() {
        return movieOriginalTitle.get();
    }

    public StringProperty movieOriginalTitleProperty() {
        return movieOriginalTitle;
    }

    public void setMovieOriginalTitle(String movieOriginalTitle) {
        this.movieOriginalTitle.set(movieOriginalTitle);
    }
}
