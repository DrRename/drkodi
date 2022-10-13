package drrename.kodi;

import drrename.RenameUtil;
import drrename.ui.FXUtil;
import drrename.ui.mainview.GoCancelButtonsComponentController;
import drrename.ui.mainview.StartDirectoryComponentController;
import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
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
import java.util.ArrayList;
import java.util.List;
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

    public TreeView<KodiTreeItemContent> treeView;

    public Button buttonExpandAll;

    public Button buttonCollapseAll;

    public CheckBox checkBoxHideEmpty;

    private Stage mainStage;

    Stage imageStage;

    public StartDirectoryComponentController startDirectoryComponentController;

    public GoCancelButtonsComponentController goCancelButtonsComponentController;

    private final KodiService service;

    private final Executor executor;

    private KodiTreeRootItem treeRoot;

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

        treeRoot = new KodiTreeRootItem();
        treeView.setRoot(treeRoot);
        buttonExpandAll.setDisable(true);
        buttonCollapseAll.setDisable(true);
        treeRoot.getChildren().addListener((ListChangeListener<? super TreeItem<KodiTreeItemContent>>) e -> {
            buttonExpandAll.setDisable(e.getList().isEmpty());
            buttonCollapseAll.setDisable(e.getList().isEmpty());
        });
        checkBoxHideEmpty.selectedProperty().addListener((observable, oldValue, newValue) -> updateTreeRootPredicate(newValue));
        startDirectoryComponentController.inputPathProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                onButtonGoEvent(null);
            }
        });
        treeView.setCellFactory(tv -> new TreeCell<>() {

            @Override
            protected void updateItem(KodiTreeItemContent item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null) {
                    setText(null);
                    setStyle(null);
                } else {
                    setText(item.toString());
                    List<String> styles = new ArrayList<>();
                    if (item.hasWarning()) {
                        styles.add("-fx-font-weight: bold;");
                        if (item instanceof KodiLevel1TreeItemContent) {
                            styles.add("-fx-background-color: wheat;");
                        } else if (item instanceof KodiLevel2TreeItemContent) {
                            styles.add("-fx-background-color: wheat;");
                        } else {
                            styles.add("-fx-background-color: wheat;");
                        }
                        var joinedStylesString = String.join(" ", styles);
                        setStyle(joinedStylesString);
                    } else {
                        setStyle(null);
                    }
                }
            }
        });

        treeView.getSelectionModel().getSelectedIndices().addListener((ListChangeListener<Integer>) c -> {
            imageStage.close();
            while (c.next()) {
                if (c.getAddedSubList().isEmpty()) {
                    continue;
                }
                var hans = treeView.getTreeItem(c.getAddedSubList().get(0)).getValue();
                log.debug("Selection changed: {}", hans);
                if (hans instanceof KodiLevel3TreeItemContent peter) {
                    log.debug("Handling {}", hans);
                    if (peter.getCheckResult() instanceof NfoFileContentCheckResult) {
                        Path nfoFile = ((NfoFileContentCheckResult) peter.getCheckResult()).getNfoFile();
                        executor.execute(() -> showImage(nfoFile));
                    } else if (peter.getCheckResult() instanceof NfoFileCheckResult) {
                        if (!((NfoFileCheckResult) peter.getCheckResult()).getNfoFiles().isEmpty()) {
                            Path nfoFile = ((NfoFileCheckResult) peter.getCheckResult()).getNfoFiles().get(0);
                            executor.execute(() -> showImage(nfoFile));
                        }
                    }
                }
            }
        });
    }

    private void showImage(Path nfoFile) {
        if (nfoFile != null && Files.exists(nfoFile) && Files.isReadable(nfoFile)) {
            try {
                Path imagePath = RenameUtil.getImagePathFromNfo(nfoFile);
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

    private void updateTreeRootPredicate(boolean onlyWarnings) {
        treeRoot.setPredicate(buildHideEmptyPredicate(onlyWarnings));
    }

    private static Predicate<KodiTreeItemContent> buildHideEmptyPredicate(boolean onlyWarnings) {
        return item -> {
            if (onlyWarnings)
                return item.hasWarning();
            return true;
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
        service.setDirectory(startDirectoryComponentController.getInputPath());
        progressBar.progressProperty().bind(service.progressProperty());
        service.setOnSucceeded(this::handleResult);
        service.setOnFailed(this::handleFailed);
        service.setOnCancelled(this::handleResult);
        treeRoot.getSourceChildren().clear();
        service.start();
    }

    private void handleResult(WorkerStateEvent workerStateEvent) {
        if (service.getValue() != null) {
            treeRoot.getSourceChildren().clear();
            treeRoot.getSourceChildren().addAll(buildAndFillLevel1Items(service.getValue()));
        } else {
            log.info("Got no result. Cancelled?");
        }
    }

    private List<KodiLevel1TreeItem> buildAndFillLevel1Items(List<Path> movieFolders) {
        List<KodiLevel1TreeItem> result = new ArrayList<>();

        // base/ level 1 items
        var nfoFileNameLevel1TreeItem = new NfoFileNameLevel1TreeItem();
        var nfoFileContentLevel1TreeItem = new NfoFileContentLevel1TreeItem();
        var subdirsLevel1TreeItem = new SubdirsLevel1TreeItem();
        // add level 1 items to return list
        result.add(nfoFileNameLevel1TreeItem);
        result.add(nfoFileContentLevel1TreeItem);
        result.add(subdirsLevel1TreeItem);

        // fill level 1 items with level 2 items
        for (Path movieFolder : movieFolders) {
            nfoFileNameLevel1TreeItem.add(new NfoFileNameLevel2TreeItem(movieFolder, executor));
            nfoFileContentLevel1TreeItem.add(new NfoFileContentLevel2TreeItem(movieFolder, executor));
            subdirsLevel1TreeItem.add(new SubdirsLevel2TreeItem(movieFolder, executor));
        }

        return result;
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
