package drrename;

import drrename.model.RenamingBean;
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
            return Styles.filteredStyle();
        }
        if (f.willChange()) {
            return Styles.changingStyle();
        }
        if (f.externalChanged()) {
            return Styles.externalChangingStyle();
        }

        return Styles.defaultStyle();

    }

    public static PairSame<Control> buildRenameEntryNode(final RenamingBean f) {

        final Control tLeft = buildLeft(f);
        final Control tRight = buildRight(f);
        return new PairSameImpl<>(tLeft, tRight);
    }

    static String calcJohn(final RenamingBean f) {
        var result = f.getOldPath().getFileName().toString();
//		log.debug("Recalculate text property for left label, new value is {}", result);
        return result;
    }

    static Control buildRight(final RenamingBean renamingBean) {

        final Label tRight = new Label();
        tRight.setPadding(new Insets(2, 2, 2, 2));
        tRight.setMaxWidth(Double.POSITIVE_INFINITY);
        tRight.textProperty().bind(Bindings.createStringBinding(getNewPath(renamingBean), renamingBean.getException(), renamingBean.getNewPath()));
        tRight.styleProperty().bind(
                Bindings.createObjectBinding(() -> calcStyleRight(renamingBean), renamingBean.willChangeProperty(), renamingBean.filteredProperty()));
        return tRight;
    }

    static String calcStyleLeft(final RenamingBean renamingBean) {
        final StringBuilder sb = new StringBuilder();
        if (renamingBean.isFiltered()) {
            sb.append(Styles.filteredStyle());
        }
        if(renamingBean.externalChanged()){
            sb.append(Styles.externalChangedStyle());
        }
        if(renamingBean.getOldPath().toFile().isDirectory()){
            sb.append(Styles.directoryStyle());
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
