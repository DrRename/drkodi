package com.github.drrename;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.kerner1000.drrename.RenamingBean;
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

        this.files = Objects.requireNonNull(events);
    }

    @Override
    public String toString() {

        return getClass().getSimpleName() + " [" + files.size() + "]";
    }
}
