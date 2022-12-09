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

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Data
public class MovieDetailsDto {

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Genres {

        @JsonProperty("id")
        Number id;

        @JsonProperty("name")
        String name;
    }

    @JsonProperty("genres")
    List<Genres> genres;

    @JsonProperty("tagline")
    String taline;

    @JsonProperty("overview")
    String overview;

    @JsonProperty("original_title")
    String originalTitle;

    @JsonProperty("title")
    String title;

    @JsonProperty("plot")
    String plot;

    @JsonProperty("release_date")
    LocalDate releaseDate;

    @JsonProperty("poster_path")
    String posterPath;
}
