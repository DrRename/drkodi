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
import drkodi.data.dynamic.Movie;
import drkodi.ui.UiUtil;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;

public class KodiYearWarningBox extends HBox {

    public KodiYearWarningBox(Movie staticMovieData, AppConfig appConfig){

        Label label1 = new KodiWarningKeyLabel("Folder year does not match:");

        // NFO value is displayed, and we have a mismatch, so display folder name
        Label label2 = new KodiWarningValueLabel(staticMovieData.getMovieYearFromFolder().toString());

        getChildren().add(UiUtil.applyDebug(label1, appConfig));
        getChildren().add(UiUtil.applyDebug(label2, appConfig));
        final Pane spacer = new Pane();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        getChildren().add(UiUtil.applyDebug(spacer, appConfig));
        getChildren().add(UiUtil.applyDebug(buildRenameFolderButton(staticMovieData), appConfig));

        setAlignment(Pos.CENTER_LEFT);
        setSpacing(4);

        getStyleClass().add("kodi-warning-box-element");

    }

    private Node buildRenameFolderButton(Movie element) {
        Button button = new Button("Fix");
        button.setOnAction(event -> {
            element.renameFolder();
        });
        button.setMinWidth(40);
        button.setAlignment(Pos.CENTER_RIGHT);
        return button;
    }

}
