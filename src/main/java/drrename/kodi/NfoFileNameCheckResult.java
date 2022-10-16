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

import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.nio.file.Path;
import java.util.Collection;

public class NfoFileNameCheckResult extends CheckResult {

    private final ListProperty<Path> nfoFiles;
    private final NfoFileNameType type;

    public NfoFileNameCheckResult(NfoFileNameType result, Collection<Path> nfoFiles) {
        super("NFO File Name: ", result.toString());
        this.type = result;
        this.nfoFiles = new SimpleListProperty<>(FXCollections.observableArrayList(nfoFiles));
    }

    // Getter / Setter //

    public NfoFileNameType getType() {
        return type;
    }

    public ObservableList<Path> getNfoFiles() {
        return nfoFiles.get();
    }

    public ListProperty<Path> nfoFilesProperty() {
        return nfoFiles;
    }

    public void setNfoFiles(ObservableList<Path> nfoFiles) {
        this.nfoFiles.set(nfoFiles);
    }
}
