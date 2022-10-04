package drrename.ui;

import drrename.*;
import drrename.config.AppConfig;
import drrename.event.MainViewButtonCancelEvent;
import drrename.event.MainViewButtonGoEvent;
import drrename.filecreator.DummyFileCreatorController;
import drrename.kodi.KodiToolsController;
import drrename.ui.mainview.GoCancelButtonsComponentController;
import drrename.ui.mainview.ReplacementStringComponentController;
import drrename.ui.mainview.StartDirectoryComponentController;
import drrename.ui.mainview.controller.FileListComponentController;
import drrename.model.RenamingEntry;
import drrename.service.EntriesService;
import drrename.strategy.*;
import drrename.ui.service.FileTypeService;
import drrename.ui.service.ListFilesService;
import drrename.ui.service.PreviewService;
import drrename.ui.service.RenamingService;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.concurrent.Service;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.rgielen.fxweaver.core.FxWeaver;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RequiredArgsConstructor
@Slf4j
@Component
@FxmlView("/fxml/MainView.fxml")
public class MainController implements Initializable {

    private static final String RENAMING_FILES = "mainview.status.renaming_files";
    private static final String LOADING_FILES = "mainview.status.loading_files";
    private static final String LOADING_PREVIEVS = "mainview.status.loading_previews";
    private static final String LOADING_FILE_TYPES = "mainview.status.loading_filetypes";
    private final AppConfig config;

    private final ListFilesService listFilesService;
    private final PreviewService previewService;

    private final FileTypeService fileTypeService;

    private final ResourceBundle resourceBundle;

    private final Executor executor;
    public HBox goCancelButtonsComponent;
    public BorderPane layer04_3;
    public BorderPane layer04_2;
    public VBox fileListComponent;
    public BorderPane startDirectoryComponent;
    public HBox replacementStringComponent;

    private final RenamingService renamingService;


    private final EntriesService entriesService;

    public ListView<Control> content1;
    public ListView<Control> content2;
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


    @FXML
    private ComboBox<RenamingStrategy> comboBoxRenamingStrategy;
    @FXML
    private TextField textFieldReplacementStringFrom;
    @FXML
    private TextField textFieldReplacementStringTo;
    private ChangeListener<? super String> replaceStringFromChangeListener;
    private ChangeListener<? super String> replaceStringToChangeListener;

    private ChangeListener<? super Boolean> ignoreDirectoriesChangeListener;

    private ChangeListener<? super Boolean> ignoreHiddenFilesChangeListener;

    private ChangeListener<? super String> textFieldChangeListener;

    @FXML
    private ProgressBar progressBar;
    @FXML
    MenuBar menuBar;

    @FXML
    Node layer01;

    @FXML
    Node layer02_3;

    @FXML
    Node comboboxBox;

    @FXML
    Node layer04_1;
    @FXML
    CheckBox ignoreHiddenFiles;
    @FXML
    CheckBox ignoreDirectories;

    @FXML
    Node layer05_1;

    @FXML
    Node layer05_2;

    private final FxWeaver fxWeaver;

    public GoCancelButtonsComponentController goCancelButtonsComponentController;

    public StartDirectoryComponentController startDirectoryComponentController;

    public FileListComponentController fileListComponentController;

    public ReplacementStringComponentController replacementStringComponentController;

    private void applyRandomColors() {
        Stream.of(layer01, layer02_3, comboboxBox, layer04_1, layer04_2, layer05_1, goCancelButtonsComponent, statusLabelLoaded, statusLabelLoadedFileTypes, statusLabelFilesWillRename, statusLabelFilesWillRename, statusBox).forEach(l -> l.setStyle("-fx-background-color: " + getRandomColorString()));
    }

    private String getRandomColorString() {
        return String.format("#%06x", new Random().nextInt(256 * 256 * 256));
    }

    public static List<Path> filesToPathList(Collection<File> files) {
        return files.stream().map(File::toPath).collect(Collectors.toList());
    }

