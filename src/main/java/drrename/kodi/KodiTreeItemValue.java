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
import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
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
public abstract class KodiTreeItemValue {

    private static final String warning_font = "-fx-font-weight: bold;";

    private static final String warning_color = "-fx-background-color: wheat;";

    private final ListProperty<String> styles;

    private final StringProperty style;

    private final StringProperty buttonText;

    private final BooleanProperty canFix;

    private final ObjectProperty<Boolean> warning;

    private final StringProperty message;

    private final StringProperty identifier;

    private final ObjectProperty<Node> graphic;

    private final boolean fixable;

    private final WarningsConfig warningsConfig;

    private final RenamingPath renamingPath;

    private final ObjectProperty<KodiTreeItem> treeItem;

    private final Executor executor;

    public KodiTreeItemValue(RenamingPath moviePath, boolean fixable, Executor executor) {
        this.renamingPath = moviePath;
        this.fixable = fixable;
        this.executor = executor;
        this.buttonText = new SimpleStringProperty();
        this.canFix = new SimpleBooleanProperty();
        this.styles = new SimpleListProperty<>(FXCollections.observableArrayList());
        this.style = new SimpleStringProperty();
        this.warning = new SimpleObjectProperty<>();
        this.message = new SimpleStringProperty();
        this.graphic = new SimpleObjectProperty<>();
        this.treeItem = new SimpleObjectProperty<>();
        this.warningsConfig = new WarningsConfig();
        this.identifier = new SimpleStringProperty();
        init();
    }

    private void init() {
        getStyles().addListener((ListChangeListener<String>) c -> {
            while (c.next()) {
                setStyle(String.join(" ", c.getList()));
            }
        });
        canFixProperty().addListener((observable, oldValue, newValue) -> updateButtonText());
        warningProperty().addListener((observable, oldValue, newValue) -> {
            updateStyles(newValue);
            updateButtonText();
            setMessage(updateMessage(newValue));
        });
        setGraphic(buildGraphic());
        setIdentifier(updateIdentifier());
    }

    protected abstract String updateMessage(Boolean newValue);


    public abstract void fix() throws FixFailedException;

    protected abstract String updateIdentifier();

    protected void performFix() {

        executor.execute(() -> {
            try {
                fix();
                Platform.runLater(this::updateStatus);
            } catch (FixFailedException e) {
                throw new RuntimeException(e);
            }
        });
    }

    protected abstract void updateStatus();

    protected void updateButtonText() {
        updateButtonText(isCanFix(), isWarning());
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
        button.disableProperty().bind(canFixProperty().not());
        button.setOnAction(actionEvent -> {
            performFix();
        });
        return button;
    }

    public boolean contains(KodiTreeItem childItem) {
        return getTreeItem() != null && new ArrayList<>(getTreeItem().getSourceChildren()).stream().map(TreeItem::getValue).anyMatch(v -> v.equals(childItem.getValue()));
    }

    // FX Getter / Setter //


    public KodiTreeItem getTreeItem() {
        return treeItem.get();
    }

    public ObjectProperty<KodiTreeItem> treeItemProperty() {
        return treeItem;
    }

    public void setTreeItem(KodiTreeItem treeItem) {
        this.treeItem.set(treeItem);
    }

    public String getMessage() {
        return message.get();
    }

    public StringProperty messageProperty() {
        return message;
    }

    public void setMessage(String message) {
        this.message.set(message);
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

    public boolean isCanFix() {
        return canFix.get();
    }

    public BooleanProperty canFixProperty() {
        return canFix;
    }

    public void setCanFix(boolean canFix) {
        this.canFix.set(canFix);
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

    public RenamingPath getRenamingPath() {
        return renamingPath;
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

    public WarningsConfig getWarningsConfig() {
        return warningsConfig;
    }

    public String getIdentifier() {
        return identifier.get();
    }

    public StringProperty identifierProperty() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier.set(identifier);
    }
}
