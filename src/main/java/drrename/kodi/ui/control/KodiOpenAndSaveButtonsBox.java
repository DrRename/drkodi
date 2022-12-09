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
import drrename.util.DrRenameUtil;
import javafx.scene.control.Hyperlink;
import javafx.scene.layout.HBox;
import org.apache.commons.lang3.SystemUtils;

import java.util.concurrent.Executor;

public class KodiOpenAndSaveButtonsBox extends HBox  {

    public KodiOpenAndSaveButtonsBox(Movie item, Executor executor){
        if (SystemUtils.IS_OS_MAC) {
            Hyperlink button = new Hyperlink("Open in finder");
            button.setOnAction(event -> DrRenameUtil.runOpenFolderCommandMacOs(item.getRenamingPath().getOldPath()));
            getChildren().add(button);
        }
        Hyperlink button = new Hyperlink("Save to NFO");
        button.setOnAction(event -> {
            item.writeNfoDataAndImage(executor);
        });
        getChildren().add(button);
    }
}
