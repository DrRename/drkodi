/*
 *     Dr.Rename - A Minimalistic Batch Renamer
 *
 *     Copyright (C) 2022
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Affero General Public License as
 *     published by the Free Software Foundation, either version 3 of the
 *     License, or (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Affero General Public License for more details.
 *
 *     You should have received a copy of the GNU Affero General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package drrename;

import drrename.config.TheMovieDbConfig;
import drrename.kodi.nfo.MovieDbCheckType;
import drrename.model.themoviedb.SearchResultDto;
import drrename.model.themoviedb.TranslationDto;
import drrename.model.themoviedb.TranslationsDto;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.io.IOError;
import java.io.IOException;
import java.time.LocalDate;
import java.util.*;

@Getter
@Setter
@Slf4j
@Component
@Scope("prototype")
public class MovieDbChecker {

    private final MovieDbClient client;

    private final TheMovieDbConfig config;

    private final ResourceBundle resourceBundle;

    private Set<String> onlineTitles;

    private String theMovieDbId;

    public MovieDbChecker(MovieDbClient client, TheMovieDbConfig config, ResourceBundle resourceBundle) {
        this.client = client;
        this.config = config;
        this.resourceBundle = resourceBundle;
    }

    protected void reset(){
        this.onlineTitles = new LinkedHashSet<>();
        this.theMovieDbId = null;
    }

    public MovieDbCheckType check(String searchString, Integer year) throws IOException {
        reset();
        var searchResult = client.searchMovie("ca540140c89af81851d4026286942896", null, config.isIncludeAdult(), searchString, year);
        ResponseEntity<TranslationsDto> translatinos;
        if(searchResult.getBody() != null && !searchResult.getBody().getResults().isEmpty()){
            SearchResultDto firstResult = searchResult.getBody().getResults().get(0);
            getOnlineTitles().add(buildNameString(firstResult.getTitle(), firstResult.getReleaseDate()));
            getOnlineTitles().add(buildNameString(firstResult.getOriginalTitle(), firstResult.getReleaseDate()));
            if(searchString.equals(firstResult.getTitle())){
                theMovieDbId = firstResult.getPosterPath();
                return MovieDbCheckType.ORIGINAL_TITEL;
            }
            if(searchString.equals(firstResult.getOriginalTitle())){
                theMovieDbId = firstResult.getPosterPath();
                return MovieDbCheckType.ORIGINAL_TITEL;
            }
            translatinos = client.getTranslations(firstResult.getId(), "ca540140c89af81851d4026286942896");
            if(translatinos.getBody() != null){
                for(TranslationDto translationDto : translatinos.getBody().getTranslations()){
                    String iso1 = translationDto.getIso3166();
                    String iso2 = translationDto.getIso639();
                    String title = translationDto.getData().getTitle();
                    if(resourceBundle.getLocale().getLanguage().equals(iso1) || resourceBundle.getLocale().getLanguage().equals(iso2)){
                        getOnlineTitles().add(buildNameString(translationDto.getData().getTitle(), firstResult.getReleaseDate()));
                    }
                    if(searchString.equals(title)){
                        theMovieDbId = firstResult.getPosterPath();
                        return MovieDbCheckType.LOCALIZED_TITLE;
                    }
                }
            } else {
                log.warn("Translations body is null");
            }
        }
        return MovieDbCheckType.NOT_FOUND;
    }

    private String buildNameString(String title, LocalDate date) {
        if(date == null){
            return title;
        }
       return title + " (" + date.getYear() + ")";
    }
}
