package drrename.ui.service;

import drrename.RenamingStrategy;
import drrename.event.FilePreviewEvent;
import drrename.event.StartingPreviewEvent;
import drrename.model.RenamingEntry;
import javafx.concurrent.Task;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class PreviewTask extends Task<Void> {

    private final List<RenamingEntry> beans;
    private final RenamingStrategy renamingStrategy;
    private final ApplicationEventPublisher applicationEventPublisher;

    @Override
    protected Void call() throws Exception {

        if (beans == null) return null;
        long cnt = 0;
        var event = new StartingPreviewEvent();
        log.debug("Publishing event {}", event);
        applicationEventPublisher.publishEvent(event);
        for (final RenamingEntry p : beans) {
            if (Thread.currentThread().isInterrupted())
                throw new InterruptedException("Cancelled");
            String newName = p.preview(renamingStrategy);
            if (!p.getOldPath().getFileName().toString().equals(newName)) {
                applicationEventPublisher.publishEvent(new FilePreviewEvent(p));
            }
            updateProgress(cnt++, beans.size());
        }
        return null;
    }
}
