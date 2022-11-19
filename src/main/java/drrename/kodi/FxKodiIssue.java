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

package drrename.kodi;

import drrename.model.RenamingPath;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import lombok.Getter;

import java.io.IOException;

@Getter
public abstract class FxKodiIssue<R> implements KodiIssue<R> {

    private final RenamingPath renamingPath;

    private final BooleanProperty fixable;

    private final StringProperty message;

    public FxKodiIssue(RenamingPath renamingPath){
        this.renamingPath = renamingPath;
        this.fixable = new SimpleBooleanProperty();
        this.message = new SimpleStringProperty();
    }

    @Override
    public abstract R checkStatus() throws IOException;

    @Override
    public abstract void fix(R checkStatusResult) throws FixFailedException;

    @Override
    public abstract void updateStatus(R checkStatusResult);

    @Override
    public abstract String getHelpText();

    @Override
    public abstract String getIdentifier();

    @Override
    public String getMovieNameFromFolder() {
        return getRenamingPath() == null ? null : getRenamingPath().getMovieName();
    }

    // FX Getter / Setter //

    @Override
    public boolean isFixable() {
        return fixable.get();
    }

    public BooleanProperty fixableProperty() {
        return fixable;
    }

    public void setFixable(boolean fixable) {
        this.fixable.set(fixable);
    }

    @Override
    public String getMessage() {
        return message.get();
    }

    public StringProperty messageProperty() {
        return message;
    }

    public void setMessage(String message) {
        this.message.set(message);
    }
}
