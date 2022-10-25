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

package drrename.kodi.nfo;

import drrename.kodi.KodiTreeItemValue;
import drrename.model.RenamingPath;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import lombok.Getter;
import lombok.Setter;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;

/**
 * Base class for all NFO file related {@code KodiTreeItemValue}s.
 *
 * @see drrename.kodi.KodiRootTreeItem
 */
@Setter
@Getter
public abstract class NfoFileTreeItemValue extends KodiTreeItemValue {

    private List<Path> nfoFiles;

    private BooleanProperty missingNfoFileIsWarning;

    public NfoFileTreeItemValue(RenamingPath moviePath, boolean fixable, Executor executor) {
        super(moviePath, fixable, executor);
        this.nfoFiles = new ArrayList<>();
        this.missingNfoFileIsWarning = new SimpleBooleanProperty();
    }

    @Override
    public String toString() {
        return super.toString();
    }

    public Path getNfoFile() {
        return nfoFiles.isEmpty() ? null : nfoFiles.get(0);
    }

    public boolean isMissingNfoFileIsWarning() {
        return missingNfoFileIsWarning.get();
    }

    public BooleanProperty missingNfoFileIsWarningProperty() {
        return missingNfoFileIsWarning;
    }

    public void setMissingNfoFileIsWarning(boolean missingNfoFileIsWarning) {
        this.missingNfoFileIsWarning.set(missingNfoFileIsWarning);
    }
}

