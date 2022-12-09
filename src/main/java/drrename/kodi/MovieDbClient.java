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

import drrename.kodi.data.SearchResultsDto;
import drrename.kodi.themoviedb.TranslationsDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "moviedb", url = "${app.kodi.themoviedb.baseurl}", configuration = MovieDbClientConfig.class)
public interface MovieDbClient {

    @RequestMapping(method = RequestMethod.GET, value = "/search/movie", produces = "application/json")
    ResponseEntity<SearchResultsDto> searchMovie(@RequestParam(name = "api_key") String apiKey, @RequestParam(name = "langauge", required = false) String language, @RequestParam(name = "include_adult", required = false) Boolean includeAdult, @RequestParam(name = "query") String query, @RequestParam(name = "year") Number year);

    @RequestMapping(method = RequestMethod.GET, value = "/movie/{id}/translations", produces = "application/json")
    ResponseEntity<TranslationsDto> getTranslations(@RequestParam(name = "api_key") String apiKey, @PathVariable(name = "id") Number id);

    @RequestMapping(method = RequestMethod.GET, value = "/movie/{id}", produces = "application/json")
    ResponseEntity<MovieDetailsDto> getDetails(@RequestParam(name = "api_key") String apiKey, @PathVariable(name = "id") Number id, @RequestParam(name = "langauge", required = false) String language);
}
