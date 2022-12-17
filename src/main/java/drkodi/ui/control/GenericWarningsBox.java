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

package drkodi.ui.control;

import drkodi.config.AppConfig;
import drkodi.data.StaticMovieData;
import drkodi.ui.UiUtil;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

public class GenericWarningsBox extends HBox {

    private final String text;

    public GenericWarningsBox(AppConfig appConfig, String text){
        this.text = text;

        Label label1 = new KodiWarningKeyLabel(text);
        getChildren().add(UiUtil.applyDebug(label1, appConfig));
        setAlignment(Pos.CENTER_LEFT);
        setSpacing(4);
        getStyleClass().add("kodi-warning-box-element");
    }

}
