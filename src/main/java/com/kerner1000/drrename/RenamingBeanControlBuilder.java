package com.kerner1000.drrename;

import javafx.beans.binding.Bindings;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import net.sf.kerner.utils.pair.PairSame;
import net.sf.kerner.utils.pair.PairSameImpl;

import java.util.concurrent.Callable;

public class RenamingBeanControlBuilder {
    static Callable<String> getNewPath(final RenamingBean f) {

        return () -> {
            if (f.getException().get() != null)
                return f.getException().get().getLocalizedMessage();
            return f.getNewPath().getValue() == null ? null : f.getNewPath().getValue();
        };
    }

    static String calcStyleRight(final RenamingBean f) {
        final StringBuilder sb = new StringBuilder();

        if (f.isFiltered()) {
            sb.append(Styles.filteredStyle());
        }
        if (f.willChange()) {
            sb.append(Styles.changingStyle());
        }
        if (f.externalChanged()) {
            sb.append(Styles.externalChangingStyle());
        }

        if (sb.toString().length() > 0)
            return sb.toString();

        return Styles.defaultStyle();

    }

    static PairSame<Control> buildRenameEntryNode(final RenamingBean f) {

        final Control tLeft = buildLeft(f);
        final Control tRight = buildRight(f);
        return new PairSameImpl<>(tLeft, tRight);
    }

    static String calcJohn(final RenamingBean f) {
        var result = f.getOldPath().getFileName().toString();
//		log.debug("Recalculate text property for left label, new value is {}", result);
        return result;
    }

    static Control buildRight(final RenamingBean f) {

        final Label tRight = new Label();
        tRight.setPadding(new Insets(2, 2, 2, 2));
        tRight.setMaxWidth(Double.POSITIVE_INFINITY);
        tRight.textProperty().bind(Bindings.createStringBinding(getNewPath(f), f.getException(), f.getNewPath()));
        tRight.styleProperty().bind(
                Bindings.createObjectBinding(() -> calcStyleRight(f), f.willChangeProperty(), f.filteredProperty()));
        return tRight;
    }

    static String calcStyleLeft(final RenamingBean f) {
        final StringBuilder sb = new StringBuilder();
        if (f.isFiltered()) {
            sb.append(Styles.filteredStyle());
        }
        if(f.externalChanged()){
            sb.append(Styles.externalChangedStyle());
        }
        if (sb.toString().length() > 0)
            return sb.toString();

        return Styles.defaultStyle();
    }

    static ObservableValue<String> buildTextBindingLeft(RenamingBean f) {
        return Bindings.createObjectBinding(() -> calcJohn(f), f.oldPathProperty());
    }

    static ObservableValue<String> buildStyleBindingLeft(RenamingBean f) {
        return Bindings.createObjectBinding(() -> calcStyleLeft(f), f.filteredProperty(), f.externalChangedProperty());
    }

    static Control buildLeft(final RenamingBean f) {

        final Label tLeft = new Label();
        tLeft.setPadding(new Insets(2, 2, 2, 2));
        tLeft.textProperty().bind(buildTextBindingLeft(f));
        tLeft.styleProperty().bind(buildStyleBindingLeft(f));

        return tLeft;
    }
}
