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

import javafx.beans.binding.Bindings;
import javafx.scene.control.TreeItem;

import java.util.concurrent.Executor;

/**
 * {@link KodiRootTreeItem}'s value.
 *
 * @see KodiRootTreeItem
 */
public class KodiRootTreeItemValue extends KodiTreeItemValue<Object> {

    public KodiRootTreeItemValue(Executor executor) {
        super(null, executor);
        setGraphic(null);
    }

    @Override
    public void setTreeItem(KodiTreeItem treeItem) {
        super.setTreeItem(treeItem);
        initWarning(treeItem);
    }

    protected void initWarning(KodiTreeItem treeItem) {
        warningProperty().bind(Bindings.createBooleanBinding(() -> calculateWarning(treeItem), treeItem.getSourceChildren()));
    }

    protected boolean calculateWarning(KodiTreeItem treeItem) {
        return treeItem.getSourceChildren().stream().map(TreeItem::getValue).filter(v -> v.warningProperty().get() != null).anyMatch(KodiTreeItemValue::isWarning);
    }

    @Override
    public String getHelpText() {
        return null;
    }

    @Override
    public String getIdentifier() {
        return "Analysis";
    }

    @Override
    protected String buildNewMessage(Boolean newValue) {
        return null;
    }

    @Override
    public Object checkStatus() {
        return null;
    }

    @Override
    public void fix(Object checkStatusResult) throws FixFailedException {
        // Ignore
    }

    @Override
    public void updateStatus(Object checkStatusResult) {
       // Ignore
    }
}
