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

import drkodi.data.MovieDetailsDto;
import drkodi.data.TvDetailsDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class TvDetailsMapper2 {

    private final TvDetailsMapper tvDetailsMapper;

    private final ImageMapper imageMapper;

    public MovieDbTvDetails map(TvDetailsDto dto) {
        var result = tvDetailsMapper.map(dto);
        var result2 = imageMapper.map(dto.getPosterPath());
        result.setImage(result2.image);
        result.setImageData(result2.imageData);
        return result;
    }
}
