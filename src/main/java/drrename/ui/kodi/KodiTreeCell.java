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
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Control;
import javafx.scene.control.TreeCell;

public class KodiTreeCell<R> extends TreeCell<KodiTreeItemValue<R>> {

    public KodiTreeCell(Control treeView) {
        prefWidthProperty().bind(treeView.widthProperty().subtract(20.0));
    }

    @Override
    protected void updateItem(KodiTreeItemValue<R> item, boolean empty) {

        super.updateItem(item, empty);
        if (item == null) {
            textProperty().unbind();
            graphicProperty().unbind();
            setText(null);
            setGraphic(null);
            getStyleClass().remove("warning");

        } else {
            if(item.isWarning()){
                getStyleClass().add("warning");
            } else {
                getStyleClass().remove("warning");
            }
            item.warningProperty().addListener(new ChangeListener<Boolean>() {
                @Override
                public void changed(ObservableValue observable, Boolean oldValue, Boolean newValue) {
                    if(newValue != null && newValue){
                        getStyleClass().add("warning");
                    }
                    else {
                        getStyleClass().remove("warning");
                    }
                }
            });
            graphicProperty().bind(item.graphicProperty());
            textProperty().bind(Bindings.createStringBinding(() -> calculateMessageString(item), item.messageProperty()));
        }
    }

    private String calculateMessageString(KodiTreeItemValue<?> item) {
        return item.getIdentifier() + (item.getMessage() == null ? "" : ": " + item.getMessage());
    }
}
