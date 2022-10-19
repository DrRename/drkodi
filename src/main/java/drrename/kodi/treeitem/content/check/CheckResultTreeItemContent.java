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

package drrename.kodi.treeitem.content.check;

import drrename.kodi.treeitem.content.KodiTreeItemContent;
import lombok.RequiredArgsConstructor;

import java.util.Objects;

@Deprecated
@RequiredArgsConstructor
public class CheckResultTreeItemContent<T extends CheckResult> extends KodiTreeItemContent {

    private final T checkResult;

    public T getCheckResult() {
        return checkResult;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CheckResultTreeItemContent<?> that)) return false;
        return checkResult.equals(that.checkResult);
    }

    @Override
    public int hashCode() {
        return Objects.hash(checkResult);
    }

    @Override
    public String toString() {
        return checkResult.toString();
    }
}
