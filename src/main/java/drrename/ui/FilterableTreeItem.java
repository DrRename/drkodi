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

package drrename.ui;

import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.scene.control.TreeItem;

import java.util.function.Predicate;

public class FilterableTreeItem<T> extends TreeItem<T> {
    private final ObservableList<TreeItem<T>> sourceChildren;

    // Do not convert this to a local variable. Thinks will break.
    private final FilteredList<TreeItem<T>> filteredChildren;
    private final ObjectProperty<Predicate<T>> predicate = new SimpleObjectProperty<>();

    public FilterableTreeItem(T value) {
        super(value);

         sourceChildren = FXCollections.observableArrayList(item -> getCallback());
         filteredChildren = new FilteredList<>(sourceChildren);

        filteredChildren.predicateProperty().bind(Bindings.createObjectBinding(this::buildFilterableListPredicate, predicate));

        filteredChildren.addListener((ListChangeListener<TreeItem<T>>) c -> {
            while (c.next()) {
                if(c.wasRemoved()) {
                    getChildren().removeAll(c.getRemoved());
                }
                if(c.wasAdded()) {
                    getChildren().addAll(c.getAddedSubList());
                }
            }
        });
    }

    protected Observable[] getCallback() {
        return new Observable[]{};
    }

    private Predicate<? super TreeItem<T>> buildFilterableListPredicate() {
        return child -> {
            if (child instanceof FilterableTreeItem) {
                ((FilterableTreeItem<T>) child).predicateProperty().set(predicate.get());
            }
            if (predicate.get() == null || !child.getChildren().isEmpty()) {
                return true;
            }
            return predicate.get().test(child.getValue());
        };
    }

    public ObservableList<TreeItem<T>> getSourceChildren() {
        return sourceChildren;
    }

    public ObjectProperty<Predicate<T>> predicateProperty() {
        return predicate;
    }

    public Predicate<T> getPredicate() {
        return predicate.get();
    }

    public void setPredicate(Predicate<T> predicate) {
        this.predicate.set(predicate);
    }
}
