/*
 *     Dr.Rename - A Minimalistic Batch Renamer
 *
 *     Copyright (C) 2022
 *
 *     This file is part of Dr.Rename.
 *
 *     You can redistribute it and/or modify it under the terms of the GNU Affero
 *     General Public License as published by the Free Software Foundation, either
 *     version 3 of the License, or (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful, but WITHOUT
 *     ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 *     FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License
 *     for more details.
 *
 *     You should have received a copy of the GNU Affero General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package drrename.ui.kodi;

import drrename.kodi.FixFailedException;
import drrename.kodi.SubdirsCheckResult;
import drrename.kodi.SubdirsChecker;
import drrename.kodi.WarningsConfig;
import drrename.util.DrRenameUtil;
import drrename.model.RenamingPath;
import javafx.concurrent.WorkerStateEvent;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;

@Slf4j
public class SubdirsTreeItemValue extends KodiTreeItemValue<SubdirsCheckResult> {

    private SubdirsCheckResult checkResult;

    public SubdirsTreeItemValue(RenamingPath moviePath, Executor executor, WarningsConfig warningsConfig) {
        super(moviePath, executor, warningsConfig);
    }

    @Override
    public String getIdentifier() {
        return "Subdirs";
    }

    @Override
    public String getHelpText() {
        return null;
    }

    @Override
    public SubdirsCheckResult checkStatus() throws IOException {
        log.debug("Triggering check status on thread {}", Thread.currentThread());
        SubdirsChecker checker = new SubdirsChecker();
        return checker.checkDir(getRenamingPath().getOldPath());
    }

    public void statusCheckerSucceeded(WorkerStateEvent event) {
        log.debug("Status checker succeeded, updating status on thread {}", Thread.currentThread());
        checkResult = (SubdirsCheckResult) event.getSource().getValue();
        updateStatus(checkResult);
    }

    public void defaultTaskFailed(WorkerStateEvent workerStateEvent) {
        log.error(workerStateEvent.getSource().getException().getLocalizedMessage(), workerStateEvent.getSource().getException());
    }

    @Override
    public void updateStatus(SubdirsCheckResult checkResult) {
        log.debug("Triggering status update on thread {}", Thread.currentThread());
        setWarning(calculateWarning());
        setFixable(isWarning());
    }

    @Override
    protected String buildNewMessage(Boolean newValue) {
        return newValue ? "Has subdirs: " + checkResult.getSubdirs().stream().map(p -> p.getFileName() + "[" + countChildFilesAndFolders(p) + "]").toList() : "No subdirs";
    }

    @Override
    public void fix(SubdirsCheckResult checkResult) throws FixFailedException {
        log.debug("Triggering fixing on thread {}", Thread.currentThread());
        for(Path p : checkResult.getSubdirs()){
            try {
                DrRenameUtil.deleteRecursively(p);
            } catch (IOException e) {
                throw new FixFailedException(e);
            }
        }
    }

    public void fixSucceeded(WorkerStateEvent workerStateEvent) {
        updateStatus(getCheckResult());
    }

    protected List<Path> getChildFilesAndFolders(Path path) throws IOException {
        List<Path> subdirs = new ArrayList<>();
        try (DirectoryStream<Path> ds = Files.newDirectoryStream(path)) {
            for (Path child : ds) {
                    subdirs.add(child);
            }
        }
        return subdirs;
    }

    protected String countChildFilesAndFolders(Path path) {
        try {
            return Integer.toString(getChildFilesAndFolders(path).size());
        } catch (IOException e) {
            return e.getLocalizedMessage();
        }
    }

    private boolean calculateWarning(){
        return !checkResult.getSubdirs().isEmpty();
    }

}
