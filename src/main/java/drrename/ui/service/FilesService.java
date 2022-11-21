package drrename.ui.service;

import drrename.RenamingControl;
import javafx.concurrent.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class FilesService<V> extends Service<V> {

    private List<RenamingControl> renamingEntries;

    public FilesService() {

        this.renamingEntries = new ArrayList<>();
    }

    public List<RenamingControl> getRenamingEntries() {

        return renamingEntries;
    }

    public void setRenamingEntries(final Collection<? extends RenamingControl> renamingEntries) {

        this.renamingEntries = new ArrayList<>(renamingEntries);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " file cnt: " + (renamingEntries == null ? 0 : renamingEntries.size());
    }
}
