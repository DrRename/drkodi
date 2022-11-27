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

package drrename.ui;

import drrename.Entries;
import drrename.FileTypeByMimeProvider;
import drrename.FileTypeProvider;
import drrename.RenamingControl;
import drrename.config.AppConfig;
import drrename.kodi.ui.ServiceStarter;
import drrename.strategy.RenamingConfig;
import drrename.strategy.RenamingStrategies;
import drrename.strategy.RenamingStrategy;
import drrename.strategy.SimpleRenamingConfig;
import drrename.ui.service.FileTypeService;
import drrename.ui.service.PreviewService;
import drrename.ui.service.RenamingService;
import drrename.util.FXUtil;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.collections.ListChangeListener;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import lombok.extern.slf4j.Slf4j;
import net.rgielen.fxweaver.core.FxWeaver;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.ResourceBundle;
import java.util.concurrent.Executor;

@Slf4j
@Component
@FxmlView("/fxml/RenameView.fxml")
public class RenameController extends DebuggableController implements Initializable, DrRenameController {

    // Spring Injected //

    private final PreviewService previewService;

    private final FileTypeService fileTypeService;

    private final RenamingStrategies renamingStrategies;

    private final Executor executor;

    private final RenamingService renamingService;

    private final Entries entries;

    private final FxWeaver fxWeaver;

    //

    public HBox goCancelButtonsComponent;

    public BorderPane buttonPane;

    public VBox fileListComponent;

    public HBox replacementStringComponent;
    public ListView<Control> leftContent;

    public ListView<Control> rightContent;

    public Label statusLabelLoaded;

    public Label statusLabelLoadedFileTypes;

    public Label statusLabelFilesWillRename;

    public Label statusLabelFilesWillRenameFileTypes;

    public Label statusLabelRenamed;

    public Label statusLabelRenamedFileTypes;


    public VBox statusBox;



    public ComboBox<RenamingStrategy> comboBoxRenamingStrategy;

    public TextField textFieldReplacementStringFrom;

    public TextField textFieldReplacementStringTo;

    public CheckBox showOnlyChanging;

    public Label selectedStrategyLabel;

    public CheckBox includeExtension;
    public GoCancelButtonsComponentController goCancelButtonsComponentController;
    public FileListComponentController fileListComponentController;
    public ReplacementStringComponentController replacementStringComponentController;

    @FXML
    ProgressAndStatusGridPane progressAndStatusGridPane;

    @FXML
    Parent layer01;

    @FXML
    Parent layer02_3;

    @FXML
    Parent comboboxBox;

    @FXML
    Parent filterAndButtonPane;
    @FXML
    CheckBox ignoreHiddenFiles;
    @FXML
    CheckBox ignoreDirectories;

    @FXML
    Parent filterBox;
    private ChangeListener<? super String> replaceStringFromChangeListener;
    private ChangeListener<? super String> replaceStringToChangeListener;
    private ChangeListener<? super Boolean> ignoreDirectoriesChangeListener;
    private ChangeListener<? super Boolean> ignoreHiddenFilesChangeListener;
    private ChangeListener<? super Boolean> showOnlyChangingChangeListener;

    private ChangeListener<? super String> textFieldChangeListener;


    private RenamingConfig renamingConfig;


    // Default fields //

    private final PreviewServiceStarter previewServiceStarter;

    private final FileTypeServiceStarter fileTypeServiceStarter;

    private final RenamingServiceStarter renamingServiceStarter;

    private final Label loadingFilesStatusLabel;

    private final Label fileTypeStatusLabel;

    private final Label previewFilesStatusLabel;

    private final Label renameFilesStatusLabel;


    //

    // Nested classes //
    private class PreviewServiceStarter extends ServiceStarter<PreviewService> {

        public PreviewServiceStarter(PreviewService service) {
            super(service);
        }

        @Override
        protected void onCancelled(WorkerStateEvent workerStateEvent) {

        }

        @Override
        protected void onSucceeded(WorkerStateEvent workerStateEvent) {

        }

        @Override
        protected void doInitService(PreviewService service) {
// Set all entries, filter-state might have changed
            service.setRenamingEntries(new ArrayList<>(entries.getEntries()));
            progressAndStatusGridPane.getProgressBar().progressProperty().bind(service.progressProperty());
            var strat = initAndGetStrategy();
            if (strat != null)
                service.setRenamingStrategy(strat);
        }

        @Override
        protected void prepareUi() {

        }

        @Override
        protected boolean checkPreConditions() {
            return true;
        }
    }

    private class FileTypeServiceStarter extends ServiceStarter<FileTypeService> {

        public FileTypeServiceStarter(FileTypeService service) {
            super(service);
        }

        @Override
        protected void doInitService(FileTypeService service) {
            fileTypeService.setRenamingEntries(entries.getEntries());
            fileTypeService.setFileTypeProvider(initAndGetFileTypeStrategy());
        }
    }

    private class RenamingServiceStarter extends ServiceStarter<RenamingService> {

