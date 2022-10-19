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
import drrename.kodi.treeitem.content.MultipleSpacesTreeItem;

import java.io.IOException;
import java.nio.file.Path;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MultipleSpacesService extends CheckService<MultipleSpacesCheckResult>{

    @Override
    public MultipleSpacesCheckResult checkPath(Path path) throws IOException {
        return new MultipleSpacesCheckResult(path);
    }

    @Override
    public KodiTreeItem buildChildItem(MultipleSpacesCheckResult checkResult) {
        return new MultipleSpacesTreeItem(new MultipleSpacesTreeItemContent(checkResult.getPath()));
    }
}
