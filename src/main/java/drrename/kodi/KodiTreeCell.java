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

package drrename.kodi;

import drrename.kodi.treeitem.content.KodiTreeItemContent;
import drrename.kodi.treeitem.content.MovieTreeItemContent;
import javafx.scene.control.TreeCell;

import java.util.ArrayList;
import java.util.List;

public class KodiTreeCell extends TreeCell<KodiTreeItemContent> {

    @Override
    protected void updateItem(KodiTreeItemContent item, boolean empty) {

        super.updateItem(item, empty);
        if (item == null) {
            setText(null);
            setStyle(null);
        } else {
            setText(item.toString());
            List<String> styles = new ArrayList<>();
            if (item.hasWarning()) {
                if (item instanceof MovieTreeItemContent) {
                    styles.add("-fx-font-size: 13;");
                }
                styles.add("-fx-font-weight: bold;");
                styles.add("-fx-background-color: wheat;");
                var joinedStylesString = String.join(" ", styles);
                setStyle(joinedStylesString);
            } else {
                if (item instanceof MovieTreeItemContent) {
                    styles.add("-fx-font-size: 14;");
                } else
                    setStyle(null);
            }
        }
    }
}
