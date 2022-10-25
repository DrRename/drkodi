package drrename.ui.service;

import drrename.FileTypeProvider;
import javafx.concurrent.Task;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FileTypeService extends FilesService<Void> {

    private FileTypeProvider fileTypeProvider;

    @Override
    protected Task<Void> createTask() {
        return new FileTypeTask(getRenamingEntries(), getFileTypeProvider());
    }

    public FileTypeProvider getFileTypeProvider() {
        return fileTypeProvider;
    }

    public void setFileTypeProvider(FileTypeProvider fileTypeProvider) {
        this.fileTypeProvider = fileTypeProvider;
    }
}
