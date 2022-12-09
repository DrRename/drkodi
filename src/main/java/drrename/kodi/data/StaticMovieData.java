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
import drrename.kodi.KodiUtil;
import drrename.kodi.MovieDbGenre;
import drrename.kodi.NfoMovie;
import drrename.kodi.NfoRoot;
import drrename.kodi.data.json.SearchResultDto;
import drrename.kodi.data.json.WebSearchResults;
import drrename.kodi.data.json.TranslationDto;
import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.image.Image;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.nio.file.Path;

@Slf4j
public class StaticMovieData {

    private final SearchResultMapper mapper;

    // Underlying path

    private final RenamingPath renamingPath;

    //

    // Final properties //

    private final StringProperty movieTitle;

    private final ObjectProperty<Integer> movieYear;

    //

    // Properties from web //

    private final StringProperty movieTitleFromWeb;

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

    public StaticMovieData(RenamingPath renamingPath, SearchResultMapper mapper) {
        this.renamingPath = renamingPath;
        this.mapper = mapper;
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

        initWebProperties();



    }


    private void initMovieTitleAndMovieYear() {

        // init from folder name
        setMovieTitleFromFolder(KodiUtil.getMovieNameFromDirectoryName(renamingPath.getFileName()));
        setMovieYearFromFolder(KodiUtil.getMovieYearFromDirectoryName(renamingPath.getFileName()));

        // init title and year from folder
        setMovieTitle(getMovieTitleFromFolder());
        setMovieYear(getMovieYearFromFolder());
    }




    @Deprecated
    private void initWebProperties() {

        // Web data changed, update all properties
        webSearchResult.addListener(new ChangeListener<WebSearchResults>() {
            @Override
            public void changed(ObservableValue<? extends WebSearchResults> observable, WebSearchResults oldValue, WebSearchResults newValue) {
                if (newValue != null) {
                    for (Number id : newValue.getSearchResults().keySet()) {
                        SearchResultDto searchResultDto = newValue.getSearchResults().get(id);
                        TranslationDto translationDto = newValue.getTranslations().get(id);
                        Image image = newValue.getImages().get(id);
                        byte[] imageData = newValue.getImageData().get(id);

                        SearchResult searchResult = mapper.map(searchResultDto, imageData, image);
                        getSearchResults().add(searchResult);

                        if (translationDto != null && StringUtils.isNotBlank(translationDto.getData().getTitle())) {
                            searchResult = new SearchResult(searchResult);
                            searchResult.setTitle(translationDto.getData().getTitle());
                            getSearchResults().add(searchResult);
                        }
                    }
                } else {
                    getSearchResults().clear();
                }
            }
        });
    }

    public void takeOverSearchResultData(SearchResult searchResult) {
        log.debug("Taking over data for {} from {}", getMovieTitle(), searchResult);
        setMovieDbId(searchResult.getId());

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

    private void initEmptyNfoData() {
        NfoRoot data = new NfoRoot();
        data.setMovie(new NfoMovie());
        data.getMovie().setArt(new NfoMovie.Art());
        setNfoData(QualifiedNfoData.from(data));
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
        return isDataLoadingComplete() && getPlot() != null && Qualified.isOk(getImagePath());
    }

    public boolean isDataLoadingComplete(){
        return getNfoData() != null;
    }

    public boolean isDetailsComplete(){
        return !getGenres().isEmpty() && StringUtils.isNotBlank(getPlot());
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


}