        public RenamingServiceStarter(RenamingService service) {
            super(service);
        }

        @Override
        protected void onSucceeded(WorkerStateEvent workerStateEvent) {
            updatePreview();
        }

        @Override
        protected void doInitService(RenamingService service) {
            final RenamingStrategy s = initAndGetStrategy();
            if (s != null) {
                service.setRenamingEntries(new ArrayList<>(entries.getEntriesFiltered()));
                service.setStrategy(s);
                progressAndStatusGridPane.getProgressBar().progressProperty().bind(service.progressProperty());
            } else {
                log.warn("Cannot init service, no renaming strategy selected");
            }
        }

        @Override
        protected void prepareUi() {
            entries.getEntriesRenamed().clear();
        }
    }

    //

    public RenameController(AppConfig config, PreviewService previewService, FileTypeService fileTypeService, RenamingStrategies renamingStrategies, Executor executor, RenamingService renamingService, Entries entries, FxWeaver fxWeaver) {
        super(config);
        this.previewService = previewService;
        this.fileTypeService = fileTypeService;

        this.renamingStrategies = renamingStrategies;
        this.executor = executor;
        this.renamingService = renamingService;
        this.entries = entries;
        this.fxWeaver = fxWeaver;
        this.renamingServiceStarter = new RenamingServiceStarter(renamingService);
        this.previewServiceStarter = new PreviewServiceStarter(previewService);
        this.fileTypeServiceStarter = new FileTypeServiceStarter(fileTypeService);
        this.loadingFilesStatusLabel = new Label();
        this.fileTypeStatusLabel = new Label();
        this.previewFilesStatusLabel = new Label();
        this.renameFilesStatusLabel = new Label();
    }



    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        super.initialize(url, resourceBundle);


        renamingConfig = new SimpleRenamingConfig();

        leftContent = fileListComponentController.content1;
        rightContent = fileListComponentController.content2;

        textFieldReplacementStringTo = replacementStringComponentController.textFieldReplacementStringTo;
        textFieldReplacementStringFrom = replacementStringComponentController.textFieldReplacementStringFrom;

        initRenamingStrategies();

        initServices();

        registerInputChangeListener();

        configureButtons();

        configureStatusLabels();

        if (!getAppConfig().isDebug())
            progressAndStatusGridPane.getProgressBar().visibleProperty().bind(previewService.runningProperty().or(renamingService.runningProperty()));

        entries.getEntriesFiltered().addListener((ListChangeListener<RenamingControl>) c -> {
            while (c.next()) {

                    Collection<RenamingControl> removeFinal = new LinkedHashSet<>(c.getRemoved());
                    c.getAddedSubList().forEach(removeFinal::remove);
                    Collection<RenamingControl> addFinal = new LinkedHashSet<>(c.getAddedSubList());
                    c.getRemoved().forEach(addFinal::remove);

                        removeFromContent(removeFinal);
                        addToContent(addFinal);


            }
        });

        /* Make scrolling of both lists symmetrical */
        Platform.runLater(() -> FXUtil.getListViewScrollBar(leftContent).valueProperty()
                .bindBidirectional(FXUtil.getListViewScrollBar(rightContent).valueProperty()));

