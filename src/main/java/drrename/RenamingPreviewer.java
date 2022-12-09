package drrename;

import drrename.commons.RenamingPath;
import drrename.strategy.RenamingStrategy;
import javafx.application.Platform;

import java.io.FileNotFoundException;
import java.nio.file.Files;

public class RenamingPreviewer {

    public String preview(RenamingPath renamingPath, RenamingStrategy renamingStrategy) {
        if (Files.exists(renamingPath.getOldPath())) {
            final String s = renamingStrategy.getNameNew(renamingPath.getOldPath());
            Platform.runLater(() -> renamingPath.setNewPath(s));
            return s;
        }
        var exception = new FileNotFoundException(renamingPath.getOldPath().getFileName().toString());
        Platform.runLater(() -> renamingPath.setException(exception));
        return null;
    }
}
