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

package drrename.kodi.ui;

import drrename.RenamingPath;
import drrename.kodi.*;
import javafx.concurrent.WorkerStateEvent;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Executor;
import java.util.stream.Collectors;

@Slf4j
public class NfoFileNameTreeItemValue extends KodiTreeItemValue<NfoFileCheckResult> {

    private final NfoFileNameIssue delegate;

    public NfoFileNameTreeItemValue(RenamingPath moviePath, Executor executor, WarningsConfig warningsConfig){
        super(moviePath, executor, warningsConfig);
        delegate = new NfoFileNameIssue(moviePath);
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

    protected boolean calculateWarning() {
        if (delegate.getCheckResult() == null) {
            return false;
        }
       return delegate.getCheckResult().getType().isWarning(getWarningsConfig());
    }

    protected String buildNewMessage(Boolean newValue) {
        if (delegate.getCheckResult() == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder(delegate.getCheckResult().getType().toString());
        var additionalInfo = getWarningAdditionalInfo();
        if(additionalInfo != null){
            sb.append(": ");
            sb.append(additionalInfo);
        } else {
            sb.append(".");
        }
        return sb.toString();
    }

    private String getWarningAdditionalInfo() {
        return delegate.getCheckResult().getNfoFiles().isEmpty() ? null : delegate.getCheckResult().getNfoFiles().stream().map(f -> f.getFileName().toString()).collect(Collectors.joining(", "));
    }

    @Override
    public void updateStatus(NfoFileCheckResult result) {
        if(result == null){
            return;
        }
        delegate.updateStatus(result);
        setCheckResult(result);
        setWarning(calculateWarning());
        setFixable(isWarning() && delegate.getCheckResult().getNfoFiles().size() == 1);
        if (isFixable()) {
            setGraphic(buildGraphic2());
        } else {
            setGraphic(super.buildGraphic());
        }
    }

    private Node buildGraphic2() {
        VBox box = new VBox(4);
        Button button = new Button("Fix to \"" + getMovieNameFromFolder() + ".nfo\"");
        VBox.setVgrow(button, Priority.ALWAYS);
        button.setMaxWidth(500);
        button.setOnAction(event ->
                triggerFixer());
        box.getChildren().add(button);
        return box;
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
    public NfoFileCheckResult checkStatus() {
        return delegate.checkStatus();
    }

    @Override
    public void fix(NfoFileCheckResult result) throws FixFailedException {
        delegate.fix(result);
    }
}
