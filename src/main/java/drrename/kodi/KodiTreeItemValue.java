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

package drrename.kodi;

import drrename.model.RenamingPath;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.concurrent.WorkerStateEvent;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TreeItem;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.concurrent.Executor;

/**
 * Base class for all {@link KodiTreeItem} values.
 *
 * @see KodiTreeItem
 */
@Slf4j
public abstract class KodiTreeItemValue<R> extends FxKodiIssue<R> {

    private static final String warning_font = "-fx-font-weight: bold;";

    private static final String warning_color = "-fx-background-color: wheat;";

    private final ListProperty<String> styles;

    private final StringProperty style;

    private final StringProperty buttonText;

    private final ObjectProperty<Boolean> warning;

    private final ObjectProperty<Node> graphic;

    private final WarningsConfig warningsConfig;

    private final ObjectProperty<KodiTreeItem> treeItem;

    private final ObjectProperty<R> checkResult;

    private final Executor executor;

    public KodiTreeItemValue(RenamingPath moviePath, Executor executor) {
        super(moviePath);
        this.executor = executor;
        this.buttonText = new SimpleStringProperty();
        this.styles = new SimpleListProperty<>(FXCollections.observableArrayList());
        this.style = new SimpleStringProperty();
        this.warning = new SimpleObjectProperty<>();
        this.graphic = new SimpleObjectProperty<>();
        this.treeItem = new SimpleObjectProperty<>();
        this.warningsConfig = new WarningsConfig();
        this.checkResult = new SimpleObjectProperty<>();
        init();
    }

    protected void init() {
        getStyles().addListener((ListChangeListener<String>) c -> {
            while (c.next()) {
                setStyle(String.join(" ", c.getList()));
            }
        });
        fixableProperty().addListener((observable, oldValue, newValue) -> updateButtonText());
        warningProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue != null) {
                updateStyles(newValue);
                updateButtonText();
                setMessage(buildNewMessage(newValue));
            }
        });
        setGraphic(buildGraphic());
    }

     protected void triggerStatusCheck(){
        // Start the processing cascade:
        // - check status: worker thread
        // - update status: UI thread
        var fixableStatusChecker = new FixableStatusChecker<>(this);
        fixableStatusChecker.setOnFailed(this::defaultTaskFailed);
        fixableStatusChecker.setOnSucceeded(this::statusCheckerSucceeded);
        getExecutor().execute(fixableStatusChecker);
    }

    protected void defaultTaskFailed(WorkerStateEvent workerStateEvent) {
        log.error(workerStateEvent.getSource().getException().getLocalizedMessage(), workerStateEvent.getSource().getException());
    }

    protected void statusCheckerSucceeded(WorkerStateEvent event) {
//        log.debug("Status checker succeeded, updating status on thread {}", Thread.currentThread());
        updateStatus((R) event.getSource().getValue());
    }

    public abstract void updateStatus(R result);

    protected abstract String buildNewMessage(Boolean newValue);

    protected void updateButtonText() {
        updateButtonText(isFixable(), isWarning());
    }

    protected void updateButtonText(boolean canFix, boolean isWarning) {
        if (canFix) {
            buttonText.set("Fix");
        } else if (isWarning) {
            buttonText.set("Fix manually");
        } else {
            buttonText.set("OK");
        }
    }

    protected void updateStyles(Boolean warning) {
        if (warning) {
            styles.add(warning_font);
            styles.add(warning_color);
        } else {
            styles.remove(warning_font);
            styles.remove(warning_color);
        }
    }

    protected Node buildGraphic() {
        Button button = new Button();
        button.textProperty().bind(buttonText);
        button.disableProperty().bind(fixableProperty().not());
        button.setOnAction(actionEvent -> {
            try {
                fix(getCheckResult());
            } catch (FixFailedException e) {
                throw new RuntimeException(e);
            }
        });
        return button;
    }

    public boolean contains(KodiTreeItem childItem) {
        return getTreeItem() != null && new ArrayList<>(getTreeItem().getSourceChildren()).stream().map(TreeItem::getValue).anyMatch(v -> v.equals(childItem.getValue()));
    }

    // Getter //

    public Executor getExecutor() {
        return executor;
    }

    public WarningsConfig getWarningsConfig() {
        return warningsConfig;
    }

    // FX Getter / Setter //


    public R getCheckResult() {
        return checkResult.get();
    }

    public ObjectProperty<R> checkResultProperty() {
        return checkResult;
    }

    public void setCheckResult(R checkResult) {
        this.checkResult.set(checkResult);
    }

    public KodiTreeItem getTreeItem() {
        return treeItem.get();
    }

    public ObjectProperty<KodiTreeItem> treeItemProperty() {
        return treeItem;
    }

    public void setTreeItem(KodiTreeItem treeItem) {
        this.treeItem.set(treeItem);
    }

    public boolean isWarning() {
        return warning.get();
    }

    public ObjectProperty<Boolean> warningProperty() {
        return warning;
    }

    public void setWarning(boolean warning) {
        this.warning.set(warning);
    }

    public String getStyle() {
        return style.get();
    }

    public StringProperty styleProperty() {
        return style;
    }

    public void setStyle(String style) {
        this.style.set(style);
    }

    protected ObservableList<String> getStyles() {
        return styles.get();
    }

    protected ListProperty<String> stylesProperty() {
        return styles;
    }

    protected void setStyles(ObservableList<String> styles) {
        this.styles.set(styles);
    }

    public String getButtonText() {
        return buttonText.get();
    }

    public StringProperty buttonTextProperty() {
        return buttonText;
    }

    public void setButtonText(String buttonText) {
        this.buttonText.set(buttonText);
    }

    public Node getGraphic() {
        return graphic.get();
    }

    public ObjectProperty<Node> graphicProperty() {
        return graphic;
    }

    public void setGraphic(Node graphic) {
        this.graphic.set(graphic);
    }


}
