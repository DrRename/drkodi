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

import drrename.kodi.*;
import drrename.model.RenamingPath;
import javafx.concurrent.WorkerStateEvent;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Executor;
import java.util.stream.Collectors;

@Slf4j
public class NfoFileNameTreeItemValue extends KodiTreeItemValue<NfoFileNameCheckResult> {

    private final NfoFileNameIssue delegate;

    public NfoFileNameTreeItemValue(RenamingPath moviePath, Executor executor){
        super(moviePath, executor);
        delegate = new NfoFileNameIssue(moviePath);
        triggerStatusCheck();
    }



    private void triggerFixer(){
        var fixableFixer = new IssueFixer<>(this, delegate.getCheckResult());
        fixableFixer.setOnFailed(this::defaultTaskFailed);
        fixableFixer.setOnSucceeded(this::fixSucceeded);
        getExecutor().execute(fixableFixer);
    }

    private void fixSucceeded(WorkerStateEvent workerStateEvent) {
        triggerStatusCheck();
    }

    protected boolean calculateWarning() {
        if (delegate.getCheckResult() == null) {
            return false;
        }
        if (NfoFileContentType.NO_FILE.equals(delegate.getCheckResult().getType()) && !getWarningsConfig().isMissingNfoFileIsWarning()) {
            return false;
        }
        return !NfoFileContentType.MOVIE_NAME.equals(delegate.getCheckResult().getType());
    }

    protected String buildNewMessage(Boolean newValue) {
        if (delegate.getCheckResult() == null) {
            return null;
        }
        if (newValue) {
            return (delegate.getCheckResult().getType().toString() + getWarningAdditionalInfo());
        }
        return (delegate.getCheckResult().getType().toString());
    }

    private String getWarningAdditionalInfo() {
        return delegate.getCheckResult().getNfoFiles().isEmpty() ? "" : ": " + delegate.getCheckResult().getNfoFiles().stream().map(f -> f.getFileName().toString()).collect(Collectors.joining(", "));
    }

    @Override
    public void updateStatus(NfoFileNameCheckResult result) {
        delegate.updateStatus(result);
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
        Button button = new Button("Fix to \"" + getMovieName() + ".nfo\"");
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
    public NfoFileNameCheckResult checkStatus() {
        return delegate.checkStatus();
    }

    @Override
    public void fix(NfoFileNameCheckResult result) throws FixFailedException {
        delegate.fix(result);
    }
}
