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

import drkodi.ui.control.KodiMoviePathEntryBox;
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
 * {@link Component} that holds loaded instances of {@link KodiMoviePathEntryBox}.
 */
@Slf4j
@Component
public class MovieEntries {

    private final ListProperty<KodiMoviePathEntryBox> entries;

    private final ListProperty<KodiMoviePathEntryBox> entriesFiltered;

    private final Predicate<KodiMoviePathEntryBox> entriesFilteredDefaultPredicate = e -> true;

    public MovieEntries() {
        entries = new SimpleListProperty<>(FXCollections.observableArrayList(item -> new Observable[]{}));
        entriesFiltered = new SimpleListProperty<>(new FilteredList<>(entries, entriesFilteredDefaultPredicate));
    }

    @PostConstruct
    public void init() {
        initListeners();
    }

    protected void initListeners() {


    }


    // FX Getter / Setter //


    public ObservableList<KodiMoviePathEntryBox> getEntries() {
        return entries.get();
    }

    public ListProperty<KodiMoviePathEntryBox> entriesProperty() {
        return entries;
    }

    public void setEntries(ObservableList<KodiMoviePathEntryBox> entries) {
        this.entries.set(entries);
    }

    public ObservableList<KodiMoviePathEntryBox> getEntriesFiltered() {
        return entriesFiltered.get();
    }

    public ReadOnlyListProperty<KodiMoviePathEntryBox> entriesFilteredProperty() {
        return entriesFiltered;
    }


}
