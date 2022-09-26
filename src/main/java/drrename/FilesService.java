package drrename;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import drrename.model.RenamingBean;
import javafx.concurrent.Service;

public abstract class FilesService<V> extends Service<V> {

    private List<RenamingBean> files;

    public FilesService() {

        this.files = new ArrayList<>();
    }

    public List<RenamingBean> getFiles() {

        return files;
    }

    public void setFiles(final List<RenamingBean> events) {

        this.files = new ArrayList<>(events);
    }

    @Override
    public boolean cancel() {
        this.files = null;
        return super.cancel();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " file cnt: " + (files == null ? 0 : files.size());
    }
}
