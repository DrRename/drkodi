package drrename;

import drrename.event.StartingListFilesEvent;
import drrename.model.RenamingBean;
import drrename.event.FileEntryEvent;
import javafx.concurrent.Task;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.ApplicationEventMulticaster;

import java.nio.file.Path;
import java.util.Collection;
import java.util.Objects;
import java.util.UUID;

@RequiredArgsConstructor
@Slf4j
public class ListFilesTask extends Task<Void> {

    private final Collection<Path> files;

    private final String fileNameFilterRegex;

    private final ApplicationEventPublisher eventPublisher;


    @Override
    protected Void call() {
        if(files != null)
            getEntries(files);
            return null;
    }

    void getEntries(final Collection<Path> files) {
        long cnt = 0;
        UUID uuid = UUID.randomUUID();
        eventPublisher.publishEvent(new StartingListFilesEvent(uuid));
        for (final Path f : files) {
            if (Thread.interrupted()) {
                break;
            }
            if (FilterTask.matches(f.toFile().getName(), fileNameFilterRegex)) {
                eventPublisher.publishEvent(new FileEntryEvent(uuid, new RenamingBean(f)));
                updateProgress(cnt++, files.size());
            }
        }
    }
}

