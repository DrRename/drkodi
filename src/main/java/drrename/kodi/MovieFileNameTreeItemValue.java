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
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

import java.util.concurrent.Executor;

public class MovieFileNameTreeItemValue extends KodiTreeItemValue {

    private final ObjectProperty<MovieFileNameType> type;

    private final MovieFileNameChecker checker;

    public MovieFileNameTreeItemValue(RenamingPath path, Executor executor) {
        super(path, true, executor);
        this.type = new SimpleObjectProperty<>();
        checker = new MovieFileNameChecker();
        updateStatus();

    }

    @Override
    protected String updateMessage(Boolean newValue) {
        if (getType() == null) {
            return "unknown";
        }
        if (newValue) {
            return getType().toString() + (checker.getMediaFiles().isEmpty() ? "" : getWarningAdditionalInfo());
        }
        return (getType().toString());
    }

    private String getWarningAdditionalInfo() {
        return ": " + checker.getMediaFiles();
    }

    @Override
    public void fix() throws FixFailedException {
        throw new IllegalStateException("Cannot fix");
    }

    @Override
    protected String updateIdentifier() {
        return "Movie File Name";
    }

    @Override
    protected void updateStatus() {
        setType(checker.checkDir(getRenamingPath().getOldPath()));
        setWarning(calculateWarning());
    }

    private boolean calculateWarning() {
        return getType().isWarning();
    }

    // Getter / Setter //

    public MovieFileNameType getType() {
        return type.get();
    }

    public ObjectProperty<MovieFileNameType> typeProperty() {
        return type;
    }

    public void setType(MovieFileNameType type) {
        this.type.set(type);
    }
}
