package drrename.ui.service;

import drrename.strategy.RenamingStrategy;
import drrename.event.FilePreviewEvent;
import drrename.event.StartingPreviewEvent;
import drrename.model.RenamingControl;
import javafx.concurrent.Task;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class PreviewTask extends Task<List<RenamingControl>> {

    private final List<RenamingControl> beans;
    private final RenamingStrategy renamingStrategy;
    private final ApplicationEventPublisher applicationEventPublisher;

    @Override
    protected List<RenamingControl> call() throws Exception {

        List<RenamingControl> result = new ArrayList<>();
        if (beans == null) return result;
        var event = new StartingPreviewEvent();
        log.debug("Publishing event {}", event);
        applicationEventPublisher.publishEvent(event);
        long cnt = 0;
        for (final RenamingControl p : beans) {
            if (Thread.currentThread().isInterrupted())
                throw new InterruptedException("Cancelled");
            String newName = p.preview(renamingStrategy);
            if (!p.getOldPath().getFileName().toString().equals(newName)) {
                applicationEventPublisher.publishEvent(new FilePreviewEvent(p));
                result.add(p);
            }
            updateProgress(cnt++, beans.size());
        }
        return result;
    }
}
