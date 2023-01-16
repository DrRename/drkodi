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

package drkodi;


import drkodi.data.SearchResult;
import drkodi.data.TvSearchResult;
import drkodi.data.json.MovieSearchResultDto;
import drkodi.data.json.TvSearchResultDto;
import javafx.scene.image.Image;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.LocalDate;

@Mapper(componentModel = "spring")
public interface TvSearchResultDtoMapper {

    @Mapping(target = "title", source="tvSearchResultDto.name")
    @Mapping(target = "originalTitle", source="tvSearchResultDto.originalName")
    @Mapping(target = "plot", source="tvSearchResultDto.overview")
    @Mapping(target = "releaseDate", source="tvSearchResultDto.firstAirDate")
    TvSearchResult map(TvSearchResultDto tvSearchResultDto, byte[] imageData, Image image);

    default Integer mapReleaseDateToYear(LocalDate localDate){
        return localDate == null ? null : localDate.getYear();
    }

}
