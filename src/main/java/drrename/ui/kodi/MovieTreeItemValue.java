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

import drrename.kodi.FixFailedException;
import drrename.kodi.WarningsConfig;
import drrename.model.RenamingPath;
import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TreeItem;
import javafx.scene.layout.HBox;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.SystemUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.concurrent.Executor;

@Slf4j
public class MovieTreeItemValue extends KodiTreeItemValue<Object> {

    public MovieTreeItemValue(RenamingPath moviePath, Executor executor, WarningsConfig warningsConfig) {
        super(moviePath, executor, warningsConfig);
    }

    @Override
    protected HBox buildGraphic() {

        HBox box = new HBox(2);

        if(SystemUtils.IS_OS_MAC) {
            Button button = new Button("Open in finder");
            button.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    runOpenFolderCommandMacOs(getRenamingPath().getOldPath());
                }
            });
           box.getChildren().add(button);
            return box;
        }
        return null;

    }

    @Override
    public void setTreeItem(FilterableKodiTreeItem treeItem) {
        super.setTreeItem(treeItem);
        initWarning(treeItem);
    }

    @Override
    protected String buildNewMessage(Boolean newValue) {
        return null;
    }

    @Override
    public Object checkStatus() throws IOException {
        return null;
    }

    @Override
    public void fix(Object hans) throws FixFailedException {
        throw new IllegalStateException("Cannot fix");
    }

    @Override
    public void updateStatus(Object checkStatusResult) {
        // Ignore
    }

    @Override
    public String getHelpText() {
        return null;
    }

    @Override
    public String getIdentifier() {
        return getRenamingPath().getMovieName();
    }

    protected void initWarning(FilterableKodiTreeItem treeItem) {
        warningProperty().bind(Bindings.createBooleanBinding(() -> calculateWarning(treeItem), treeItem.getSourceChildren()));
    }

    protected boolean calculateWarning(FilterableKodiTreeItem treeItem) {
        return treeItem.getSourceChildren().stream().map(TreeItem::getValue).filter(v -> v.warningProperty().get() != null).anyMatch(KodiTreeItemValue::isWarning);
    }

    private void runOpenFolderCommandMacOs(Path path) {
        new CommandRunner().runCommand(new String[]{"open", "-R", path.toString()});
    }

    @Override
    public HBox getGraphic() {
        return (HBox) super.getGraphic();
    }
}
