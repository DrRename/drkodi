package drrename;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import drrename.model.RenamingBean;
import javafx.concurrent.Task;

public class PreviewTask extends Task<Void> {

    final List<RenamingBean> beans;
    final RenamingStrategy renamingStrategy;

    public PreviewTask(final List<RenamingBean> beans, final RenamingStrategy renamingStrategy) {

        this.beans = new ArrayList(Objects.requireNonNull(beans));
        this.renamingStrategy = Objects.requireNonNull(renamingStrategy);
    }

    @Override
    protected Void call() throws Exception {

        long cnt = 0;
        for (final RenamingBean p : beans) {
            if (Thread.currentThread().isInterrupted())
                throw new InterruptedException("Cancelled");
            if (!p.isFiltered()) {
                p.preview(renamingStrategy);
                updateProgress(cnt++, beans.size());
            }
        }
        return null;
    }
}
