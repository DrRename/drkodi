package drrename;

import drrename.mainview.GoCancelButtonsComponentController;
import drrename.mainview.ReplacementStringComponentController;
import drrename.mainview.StartDirectoryComponentController;
import drrename.model.RenamingBean;
import drrename.strategy.CapitalizeFirstStrategy;
import drrename.event.FileEntryEvent;
import drrename.event.MainViewButtonCancelEvent;
import drrename.event.MainViewButtonGoEvent;
import drrename.filecreator.DummyFileCreatorController;
import drrename.kodi.KodiToolsController;
import drrename.mainview.MainViewConfig;
import drrename.mainview.controller.FileListComponentController;
import drrename.strategy.*;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.concurrent.Service;
import javafx.concurrent.Worker;
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
import net.sf.kerner.utils.pair.PairSame;
import org.springframework.boot.info.BuildProperties;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RequiredArgsConstructor
@Slf4j
@Component
@FxmlView("/fxml/MainView.fxml")
public class MainController implements Initializable, ApplicationListener<ApplicationEvent> {

    private final MainViewConfig config;

    private final ListFilesService listFilesService;
    private final PreviewService previewService;
    private final ConfigurableApplicationContext applicationContext;
    public HBox goCancelButtonsComponent;
    public BorderPane layer04_3;
    public BorderPane layer04_2;
    public VBox fileListComponent;
    public BorderPane startDirectoryComponent;
    public HBox replacementStringComponent;

    private RenamingService renamingService;

    public ListView<Control> content1;
    public ListView<Control> content2;

    /**
     * Internal list of renaming entries. Should always be in sync with {@link content1} and {@link content2}.
     */
    private List<RenamingBean> entries;
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
    Node layer02_2;

    @FXML
    Node layer02_3;

    @FXML
    Node layer03_1;

    @FXML
    Node layer03_2;

    @FXML
    Node layer03_3;

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

    private final BuildProperties buildProperties;

    public GoCancelButtonsComponentController goCancelButtonsComponentController;

    public StartDirectoryComponentController startDirectoryComponentController;

    public FileListComponentController fileListComponentController;

    public ReplacementStringComponentController replacementStringComponentController;

    private void applyRandomColors() {
        Stream.of(layer01, layer02_3, layer03_2, layer04_1, layer04_2, layer04_3, layer05_1, goCancelButtonsComponent).forEach(l -> l.setStyle("-fx-background-color: " + getRandomColorString()));
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
        setRenamingServiceCallbacks();
        setPreviewServiceCallbacks();
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

        this.entries = new ArrayList<>();
        renamingService = new RenamingService();
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

        goCancelButtonsComponentController.buttonGo.setTooltip(new Tooltip("Start renaming"));
        goCancelButtonsComponentController.setButtonCancelActionEventFactory(MainViewButtonCancelEvent::new);
        goCancelButtonsComponentController.setButtonGoActionEventFactory(MainViewButtonGoEvent::new);

        if (config.isDebug())
            applyRandomColors();

        log.debug("Input component: {}", startDirectoryComponentController);
        log.debug("Buttons component: {}", goCancelButtonsComponentController);

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
                try {
                    updateInputView(filesToPathList(db.getFiles()));
                    if (db.getFiles().size() == 1 && db.getFiles().iterator().next().isDirectory())
                        startDirectoryComponentController.textFieldDirectory.setText(db.getFiles().iterator().next().getPath());
                    else startDirectoryComponentController.textFieldDirectory.setText(null);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
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
                try {
                    updateInputView(filesToPathList(db.getFiles()));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                success = true;
                startDirectoryComponentController.textFieldDirectory.setText(db.getFiles().iterator().next().getPath());
                updateOutputView();
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

        DummyFileCreatorController controller = fxWeaver.loadController(DummyFileCreatorController.class);
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

        cancelCurrentOperation();
        clearView();
        initListFilesService(files);
        startService(listFilesService);
    }

    private void updateInputView(final Path path) {
        cancelCurrentOperation();
        clearView();
        initListFilesService(path);
        startService(listFilesService);
    }

    private void updateInputView(final String path) {
        if (path != null)
            updateInputView(Path.of(path));
    }

    private void initListFilesService(Path path) {
        listFilesService.setFiles(Collections.singleton(path));
        listFilesService.setOnSucceeded((e) -> updateOutputView());
        progressBar.progressProperty().bind(listFilesService.progressProperty());
    }

    private void initListFilesService(Collection<Path> files) throws IOException {
        listFilesService.setFiles(files);
        listFilesService.setOnSucceeded((e) -> updateOutputView());
        progressBar.progressProperty().bind(listFilesService.progressProperty());
    }

    private void updateOutputView() {
        initPreviewService();
        startService(previewService);
    }

    private void initPreviewService() {
        previewService.setFiles(entries);
        progressBar.progressProperty().bind(previewService.progressProperty());
        var strat = initAndGetStrategy();
        if (strat != null)
            previewService.setRenamingStrategy(strat);
    }

    private void addToContent(final RenamingBean renamingBean) {

        renamingBean.filteredProperty().bind(Bindings.createBooleanBinding(() -> calcIsFiltered(renamingBean), ignoreDirectories.selectedProperty(), ignoreHiddenFiles.selectedProperty()));
        final PairSame<Control> entry = RenamingBeanControlBuilder.buildRenameEntryNode(renamingBean);
        content1.getItems().add(entry.getFirst());
        content2.getItems().add(entry.getSecond());
    }

    private boolean calcIsFiltered(RenamingBean renamingBean) {
        return (renamingBean.getOldPath().toFile().isDirectory() && ignoreDirectories.isSelected()) ||
                (renamingBean.getOldPath().toFile().isHidden() && ignoreHiddenFiles.isSelected());
    }


    /**
     * Starts a {@link Service}. Call in UI thread.
     *
     * @param service Service to start
     */
    private void startService(Service<?> service) {
        if ((service.getState() == Worker.State.RUNNING) || (service.getState() == Worker.State.SCHEDULED)) {
            if (log.isDebugEnabled()) {
                log.debug("Still busy, waiting for cancel " + service);
            }
            service.setOnCancelled(e -> {
                if (log.isDebugEnabled()) {
                    log.debug("Cancelled " + service);
                }
                startService(service);
            });
            service.cancel();
            return;
        }
        service.reset();
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
        service.start();
    }

    private void clearView() {
        entries.clear();
        content1.getItems().clear();
        content2.getItems().clear();
    }

    private void cancelCurrentOperation() {
        previewService.cancel();
        listFilesService.cancel();
    }

    @Override
    public void onApplicationEvent(ApplicationEvent event) {
//		log.debug("Event received: {}", event);
        if (event instanceof FileEntryEvent) {
            entries.add((RenamingBean) event.getSource());
            Platform.runLater(() -> addToContent((RenamingBean) event.getSource()));
        }
//		entries.forEach(b -> b.onApplicationEvent(event));
        else if (event instanceof MainViewButtonGoEvent) {
            handleButtonActionGo(((MainViewButtonGoEvent) event).getActionEvent());
        } else if (event instanceof MainViewButtonCancelEvent) {
            // TODO
        }
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
            renamingService.setEvents(entries);
            renamingService.setStrategy(s);
            progressBar.progressProperty().bind(renamingService.progressProperty());
            renamingService.start();
        } else {
            log.info("No renaming strategy selected");
        }
    }

    public void handleMenuItemAbout(ActionEvent actionEvent) {
    }


}
