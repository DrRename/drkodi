package drrename.model;

import drrename.RenamingStrategy;
import javafx.application.Platform;
import javafx.beans.property.*;
import lombok.extern.slf4j.Slf4j;

import java.io.FileNotFoundException;
import java.nio.file.*;
import java.util.Objects;

@Slf4j
public class RenamingEntry {

    private final ObjectProperty<Path> oldPath;
    private final StringProperty newPath;
    private final ObjectProperty<Throwable> exception;
    private final BooleanProperty willChange;
    private final BooleanProperty filtered;
    private final StringProperty fileType;

    public RenamingEntry(final Path path) {

        this.oldPath = new SimpleObjectProperty<>(Objects.requireNonNull(path));
        this.newPath = new SimpleStringProperty();
        exception = new SimpleObjectProperty<>();
        this.willChange = new SimpleBooleanProperty();
        this.fileType = new SimpleStringProperty();
        this.filtered = new SimpleBooleanProperty();
        newPath.addListener((v, o, n) -> willChange.set(!getOldPath().getFileName().toString().equals(n)));
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
        try{
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
        setWillChange(false);
        exceptionProperty().set(null);
    }

    @Override
    public String toString() {
        return getOldPath().toString();
    }

    // Getter / Setter //


    public BooleanProperty filteredProperty() {
        return filtered;
    }

    public boolean isFiltered() {
        return filtered.get();
    }

    public void setFiltered(boolean filtered) {
        this.filtered.set(filtered);
    }

    public StringProperty fileTypeProperty() {
        return fileType;
    }

    public String getFileType() {
        return fileType.get();
    }

    public void setFileType(String fileType) {
        fileTypeProperty().set(fileType);
    }

    public void setNewPath(String newPath) {
        this.newPath.set(newPath);
    }

    public StringProperty getNewPath() {

        return newPath;
    }

    public ObjectProperty<Throwable> exceptionProperty() {
        return exception;
    }

    public StringProperty newPathProperty() {
        return newPath;
    }

    public ObjectProperty<Throwable> getException() {

        return exception;
    }

    public BooleanProperty willChangeProperty() {
        return this.willChange;
    }

    public boolean willChange() {
        return this.willChangeProperty().get();
    }

    public void setWillChange(final boolean willChange) {
        this.willChangeProperty().set(willChange);
    }

    public ObjectProperty<Path> oldPathProperty() {
        return this.oldPath;
    }

    public Path getOldPath() {
        return this.oldPathProperty().get();
    }

    public void setOldPath(final Path oldPath) {
        this.oldPathProperty().set(oldPath);
    }


}
