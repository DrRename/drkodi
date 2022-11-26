package drrename.ui.service;

import drrename.config.AppConfig;
import drrename.strategy.RenamingStrategy;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

import java.util.Objects;
import java.util.ResourceBundle;

public abstract class StrategyService<V> extends FilesService<V> {

    protected final ObjectProperty<RenamingStrategy> renamingStrategy;

    public StrategyService(AppConfig appConfig, ResourceBundle resourceBundle) {
        super(appConfig, resourceBundle);
        this.renamingStrategy = new SimpleObjectProperty<>();
    }

    // FX Getter / Setter //

    public ObjectProperty<RenamingStrategy> renamingStrategyProperty() {
        return this.renamingStrategy;
    }

    public RenamingStrategy getRenamingStrategy() {
        return this.renamingStrategyProperty().get();
    }

    public void setRenamingStrategy(final RenamingStrategy renamingStrategy) {
        this.renamingStrategyProperty().set(Objects.requireNonNull(renamingStrategy));
    }
}
