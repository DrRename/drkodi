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

import drrename.util.DrRenameUtil;
import drrename.config.TheMovieDbConfig;
import drrename.kodi.nfo.MovieDbCheckType;
import drrename.kodi.nfo.MovieDbLookupCheckResult;
import drrename.model.themoviedb.SearchResultDto;
import drrename.model.themoviedb.TranslationDto;
import drrename.model.themoviedb.TranslationsDto;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;

@Getter
@Setter
@Slf4j
@Component
@Scope("prototype")
public class MovieDbQuerier {

    private final MovieDbClient client;

    private final TheMovieDbConfig config;

    private final ResourceBundle resourceBundle;

    private Set<String> onlineTitles;

    private Set<Number> theMovieDbId;

    public MovieDbQuerier(MovieDbClient client, TheMovieDbConfig config, ResourceBundle resourceBundle) {
        this.client = client;
        this.config = config;
        this.resourceBundle = resourceBundle;
    }

    protected void reset(){
        this.onlineTitles = new LinkedHashSet<>();
        this.theMovieDbId = new LinkedHashSet<>();
    }

    public MovieDbLookupCheckResult query(String searchString, Integer year) throws IOException {
        reset();
        var searchResult = client.searchMovie("ca540140c89af81851d4026286942896", null, config.isIncludeAdult(), searchString, null);

        if(searchResult.getBody() != null && !searchResult.getBody().getResults().isEmpty()){
            for(SearchResultDto result2 : searchResult.getBody().getResults()){
                if(result2.getReleaseDate() != null && (searchString.equals(result2.getOriginalTitle()) && year.equals(result2.getReleaseDate().getYear()))){
                    theMovieDbId.add(result2.getId());
                    return new MovieDbLookupCheckResult(MovieDbCheckType.ORIGINAL_TITEL, onlineTitles);
                }
            }
            if(searchString.equals(searchResult.getBody().getResults().get(0).getOriginalTitle()) && year.equals(searchResult.getBody().getResults().get(0).getReleaseDate().getYear())){
                theMovieDbId.add(searchResult.getBody().getResults().get(0).getId());
                return new MovieDbLookupCheckResult(MovieDbCheckType.ORIGINAL_TITEL, onlineTitles);
            }
            List<SearchResultDto> subList = DrRenameUtil.getSubList(searchResult.getBody().getResults(), config.getNumberOfMaxSuggestions());
            for(SearchResultDto searchResultDto : subList){
                getOnlineTitles().add(buildNameString(searchResultDto.getTitle(), searchResultDto.getReleaseDate()));
                getOnlineTitles().add(buildNameString(searchResultDto.getOriginalTitle(), searchResultDto.getReleaseDate()));
                try {
                    ResponseEntity<TranslationsDto> translations = client.getTranslations(searchResultDto.getId(), "ca540140c89af81851d4026286942896");
                    if (translations.getBody() != null) {
                        for (TranslationDto translationDto : translations.getBody().getTranslations()) {
                            String iso1 = translationDto.getIso3166();
                            String iso2 = translationDto.getIso639();
                            String title = translationDto.getData().getTitle();
                            if (searchString.equals(title) && year.equals(searchResultDto.getReleaseDate().getYear())) {
                                return new MovieDbLookupCheckResult(MovieDbCheckType.LOCALIZED_TITLE, onlineTitles);
                            }
                            if (resourceBundle.getLocale().getLanguage().equals(iso1) || resourceBundle.getLocale().getLanguage().equals(iso2)) {
                                getOnlineTitles().add(buildNameString(translationDto.getData().getTitle(), searchResultDto.getReleaseDate()));
                            }
                        }
                    } else {
                        log.warn("Translations body is null");
                    }
                }catch(Exception e){
                    log.error("Failed to query for translations", e);
                }
            }
        }
        return new MovieDbLookupCheckResult(MovieDbCheckType.NOT_FOUND, getOnlineTitles());
    }

    private String buildNameString(String title, LocalDate date) {
        if(date == null){
            return title;
        }
       return title + " (" + date.getYear() + ")";
    }
}
