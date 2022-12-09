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

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Objects;


@Getter
public class Qualified<T> {

    public static boolean isOk(Qualified<?> qualified) {
        return qualified != null && Type.OK.equals(qualified.type);
    }

    public enum Type {
        NOT_FOUND, INVALID, OK
    }

    public Qualified(T element, Type type) {
        this.element = element;
        this.type = Objects.requireNonNull(type, "Type must not be null");
    }

    private final T element;

    private final Type type;

    @Override
    public String toString() {
        return getClass().getSimpleName() + ": " + getType() + ", " + getElement();
    }
}
