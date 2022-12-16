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

package drkodi;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

public class WarningsConfig {

    private final BooleanProperty missingNfoFileIsWarning;

    private final BooleanProperty defaultNfoFileNameIsWarning;

    public WarningsConfig(){
        this.missingNfoFileIsWarning = new SimpleBooleanProperty();
        this.defaultNfoFileNameIsWarning = new SimpleBooleanProperty();
    }

    // Getter / Setter //

    public boolean isMissingNfoFileIsWarning() {
        return missingNfoFileIsWarning.get();
    }

    public BooleanProperty missingNfoFileIsWarningProperty() {
        return missingNfoFileIsWarning;
    }

    public void setMissingNfoFileIsWarning(boolean missingNfoFileIsWarning) {
        this.missingNfoFileIsWarning.set(missingNfoFileIsWarning);
    }

    public boolean isDefaultNfoFileNameIsWarning() {
        return defaultNfoFileNameIsWarning.get();
    }

    public BooleanProperty defaultNfoFileNameIsWarningProperty() {
        return defaultNfoFileNameIsWarning;
    }

    public void setDefaultNfoFileNameIsWarning(boolean defaultNfoFileNameIsWarning) {
        this.defaultNfoFileNameIsWarning.set(defaultNfoFileNameIsWarning);
    }
}
