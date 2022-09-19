package drrename;

import drrename.model.RenamingBean;
import drrename.event.FileEntryEvent;
import javafx.concurrent.Task;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ConfigurableApplicationContext;

import java.nio.file.Path;
import java.util.Collection;
import java.util.Objects;

@Slf4j
public class ListFilesTask extends Task<Void> {

    private final Collection<Path> files;

    private final String fileNameFilterRegex;

    private final ConfigurableApplicationContext applicationContext;

    public ListFilesTask(final Collection<Path> files, final String fileNameFilterRegex, ConfigurableApplicationContext applicationContext) {

        this.files = Objects.requireNonNull(files);
        this.fileNameFilterRegex = fileNameFilterRegex;
        this.applicationContext = Objects.requireNonNull(applicationContext);
    }

    @Override
    protected Void call() {
            getEntries(files);
            return null;
    }

    void getEntries(final Collection<Path> files) {
        long cnt = 0;
        for (final Path f : files) {
            if (Thread.interrupted()) {
                break;
            }
            if (FilterTask.matches(f.toFile().getName(), fileNameFilterRegex)) {
                applicationContext.publishEvent(new FileEntryEvent(new RenamingBean(f)));
                updateProgress(cnt++, files.size());
            }
        }
    }
}

