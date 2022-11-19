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
import javafx.scene.Node;
import javafx.scene.control.TreeItem;
import javafx.util.Callback;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Optional;
import java.util.function.Predicate;

public class FilterableTreeItem<T> extends TreeItem<T> {
    protected final ObservableList<TreeItem<T>> sourceChildren;

    // Do not convert this to a local variable. Thinks will break.
    protected final FilteredList<TreeItem<T>> filteredChildren;
    protected final ObjectProperty<Predicate<T>> predicate;

    public FilterableTreeItem() {
        this(null);
    }

    public FilterableTreeItem(T value) {
        this(value, null);
    }

    protected Comparator<TreeItem<T>> getComparator() {
        return null;
    }

    public FilterableTreeItem(T value, Node graphic) {
        super(value, graphic);
        sourceChildren = FXCollections.observableList(new ArrayList<>(),this::getExtractorCallback);
        filteredChildren = new FilteredList<>(sourceChildren);
        predicate = new SimpleObjectProperty<>();
        init();
    }

    private void init() {
        Bindings.bindContent(super.getChildren(), filteredChildren);
        filteredChildren.predicateProperty().bind(Bindings.createObjectBinding(this::buildFilterableListPredicate, predicate));
    }

    /**
     * Override to add callbacks to the {@link #sourceChildren} collection. See
     * {@link FXCollections#observableArrayList(Callback)}
     *
     * @return callbacks that are passed to the observable source list
     */
    protected Observable[] getExtractorCallback(TreeItem<T> item) {
        return new Observable[]{};
    }

    protected Predicate<? super TreeItem<T>> buildFilterableListPredicate() {
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

    // Getter / Setter //

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
