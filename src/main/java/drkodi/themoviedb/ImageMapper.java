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
import drkodi.config.TheMovieDbConfig;
import javafx.scene.image.Image;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;

@Slf4j
@Component
@RequiredArgsConstructor
public class ImageMapper {

    private final MovieDbImagesClient imagesClient;

    private final TheMovieDbConfig config;

    static class MappingResult {
        Image image;
        byte[] imageData;
    }

    MappingResult map(String imagePath){
        MappingResult result = new MappingResult();
        if(imagePath != null) {
            var imageData = imagesClient.searchMovie("ca540140c89af81851d4026286942896", null, config.isIncludeAdult(), imagePath);
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
        return result;
    }


}