        leftContent.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        rightContent.setEditable(false);


    }

    @Override
    protected Parent[] getUiElementsForRandomColor() {
        return new Parent[]{layer01, layer02_3, comboboxBox, filterAndButtonPane, buttonPane, filterBox, goCancelButtonsComponent, statusLabelLoaded, statusLabelLoadedFileTypes, statusLabelFilesWillRename, statusLabelFilesWillRename, statusBox, progressAndStatusGridPane, progressAndStatusGridPane.getProgressStatusBox()};
    }


    private void configureStatusLabels() {
        statusLabelLoaded.textProperty().bind(entries.statusLoadedProperty());
        statusLabelLoadedFileTypes.textProperty().bind(entries.statusLoadedFileTypesProperty());
        statusLabelFilesWillRename.textProperty().bind((entries.statusWillRenameProperty()));
        statusLabelFilesWillRenameFileTypes.textProperty().bind(entries.statusWillRenameFileTypesProperty());
        statusLabelRenamed.textProperty().bind(entries.statusRenamedProperty());
        statusLabelRenamedFileTypes.textProperty().bind(entries.statusRenamedFileTypesProperty());
    }

    private void configureButtons() {
        goCancelButtonsComponentController.buttonGo.setTooltip(new Tooltip(getResourceBundle().getString("mainview.button.go.tooltip")));
        goCancelButtonsComponentController.buttonGo.setOnAction(this::handleButtonActionGo);
        goCancelButtonsComponentController.buttonCancel.setOnAction(this::handleButtonActionCancel);
    }

    private void initRenamingStrategies() {
        renamingStrategies.forEach(e -> comboBoxRenamingStrategy.getItems().add(e));
        selectedStrategyLabel.setLabelFor(comboBoxRenamingStrategy);
        comboBoxRenamingStrategy.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue == null)
                selectedStrategyLabel.setText(null);
            else
                selectedStrategyLabel.setText(newValue.getHelpText());
        });
        comboBoxRenamingStrategy.getSelectionModel().selectFirst();
    }

    private void registerInputChangeListener() {
        replaceStringFromChangeListener = (e, o, n) -> Platform.runLater(this::updatePreview);
        replaceStringToChangeListener = (e, o, n) -> Platform.runLater(this::updatePreview);

        ignoreDirectoriesChangeListener = (e, o, n) -> entries.setFilterDirectories(n);
        ignoreHiddenFilesChangeListener = (e, o, n) -> entries.setFilterHiddenFiles(n);
        showOnlyChangingChangeListener = (e, o, n) -> entries.setShowOnlyChainging(n);
        textFieldReplacementStringFrom.textProperty().addListener(replaceStringFromChangeListener);
        textFieldReplacementStringTo.textProperty().addListener(replaceStringToChangeListener);
        ignoreDirectories.selectedProperty().addListener(ignoreDirectoriesChangeListener);
        ignoreHiddenFiles.selectedProperty().addListener(ignoreHiddenFilesChangeListener);
        showOnlyChanging.selectedProperty().addListener(showOnlyChangingChangeListener);
        includeExtension.selectedProperty().addListener((observable, oldValue, newValue) -> {
            renamingConfig.setIncludeFileExtension(newValue);
            updatePreview();
        });
        comboBoxRenamingStrategy.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            textFieldReplacementStringFrom.setDisable(!newValue.isReplacing());
            textFieldReplacementStringTo.setDisable(!newValue.isReplacing());
            updatePreview();
        });
        goCancelButtonsComponentController.buttonGo.disableProperty().bind(renamingService.runningProperty().or(previewService.runningProperty()));
        goCancelButtonsComponentController.buttonCancel.disableProperty().bind(renamingService.runningProperty().not());

    }



    private void initServices() {
        previewService.setExecutor(executor);
        fileTypeService.setExecutor(executor);
        registerStateChangeListeners();
    }

    private void registerStateChangeListeners() {
        previewFilesStatusLabel.textProperty().bind(previewService.messageProperty());
        renameFilesStatusLabel.textProperty().bind(renamingService.messageProperty());
        fileTypeStatusLabel.textProperty().bind(fileTypeService.messageProperty());

    }


    private void addToContent(final Collection<? extends RenamingControl> renamingBeans) {
        renamingBeans.forEach(this::addToContent);
    }

    private void removeFromContent(final Collection<? extends RenamingControl> renamingBeans) {
        if (leftContent.getItems().isEmpty() && rightContent.getItems().isEmpty()) {
            return;
        }
        renamingBeans.forEach(this::removeFromContent);
    }

    private void addToContent(final RenamingControl renamingControl) {
        leftContent.getItems().add(renamingControl.getLeftControl());
        rightContent.getItems().add(renamingControl.getRightControl());
    }

    private void removeFromContent(final RenamingControl renamingControl) {
        if (leftContent.getItems().isEmpty() && rightContent.getItems().isEmpty()) {
            return;
        }

        if (!leftContent.getItems().remove(renamingControl.getLeftControl())) {
            log.warn("Failed to remove {} from left content", renamingControl.getLeftControl());
        }
        if (!rightContent.getItems().remove(renamingControl.getRightControl())) {
            log.warn("Failed to remove {} from right content", renamingControl.getRightControl());
        }

    }

    public void updateInputView() {
        log.debug("Updating input view");
    }

    void updatePreview() {
        previewServiceStarter.startService();
    }

    void updateFileTypeInfo() {
        fileTypeServiceStarter.startService();
    }

    private FileTypeProvider initAndGetFileTypeStrategy() {
        return new FileTypeByMimeProvider();
    }

    public void clearView() {
        entries.getEntries().clear();
        entries.getEntriesRenamed().clear();
    }

    public void cancelCurrentOperation() {
        log.debug("Cancelling current operation");
        previewService.cancel();
        renamingService.cancel();
        fileTypeService.cancel();
    }

    private RenamingStrategy initAndGetStrategy() {

        final RenamingStrategy strategy = comboBoxRenamingStrategy.getSelectionModel().getSelectedItem();
        if (strategy == null)
            return null;
        strategy.setReplacementStringFrom(textFieldReplacementStringFrom.getText());
        strategy.setReplacementStringTo(textFieldReplacementStringTo.getText());
        strategy.setConfig(renamingConfig);
        return strategy;
    }

    public void handleButtonActionGo(ActionEvent actionEvent) {
        renamingServiceStarter.startService();
    }

    private void handleButtonActionCancel(ActionEvent actionEvent) {
        cancelCurrentOperation();
        clearView();
        updateInputView();
    }

    public void handleMenuItemAbout(ActionEvent actionEvent) {
    }

    public void handleMenuItemSettings(ActionEvent actionEvent) {
        SettingsController controller = fxWeaver.loadController(SettingsController.class, getResourceBundle());
        controller.show();
    }

    // Getter / Setter //



}
