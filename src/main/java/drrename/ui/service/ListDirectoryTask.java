package drrename.ui.service;

import drrename.event.*;
import drrename.model.RenamingEntry;
import javafx.concurrent.Task;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

@Slf4j
public class ListDirectoryTask extends Task<List<RenamingEntry>> {

    private final Path dir;

    private final ApplicationEventPublisher eventPublisher;

    public ListDirectoryTask(final Path dir, ApplicationEventPublisher eventPublisher) {
        this.dir = Objects.requireNonNull(dir);
        this.eventPublisher = Objects.requireNonNull(eventPublisher);
        if (!Files.isDirectory(dir))
            throw new IllegalArgumentException(dir + " not a directory");
    }

    @Override
    protected List<RenamingEntry> call() throws IOException {
        return getEntries(dir);
    }

    List<RenamingEntry> getEntries(final Path dir) throws IOException {
        List<RenamingEntry> result = new ArrayList<>();
        SynchronousUuidEvent event = new StartingListFilesEvent();
        log.debug("Publishing event {}", event);
        eventPublisher.publishEvent(event);

        List<RenamingEntry> smallList = new ArrayList<>();

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir)) {
            for (Path path : stream) {
                if (Thread.interrupted()) {
                    break;
                }
                RenamingEntry newEntry = new RenamingEntry(path);
                result.add(newEntry);
                smallList.add(newEntry);
                if(smallList.size() > 3) {
                    eventPublisher.publishEvent(new NewRenamingEntryEvent(event.getUuid(), smallList));
                    smallList.clear();
                }
            }
        }
        event = new ListFilesFinishedEvent();
        log.debug("Publishing event {}", event);
        eventPublisher.publishEvent(event);
        return result;
    }
}

