package drrename;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Objects;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.ApplicationEventMulticaster;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class ListFilesService extends Service<Void> {

    private Collection<Path> files;

    private String fileNameFilterRegex;

    private final ApplicationEventPublisher eventPublisher;

    @Override
    protected Task<Void> createTask(){
        // If 'files' is one entry only, and its a directory, use ListDirectoryTask, otherwise use ListFilesTask.
        if(files != null && files.size() == 1 && Files.isDirectory(files.iterator().next())){
            return new ListDirectoryTask(files.iterator().next(), fileNameFilterRegex, eventPublisher);
        }
        return new ListFilesTask(files, fileNameFilterRegex, eventPublisher);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " file cnt: " + (files == null ? 0 : files.size());
    }

    // Getter / Setter //

    public Collection<Path> getFiles() {
        return files;
    }

    public void setFiles(final Collection<Path> files) {
        this.files = files;
    }

    @Override
    public boolean cancel() {
        this.files = null;
        return super.cancel();
    }

    public String getFileNameFilterRegex() {
        return fileNameFilterRegex;
    }

    public void setFileNameFilterRegex(final String fileNameFilterRegex) {
        this.fileNameFilterRegex = fileNameFilterRegex;
    }

}

