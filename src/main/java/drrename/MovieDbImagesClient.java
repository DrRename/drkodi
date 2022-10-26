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

import drrename.model.themoviedb.SearchResultsDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "moviedb-images", url = "${app.kodi.themoviedb.images.baseurl}", configuration = MovieDbClientConfig.class)
public interface MovieDbImagesClient {

    @RequestMapping(method = RequestMethod.GET, value = "/w500/{poster_path}", produces = MediaType.IMAGE_JPEG_VALUE)
    ResponseEntity<byte[]> searchMovie(@RequestParam(name = "api_key") String apiKey, @RequestParam(name = "langauge", required = false) String language, @RequestParam(name = "include_adult", required = false) Boolean includeAdult, @PathVariable(name = "poster_path") String posterPath);
}
