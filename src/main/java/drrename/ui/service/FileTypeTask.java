package drrename.ui.service;

import drrename.FileTypeProvider;
import drrename.model.RenamingControl;
import javafx.application.Platform;
import javafx.concurrent.Task;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class FileTypeTask extends Task<Void> {

    private final List<RenamingControl> beans;
    private final FileTypeProvider fileTypeProvider;

    public static void setFileType(FileTypeProvider fileTypeProvider, RenamingControl renamingControl) {
        final String fileType = fileTypeProvider.getFileType(renamingControl.getOldPath());
        Platform.runLater(() -> renamingControl.setFileType(fileType));
    }

    @Override
    protected Void call() throws Exception {

        if (beans == null) return null;
        long cnt = 0;
        for (final RenamingControl p : beans) {
            if (Thread.currentThread().isInterrupted())
                throw new InterruptedException("Cancelled");
            setFileType(fileTypeProvider, p);
            updateProgress(cnt++, beans.size());
        }
        return null;
    }
}
