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

package drrename.kodi.ui;

import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.beans.value.WeakChangeListener;
import javafx.css.PseudoClass;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.ContextMenuEvent;
import lombok.extern.slf4j.Slf4j;

import java.nio.file.Path;

@Slf4j
public class KodiTreeCell<R> extends TreeCell<KodiTreeItemValue<R>> {

    static final PseudoClass WARNING = PseudoClass.getPseudoClass("warning");

    private final ChangeListener<Boolean> warningListener = (obs, oldVal, newVal) -> {
        pseudoClassStateChanged(WARNING, newVal);
    };

    // use a weak listener to avoid a memory leak
    private final WeakChangeListener<Boolean> weakWarningListener = new WeakChangeListener<>(warningListener);

    private ChangeListener<KodiTreeItemValue<R>> itemListener = new ChangeListener<KodiTreeItemValue<R>>() {
        @Override
        public void changed(ObservableValue<? extends KodiTreeItemValue<R>> observable, KodiTreeItemValue<R> oldVal, KodiTreeItemValue<R> newVal) {
            if (oldVal != null) {
                oldVal.warningProperty().removeListener(weakWarningListener);
            }
            if (newVal != null) {
                newVal.warningProperty().addListener(weakWarningListener);
                // need to "observe" the initial NodeType of the new Node item.
                // You could call the listener manually to avoid code duplication
                pseudoClassStateChanged(WARNING, newVal.isWarning());
            } else {
                // no item in this cell so deactivate all PseudoClass's
                pseudoClassStateChanged(WARNING, false);
            }
        }
    };

    public KodiTreeCell(Control treeView) {
        prefWidthProperty().bind(treeView.widthProperty().subtract(20.0));
        getStyleClass().add("kodi-tree-cell");
        itemProperty().addListener(itemListener);
    }

    @Override
    public void updateItem(KodiTreeItemValue<R> item, boolean empty) {

        super.updateItem(item, empty);
        if (item == null || empty) {
            textProperty().unbind();
            graphicProperty().unbind();
            setText(null);
            setGraphic(null);
            setContextMenu(null);
        } else {
//            if (item.getRenamingPath() != null) {
//                // for now, only MacOS supported for opening the system explorer
//                if (SystemUtils.IS_OS_MAC) {
//                    setContextMenu(buildContextMenu(item.getRenamingPath().getOldPath()));
//                }
//            }
            graphicProperty().bind(Bindings.createObjectBinding(() -> buildGraphic(item), item.graphicProperty()));
            textProperty().bind(Bindings.createStringBinding(() -> calculateMessageString(item), item.messageProperty()));
        }
    }

    private Node buildGraphic(KodiTreeItemValue<R> item) {
//        if (item instanceof MovieTreeItemValue item2) {
//            HBox boxx = new HBox(2);
//            boxx.getChildren().add(item2.getGraphic());
//            Button button2 = new Button("Rename");
//            button2.setOnAction(new EventHandler<ActionEvent>() {
//                @Override
//                public void handle(ActionEvent event) {
//                    startEdit();
//                }
//            });
//            boxx.getChildren().add(button2);
//            return boxx;
//        }
        return item.getGraphic();
    }

    private void contextMenuRequested(ContextMenuEvent contextMenuEvent, Path path) {
        if (contextMenuEvent.getTarget() instanceof Node node) {
            log.debug("Showing context menu at X:{},Y:{}", contextMenuEvent.getScreenX(), contextMenuEvent.getScreenY());
            var menu = buildContextMenu(path);
            menu.setX(contextMenuEvent.getScreenX());
            menu.setY(contextMenuEvent.getScreenY());
            menu.show(node.getScene().getWindow());
        }
    }

    private ContextMenu buildContextMenu(Path path) {
        ContextMenu contextMenu = new ContextMenu();
        MenuItem menuItem1 = new MenuItem("Open with system explorer");
        menuItem1.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {

            }
        });
        contextMenu.getItems().add(menuItem1);
        return contextMenu;
    }

    private String calculateMessageString(KodiTreeItemValue<?> item) {
        return item.getIdentifier() + (item.getMessage() == null ? "" : ": " + item.getMessage());
    }
}
