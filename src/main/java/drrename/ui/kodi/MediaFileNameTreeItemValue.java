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

import drrename.kodi.*;
import drrename.model.RenamingPath;
import javafx.concurrent.WorkerStateEvent;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.nio.file.Path;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

@Getter
@Slf4j
public class MediaFileNameTreeItemValue extends KodiTreeItemValue<MediaFileNameCheckResult> {

    private final MediaFileNameIssue delegate;

    public MediaFileNameTreeItemValue(RenamingPath path, Executor executor, WarningsConfig warningsConfig) {
        super(path, executor, warningsConfig);
        this.delegate = new MediaFileNameIssue(path);
        triggerStatusCheck();
    }

    @Override
    public void updateStatus(MediaFileNameCheckResult result) {
        setCheckResult(result);
        delegate.updateStatus(result);
        setWarning(calculateWarning());
        setFixable(isWarning() && !result.getMediaFiles().isEmpty());
        if (isFixable()) {
            setGraphic(buildGraphic2());
        } else {
            setGraphic(super.buildGraphic());
        }
    }

    private boolean calculateWarning() {
        if(delegate.getCheckResult() != null) {
            return delegate.getCheckResult().getType().isWarning();
        }
        return false;
    }

    @Override
    protected String buildNewMessage(Boolean newValue) {
        if (delegate.getCheckResult() == null) {
            return null;
        }
        return (delegate.getCheckResult().getType().toString()) + (delegate.getCheckResult().getMediaFiles().isEmpty() ? "" : ":\n" + delegate.getCheckResult().getMediaFiles().stream().map(Path::getFileName).map(Object::toString).collect(Collectors.joining("\n")));
    }



    private Node buildGraphic2() {
        VBox box = new VBox(4);
        Button button = new Button("Rename all to \"" + getMovieName() + ".<extension>\"");
        VBox.setVgrow(button, Priority.ALWAYS);
        button.setMaxWidth(500);
        button.setOnAction(event ->
                triggerFixer());
        box.getChildren().add(button);
        return box;
    }

    private void triggerFixer(){
        var fixableFixer = new IssueFixer<>(this, delegate.getCheckResult());
        fixableFixer.setOnFailed(this::defaultTaskFailed);
        fixableFixer.setOnSucceeded(this::fixSucceeded);
        getExecutor().execute(fixableFixer);
    }

    protected void fixSucceeded(WorkerStateEvent workerStateEvent) {
        triggerStatusCheck();
    }


    // Delegate //

    @Override
    public String getHelpText() {
        return delegate.getHelpText();
    }

    @Override
    public String getIdentifier() {
        return delegate.getIdentifier();
    }


    @Override
    public MediaFileNameCheckResult checkStatus() {
        return delegate.checkStatus();
    }

    @Override
    public void fix(MediaFileNameCheckResult result) throws FixFailedException {
        delegate.fix(result);
    }

}
