package drrename.ui;

import drrename.model.RenamingEntry;
import drrename.ui.Styles;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.scene.control.Control;
import javafx.scene.control.Label;

import java.util.concurrent.Callable;

public class RenamingBeanControlBuilder {
    static Callable<String> getNewPath(final RenamingEntry f) {

        return () -> {
            if (f.getException().get() != null)
                return f.getException().get().getLocalizedMessage();
            return f.getNewPath().getValue() == null ? null : f.getNewPath().getValue();
        };
    }

    static String calculateOldPath(final RenamingEntry f) {
        return f.getOldPath().getFileName().toString();
    }

    static String calcStyleRight(final RenamingEntry f) {

//        if (f.isFiltered()) {
//            return Styles.filteredStyle();
//        }
        if (f.willChange()) {
            return Styles.changingStyle();
        }
//        if (f.externalChanged()) {
//            return Styles.externalChangingStyle();
//        }

        return Styles.defaultStyle();

    }

    public static Control buildRight(final RenamingEntry renamingEntry) {

        final Label tRight = new Label();
        tRight.setPadding(new Insets(2, 2, 2, 2));
        tRight.setMaxWidth(Double.POSITIVE_INFINITY);
        tRight.textProperty().bind(Bindings.createStringBinding(getNewPath(renamingEntry), renamingEntry.getException(), renamingEntry.getNewPath()));
        tRight.styleProperty().bind(
                Bindings.createObjectBinding(() -> calcStyleRight(renamingEntry), renamingEntry.willChangeProperty()));
        return tRight;
    }

    static String calcStyleLeft(final RenamingEntry renamingEntry) {
        final StringBuilder sb = new StringBuilder();
//        if (renamingBean.isFiltered()) {
//            sb.append(Styles.filteredStyle());
//        }
//        if(renamingBean.externalChanged()){
//            sb.append(Styles.externalChangedStyle());
//        }
        if(renamingEntry.getOldPath().toFile().isDirectory()){
            sb.append(Styles.directoryStyle());
        }
        if (sb.toString().length() > 0)
            return sb.toString();
        return Styles.defaultStyle();
    }

    static ObservableValue<String> buildTextBindingLeft(RenamingEntry f) {
        return Bindings.createObjectBinding(() -> calculateOldPath(f), f.oldPathProperty());
    }

    static ObservableValue<String> buildStyleBindingLeft(RenamingEntry f) {
        return Bindings.createObjectBinding(() -> calcStyleLeft(f));
    }

    public static Control buildLeft(final RenamingEntry f) {

        final Label tLeft = new Label();
        tLeft.setPadding(new Insets(2, 2, 2, 2));
        tLeft.textProperty().bind(buildTextBindingLeft(f));
        tLeft.styleProperty().bind(buildStyleBindingLeft(f));

        return tLeft;
    }
}
