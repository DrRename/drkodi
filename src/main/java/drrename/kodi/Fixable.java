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

import java.io.IOException;

public interface Fixable<R> {

    /**
     * Called on a background thread to check the status.
     * I.e., if a "fixable" state is found.
     */
    R checkStatus() throws IOException;

    boolean isFixable();

    /**
     * Called on a background thread to perform the fix.
     * @throws FixFailedException if the fix failed
     */
    void fix(R checkStatusResult) throws FixFailedException;

    /**
     * Called on the FX-Application Thread to update the (UI) status.
     */
    void updateStatus(R checkStatusResult);
}
