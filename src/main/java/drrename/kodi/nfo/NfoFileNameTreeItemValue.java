/*
 *     Dr.Rename - A Minimalistic Batch Renamer
 *
 *     Copyright (C) 2022
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Affero General Public License as
 *     published by the Free Software Foundation, either version 3 of the
 *     License, or (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Affero General Public License for more details.
 *
 *     You should have received a copy of the GNU Affero General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package drrename.kodi.nfo;

import drrename.kodi.FixFailedException;
import drrename.kodi.WarningsConfig;
import drrename.model.RenamingPath;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

@Slf4j
public class NfoFileNameTreeItemValue extends NfoFileTreeItemValue {

    private final ObjectProperty<NfoFileNameType> type;

    private final WarningsConfig warningsConfig;


    public NfoFileNameTreeItemValue(RenamingPath moviePath, WarningsConfig warningsConfig, Executor executor) {
        super(moviePath, true, executor);
        this.warningsConfig = warningsConfig;
        this.type = new SimpleObjectProperty<>();
        updateStatus();
        missingNfoFileIsWarningProperty().addListener((observable, oldValue, newValue) -> setWarning(calculateWarning()));
    }

    @Override
    protected void updateStatus() {
        var checker = new NfoFileNameChecker();
        var type = checker.checkDir(getRenamingPath().getOldPath());
            setNfoFiles(checker.getNfoFiles());
        setType(type);
        setWarning(calculateWarning());
        setCanFix(isWarning() && getNfoFiles().size() == 1);
    }

    @Override
    protected String updateIdentifier() {
        return "NFO File Name";
    }

    protected String updateMessage(Boolean newValue) {
        if (getType() == null) {
            return "unknown";
        }
        if (newValue) {
            return (getType().toString() + getWarningAdditionalInfo());
        }
        return (getType().toString());
    }

    @Override
    public void fix() throws FixFailedException {
        Path nfoFile = getNfoFile();
        try {
            Path newPath = Files.move(nfoFile, nfoFile.resolveSibling(NfoFiles.DEFAULT_NAME));
            if(!newPath.getFileName().toString().equals(NfoFiles.DEFAULT_NAME)){
                throw new FixFailedException("Rename failed");
            }
        } catch (IOException e) {
            throw new FixFailedException(e);
        }
    }

    private String getWarningAdditionalInfo() {
        return getNfoFiles().isEmpty() ? "" : ": " + getNfoFiles().stream().map(f -> f.getFileName().toString()).collect(Collectors.joining(", "));
    }

    protected boolean calculateWarning() {
        if (NfoFileNameType.NO_FILE.equals(getType()) && !isMissingNfoFileIsWarning()) {
            return false;
        }
        return !NfoFileNameType.MOVIE_NAME.equals(getType()) && !NfoFileNameType.DEFAULT_NAME.equals(getType());
    }

    // Getter / Setter //


    public NfoFileNameType getType() {
        return type.get();
    }

    public ObjectProperty<NfoFileNameType> typeProperty() {
        return type;
    }

    public void setType(NfoFileNameType type) {
        this.type.set(type);
    }

    public WarningsConfig getWarningsConfig() {
        return warningsConfig;
    }


}
