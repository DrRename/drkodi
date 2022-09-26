package drrename;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import drrename.event.FilePreviewEvent;
import drrename.event.FileRenamedEvent;
import drrename.model.RenamingBean;
import javafx.concurrent.Task;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;

@RequiredArgsConstructor
public class PreviewTask extends Task<Void> {

    private final List<RenamingBean> beans;
    private final RenamingStrategy renamingStrategy;

    private final ApplicationEventPublisher applicationEventPublisher;

    @Override
    protected Void call() throws Exception {

        if(beans == null)return null;
        long cnt = 0;
        for (final RenamingBean p : beans) {
            if (Thread.currentThread().isInterrupted())
                throw new InterruptedException("Cancelled");
            if (!p.isFiltered()) {
                String newName = p.preview(renamingStrategy);
                if(!p.getOldPath().getFileName().toString().equals(newName)){
                        applicationEventPublisher.publishEvent(new FilePreviewEvent(p));
                    }
                }
                updateProgress(cnt++, beans.size());
            }

        return null;
    }
}
