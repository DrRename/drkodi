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

package drrename.ui;

import org.apache.commons.lang3.SystemUtils;

public enum UiTheme {
    LIGHT("preferences.interface.theme.light"), //
    DARK("preferences.interface.theme.dark"), //
    AUTOMATIC("preferences.interface.theme.automatic");

    public static UiTheme[] applicableValues() {
        if (SystemUtils.IS_OS_MAC || SystemUtils.IS_OS_WINDOWS) {
            return values();
        } else {
            return new UiTheme[]{LIGHT, DARK};
        }
    }

    private final String displayName;

    UiTheme(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

}
