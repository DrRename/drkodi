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

package drkodi;

import drrename.commons.RenamingPath;
import javafx.beans.Observable;
import javafx.beans.property.ListProperty;
import javafx.beans.property.ReadOnlyListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.function.Predicate;

/**
 * {@link Component} that holds loaded instances of {@link RenamingPath}.
 */
@Slf4j
@Component
public class RenamingPathEntries {

    private final ListProperty<RenamingPath> entries;

    private final ListProperty<RenamingPath> entriesFiltered;

    private final Predicate<RenamingPath> entriesFilteredDefaultPredicate = e -> true;

    public RenamingPathEntries() {
        entries = new SimpleListProperty<>(FXCollections.observableArrayList(item -> new Observable[]{item.newPathProperty(), item.exceptionProperty(), item.filteredProperty(), item.willChangeProperty()}));
        entriesFiltered = new SimpleListProperty<>(new FilteredList<>(entries, entriesFilteredDefaultPredicate));
    }

    @PostConstruct
    public void init() {
        initListeners();
    }

    protected void initListeners() {


    }


    // FX Getter / Setter //


    public ObservableList<RenamingPath> getEntries() {
        return entries.get();
    }

    public ListProperty<RenamingPath> entriesProperty() {
        return entries;
    }

    public void setEntries(ObservableList<RenamingPath> entries) {
        this.entries.set(entries);
    }

    public ObservableList<RenamingPath> getEntriesFiltered() {
        return entriesFiltered.get();
    }

    public ReadOnlyListProperty<RenamingPath> entriesFilteredProperty() {
        return entriesFiltered;
    }


}
