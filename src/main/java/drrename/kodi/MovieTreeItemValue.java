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

import drrename.model.RenamingPath;
import javafx.beans.binding.Bindings;
import javafx.scene.control.TreeItem;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Executor;

@Slf4j
public class MovieTreeItemValue extends KodiTreeItemValue {

    public MovieTreeItemValue(RenamingPath moviePath, Executor executor) {
        super(moviePath, false, executor);
        moviePath.movieNameProperty().addListener((observableValue, s, t1) -> setMessage(t1));
        treeItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                initWarning(newValue);
            } else {
                warningProperty().unbind();
            }
        });
        setMessage(moviePath.getMovieName());
        setGraphic(null);
    }

    @Override
    protected String updateIdentifier() {
        return null;
    }

    @Override
    protected void updateStatus() {
        // message is updated via binding to movie name property
    }

    @Override
    protected String updateMessage(Boolean newValue) {
        return getRenamingPath().getMovieName();
    }

    @Override
    public void fix() throws FixFailedException {
        throw new IllegalStateException("Cannot fix");
    }

    protected void initWarning(KodiTreeItem treeItem) {
        warningProperty().bind(Bindings.createBooleanBinding(() -> calculateWarning(treeItem), treeItem.getSourceChildren()));
    }

    protected boolean calculateWarning(KodiTreeItem treeItem) {
        return treeItem.getSourceChildren().stream().map(TreeItem::getValue).anyMatch(KodiTreeItemValue::isWarning);
    }
}
