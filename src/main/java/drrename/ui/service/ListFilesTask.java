package drrename.ui.service;

import drrename.event.NewRenamingEntryEvent;
import drrename.event.StartingListFilesEvent;
import drrename.model.RenamingControl;
import javafx.concurrent.Task;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;


@Slf4j
public class ListFilesTask extends Task<List<RenamingControl>> {

    private final Collection<Path> files;

    private final ApplicationEventPublisher eventPublisher;

    public ListFilesTask(Collection<Path> files, ApplicationEventPublisher eventPublisher) {
        this.files = Objects.requireNonNull(files);
        this.eventPublisher = eventPublisher;
    }


    @Override
    protected List<RenamingControl> call() {
        return getEntries(files);
    }

    List<RenamingControl> getEntries(final Collection<Path> files) {
        List<RenamingControl> result = new ArrayList<>();
        var event = new StartingListFilesEvent();
        log.debug("Publishing event {}", event);
        eventPublisher.publishEvent(event);
        for (final Path f : files) {
            if (isCancelled()) {
                break;
            }
            var newEntry = new RenamingControl(f);
            result.add(newEntry);
            eventPublisher.publishEvent(new NewRenamingEntryEvent(event.getUuid(), newEntry));
            updateProgress(result.size(), files.size());

        }
        return result;
    }
}

