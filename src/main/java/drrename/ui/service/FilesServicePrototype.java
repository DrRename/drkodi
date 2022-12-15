package drrename.ui.service;

import drrename.ServicePrototype;
import drrename.RenamingControl;
import drrename.config.AppConfig;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.ResourceBundle;

@Getter
public abstract class FilesServicePrototype<V> extends ServicePrototype<V> {

    private List<RenamingControl> renamingEntries;

    public FilesServicePrototype(AppConfig appConfig, ResourceBundle resourceBundle) {
        super(appConfig, resourceBundle);
        this.renamingEntries = new ArrayList<>();
    }

    public void setRenamingEntries(final Collection<? extends RenamingControl> renamingEntries) {
        this.renamingEntries = new ArrayList<>(renamingEntries);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " file cnt: " + (renamingEntries == null ? 0 : renamingEntries.size());
    }
}
