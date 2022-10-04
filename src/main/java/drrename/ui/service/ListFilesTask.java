package drrename.ui.service;

import drrename.event.StartingListFilesEvent;
import drrename.model.RenamingEntry;
import drrename.event.NewRenamingEntryEvent;
import javafx.concurrent.Task;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
public class ListFilesTask extends Task<List<RenamingEntry>> {

    private final Collection<Path> files;

    private final ApplicationEventPublisher eventPublisher;


    @Override
    protected List<RenamingEntry> call() {
        return getEntries(files);

    }

    List<RenamingEntry> getEntries(final Collection<Path> files) {
        List<RenamingEntry> result = new ArrayList<>();
        var event = new StartingListFilesEvent();
        eventPublisher.publishEvent(event);
        for (final Path f : files) {
            if (Thread.interrupted()) {
                break;
            }
            var newEntry = new RenamingEntry(f);
            result.add(newEntry);
            eventPublisher.publishEvent(new NewRenamingEntryEvent(event.getUuid(), newEntry));
            updateProgress(result.size(), files.size());

        }
        return result;
    }
}

