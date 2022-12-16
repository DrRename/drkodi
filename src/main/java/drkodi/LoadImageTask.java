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

import javafx.concurrent.Task;
import javafx.scene.image.Image;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;

@Slf4j
public class LoadImageTask extends Task<Image>  {

    private final byte[] imageData;

    public LoadImageTask(byte[] imageData) {
        this.imageData = imageData;
    }

    @Override
    public Image call() throws Exception {
        Image image = new Image(new ByteArrayInputStream(imageData));
        image.exceptionProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null)
                log.error("Failed to load image", newValue);
        });
        return image;
    }
}
