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

import drrename.kodi.data.Movie;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public class PlotBox extends VBox {
    public PlotBox(Movie item) {
        setPadding(new Insets(4, 4, 4, 4));
        Label label = new Label();
        label.setWrapText(true);
        label.setText(item.getPlot());


        // register listener

        item.plotProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                log.debug("Plot changed, updating content");
                label.setText(newValue);
            }
        });

        getChildren().add(label);

        getStyleClass().add("kodi-plot");

        visibleProperty().bind(item.plotProperty().isNotEmpty());
        managedProperty().bind(visibleProperty());
    }
}
