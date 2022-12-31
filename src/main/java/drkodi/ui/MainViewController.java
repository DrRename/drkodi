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
import drkodi.data.movie.Movie;
import drkodi.ui.config.KodiUiConfig;
import drkodi.ui.control.KodiBox;
import drkodi.ui.control.KodiMoviePathEntryBox;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
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
import java.util.ResourceBundle;
import java.util.concurrent.Executor;
import java.util.function.Predicate;


@Slf4j
@Component
@FxmlView("/fxml/MainView.fxml")
public class MainViewController extends DebuggableController implements DrRenameController {

    public static final int imageStageXOffset = 600;

    // Spring injected //

    private final KodiCollectService kodiCollectService;

    private final Executor executor;

    private final ResourceBundle resourceBundle;

    private final KodiUiConfig kodiUiConfig;

    //

    // FXML injected //

    @FXML
    Pane root;

    @FXML
    ListView<KodiMoviePathEntryBox> listView;

    @FXML
    VBox detailsBox;

    @FXML
    CheckBox checkBoxHideEmpty;

    @FXML
    ProgressAndStatusGridPane progressAndStatusGridPane;

    @FXML
    Stage mainStage;

    @FXML
    Stage imageStage;

    // Default fields //

    private final KodiCollectServiceStarter serviceStarter;

    private final Label progressLabel;


    //

    // Nested classes //

    private class KodiCollectServiceStarter extends ServiceStarter<KodiCollectService> {

        public KodiCollectServiceStarter(KodiCollectService service) {
            super(service);
        }

        @Override
        protected void doInitService(KodiCollectService service) {
            service.setListView(listView);
            progressAndStatusGridPane.getProgressBar().progressProperty().bind(service.progressProperty());
            progressLabel.textProperty().bind(service.messageProperty());

        }

        @Override
        protected void prepareUi() {
            clearUi();
        }
    }


    //

    public MainViewController(KodiCollectService kodiCollectService, Executor executor, AppConfig appConfig, ResourceBundle resourceBundle, KodiUiConfig kodiUiConfig) {
        super(appConfig);
        this.kodiCollectService = kodiCollectService;
        this.executor = executor;
        this.serviceStarter = new KodiCollectServiceStarter(kodiCollectService);
        this.resourceBundle = resourceBundle;
        this.kodiUiConfig = kodiUiConfig;
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

        progressAndStatusGridPane.getProgressStatusBox().getChildren().add(progressLabel);

        UiUtil.applyDebug(listView, getAppConfig());





        listView.setCellFactory(new Callback<ListView<KodiMoviePathEntryBox>, ListCell<KodiMoviePathEntryBox>>() {
            @Override
            public ListCell<KodiMoviePathEntryBox> call(ListView<KodiMoviePathEntryBox> param) {
                ListCell<KodiMoviePathEntryBox> lc = new ListCell<>() {

                    @Override
                    protected void updateItem(KodiMoviePathEntryBox item, boolean empty) {
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

        listView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<KodiMoviePathEntryBox>() {

            @Override
            public void changed(ObservableValue<? extends KodiMoviePathEntryBox> observable, KodiMoviePathEntryBox oldValue, KodiMoviePathEntryBox newValue) {
                if(newValue != null) {
                    Movie selectedMovie = newValue.getMovie();
                    detailsBox.getChildren().setAll(new KodiBox(selectedMovie, getAppConfig(),kodiUiConfig));
                }
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
