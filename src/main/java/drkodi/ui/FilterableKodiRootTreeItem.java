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

package drkodi.ui;

import drkodi.WarningsConfig;
import javafx.beans.Observable;
import javafx.scene.control.TreeView;

import java.util.concurrent.Executor;

/**
 * The root value of the Kodi {@link TreeView}.
 *
 * @see KodiRootTreeItemValue
 */
public class FilterableKodiRootTreeItem extends FilterableKodiTreeItem {

    public FilterableKodiRootTreeItem(Executor executor, WarningsConfig warningsConfig, Observable[] extractor) {
        super(new KodiRootTreeItemValue(executor, warningsConfig), extractor);
    }
}
