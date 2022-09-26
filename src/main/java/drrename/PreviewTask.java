package drrename;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import drrename.event.FilePreviewEvent;
import drrename.event.FileRenamedEvent;
import drrename.event.StartingListFilesEvent;
import drrename.event.StartingPreviewEvent;
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
        UUID uuid = UUID.randomUUID();
        applicationEventPublisher.publishEvent(new StartingPreviewEvent(uuid));
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
