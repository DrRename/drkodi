package drrename.kodi;

import drrename.kodi.treeitem.FilterableKodiRootTreeItem;
import drrename.kodi.treeitem.KodiTreeItemValue;
import drrename.util.FXUtil;
import drrename.ui.mainview.GoCancelButtonsComponentController;
import drrename.ui.mainview.StartDirectoryComponentController;
import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.collections.ListChangeListener;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ResourceBundle;
import java.util.concurrent.Executor;
import java.util.function.Predicate;

@RequiredArgsConstructor
@Slf4j
@Component
@FxmlView("/fxml/KodiTools.fxml")
public class KodiToolsController implements Initializable {

    public static final int imageStageXOffset = 600;

    public BorderPane root;

    public ProgressBar progressBar;

    public TreeView<KodiTreeItemValue<?>> treeView;

    public Button buttonExpandAll;

    public Button buttonCollapseAll;

    public CheckBox checkBoxHideEmpty;

    public HBox goCancelButtonsComponent;

    public CheckBox checkBoxMissingNfoFileIsAWarning;

    private Stage mainStage;

    Stage imageStage;

    public StartDirectoryComponentController startDirectoryComponentController;

    public GoCancelButtonsComponentController goCancelButtonsComponentController;

    private final MovieDirectoryCollectorService service;

    private final Executor executor;

    private FilterableKodiRootTreeItem treeRoot;

    private final MovieDbClientFactory movieDbClientFactory;

    private WarningsConfig warningsConfig;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        mainStage = new Stage();
        imageStage = new Stage();
        mainStage.setScene(new Scene(root));
        mainStage.setTitle("Kodi Tools");
        goCancelButtonsComponentController.buttonGo.disableProperty().bind(service.runningProperty().or(startDirectoryComponentController.readyProperty().not()));
        goCancelButtonsComponentController.buttonCancel.disableProperty().bind(service.runningProperty().not());
        goCancelButtonsComponentController.setButtonCancelActionEventFactory(KodiToolsButtonCancelEvent::new);
        goCancelButtonsComponentController.setButtonGoActionEventFactory(KodiToolsButtonGoEvent::new);
        progressBar.visibleProperty().bind(service.runningProperty());

        treeRoot = new FilterableKodiRootTreeItem(executor, warningsConfig, null);
        treeView.setRoot(treeRoot);
        buttonExpandAll.setDisable(true);
        buttonCollapseAll.setDisable(true);
        treeRoot.getChildren().addListener((ListChangeListener<? super TreeItem<KodiTreeItemValue<?>>>) e -> {
            buttonExpandAll.setDisable(e.getList().isEmpty());
            buttonCollapseAll.setDisable(e.getList().isEmpty());
        });
        treeRoot.setPredicate(buildHideEmptyPredicate());
        startDirectoryComponentController.inputPathProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                onButtonGoEvent(null);
            }
        });
        treeView.setCellFactory(this::treeViewCellFactoryCallback);

        treeView.getSelectionModel().getSelectedIndices().addListener((ListChangeListener<Integer>) c -> {
            imageStage.close();
            while (c.next()) {
                if (c.getAddedSubList().isEmpty()) {
                    continue;
                }
                var hans = treeView.getTreeItem(c.getAddedSubList().get(0)).getValue();
                log.debug("Selection changed: {} ({})", hans, hans.getClass());
//                if (hans instanceof NfoFileTreeItemValue peter) {
//                    log.debug("Handling {}", peter);
//                    Path nfoFile = peter.getNfoFile();
//                    executor.execute(() -> showImage(nfoFile));
//                }
            }
        });

        warningsConfig = new WarningsConfig();
        warningsConfig.missingNfoFileIsWarningProperty().bind(checkBoxMissingNfoFileIsAWarning.selectedProperty());
    }

    private TreeCell<KodiTreeItemValue<?>> treeViewCellFactoryCallback(TreeView<KodiTreeItemValue<?>> kodiTreeItemContentTreeView) {
        return new KodiTreeCell(treeView);
    }

    private void showImage(Path nfoFile) {
        if (nfoFile != null && Files.exists(nfoFile) && Files.isReadable(nfoFile)) {
            try {
                Path imagePath = KodiUtil.getImagePathFromNfo(nfoFile);
                if (imagePath != null && Files.exists(imagePath) && Files.isReadable(imagePath)) {
                    log.debug("Taking a look at {}", imagePath);
                    Image image = new Image(imagePath.toFile().toURI().toString(), false);
                    image.exceptionProperty().addListener((observable, oldValue, newValue) -> {
                        if (newValue != null)
                            log.error(newValue.getLocalizedMessage(), newValue);
                    });
                    ImageView imageView = new ImageView();
                    imageView.setImage(image);
                    imageView.setPreserveRatio(true);
                    Platform.runLater(() -> showImageStage(imageView, imagePath.toString()));
                }
            } catch (FileNotFoundException e) {
                log.debug("Ignoring {}", e.getLocalizedMessage());
            } catch (IOException e) {
                log.error(e.getLocalizedMessage(), e);
            }
        } else {
            log.debug("Cannot access {}", nfoFile);
        }
    }


    private void showImageStage(ImageView imageView, String title) {
        imageStage.setScene(new Scene(new VBox(imageView)));
        imageStage.setTitle(title);
        imageStage.setX(mainStage.getX() + imageStageXOffset);
        imageStage.show();
    }

    private Predicate<KodiTreeItemValue<?>> buildHideEmptyPredicate() {
        return item -> {
            return item.warningProperty().get() != null && item.isWarning() || !checkBoxHideEmpty.isSelected();
        };
    }

    public void show() {
        mainStage.show();
    }

    @EventListener
    public void onButtonGoEvent(KodiToolsButtonGoEvent event) {
        startService();
    }

    @EventListener
    public void onButtonCancelEvent(KodiToolsButtonCancelEvent event){
        cancelService();
    }

    private void startService() {
        if (startDirectoryComponentController.getInputPath() == null) {
            log.warn("Cannot start, input path is null");
            return;
        }
        log.debug("Starting service {}", service);
        service.reset();
        treeRoot.getSourceChildren().clear();
        service.setDirectory(startDirectoryComponentController.getInputPath());
        service.setExecutor(executor);
        service.setRootTreeItem(treeRoot);
        service.setMovieDbClientFactory(movieDbClientFactory);
        service.setWarningsConfig(warningsConfig);
        service.setExtractor(new Observable[]{checkBoxHideEmpty.selectedProperty()});
        progressBar.progressProperty().bind(service.progressProperty());
        service.setOnFailed(this::handleFailed);
        treeRoot.getSourceChildren().clear();
        service.start();
    }

    private void handleFailed(WorkerStateEvent e) {
        log.error("Service failed: {}", service.getException(), service.getException());
    }

    private void cancelService(){
        service.cancel();
    }

    public void handleButtonExpandAll(ActionEvent actionEvent) {
        FXUtil.expandTreeView(treeView.getRoot(), true);
    }

    public void handleButtonCollapseAll(ActionEvent actionEvent) {
        FXUtil.expandTreeView(treeView.getRoot(), false);
    }
}
