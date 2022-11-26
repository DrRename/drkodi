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

package drrename.ui;

import drrename.config.AppConfig;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.net.URL;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.stream.Stream;

@Getter
@RequiredArgsConstructor
public abstract class DebuggableController implements Initializable {

    private final AppConfig appConfig;

    private ResourceBundle resourceBundle;

    public static String getRandomColorString() {
        return String.format("#%06x", new Random().nextInt(256 * 256 * 256));
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        if(appConfig.isDebug()) applyRandomColors();
        this.resourceBundle = resourceBundle;
    }

    protected abstract Parent[] getUiElementsForRandomColor();

    protected void applyRandomColors() {
        applyRandomColors(getUiElementsForRandomColor());
    }

    protected void applyRandomColors(Parent[] elements) {
        Stream.of(elements).forEach(l -> l.setStyle("-fx-background-color: " + getRandomColorString()));
    }

}
