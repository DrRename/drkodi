package drrename;

import drrename.commons.RenamingPath;
import drrename.strategy.RenamingStrategy;
import javafx.application.Platform;
import lombok.extern.slf4j.Slf4j;

import java.nio.file.Path;

@Slf4j
public class Renamer {

    public Path rename(RenamingPath renamingPath, RenamingStrategy renamingStrategy) {
        if (renamingPath.isFiltered()) {
            log.warn("Rename called on filtered entry, skipping {}", this);
            return renamingPath.getOldPath();
        }
        try {
            Path newPath = renamingStrategy.rename(renamingPath.getOldPath(), null);
            Platform.runLater(() -> commitRename(renamingPath, newPath));
            return newPath;
        } catch (final Exception e) {
            log.debug("Failed to rename", e);
            Platform.runLater(() -> renamingPath.setException((e)));
            return renamingPath.getOldPath();
        }
    }

    public static void commitRename(RenamingPath renamingPath, Path newPath) {
        renamingPath.setOldPath(newPath);
        renamingPath.setException(null);
        // for now set to 'false' to see an immediate effect, preview service should be triggered and should update this any time soon again.
        renamingPath.setWillChange(false);
    }
}
