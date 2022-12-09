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

package drrename.kodi.ui.control;

import drrename.kodi.data.StaticMovieData;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ImageBox extends VBox {

    public ImageBox(Image image, int imageHeight) {

        init(image, imageHeight);
    }

    public ImageBox(StaticMovieData kodieMovie, int imageHeight) {

        init(kodieMovie.getImage(), imageHeight);

        initListener(kodieMovie, imageHeight);

//        visibleProperty().bind(kodieMovie.imageProperty().isNotNull());
    }

    private void init(Image image, int imageHeight) {
        // initially set
        setNewImage(image, imageHeight);
    }

    private void initListener(StaticMovieData kodieMovie, int imageHeight) {
        // register change listener
        kodieMovie.imageProperty().addListener(new ChangeListener<Image>() {
            @Override
            public void changed(ObservableValue<? extends Image> observable, Image oldValue, Image newValue) {
                setNewImage(newValue, imageHeight);
            }
        });
    }

    private void setNewImage(Image newValue, int imageHeight) {
        getChildren().clear();
        if (newValue != null) {
            log.debug("Setting new image {}", newValue);
            getChildren().add(buildImageView(newValue, imageHeight));
        }
    }

    private Node buildImageView(Image image, int imageHeight) {
        ImageView imageView = new ImageView();
        imageView.setFitHeight(imageHeight);
        imageView.setImage(image);
        imageView.setPreserveRatio(true);
        return imageView;
    }
}
