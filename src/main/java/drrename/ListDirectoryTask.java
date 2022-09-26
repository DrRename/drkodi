package drrename;

import drrename.event.StartingListFilesEvent;
import drrename.model.RenamingBean;
import drrename.event.FileEntryEvent;
import javafx.concurrent.Task;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.ApplicationEventMulticaster;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

@Slf4j
public class ListDirectoryTask extends Task<Void> {

    private final Path dir;

    private final String fileNameFilterRegex;

    private final ApplicationEventPublisher eventPublisher;

    public ListDirectoryTask(final Path dir, final String fileNameFilterRegex, ApplicationEventPublisher eventPublisher) {
        this.dir = Objects.requireNonNull(dir);
        this.eventPublisher = Objects.requireNonNull(eventPublisher);
        if (!Files.isDirectory(dir))
            throw new IllegalArgumentException(dir + " not a directory");
        this.fileNameFilterRegex = fileNameFilterRegex;
    }

    @Override
    protected Void call() throws IOException {
        getEntries(dir);
        return null;
    }

    void getEntries(final Path dir) throws IOException {
        UUID uuid = UUID.randomUUID();
        eventPublisher.publishEvent(new StartingListFilesEvent(uuid));
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir)) {
            for (final Iterator<Path> it = stream.iterator(); it.hasNext(); ) {
                if (Thread.interrupted()) {
                    break;
                }
                final Path next = it.next();
                if (FilterTask.matches(next.getFileName().toString(), fileNameFilterRegex)) {
                    try {
                        eventPublisher.publishEvent(new FileEntryEvent(uuid, new RenamingBean(next)));
                    } catch (final Exception e) {
                        if (log.isErrorEnabled()) {
                            log.error(e.getLocalizedMessage(), e);
                        }
                    }
                }
            }
        }
    }
}

