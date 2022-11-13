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

package drrename.ui.mainview;

import drrename.FileTypeProvider;
import drrename.strategy.RenamingStrategies;
import drrename.config.AppConfig;
import drrename.event.MainViewButtonCancelEvent;
import drrename.event.MainViewButtonGoEvent;
import drrename.filecreator.DummyFileCreatorController;
import drrename.kodi.KodiToolsController;
import drrename.mime.FileTypeByMimeProvider;
import drrename.model.RenamingControl;
import drrename.EntriesService;
import drrename.strategy.RenamingConfig;
import drrename.strategy.RenamingStrategy;
import drrename.strategy.SimpleRenamingConfig;
import drrename.ui.mainview.controller.TabController;
import drrename.util.FXUtil;
import drrename.ui.settingsview.SettingsController;
import drrename.ui.mainview.controller.FileListComponentController;
import drrename.ui.service.FileTypeService;
import drrename.ui.service.LoadPathsService;
import drrename.ui.service.PreviewService;
import drrename.ui.service.RenamingService;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.concurrent.Service;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.DragEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.rgielen.fxweaver.core.FxWeaver;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.io.File;
import java.net.URL;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RequiredArgsConstructor
@Slf4j
@Component
@FxmlView("/fxml/RenameView.fxml")
public class RenameController implements Initializable {

    private static final String RENAMING_FILES = "mainview.status.renaming_files";

    private static final String LOADING_FILES = "mainview.status.loading_files";

    private static final String LOADING_PREVIEVS = "mainview.status.loading_previews";

    private static final String LOADING_FILE_TYPES = "mainview.status.loading_filetypes";

    private final TabController tabController;

    private final AppConfig config;

    private final LoadPathsService loadPathsService;

    private final PreviewService previewService;

    private final FileTypeService fileTypeService;

    private final ResourceBundle resourceBundle;

    private final RenamingStrategies renamingStrategies;

    private final Executor executor;

    public HBox goCancelButtonsComponent;

    public BorderPane buttonPane;

    public VBox fileListComponent;

    public HBox replacementStringComponent;

    private final RenamingService renamingService;

    private final EntriesService entriesService;

    public ListView<Control> leftContent;

    public ListView<Control> rightContent;

    public Label statusLabelLoaded;

    public Label statusLabelLoadedFileTypes;

    public Label statusLabelFilesWillRename;

    public Label statusLabelFilesWillRenameFileTypes;

    public Label statusLabelRenamed;

    public Label statusLabelRenamedFileTypes;

    public Label loadingFilesStatusLabel;

    public Label previewFilesStatusLabel;

    public Label renameFilesStatusLabel;

    public VBox statusBox;

    public Label fileTypeStatusLabel;

    public ComboBox<RenamingStrategy> comboBoxRenamingStrategy;

    public TextField textFieldReplacementStringFrom;

    public TextField textFieldReplacementStringTo;

    public CheckBox showOnlyChanging;

    public Label selectedStrategyLabel;

    public CheckBox includeExtension;

    private ChangeListener<? super String> replaceStringFromChangeListener;

    private ChangeListener<? super String> replaceStringToChangeListener;

    private ChangeListener<? super Boolean> ignoreDirectoriesChangeListener;

    private ChangeListener<? super Boolean> ignoreHiddenFilesChangeListener;

    private ChangeListener<? super Boolean> showOnlyChangingChangeListener;

    private ChangeListener<? super String> textFieldChangeListener;

    @FXML
    private ProgressBar progressBar;

    @FXML
    Node layer01;

    @FXML
    Node layer02_3;

    @FXML
    Node comboboxBox;

    @FXML
    Node filterAndButtonPane;
    @FXML
    CheckBox ignoreHiddenFiles;
    @FXML
    CheckBox ignoreDirectories;

    @FXML
    Node filterBox;

    private final FxWeaver fxWeaver;

    public GoCancelButtonsComponentController goCancelButtonsComponentController;

    public FileListComponentController fileListComponentController;

    public ReplacementStringComponentController replacementStringComponentController;

    private final BooleanProperty draggingOver = new SimpleBooleanProperty();

    private final ListProperty<Path> loadedPaths = new SimpleListProperty<>(FXCollections.observableArrayList());

    private RenamingConfig renamingConfig;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

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

        progressBar.visibleProperty().bind(loadPathsService.runningProperty().or(previewService.runningProperty().or(renamingService.runningProperty())));

        if (config.isDebug())
            applyRandomColors();

        entriesService.getEntriesFiltered().addListener((ListChangeListener<RenamingControl>) c -> {
            while (c.next()) {
                executor.execute(() -> {
                    Collection<RenamingControl> removeFinal = new LinkedHashSet<>(c.getRemoved());
                    c.getAddedSubList().forEach(removeFinal::remove);
                    Collection<RenamingControl> addFinal = new LinkedHashSet<>(c.getAddedSubList());
                    c.getRemoved().forEach(addFinal::remove);
                    Platform.runLater(() -> {
                        removeFromContent(removeFinal);
                        addToContent(addFinal);
                    });
                });
            }
        });

