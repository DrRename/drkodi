/*
 *     Dr.Rename - A Minimalistic Batch Renamer
 *
 *     Copyright (C) 2022
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Affero General Public License as
 *     published by the Free Software Foundation, either version 3 of the
 *     License, or (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Affero General Public License for more details.
 *
 *     You should have received a copy of the GNU Affero General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package drrename.kodi.treeitem.content.check;

import drrename.kodi.treeitem.KodiTreeItem;
import drrename.kodi.treeitem.MovieTreeItem;
import javafx.application.Platform;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Path;

@Slf4j
public abstract class CheckService<R extends CheckResult> {

    public void addChildItem(MovieTreeItem treeItem) {
        try {
            R checkResult = checkPath(treeItem.getMoviePath());
            var childItem = buildChildItem(checkResult);
            if(!treeItem.contains(childItem))
                Platform.runLater(() -> treeItem.add(childItem));
        }catch (IOException e){
            log.error(e.getLocalizedMessage(), e);
        }
    }

    public abstract R checkPath(Path path) throws IOException;

    public abstract KodiTreeItem buildChildItem(R checkResult);
}
