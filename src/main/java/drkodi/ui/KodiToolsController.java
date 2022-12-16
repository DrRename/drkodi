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

package drkodi.ui;

import drkodi.*;
import drkodi.config.AppConfig;
import drkodi.ui.config.KodiUiConfig;
import drkodi.ui.control.KodiBox;
import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.concurrent.WorkerStateEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;
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

    private final Entries entries;

    private final KodiUiConfig kodiUiConfig;

    private final KodiCollectService kodiCollectService;

    private final Executor executor;

    private final SearchResultDtoMapper searchResultDtoMapper;

    private final MovieDbQuerier2 movieDbQuerier2;

    //

    // FXML injected //

    @FXML
    Pane root;


    @FXML
    ListView<KodiBox> listView;

    @FXML
    CheckBox checkBoxHideEmpty;

    @FXML
    CheckBox checkBoxMissingNfoFileIsAWarning;

    @FXML
    CheckBox checkBoxDefaultNfoFileNameIsAWarning;


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





    //

    // Nested classes //

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
            List<KodiBox> result = (List<KodiBox>) workerStateEvent.getSource().getValue();
            if (result != null) {
                listView.getItems().setAll(result);
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

    public KodiToolsController(KodiCollectService kodiCollectService, Executor executor, AppConfig appConfig, Entries entries, KodiUiConfig kodiUiConfig, SearchResultDtoMapper searchResultDtoMapper, MovieDbQuerier2 movieDbQuerier2) {
        super(appConfig);
        this.kodiCollectService = kodiCollectService;
        this.executor = executor;
        this.serviceStarter = new KodiCollectServiceStarter(kodiCollectService);
        this.entries = entries;
        this.kodiUiConfig = kodiUiConfig;
        this.searchResultDtoMapper = searchResultDtoMapper;
        this.movieDbQuerier2 = movieDbQuerier2;
        this.progressLabel = new Label();
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        super.initialize(url, resourceBundle);

        mainStage = new Stage();
        imageStage = new Stage();

        mainStage.setScene(new Scene(root));
        mainStage.setTitle("Dr.Kodi");

        kodiCollectService.setExecutor(executor);

        if (!getAppConfig().isDebug())
            progressAndStatusGridPane.getProgressBar().visibleProperty().bind(kodiCollectService.runningProperty());

        warningsConfig = new WarningsConfig();
        warningsConfig.missingNfoFileIsWarningProperty().bind(checkBoxMissingNfoFileIsAWarning.selectedProperty());
        warningsConfig.defaultNfoFileNameIsWarningProperty().bind(checkBoxDefaultNfoFileNameIsAWarning.selectedProperty());

        progressAndStatusGridPane.getProgressStatusBox().getChildren().add(progressLabel);

        UiUtil.applyDebug(listView, getAppConfig());





        listView.setCellFactory(new Callback<ListView<KodiBox>, ListCell<KodiBox>>() {
            @Override
            public ListCell<KodiBox> call(ListView<KodiBox> param) {
                ListCell<KodiBox> lc = new ListCell<>() {

                    @Override
                    protected void updateItem(KodiBox item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setText(null);
                            setGraphic(null);
                        } else {
                            setGraphic(item);
                        }
                    }
                };
//                VBox.setVgrow(lc, Priority.ALWAYS);
//                lc.setMaxHeight(Double.MAX_VALUE);
                lc.prefWidthProperty().bind(listView.widthProperty().subtract(18));
                return lc;
            }
        });

    }


    @Override
    protected Parent[] getUiElementsForRandomColor() {
        return new Parent[]{root, listView, progressAndStatusGridPane, progressAndStatusGridPane.getProgressStatusBox()};
    }



    private void clearUi() {

        listView.getItems().clear();

    }

    @Override
    public void cancelCurrentOperation() {
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


}
