package drrename;

import javafx.application.Platform;
import javafx.beans.property.*;
import lombok.extern.slf4j.Slf4j;

import java.nio.file.*;
import java.util.Objects;

@Slf4j
public class RenamingBean   {

    private final ObjectProperty<Path> oldPath;
    private final StringProperty newPath;
    private final ObjectProperty<Throwable> exception;
    private final BooleanProperty filtered;
    private final BooleanProperty willChange;

    private final BooleanProperty externalChanged;

    public RenamingBean(final Path path) {

        this.oldPath = new SimpleObjectProperty<>(Objects.requireNonNull(path));
        if (!Files.isReadable(path))
            throw new IllegalArgumentException("Cannot read " + path);
        this.newPath = new SimpleStringProperty();
        exception = new SimpleObjectProperty<>();
        this.filtered = new SimpleBooleanProperty();
        this.willChange = new SimpleBooleanProperty();
        this.externalChanged = new SimpleBooleanProperty();
        newPath.addListener((v, o, n) -> {
            willChange.set(!getOldPath().getFileName().toString().equals(n));
        });
        filtered.addListener((v, o, n) -> {
            if ((n != null) && n) {
                willChange.set(false);
                newPath.set(null);
            }
        });
    }

    public void preview(final RenamingStrategy strategy) {

        try {
            final String s = strategy.getNameNew(getOldPath());
            Platform.runLater(() -> newPath.set(s));
        } catch (final Exception e) {
            if (log.isDebugEnabled()) {
                log.debug(e.getLocalizedMessage(), e);
            }
            Platform.runLater(() -> this.exception.set(e));
        }
    }

    public void rename(final RenamingStrategy strategy) {

        try {
            final Path s = strategy.rename(getOldPath(), null);
        } catch (final Exception e) {
            Platform.runLater(() -> this.exception.set(e));
        }
    }



    @Override
    public String toString() {
        return getOldPath().toString();
    }

    // Getter / Setter //

    public StringProperty getNewPath() {

        return newPath;
    }

    public ObjectProperty<Throwable> getException() {

        return exception;
    }

    public BooleanProperty filteredProperty() {
        return this.filtered;
    }

    public boolean isFiltered() {
        return this.filteredProperty().get();
    }

    public void setFiltered(final boolean filtered) {
        this.filteredProperty().set(filtered);
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


    public boolean externalChanged() {
        return externalChanged.get();
    }

    public BooleanProperty externalChangedProperty() {
        return externalChanged;
    }

    public void setExternalChanged(boolean externalChanged) {
        this.externalChanged.set(externalChanged);
    }
}
