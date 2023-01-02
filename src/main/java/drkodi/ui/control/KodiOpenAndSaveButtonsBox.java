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

import drkodi.data.movie.Movie;
import drkodi.util.DrRenameUtil;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.layout.FlowPane;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.SystemUtils;

@Slf4j
public class KodiOpenAndSaveButtonsBox extends FlowPane {

    public KodiOpenAndSaveButtonsBox(Movie item) {

        Button button = new Button("Open in System browser");

        if (SystemUtils.IS_OS_MAC) {
            button.setOnAction(event -> {
                DrRenameUtil.runOpenFolderCommandMacOs(item.getRenamingPath().getOldPath());
            });
        } else if (SystemUtils.IS_OS_LINUX) {
            button.setOnAction(event -> {
                DrRenameUtil.runOpenFolderCommandLinux(item.getRenamingPath().getOldPath());
            });
        } else {
            log.warn("Unknown OS {}", SystemUtils.OS_NAME);
        }

        button.getStyleClass().add("kodi-link");
        getChildren().add(button);

        button = new Button("Clear Data");
        button.setOnAction(event -> {
            item.clearData();
        });
        button.getStyleClass().add("kodi-link");
        getChildren().add(button);

        setPadding(new Insets(0,0,12,0));

    }
}
