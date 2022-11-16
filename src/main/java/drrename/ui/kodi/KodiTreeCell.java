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

package drrename.ui.kodi;

import javafx.beans.binding.Bindings;
import javafx.scene.control.Control;
import javafx.scene.control.TreeCell;

public class KodiTreeCell extends TreeCell<KodiTreeItemValue<?>> {

    public KodiTreeCell(Control treeView) {
        prefWidthProperty().bind(treeView.widthProperty().subtract(20.0));
    }

    @Override
    protected void updateItem(KodiTreeItemValue item, boolean empty) {

        super.updateItem(item, empty);
        if (item == null) {
            textProperty().unbind();
            graphicProperty().unbind();
            setText(null);
            setGraphic(null);
            getStyleClass().remove("warning");

        } else {
            graphicProperty().bind(item.graphicProperty());
            textProperty().bind(Bindings.createStringBinding(() -> calculateMessageString(item), item.messageProperty()));
            if(item.isWarning()){
                getStyleClass().add("warning");
            } else {
                getStyleClass().remove("warning");
            }
        }
    }

    private String calculateMessageString(KodiTreeItemValue<?> item) {
        return item.getIdentifier() + (item.getMessage() == null ? "" : ": " + item.getMessage());
    }
}
