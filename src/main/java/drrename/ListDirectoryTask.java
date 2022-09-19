package drrename;

import drrename.RenamingBean;
import drrename.event.FileEntryEvent;
import drrename.FilterTask;
import javafx.concurrent.Task;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ConfigurableApplicationContext;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

@Slf4j
public class ListDirectoryTask extends Task<Void> {

    private final Path dir;

    private final String fileNameFilterRegex;

    private final ConfigurableApplicationContext applicationContext;

    public ListDirectoryTask(final Path dir, final String fileNameFilterRegex, ConfigurableApplicationContext applicationContext) {
        this.dir = Objects.requireNonNull(dir);
        this.applicationContext = Objects.requireNonNull(applicationContext);
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
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir)) {
            for (final Iterator<Path> it = stream.iterator(); it.hasNext(); ) {
                if (Thread.interrupted()) {
                    break;
                }
                final Path next = it.next();

                    if (FilterTask.matches(next.getFileName().toString(), fileNameFilterRegex)) {
                        try {
                            applicationContext.publishEvent(new FileEntryEvent(new RenamingBean(next)));
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

