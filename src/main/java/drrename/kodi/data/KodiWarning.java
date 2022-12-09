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

package drrename.kodi.data;

import drrename.kodi.FixTask;
import lombok.Getter;

@Getter
public class KodiWarning {

    public enum Type {
        TITLE_MISMATCH("Title mismatch"), YEAR_MISMATCH("Year mismatch");
        private final String message;
        Type(String message) {
            this.message = message;
        }
    }

    private final Type type;

    public KodiWarning(Type type) {
        this.type = type;

    }


}
