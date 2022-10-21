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

package drrename.model;

import drrename.strategy.RenamingStrategy;
import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import lombok.extern.slf4j.Slf4j;

import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

@Slf4j
public class RenamingPath {
    protected final ObjectProperty<Path> oldPath;

    protected final StringProperty newPath;

    protected final ObjectProperty<Throwable> exception;

    protected final BooleanProperty willChange;

    protected final BooleanProperty filtered;

    protected final StringProperty movieName;
    private final ChangeListener<? super Path> listener;

    public RenamingPath(final Path path) {
        listener = new ChangeListener<Path>() {
            @Override
            public void changed(ObservableValue<? extends Path> observableValue, Path path, Path t1) {
                movieName.set(t1.getFileName().toString());
            }
        };
        this.oldPath = new SimpleObjectProperty<>();
        this.newPath = new SimpleStringProperty();
        this.movieName = new SimpleStringProperty();
        this.exception = new SimpleObjectProperty<>();
        this.willChange = new SimpleBooleanProperty();
        this.filtered = new SimpleBooleanProperty();
        oldPath.addListener(listener);
        oldPath.set(Objects.requireNonNull(path));
    }

    public String preview(final RenamingStrategy strategy) {

        if (Files.exists(getOldPath())) {
            final String s = strategy.getNameNew(getOldPath());
            Platform.runLater(() -> newPath.set(s));
            return s;
        }
        var exception = new FileNotFoundException(getOldPath().getFileName().toString());
        Platform.runLater(() -> exceptionProperty().set(exception));
        return null;
    }

    public Path rename(final RenamingStrategy strategy) {
        if (isFiltered()) {
            log.warn("Rename called on filtered entry, skipping {}", this);
            return getOldPath();
        }
        try {
            Path newPath = strategy.rename(getOldPath(), null);
            Platform.runLater(() -> commitRename(newPath));
            return newPath;
        } catch (final Exception e) {
            log.debug(e.getLocalizedMessage(), e);
            Platform.runLater(() -> this.exception.set(e));
            return getOldPath();
        }
    }

    public void commitRename(Path newPath) {
        setOldPath(newPath);
        exceptionProperty().set(null);
        // for now set to false to see an immediate effect, preview service should be triggered and should update this any time soon again.
        setWillChange(false);
    }

    // Getter / Setter //


    public String getMovieName() {
        return movieName.get();
    }

    public StringProperty movieNameProperty() {
        return movieName;
    }

    public void setMovieName(String movieName) {
        this.movieName.set(movieName);
    }

    public Path getOldPath() {
        return oldPath.get();
    }

    public void setOldPath(Path oldPath) {
        this.oldPath.set(oldPath);
    }

    public String getNewPath() {
        return newPath.get();
    }

    public void setNewPath(String newPath) {
        this.newPath.set(newPath);
    }

    public Throwable getException() {
        return exception.get();
    }

    public void setException(Throwable exception) {
        this.exception.set(exception);
    }

    public boolean isWillChange() {
        return willChange.get();
    }

    public void setWillChange(boolean willChange) {
        this.willChange.set(willChange);
    }

    public boolean isFiltered() {
        return filtered.get();
    }

    public void setFiltered(boolean filtered) {
        this.filtered.set(filtered);
    }

    public BooleanProperty filteredProperty() {
        return filtered;
    }

    public ObjectProperty<Throwable> exceptionProperty() {
        return exception;
    }

    public StringProperty newPathProperty() {
        return newPath;
    }

    public BooleanProperty willChangeProperty() {
        return this.willChange;
    }

    public ObjectProperty<Path> oldPathProperty() {
        return this.oldPath;
    }
}
