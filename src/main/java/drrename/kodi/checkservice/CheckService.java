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

package drrename.kodi.checkservice;

import drrename.kodi.KodiTreeItem;
import drrename.kodi.KodiTreeItemValue;
import drrename.kodi.MovieTreeItem;
import drrename.kodi.WarningsConfig;
import drrename.model.RenamingPath;
import javafx.application.Platform;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Executor;

@Slf4j
public abstract class CheckService<R extends KodiTreeItemValue> {

    public void addChildItem(MovieTreeItem treeItem, WarningsConfig warningsConfig, Executor executor) {

        R checkResult = checkPath(treeItem.getValue().getRenamingPath(), warningsConfig, executor);
        var childItem = buildChildItem(checkResult);
        Platform.runLater(() -> treeItem.add(childItem));

    }

    public abstract R checkPath(RenamingPath path, WarningsConfig warningsConfig, Executor executor);

    public KodiTreeItem buildChildItem(R checkResult) {
        return new KodiTreeItem(checkResult);
    }
}
