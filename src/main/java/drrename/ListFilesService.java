package drrename;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Objects;

import drrename.ListDirectoryTask;
import drrename.ListFilesTask;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class ListFilesService extends Service<Void> {

    private Collection<Path> files;

    private String fileNameFilterRegex;

    private final ConfigurableApplicationContext applicationContext;

    public ListFilesService(ConfigurableApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    protected Task<Void> createTask(){
        // If 'files' is one entry only, and its a directory, use ListDirectoryTask, otherwise use ListFilesTask.
        if(Objects.requireNonNull(files).size() == 1 && Files.isDirectory(files.iterator().next())){
            return new ListDirectoryTask(files.iterator().next(), fileNameFilterRegex, applicationContext);
        }
        return new ListFilesTask(files, fileNameFilterRegex, applicationContext);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " files:" + files.size();
    }

    // Getter / Setter //

    public Collection<Path> getFiles() {
        return files;
    }

    public void setFiles(final Collection<Path> files) {
        this.files = files;
    }

    public String getFileNameFilterRegex() {
        return fileNameFilterRegex;
    }

    public void setFileNameFilterRegex(final String fileNameFilterRegex) {
        this.fileNameFilterRegex = fileNameFilterRegex;
    }

}

