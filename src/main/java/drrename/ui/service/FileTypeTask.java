package drrename.ui.service;

import drrename.FileTypeProvider;
import drrename.event.StartingFileTypeEvent;
import drrename.model.RenamingEntry;
import javafx.application.Platform;
import javafx.concurrent.Task;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class FileTypeTask extends Task<Void> {

    private final List<RenamingEntry> beans;
    private final FileTypeProvider fileTypeProvider;

    public static void setFileType(FileTypeProvider fileTypeProvider, RenamingEntry renamingEntry) {
        final String fileType = fileTypeProvider.getFileType(renamingEntry.getOldPath());
        Platform.runLater(() -> renamingEntry.setFileType(fileType));
    }

    @Override
    protected Void call() throws Exception {

        if (beans == null) return null;
        long cnt = 0;
        for (final RenamingEntry p : beans) {
            if (Thread.currentThread().isInterrupted())
                throw new InterruptedException("Cancelled");
            setFileType(fileTypeProvider, p);
            updateProgress(cnt++, beans.size());
        }
        return null;
    }
}
