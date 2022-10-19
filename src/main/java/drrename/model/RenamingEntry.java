package drrename.model;

import drrename.strategy.RenamingStrategy;
import drrename.ui.Styles;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import lombok.extern.slf4j.Slf4j;

import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.concurrent.Callable;

@Slf4j
public class RenamingEntry {

    private final ObjectProperty<Path> oldPath;

    private final StringProperty newPath;

    private final ObjectProperty<Throwable> exception;

    private final BooleanProperty willChange;

    private final BooleanProperty filtered;

    private final StringProperty fileType;

    private final Control leftControl;

    private final Control rightControl;

    public RenamingEntry(final Path path) {

        this.oldPath = new SimpleObjectProperty<>(Objects.requireNonNull(path));
        this.newPath = new SimpleStringProperty();
        exception = new SimpleObjectProperty<>();
        this.willChange = new SimpleBooleanProperty();
        this.fileType = new SimpleStringProperty();
        this.filtered = new SimpleBooleanProperty();
        newPath.addListener((v, o, n) -> willChange.set(!getOldPath().getFileName().toString().equals(n)));
        oldPath.addListener((observable, oldValue, newValue) -> willChange.set(!getOldPath().getFileName().toString().equals(getNewPath())));
        leftControl = buildLeft();
        rightControl = buildRight();
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

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{" + getOldPath().toString() + "}";
    }

    protected Control buildLeft() {

        final Label tLeft = new Label();
        tLeft.setPadding(new Insets(2, 2, 2, 2));
        tLeft.textProperty().bind(buildTextBindingLeft());
        tLeft.styleProperty().bind(buildStyleBindingLeft());

        return tLeft;
    }

    protected ObservableValue<String> buildStyleBindingLeft() {
        return Bindings.createObjectBinding(this::calcStyleLeft);
    }

    protected ObservableValue<String> buildTextBindingLeft() {
        return Bindings.createObjectBinding(this::calculateOldPath, oldPathProperty());
    }

    protected String calculateOldPath() {
        return getOldPath().getFileName().toString();
    }

    protected String calcStyleLeft() {
        final StringBuilder sb = new StringBuilder();
        if (getOldPath().toFile().isDirectory()) {
            sb.append(Styles.directoryStyle());
        }
        if (sb.toString().length() > 0)
            return sb.toString();
        return Styles.defaultStyle();
    }

    protected Control buildRight() {

        final Label tRight = new Label();
        tRight.setPadding(new Insets(2, 2, 2, 2));
        tRight.setMaxWidth(Double.POSITIVE_INFINITY);
        tRight.textProperty().bind(Bindings.createStringBinding(buildTextRight(), exceptionProperty(), newPathProperty()));
        tRight.styleProperty().bind(
                Bindings.createObjectBinding(this::calcStyleRight, willChangeProperty(), exceptionProperty(), newPathProperty()));
        return tRight;
    }

    protected Callable<String> buildTextRight() {
        return () -> {
            if (getException() != null)
                return getException().toString();
            return getNewPath() == null ? null : getNewPath();
        };
    }

    protected String calcStyleRight() {
        if (willChange()) {
            return Styles.changingStyle();
        }
        return Styles.defaultStyle();
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

    public String getNewPath() {
        return newPath.get();
    }

    public Throwable getException() {
        return exception.get();
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

    public Control getLeftControl() {
        return leftControl;
    }

    public Control getRightControl() {
        return rightControl;
    }
}
