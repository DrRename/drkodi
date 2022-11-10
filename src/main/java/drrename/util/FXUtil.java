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

package drrename.util;

import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuBar;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.TreeItem;

public class FXUtil {
    public static ScrollBar getListViewScrollBar(final ListView<?> listView) {

        ScrollBar scrollbar = null;
        for (final Node node : listView.lookupAll(".scroll-bar")) {
            if (node instanceof final ScrollBar bar) {
                if (bar.getOrientation().equals(Orientation.VERTICAL)) {
                    scrollbar = bar;
                }
            }
        }
        return scrollbar;
    }

    public static void initAppMenu(MenuBar menuBar) {
        final String os = System.getProperty("os.name");
        if (os != null && os.startsWith("Mac")) {
            menuBar.useSystemMenuBarProperty().set(true);
        }
    }

    public static void expandTreeView(TreeItem<?> item, boolean expand){
        if(item != null && !item.isLeaf()){
            item.setExpanded(expand);
            for(TreeItem<?> child:item.getChildren()){
                expandTreeView(child, expand);
            }
        }
    }
}
