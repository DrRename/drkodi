package drrename.ui.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import drrename.model.RenamingEntry;
import javafx.concurrent.Service;

public abstract class FilesService<V> extends Service<V> {

    private List<RenamingEntry> renamingEntries;

    public FilesService() {

        this.renamingEntries = new ArrayList<>();
    }

    public List<RenamingEntry> getRenamingEntries() {

        return renamingEntries;
    }

    public void setRenamingEntries(final Collection<? extends RenamingEntry> renamingEntries) {

        this.renamingEntries = new ArrayList<>(renamingEntries);
    }

    @Override
    public void reset() {
        super.reset();
        this.renamingEntries = new ArrayList<>();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " file cnt: " + (renamingEntries == null ? 0 : renamingEntries.size());
    }
}
