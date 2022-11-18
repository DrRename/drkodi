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

package drrename.ui.kodi;

import drrename.kodi.*;
import drrename.ui.mainview.controller.TabController;
import drrename.util.FXUtil;
import drrename.ui.mainview.GoCancelButtonsComponentController;
import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
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


@Slf4j
@Component
@FxmlView("/fxml/KodiTools.fxml")
public class KodiToolsController implements Initializable {

    public static final int imageStageXOffset = 600;

    private final TabController tabController;

    public Parent root;

    public ProgressBar progressBar;

    public TreeView<KodiTreeItemValue<?>> treeView;

    public Button buttonExpandAll;

    public Button buttonCollapseAll;

    public CheckBox checkBoxHideEmpty;


    public CheckBox checkBoxMissingNfoFileIsAWarning;

    public CheckBox checkBoxDefaultNfoFileNameIsAWarning;

    private Stage mainStage;

    Stage imageStage;

    private final MovieDirectoryCollectorService service;

    private final Executor executor;

    private FilterableKodiRootTreeItem treeRoot;

    private final MovieDbClientFactory movieDbClientFactory;

    private WarningsConfig warningsConfig;

    private class KodiServiceStarter extends ServiceStarter<MovieDirectoryCollectorService> {

        public KodiServiceStarter(MovieDirectoryCollectorService service) {
            super(service);
        }

        @Override
        protected void onSucceeded(WorkerStateEvent workerStateEvent) {

        }

        @Override
        protected void doInitService(MovieDirectoryCollectorService service) {
            service.setDirectory(tabController.startDirectoryComponentController.getInputPath());
            service.setExecutor(executor);
            service.setRootTreeItem(treeRoot);
            service.setMovieDbClientFactory(movieDbClientFactory);
            service.setWarningsConfig(warningsConfig);
            service.setExtractor(new Observable[]{checkBoxHideEmpty.selectedProperty()});

        }

        @Override
        protected void prepareUi() {
            clearUi();
        }

        @Override
        protected boolean checkPreConditions() {
           return true;
        }
    }

    private final KodiServiceStarter serviceStarter;

    public KodiToolsController(TabController tabController, MovieDirectoryCollectorService service, Executor executor, MovieDbClientFactory movieDbClientFactory) {
        this.tabController = tabController;
        this.service = service;
        this.executor = executor;
        this.movieDbClientFactory = movieDbClientFactory;
        this.serviceStarter = new KodiServiceStarter(service);
    }



    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        mainStage = new Stage();
        imageStage = new Stage();
        mainStage.setScene(new Scene(root));
        mainStage.setTitle("Kodi Tools");

        initTreeRoot();

        buttonExpandAll.setDisable(true);
        buttonCollapseAll.setDisable(true);

        progressBar.progressProperty().bind(service.progressProperty());
        progressBar.visibleProperty().bind(service.runningProperty());

        tabController.startDirectoryComponentController.readyProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if(newValue)
                    serviceStarter.startService();
                else {
                    cancelAllAndClearUi();
                }
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
        warningsConfig.defaultNfoFileNameIsWarningProperty().bind(checkBoxDefaultNfoFileNameIsAWarning.selectedProperty());




    }

    private void initTreeRoot() {
        treeRoot = new FilterableKodiRootTreeItem(executor, warningsConfig, null);
        treeRoot.setExpanded(true);
        treeRoot.getChildren().addListener((ListChangeListener<? super TreeItem<KodiTreeItemValue<?>>>) e -> {
            buttonExpandAll.setDisable(e.getList().isEmpty());
            buttonCollapseAll.setDisable(e.getList().isEmpty());
        });
        treeRoot.setPredicate(buildHideEmptyPredicate());
        treeView.setRoot(treeRoot);
    }

    private void cancelAllAndClearUi() {
        service.setOnCancelled(event -> clearUi());
        service.cancel();
        clearUi();
    }

    private void clearUi() {

        treeRoot.getSourceChildren().clear();
        // for some reason, always one element is left in the children list
        treeRoot.getChildren().clear();
        log.debug("UI cleared. Elements left: {}, {}", treeRoot.getSourceChildren(), treeRoot.getChildren());

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
        return item -> item.warningProperty().get() != null && item.isWarning() || !checkBoxHideEmpty.isSelected();
    }

    public void show() {
        mainStage.show();
    }

    public void handleButtonExpandAll(ActionEvent actionEvent) {
        FXUtil.expandTreeView(treeView.getRoot(), true);
    }

    public void handleButtonCollapseAll(ActionEvent actionEvent) {
        FXUtil.expandTreeView(treeView.getRoot(), false);
    }
}
