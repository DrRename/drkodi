package drrename.ui.service;

import drrename.FileTypeProvider;
import drrename.RenamingControl;
import drrename.Tasks;
import drrename.config.AppConfig;
import javafx.application.Platform;
import javafx.concurrent.Task;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ResourceBundle;

@Slf4j
@Setter
@Component
public class FileTypeService extends FilesService<Void> {

    static final String LOADING_FILE_TYPES = "mainview.status.loading_filetypes";

    public static void setFileType(FileTypeProvider fileTypeProvider, RenamingControl renamingControl) {
        final String fileType = fileTypeProvider.getFileType(renamingControl.getOldPath());
        Platform.runLater(() -> renamingControl.setFileType(fileType));
    }

    private FileTypeProvider fileTypeProvider;

    public FileTypeService(AppConfig appConfig, ResourceBundle resourceBundle) {
        super(appConfig, resourceBundle);
    }

    @Override
    protected Task<Void> createTask() {
        return new Task<>() {
            @Override
            protected Void call() throws Exception {
                log.debug("Starting");
                updateMessage(String.format(getResourceBundle().getString(FileTypeService.LOADING_FILE_TYPES)));
                long cnt = 0;
                for (final RenamingControl p : getRenamingEntries()) {
                    if (isCancelled()) {
                        log.debug("Cancelled");
                        updateMessage(String.format(getResourceBundle().getString(Tasks.MESSAGE_CANCELLED)));
                        break;
                    }
                    FileTypeService.setFileType(fileTypeProvider, p);
                    updateProgress(cnt++, getRenamingEntries().size());
                    if (getAppConfig().isDebug()) {
                        try {
                            Thread.sleep(getAppConfig().getLoopDelayMs());
                        } catch (InterruptedException e) {
                            if (isCancelled()) {
                                log.debug("Cancelled");
                                updateMessage(String.format(getResourceBundle().getString(Tasks.MESSAGE_CANCELLED)));
                                break;
                            }
                        }
                    }
                }
                log.debug("Finished");
                updateMessage(null);
                return null;
            }
        };
    }
}