    private void registerInputChangeListener() {
        replaceStringFromChangeListener = (e, o, n) -> Platform.runLater(this::updateOutputView);
        replaceStringToChangeListener = (e, o, n) -> Platform.runLater(this::updateOutputView);
        textFieldChangeListener = (e, o, n) -> Platform.runLater(() -> updateInputView(n));
        ignoreDirectoriesChangeListener = (e, o, n) -> Platform.runLater(this::updateOutputView);
        ignoreHiddenFilesChangeListener = (e, o, n) -> Platform.runLater(this::updateOutputView);
        textFieldReplacementStringFrom.textProperty().addListener(replaceStringFromChangeListener);
        textFieldReplacementStringTo.textProperty().addListener(replaceStringToChangeListener);
        startDirectoryComponentController.textFieldDirectory.textProperty().addListener(textFieldChangeListener);
        ignoreDirectories.selectedProperty().addListener(ignoreDirectoriesChangeListener);
        ignoreHiddenFiles.selectedProperty().addListener(ignoreHiddenFilesChangeListener);
        comboBoxRenamingStrategy.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            textFieldReplacementStringFrom.setDisable(!newValue.isReplacing());
            textFieldReplacementStringTo.setDisable(!newValue.isReplacing());
            updateOutputView();
        });
        goCancelButtonsComponentController.buttonGo.disableProperty().bind(renamingService.runningProperty().or(previewService.runningProperty()).or(listFilesService.runningProperty()));
        goCancelButtonsComponentController.buttonCancel.disableProperty().bind(renamingService.runningProperty().not());

    }

    private void initServices() {
        setLoadingServiceCallbacks();
        setRenamingServiceCallbacks();
        setPreviewServiceCallbacks();
        registerStateChangeListeners();
    }

    private void registerStateChangeListeners() {
        listFilesService.runningProperty().addListener((observable, oldValue, newValue) -> loadingFilesServiceStausChanged(newValue));
        previewService.runningProperty().addListener((observable, oldValue, newValue) -> previewFilesServiceStateChanged(newValue));
        fileTypeService.runningProperty().addListener((observable, oldValue, newValue) -> fileTypeServiceStateChanged(newValue));
        renamingService.runningProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                renamingServiceStatusChange(newValue);
            }
        });
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
        listFilesService.setOnFailed(e -> {
            log.error(e.toString());
        });
    }

    private void setPreviewServiceCallbacks() {
        previewService.setOnFailed(e -> {
            log.error(e.toString());
        });
    }

    private void setRenamingServiceCallbacks() {
        renamingService.setOnFailed(e -> {
            log.error(e.toString());
        });
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        content1 = fileListComponentController.content1;
        content2 = fileListComponentController.content2;

        textFieldReplacementStringTo = replacementStringComponentController.textFieldReplacementStringTo;
        textFieldReplacementStringFrom = replacementStringComponentController.textFieldReplacementStringFrom;

        initServices();
        initAppMenu(menuBar);
        /* Make scrolling of both lists symmetrical */
        Platform.runLater(() -> {
            FXUtil.getListViewScrollBar(content1).valueProperty()
                    .bindBidirectional(FXUtil.getListViewScrollBar(content2).valueProperty());
        });
        initDragAndDropForLeftFileList();
        comboBoxRenamingStrategy.getItems().add(new SimpleReplaceRenamingStrategy());
        comboBoxRenamingStrategy.getItems().add(new MediaMetadataRenamingStrategy());
        comboBoxRenamingStrategy.getItems().add(new RegexReplaceRenamingStrategy());
        comboBoxRenamingStrategy.getItems().add(new ToLowerCaseRenamingStrategy());
        comboBoxRenamingStrategy.getItems().add(new SpaceToCamelCaseRenamingStrategy());
        comboBoxRenamingStrategy.getItems().add(new UnhideStrategy());
        comboBoxRenamingStrategy.getItems().add(new ExtensionFromMimeStrategy());
        comboBoxRenamingStrategy.getItems().add(new CapitalizeFirstStrategy());
        comboBoxRenamingStrategy.getSelectionModel().selectFirst();

        registerInputChangeListener();

        progressBar.visibleProperty().bind(listFilesService.runningProperty().or(previewService.runningProperty().or(renamingService.runningProperty())));

        goCancelButtonsComponentController.buttonGo.setTooltip(new Tooltip(resourceBundle.getString("mainview.button.go.tooltip")));
        goCancelButtonsComponentController.setButtonCancelActionEventFactory(MainViewButtonCancelEvent::new);
        goCancelButtonsComponentController.setButtonGoActionEventFactory(MainViewButtonGoEvent::new);

        if (config.isDebug())
            applyRandomColors();

        entriesService.getEntries().addListener((ListChangeListener<RenamingEntry>) c -> {
            while (c.next()) {
                if (c.wasAdded()) {
                    var list = new ArrayList<>(c.getAddedSubList());
                    executor.execute(() -> addToContent(list));
                }
            }
        });

        statusLabelLoaded.textProperty().bind(entriesService.statusLoadedProperty());
        statusLabelLoadedFileTypes.textProperty().bind(entriesService.statusLoadedFileTypesProperty());
        statusLabelFilesWillRename.textProperty().bind((entriesService.statusWillRenameProperty()));
        statusLabelFilesWillRenameFileTypes.textProperty().bind(entriesService.statusWillRenameFileTypesProperty());
        statusLabelRenamed.textProperty().bind(entriesService.statusRenamedProperty());
        statusLabelRenamedFileTypes.textProperty().bind(entriesService.statusRenamedFileTypesProperty());

        statusLabelLoadedFileTypes.visibleProperty().bind(statusLabelLoadedFileTypes.textProperty().isNotEmpty());


    }

    private void addToContent(final Collection<? extends RenamingEntry> renamingBeans) {
        renamingBeans.forEach(this::addToContent);
    }

    private void addToContent(final RenamingEntry renamingEntry) {
        var left = RenamingBeanControlBuilder.buildLeft(renamingEntry);
        var right = RenamingBeanControlBuilder.buildRight(renamingEntry);
        Platform.runLater(() -> content1.getItems().add(left));
        Platform.runLater(() -> content2.getItems().add(right));

    }

    private void initDragAndDropForLeftFileList() {
        content1.setOnDragOver(event -> {
            if ((event.getGestureSource() != content1) && event.getDragboard().hasFiles()) {
                /* allow for both copying and moving, whatever user chooses */
                event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
            }
            event.consume();
        });
        content1.setOnDragDropped(event -> {
            final Dragboard db = event.getDragboard();
            boolean success = false;
            if (db.hasFiles()) {

//                    updateInputView(filesToPathList(db.getFiles()));
                if (db.getFiles().size() == 1 && db.getFiles().iterator().next().isDirectory())
                    startDirectoryComponentController.textFieldDirectory.setText(db.getFiles().iterator().next().getPath());
                else startDirectoryComponentController.textFieldDirectory.setText(null);

                success = true;
            }
            /*
             * let the source know whether the string was successfully transferred and used
             */
            event.setDropCompleted(success);
            event.consume();
        });
        startDirectoryComponentController.textFieldDirectory.setOnDragOver(event -> {
            if ((event.getGestureSource() != content1) && event.getDragboard().hasFiles()) {
                /* allow for both copying and moving, whatever user chooses */
                event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
            }
            event.consume();
        });
        startDirectoryComponentController.textFieldDirectory.setOnDragDropped(event -> {
            final Dragboard db = event.getDragboard();
            boolean success = false;
            if (db.hasFiles() && db.getFiles().size() == 1 && db.getFiles().iterator().next().isDirectory()) {

//                    updateInputView(filesToPathList(db.getFiles()));

                success = true;
                startDirectoryComponentController.textFieldDirectory.setText(db.getFiles().iterator().next().getPath());
            }
            /*
             * let the source know whether the string was successfully transferred and used
             */
            event.setDropCompleted(success);
            event.consume();
        });
    }

    public static void initAppMenu(MenuBar menuBar) {
        final String os = System.getProperty("os.name");
        if (os != null && os.startsWith("Mac")) {
            menuBar.useSystemMenuBarProperty().set(true);
        }
    }

    public void handleMenuItemDummyFileCreator(ActionEvent actionEvent) {

        DummyFileCreatorController controller = fxWeaver.loadController(DummyFileCreatorController.class, resourceBundle);
        controller.show();

//        try {
//            final FXMLLoader loader = new FXMLLoader(this.getClass().getResource("/fxml/DummyFileCreator.fxml"));
//            final Parent root = loader.load();
//            final Stage stage = new Stage();
//            stage.initStyle(StageStyle.UTILITY);
//            stage.setMinWidth(root.minWidth(-1));
//            stage.setMinHeight(root.minHeight(-1));
//            final Scene scene = new Scene(root);
//            stage.setTitle("Dummy File Creator");
//            stage.setScene(scene);
//            stage.show();
//        } catch (final IOException e) {
//            log.error(e.getLocalizedMessage(), e);
//        }
    }

    public void handleMenuItemKodiTools(ActionEvent actionEvent) {
        KodiToolsController controller = fxWeaver.loadController(KodiToolsController.class);
        controller.show();
    }

    @FXML
    private void handleMenuItemRegexTips(final ActionEvent event) {

        try {
            final FXMLLoader loader = new FXMLLoader(this.getClass().getResource("/fxml/RegexTipsView.fxml"));
            final Parent root = loader.load();
            final Stage stage = new Stage();
            final Scene scene = new Scene(root);
            stage.setTitle("Regex Tips");
            stage.setScene(scene);
            stage.setWidth(400);
            stage.setHeight(400);
            stage.show();
        } catch (final IOException e) {
            log.error(e.getLocalizedMessage(), e);
        }
    }

    private void updateInputView(final Collection<Path> files) throws IOException {
        log.debug("Updating input view");
        clearView();
        initLoadService(files);
        startService(listFilesService);
    }

    private void updateInputView(final Path path) {
        log.debug("Updating input view");
        clearView();
        initLoadService(path);
        startService(listFilesService);
    }

    private void updateInputView(final String path) {
        if (path != null)
            updateInputView(Path.of(path));
    }

    private void initLoadService(Path path) {
        initLoadService(Collections.singleton(path));
    }

    private void initLoadService(Collection<Path> files) {
        listFilesService.cancel();
        listFilesService.reset();
        listFilesService.setFiles(files);
        listFilesService.setOnSucceeded((e) -> {
            initFileTypeService(entriesService.getEntries());
            startService(fileTypeService);
            updateOutputView();

        });
        progressBar.progressProperty().bind(listFilesService.progressProperty());
    }

    private void updateOutputView() {
        log.debug("Updating output view");
        initPreviewService();
        startService(previewService);
    }

    private void initFileTypeService(Collection<RenamingEntry> renamingEntries) {
        fileTypeService.cancel();
        fileTypeService.reset();
        fileTypeService.setRenamingEntries(renamingEntries);
        var typeProvider = initAndGetFileTypeStrategy();
        fileTypeService.setFileTypeProvider(typeProvider);
    }

    private FileTypeProvider initAndGetFileTypeStrategy() {
        return new FileTypeByMimeProvider();
    }

    private void initPreviewService() {
        previewService.cancel();
        previewService.reset();
        previewService.setRenamingEntries(entriesService.getEntries());
        progressBar.progressProperty().bind(previewService.progressProperty());
        var strat = initAndGetStrategy();
        if (strat != null)
            previewService.setRenamingStrategy(strat);
    }

    private boolean calcIsFiltered(RenamingEntry renamingEntry) {
        return (renamingEntry.getOldPath().toFile().isDirectory() && ignoreDirectories.isSelected()) ||
                (renamingEntry.getOldPath().toFile().isHidden() && ignoreHiddenFiles.isSelected());
    }


    /**
     * Starts a {@link Service}. Call in UI thread.
     *
     * @param service Service to start
     */
    private void startService(Service<?> service) {

        log.debug("Starting service {}", service);

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
        content1.getItems().clear();
        content2.getItems().clear();
    }

    private void cancelCurrentOperation() {
        log.debug("Cancelling current operation");
        previewService.cancel();
        renamingService.cancel();
        listFilesService.cancel();
        updateInputView(startDirectoryComponentController.textFieldDirectory.getText().trim());
    }

    @EventListener
    public void onButtonGoEvent(MainViewButtonGoEvent event){
        handleButtonActionGo(event.getActionEvent());
    }

    @EventListener
    public void onButtonCancelEvent(MainViewButtonCancelEvent event){
        handleButtonActionCancel(event.getActionEvent());
    }

    private RenamingStrategy initAndGetStrategy() {

        final RenamingStrategy strategy = comboBoxRenamingStrategy.getSelectionModel().getSelectedItem();
        if (strategy == null)
            return null;
        strategy.setReplacementStringFrom(textFieldReplacementStringFrom.getText());
        strategy.setReplacementStringTo(textFieldReplacementStringTo.getText());
        return strategy;
    }

    public void handleButtonActionGo(ActionEvent actionEvent) {

        final RenamingStrategy s = initAndGetStrategy();
        if (s != null) {
            renamingService.cancel();
            renamingService.reset();
            renamingService.setEvents(entriesService.getEntries());
            renamingService.setStrategy(s);
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


}
