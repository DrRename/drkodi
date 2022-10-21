package drrename.model;

import drrename.ui.Styles;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import lombok.extern.slf4j.Slf4j;

import java.nio.file.Path;
import java.util.concurrent.Callable;

@Slf4j
public class RenamingControl extends RenamingPath {

    private final StringProperty fileType;

    private final Control leftControl;

    private final Control rightControl;

    public RenamingControl(final Path path) {
        super(path);

        this.fileType = new SimpleStringProperty();
        newPath.addListener((v, o, n) -> willChange.set(!getOldPath().getFileName().toString().equals(n)));
        oldPath.addListener((observable, oldValue, newValue) -> willChange.set(!getOldPath().getFileName().toString().equals(getNewPath())));
        leftControl = buildLeft();
        rightControl = buildRight();
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
        if (isWillChange()) {
            return Styles.changingStyle();
        }
        return Styles.defaultStyle();
    }

    // Getter / Setter //


    public StringProperty fileTypeProperty() {
        return fileType;
    }

    public String getFileType() {
        return fileType.get();
    }

    public void setFileType(String fileType) {
        fileTypeProperty().set(fileType);
    }

    public Control getLeftControl() {
        return leftControl;
    }

    public Control getRightControl() {
        return rightControl;
    }
}
