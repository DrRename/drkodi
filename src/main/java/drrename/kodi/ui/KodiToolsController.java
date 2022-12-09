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

import drrename.config.AppConfig;
import drrename.kodi.KodiCollectService;
import drrename.kodi.KodiSuggestionsService;
import drrename.kodi.KodiUtil;
import drrename.kodi.WarningsConfig;
import drrename.kodi.data.Movie;
import drrename.kodi.data.StaticMovieData;
import drrename.kodi.ui.config.KodiUiConfig;
import drrename.kodi.ui.control.HansBox;
import drrename.ui.DebuggableController;
import drrename.ui.DrRenameController;
import drrename.ui.ProgressAndStatusGridPane;
import drrename.ui.config.UiConfig;
import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.concurrent.WorkerStateEvent;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.stereotype.Component;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.Executor;
import java.util.function.Predicate;


@Slf4j
@Component
@FxmlView("/fxml/KodiTools.fxml")
public class KodiToolsController extends DebuggableController implements DrRenameController {

    public static final int imageStageXOffset = 600;

    // Spring injected //

    private final UiConfig uiConfig;

    private final KodiUiConfig kodiUiConfig;

    private final KodiCollectService kodiCollectService;

//    private final KodiAddChildItemsService kodiAddChildItemsService;

    private final Executor executor;

//    private final TheMovieDbConfig movieDbConfig;

//    private final MovieDbClient movieDbClient;

//    private final KodiSuggestionsService kodiSuggestionsService;


    //

    // FXML injected //

    @FXML
    Parent root;


    @FXML
    ScrollPane scrollPane;

//    @FXML
//    Button buttonExpandAll;
//
//    @FXML
//    Button buttonCollapseAll;

    @FXML
    CheckBox checkBoxHideEmpty;

    @FXML
    CheckBox checkBoxMissingNfoFileIsAWarning;

    @FXML
    CheckBox checkBoxDefaultNfoFileNameIsAWarning;

    @FXML
    HBox buttonBox;

    @FXML
    ProgressAndStatusGridPane progressAndStatusGridPane;

    @FXML
    Stage mainStage;

    @FXML
    Stage imageStage;

    // Default fields //

//    private FilterableKodiRootTreeItem treeRoot;

    private WarningsConfig warningsConfig;

    private final KodiCollectServiceStarter serviceStarter;

//    private final KodiSuggestionsServiceStarter kodiSuggestionsServiceStarter;

    private final Label progressLabel;

//    private final FlowPane flowPane;

    private final VBox listView;

    private final KodiUiElementBuilder uiElementBuilder;

    //

    // Nested classes //

    private class KodiSuggestionsServiceStarter extends ServiceStarter<KodiSuggestionsService> {

        public KodiSuggestionsServiceStarter(KodiSuggestionsService service) {
            super(service);
        }

        @Override
        protected void doInitService(KodiSuggestionsService service) {
            service.setExecutor(executor);
            progressAndStatusGridPane.getProgressBar().progressProperty().bind(service.progressProperty());
            progressLabel.textProperty().bind(service.messageProperty());
        }
    }

    private class KodiAddChildItemsServiceStarter extends ServiceStarter<KodiAddChildItemsService> {

        public KodiAddChildItemsServiceStarter(KodiAddChildItemsService service) {
            super(service);
        }

        @Override
        protected void doInitService(KodiAddChildItemsService service) {
            service.setWarningsConfig(warningsConfig);
            progressAndStatusGridPane.getProgressBar().progressProperty().bind(service.progressProperty());
            progressLabel.textProperty().bind(service.messageProperty());
        }
    }

    private class KodiCollectServiceStarter extends ServiceStarter<KodiCollectService> {

        public KodiCollectServiceStarter(KodiCollectService service) {
            super(service);
        }

//        @Override
//        protected void onCancelled(WorkerStateEvent workerStateEvent) {
//            kodiSuggestionsService.cancel();
//        }

        @Override
        protected void onSucceeded(WorkerStateEvent workerStateEvent) {

            @SuppressWarnings("unchecked")
            List<Movie> result = (ObservableList<Movie>) workerStateEvent.getSource().getValue();



            if (result != null) {

                // TODO: maybe do this on a worker thread
                List<HansBox> result2 = result.stream().map(e ->
                    new HansBox(e, executor, getAppConfig(), kodiUiConfig)
                ).toList();

                listView.getChildren().setAll(result2);
//                kodiSuggestionsService.setElements(result);
//                kodiSuggestionsServiceStarter.startService();

            } else {
                log.error("Got no result from {} with state {}", workerStateEvent.getSource(), workerStateEvent.getSource().getState());
            }
        }

        @Override
        protected void doInitService(KodiCollectService service) {
//            service.setRootTreeItem(treeRoot);
            service.setWarningsConfig(warningsConfig);
            service.setExtractor(new Observable[]{checkBoxHideEmpty.selectedProperty()});
            progressAndStatusGridPane.getProgressBar().progressProperty().bind(service.progressProperty());
            progressLabel.textProperty().bind(service.messageProperty());

        }

        @Override
        protected void prepareUi() {
            clearUi();
        }
    }

    //