        /* Make scrolling of both lists symmetrical */
        Platform.runLater(() -> FXUtil.getListViewScrollBar(leftContent).valueProperty()
                .bindBidirectional(FXUtil.getListViewScrollBar(rightContent).valueProperty()));

        leftContent.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        rightContent.setEditable(false);


    }

    private void configureStatusLabels() {
        statusLabelLoaded.textProperty().bind(entriesService.statusLoadedProperty());
        statusLabelLoadedFileTypes.textProperty().bind(entriesService.statusLoadedFileTypesProperty());
        statusLabelFilesWillRename.textProperty().bind((entriesService.statusWillRenameProperty()));
        statusLabelFilesWillRenameFileTypes.textProperty().bind(entriesService.statusWillRenameFileTypesProperty());
        statusLabelRenamed.textProperty().bind(entriesService.statusRenamedProperty());
        statusLabelRenamedFileTypes.textProperty().bind(entriesService.statusRenamedFileTypesProperty());
    }

    private void configureButtons() {
        goCancelButtonsComponentController.buttonGo.setTooltip(new Tooltip(resourceBundle.getString("mainview.button.go.tooltip")));
        goCancelButtonsComponentController.setButtonCancelActionEventFactory(MainViewButtonCancelEvent::new);
        goCancelButtonsComponentController.setButtonGoActionEventFactory(MainViewButtonGoEvent::new);
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
        tabController.startDirectoryComponentController.inputPathProperty().addListener(new ChangeListener<Path>() {
            @Override
            public void changed(ObservableValue<? extends Path> observable, Path oldValue, Path n) {
                if (n != null) loadedPaths.setAll(n);
                updateInputView();
            }
        });
        ignoreDirectoriesChangeListener = (e, o, n) -> entriesService.setFilterDirectories(n);
        ignoreHiddenFilesChangeListener = (e, o, n) -> entriesService.setFilterHiddenFiles(n);
        showOnlyChangingChangeListener = (e, o, n) -> entriesService.setShowOnlyChainging(n);
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
        goCancelButtonsComponentController.buttonGo.disableProperty().bind(renamingService.runningProperty().or(previewService.runningProperty()).or(loadPathsService.runningProperty()));
        goCancelButtonsComponentController.buttonCancel.disableProperty().bind(renamingService.runningProperty().not());

    }

    private void initServices() {
        setLoadingServiceCallbacks();
        setRenamingServiceCallbacks();
        setPreviewServiceCallbacks();
        registerStateChangeListeners();
    }

    private void registerStateChangeListeners() {
        loadPathsService.runningProperty().addListener((observable, oldValue, newValue) -> loadingFilesServiceStausChanged(newValue));
        previewService.runningProperty().addListener((observable, oldValue, newValue) -> previewFilesServiceStateChanged(newValue));
        fileTypeService.runningProperty().addListener((observable, oldValue, newValue) -> fileTypeServiceStateChanged(newValue));
        renamingService.runningProperty().addListener((observable, oldValue, newValue) -> renamingServiceStatusChange(newValue));
    }


    private void loadingFilesServiceStausChanged(Boolean newValue) {
        if (newValue) loadingFilesStatusLabel.setText(String.format(resourceBundle.getString(LOADING_FILES)));
        else loadingFilesStatusLabel.setText(null);
    }

    private void previewFilesServiceStateChanged(Boolean newValue) {
        if (newValue) previewFilesStatusLabel.setText(String.format(resourceBundle.getString(LOADING_PREVIEVS)));
        else previewFilesStatusLabel.setText(null);
    }

    private void fileTypeServiceStateChanged(Boolean newValue) {
        if (newValue) fileTypeStatusLabel.setText(String.format(resourceBundle.getString(LOADING_FILE_TYPES)));
        else fileTypeStatusLabel.setText(null);
    }

    private void renamingServiceStatusChange(Boolean newValue) {
        if (newValue) renameFilesStatusLabel.setText(String.format(resourceBundle.getString(RENAMING_FILES)));
        else renameFilesStatusLabel.setText(null);
    }


    private void setLoadingServiceCallbacks() {
        loadPathsService.setOnFailed(e -> {
            log.error(e + ", " + renamingService.getException());
        });
    }

    private void setPreviewServiceCallbacks() {
        previewService.setOnFailed(e -> {
            log.error(e + ", " + renamingService.getException());
        });
    }

    private void setRenamingServiceCallbacks() {
        renamingService.setOnFailed(e -> {
            log.error(e + ", " + renamingService.getException());
        });
    }

    private void applyRandomColors() {
        Stream.of(layer01, layer02_3, comboboxBox, filterAndButtonPane, buttonPane, filterBox, goCancelButtonsComponent, statusLabelLoaded, statusLabelLoadedFileTypes, statusLabelFilesWillRename, statusLabelFilesWillRename, statusBox).forEach(l -> l.setStyle("-fx-background-color: " + getRandomColorString()));
    }

    public static String getRandomColorString() {
        return String.format("#%06x", new Random().nextInt(256 * 256 * 256));
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

    public void handleMenuItemDummyFileCreator(ActionEvent actionEvent) {
        DummyFileCreatorController controller = fxWeaver.loadController(DummyFileCreatorController.class, resourceBundle);
        controller.show();

    }

    public void handleMenuItemKodiTools(ActionEvent actionEvent) {
        KodiToolsController controller = fxWeaver.loadController(KodiToolsController.class, resourceBundle);
        controller.show();
    }

    private void updateInputView() {
        log.debug("Updating input view with {} elements", loadedPaths.size());
        clearView();
        initLoadService();
        startService(loadPathsService);
    }

    private void initLoadService() {
        loadPathsService.cancel();
        loadPathsService.reset();
        loadPathsService.setFiles(new ArrayList<>(loadedPaths));
        loadPathsService.setOnSucceeded((e) -> {
            updateFileTypeInfo();
            updatePreview();

        });
        progressBar.progressProperty().bind(loadPathsService.progressProperty());

    }

    private void initPreviewService() {
        previewService.cancel();
        previewService.reset();
        // Set all entries, filter-state might have changed
        previewService.setRenamingEntries(new ArrayList<>(entriesService.getEntries()));
        progressBar.progressProperty().bind(previewService.progressProperty());
        var strat = initAndGetStrategy();
        if (strat != null)
            previewService.setRenamingStrategy(strat);
    }

    private void updatePreview() {
        initPreviewService();
        startService(previewService);
    }

    private void updateFileTypeInfo() {
        initFileTypeService(entriesService.getEntries());
        startService(fileTypeService);
    }

    private void initFileTypeService(Collection<RenamingControl> renamingEntries) {
        fileTypeService.cancel();
        fileTypeService.reset();
        fileTypeService.setRenamingEntries(renamingEntries);
        var typeProvider = initAndGetFileTypeStrategy();
        fileTypeService.setFileTypeProvider(typeProvider);
    }

    private FileTypeProvider initAndGetFileTypeStrategy() {
        return new FileTypeByMimeProvider();
    }


    /**
     * Starts a {@link Service}. Call in UI thread.
     *
     * @param service Service to start
     */
    private void startService(Service<?> service) {

        service.setOnRunning(e -> {
            if (log.isDebugEnabled()) {
                log.debug("Service running " + service);
            }
        });
        service.setOnFailed(e -> {
            if (log.isDebugEnabled()) {
                log.debug("Service {} failed with exception {}", service, service.getException());
            }
        });

        /* This is only *one* callback. configure this separately. */
		/*service.setOnSucceeded(e -> {
			if (log.isDebugEnabled()) {
				log.debug("Service succeeded {} ", service);
			}
		});*/
        log.debug("Starting service {}", service);
        service.start();


    }

    private void clearView() {
        entriesService.getEntries().clear();
        entriesService.getEntriesRenamed().clear();
    }

    private void cancelCurrentOperation() {
        log.debug("Cancelling current operation");
        previewService.cancel();
        renamingService.cancel();
        loadPathsService.cancel();
        fileTypeService.cancel();
        updateInputView();
    }

    @EventListener
    public void onButtonGoEvent(MainViewButtonGoEvent event) {
        handleButtonActionGo(event.getActionEvent());
    }

    @EventListener
    public void onButtonCancelEvent(MainViewButtonCancelEvent event) {
        handleButtonActionCancel(event.getActionEvent());
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

        final RenamingStrategy s = initAndGetStrategy();
        if (s != null) {
            entriesService.getEntriesRenamed().clear();
            renamingService.cancel();
            renamingService.reset();
            renamingService.setRenamingEntries(new ArrayList<>(entriesService.getEntriesFiltered()));
            renamingService.setStrategy(s);
            renamingService.setOnSucceeded(e -> {
                updatePreview();
            });
            progressBar.progressProperty().bind(renamingService.progressProperty());
            renamingService.start();
        } else {
            log.info("No renaming strategy selected");
        }
    }

    private void handleButtonActionCancel(ActionEvent actionEvent) {
        cancelCurrentOperation();
    }

    public void handleMenuItemAbout(ActionEvent actionEvent) {
    }


    public void handleMenuItemSettings(ActionEvent actionEvent) {
        SettingsController controller = fxWeaver.loadController(SettingsController.class, resourceBundle);
        controller.show();
    }
}
