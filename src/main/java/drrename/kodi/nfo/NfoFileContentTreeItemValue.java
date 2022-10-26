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

import drrename.model.RenamingPath;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Path;
import java.util.concurrent.Executor;

@Slf4j
public abstract class NfoFileContentTreeItemValue extends NfoFileTreeItemValue {

    private final ObjectProperty<NfoFileContentType> parseResult;

    public NfoFileContentTreeItemValue(RenamingPath moviePath, boolean fixable, Executor executor) {
        super(moviePath, fixable, executor);
        this.parseResult = new SimpleObjectProperty<>();
        parseResult.addListener((observable, oldValue, newValue) -> setWarning(newValue.isWarning(isMissingNfoFileIsWarning())));
        missingNfoFileIsWarningProperty().addListener((observable, oldValue, newValue) -> setWarning(getParseResult().isWarning(isMissingNfoFileIsWarning())));
        updateStatus();
    }

    protected NfoFileContentType parseNfoFile() {

        try {
            var nfoFiles = new NfoFileCollector().collectNfoFiles(getRenamingPath().getOldPath());
            if (nfoFiles.isEmpty()) {
                return NfoFileContentType.NO_FILE;
            }
            // take a look only at the first file
            var result = parseNfoFile(nfoFiles.get(0));
            setNfoFiles(nfoFiles);
            return result;
        } catch (IOException e) {
            log.debug("Failed to parse NFO file. Reason: {}", e.getLocalizedMessage());
            return NfoFileContentType.EXCEPTION;
        }
    }


    protected abstract NfoFileContentType parseNfoFile(Path child);


    @Override
    protected void updateStatus() {
        parseResult.set(parseNfoFile());
    }

    @Override
    protected String updateMessage(Boolean warning) {
        return getParseResult().toString();
    }

    @Override
    public void fix() {
        throw new IllegalStateException("Cannot fix");
    }

    // Getter / Setter //

    public NfoFileContentType getParseResult() {
        return parseResult.get();
    }

    public ObjectProperty<NfoFileContentType> parseResultProperty() {
        return parseResult;
    }

    public void setParseResult(NfoFileContentType parseResult) {
        this.parseResult.set(parseResult);
    }


}
