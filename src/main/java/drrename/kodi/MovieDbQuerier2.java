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

package drrename.kodi;

import drrename.config.TheMovieDbConfig;
import drrename.kodi.data.json.WebSearchResults;
import drrename.kodi.data.json.SearchResultDto;
import drrename.kodi.data.json.TranslationDto;
import drrename.kodi.themoviedb.TranslationsDto;
import drrename.util.DrRenameUtil;
import javafx.scene.image.Image;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;


@Slf4j
@Component
@Scope("prototype")
public class MovieDbQuerier2 {


    private final MovieDbClient client;

    private final MovieDbImagesClient imagesClient;

    private final TheMovieDbConfig config;

    private final ResourceBundle resourceBundle;

    public MovieDbQuerier2(MovieDbClient client, MovieDbImagesClient imagesClient, TheMovieDbConfig config, ResourceBundle resourceBundle) {
        this.client = client;
        this.imagesClient = imagesClient;
        this.config = config;
        this.resourceBundle = resourceBundle;
    }

    protected void reset() {

    }

    public MovieDbDetails query(Number movieDbId){
        MovieDbDetails result = new MovieDbDetails();
        try {
            ResponseEntity<MovieDetailsDto> details = client.getDetails("ca540140c89af81851d4026286942896", movieDbId, null);
            if(details.getBody() != null) {
                result.genres = details.getBody().genres.stream().map(e -> new MovieDbGenre(e.id, e.name)).toList();
                result.taline = details.getBody().taline;
                result.overview = details.getBody().overview;
                result.title = details.getBody().getTitle();
                result.overview = details.getBody().getOverview();
                result.plot = details.getBody().getPlot();
                if(details.getBody().getReleaseDate() != null) {
                    result.releaseDate = details.getBody().getReleaseDate().getYear();
                }
                if(details.getBody().getPosterPath() != null) {
                    var imageData = imagesClient.searchMovie("ca540140c89af81851d4026286942896", null, config.isIncludeAdult(), details.getBody().getPosterPath());
                    if (imageData.getBody() != null) {
                        Image image = new Image(new ByteArrayInputStream(imageData.getBody()));
                        image.exceptionProperty().addListener((observable, oldValue, newValue) -> {
                            if (newValue != null)
                                log.error(newValue.getLocalizedMessage(), newValue);
                        });
                        result.image = image;
                        result.imageData = imageData.getBody();
                    }
                }

            }
        }catch (Exception e){
            log.error("Failed to get details for ID", e);
        }
        return result;
    }

    public WebSearchResults query(String searchString, Integer year) throws IOException {
        reset();
        WebSearchResults result = new WebSearchResults();

        var searchResult = client.searchMovie("ca540140c89af81851d4026286942896", null, config.isIncludeAdult(), searchString, null);

        if (searchResult.getBody() == null || searchResult.getBody().getResults().isEmpty()) return result;

        List<SearchResultDto> subList = DrRenameUtil.getSubList(searchResult.getBody().getResults(), config.getNumberOfMaxSuggestions());

        subList.forEach(dto -> result.getSearchResults().put(dto.getId(), dto));

        for (SearchResultDto searchResultDto : subList) {

            if(searchResultDto.getPosterPath() != null) {
                var imageData = imagesClient.searchMovie("ca540140c89af81851d4026286942896", null, config.isIncludeAdult(), searchResultDto.getPosterPath());
                if (imageData.getBody() != null) {
                    Image image = new Image(new ByteArrayInputStream(imageData.getBody()));
                    image.exceptionProperty().addListener((observable, oldValue, newValue) -> {
                        if (newValue != null)
                            log.error(newValue.getLocalizedMessage(), newValue);
                    });
                    result.getImageData().put(searchResultDto.getId(), imageData.getBody());
                    result.getImages().put(searchResultDto.getId(), image);
                }
            }

            try {
                ResponseEntity<TranslationsDto> translations = client.getTranslations("ca540140c89af81851d4026286942896", searchResultDto.getId());
                if (translations.getBody() != null) {
                    for (TranslationDto translationDto : translations.getBody().getTranslations()) {
                        String iso1 = translationDto.getIso3166();
                        String iso2 = translationDto.getIso639();
                        if (resourceBundle.getLocale().getLanguage().equals(iso1) || resourceBundle.getLocale().getLanguage().equals(iso2)) {
                            result.getTranslations().put(searchResultDto.getId(), translationDto);
                        }
                    }
                } else {
                    log.warn("Translations body is null");
                }
            } catch (Exception e) {
                log.error("Failed to query for translations", e);
            }
        }

//        ResponseEntity<byte[]> hans = imagesClient.searchMovie("ca540140c89af81851d4026286942896", null, config.isIncludeAdult(), "");

        return result;
    }

    private String buildNameString(String title, LocalDate date) {
        if (date == null) {
            return title;
        }
        return title + " (" + date.getYear() + ")";
    }


}