    public KodiToolsController(KodiCollectService kodiCollectService, /*KodiAddChildItemsService kodiAddChildItemsService,*/ Executor executor, AppConfig appConfig, UiConfig uiConfig, KodiUiConfig kodiUiConfig/*, TheMovieDbConfig movieDbConfig, MovieDbClient movieDbClient, KodiSuggestionsService kodiSuggestionsService*/) {
        super(appConfig);
        this.kodiCollectService = kodiCollectService;
//        this.kodiAddChildItemsService = kodiAddChildItemsService;
        this.executor = executor;
        this.serviceStarter = new KodiCollectServiceStarter(kodiCollectService);
//        KodiAddChildItemsServiceStarter kodiAddChildItemsServiceStarter = new KodiAddChildItemsServiceStarter(kodiAddChildItemsService);
        this.uiConfig = uiConfig;
        this.kodiUiConfig = kodiUiConfig;
//        this.kodiSuggestionsServiceStarter = new KodiSuggestionsServiceStarter(kodiSuggestionsService);
//        this.movieDbConfig = movieDbConfig;
//        this.movieDbClient = movieDbClient;
//        this.kodiSuggestionsService = kodiSuggestionsService;
        this.progressLabel = new Label();
//        this.flowPane = new FlowPane();
        this.listView = new VBox(20);
        this.listView.setPadding(new Insets(4,4,4,4));
        this.uiElementBuilder = new KodiUiElementBuilder(appConfig, kodiUiConfig);
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        super.initialize(url, resourceBundle);

        mainStage = new Stage();
        imageStage = new Stage();
        mainStage.setScene(new Scene(root));
        mainStage.setTitle("Kodi Tools");

        kodiCollectService.setExecutor(executor);
//        kodiAddChildItemsService.setExecutor(executor);

//        initTreeRoot();

//        buttonExpandAll.setDisable(true);
//        buttonCollapseAll.setDisable(true);

        if (!getAppConfig().isDebug())
            progressAndStatusGridPane.getProgressBar().visibleProperty().bind(kodiCollectService.runningProperty()/*.or(kodiAddChildItemsService.runningProperty())*/);

//        treeView.setCellFactory(this::treeViewCellFactoryCallback);
//
//        treeView.getSelectionModel().getSelectedIndices().addListener((ListChangeListener<Integer>) c -> {
//            imageStage.close();
//            while (c.next()) {
//                if (c.getAddedSubList().isEmpty()) {
//                    continue;
//                }
//                var hans = treeView.getTreeItem(c.getAddedSubList().get(0)).getValue();
//                log.debug("Selection changed: {} ({})", hans, hans.getClass());
////                if (hans instanceof NfoFileTreeItemValue peter) {
////                    log.debug("Handling {}", peter);
////                    Path nfoFile = peter.getNfoFile();
////                    executor.execute(() -> showImage(nfoFile));
////                }
//            }
//        });


        warningsConfig = new WarningsConfig();
        warningsConfig.missingNfoFileIsWarningProperty().bind(checkBoxMissingNfoFileIsAWarning.selectedProperty());
        warningsConfig.defaultNfoFileNameIsWarningProperty().bind(checkBoxDefaultNfoFileNameIsAWarning.selectedProperty());

        progressAndStatusGridPane.getProgressStatusBox().getChildren().add(progressLabel);


//        listView.setPadding(new Insets(5, 5, 5, 5));

//
//
        scrollPane.setContent(listView);

        scrollPane.viewportBoundsProperty().addListener(new ChangeListener<Bounds>() {
            @Override
            public void changed(ObservableValue<? extends Bounds> ov, Bounds oldBounds, Bounds bounds) {
                listView.setPrefWidth(bounds.getWidth());
                listView.setPrefHeight(bounds.getHeight());
            }
        });
//


    }

    @Override
    protected Parent[] getUiElementsForRandomColor() {
        return new Parent[]{root, listView/*, flowPane*/, buttonBox, progressAndStatusGridPane, progressAndStatusGridPane.getProgressStatusBox()};
    }

//    private void initTreeRoot() {
//        treeRoot = new FilterableKodiRootTreeItem(executor, warningsConfig, null);
//        treeRoot.setExpanded(true);
//        treeRoot.getChildren().addListener((ListChangeListener<? super TreeItem<KodiTreeItemValue<?>>>) e -> {
//            buttonExpandAll.setDisable(e.getList().isEmpty());
//            buttonCollapseAll.setDisable(e.getList().isEmpty());
//        });
//        treeRoot.setPredicate(buildHideEmptyPredicate());
////        treeView.setRoot(treeRoot);
//    }

    private void clearUi() {

//        treeRoot.getSourceChildren().clear();
//        // for some reason, always one element is left in the children list
////        treeRoot.getChildren().clear();
//        log.debug("UI cleared. Elements left: {}, {}", treeRoot.getSourceChildren(), treeRoot.getChildren());

        listView.getChildren().clear();

    }

    @Override
    public void cancelCurrentOperation() {
//        kodiAddChildItemsService.cancel();

        kodiCollectService.cancel();
    }

    @Override
    public void clearView() {
        clearUi();
    }

    @Override
    public void updateInputView() {
        serviceStarter.startService();
    }

//    private TreeCell<KodiTreeItemValue<?>> treeViewCellFactoryCallback(TreeView<KodiTreeItemValue<?>> kodiTreeItemContentTreeView) {
//        return new KodiTreeCell(treeView);
//    }

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
        return item -> item.warningProperty().get() != null && item.isWarning() || !checkBoxHideEmpty.isSelected();
    }

    public void show() {
        mainStage.show();
    }


    private <T> Pane buildFolderNameBox(StaticMovieData item) {
        Pane result = new HBox();
        Label folderNameLabel2 = new Label(item.getMovieTitleFromFolder());
        result.getChildren().add(folderNameLabel2);
        result.getStyleClass().add("kodi-folder-name");
        return result;
    }

//    public void handleButtonExpandAll(ActionEvent actionEvent) {
//        FXUtil.expandTreeView(treeView.getRoot(), true);
//    }
//
//    public void handleButtonCollapseAll(ActionEvent actionEvent) {
//        FXUtil.expandTreeView(treeView.getRoot(), false);
//    }
}
