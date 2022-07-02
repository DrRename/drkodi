package com.github.drrename;

import java.util.List;

import com.kerner1000.drrename.RenamingBean;
import javafx.concurrent.Task;

public class FilterTask extends Task<Void> {

    private final List<RenamingBean> entries;
    private final String fileNameFilterRegex;

    public static boolean matches(final String fileName, final String fileNameFilterRegex) {
        if ((fileNameFilterRegex != null) && (fileNameFilterRegex.length() > 0))
            return fileName.toLowerCase().contains(fileNameFilterRegex);
        else
            return true;
    }

    public FilterTask(final List<RenamingBean> entries, final String fileNameFilterRegex) {
        this.entries = entries;
        this.fileNameFilterRegex = fileNameFilterRegex;

    }

    @Override
    protected Void call() {
        for (final RenamingBean e : entries) {
            final String fileName = e.getOldPath().getFileName().toString();
            e.setFiltered(!matches(fileName, fileNameFilterRegex));
        }
        return null;
    }

}
