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

import java.util.concurrent.Executor;

/**
 * {@link KodiRootTreeItem}'s value.
 *
 * @see KodiRootTreeItem
 */
public class KodiRootTreeItemValue extends KodiTreeItemValue {

    public KodiRootTreeItemValue(Executor executor) {
        super(null, false, executor);
        setMessage("Analysis");
        setGraphic(null);
    }

    @Override
    protected String updateMessage(Boolean newValue) {
        return null;
    }

    @Override
    protected String updateIdentifier() {
        return null;
    }

    @Override
    public void fix() throws FixFailedException {
        throw new IllegalStateException("Cannot fix");
    }

    @Override
    protected void updateStatus() {
        // nothing to update
    }
}
