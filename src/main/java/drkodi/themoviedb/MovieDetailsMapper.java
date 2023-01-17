/*
 *     Dr.Kodi - A Minimalistic Batch Renamer
 *
 *     Copyright (C) 2023
 *
 *     This file is part of Dr.Kodi.
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

package drkodi.themoviedb;

import drkodi.MovieDbImagesClient;
import drkodi.data.MovieDetailsDto;
import javafx.scene.image.Image;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.LocalDate;

@Mapper(componentModel = "spring")
public interface MovieDetailsMapper {

    MovieDbMovieDetails map(MovieDetailsDto dto);

    default Integer mapReleaseDateToYear(LocalDate localDate){
        return localDate == null ? null : localDate.getYear();
    }
}
