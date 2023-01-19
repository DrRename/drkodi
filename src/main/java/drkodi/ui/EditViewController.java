/*
 *     Dr.Kodi - A Minimalistic Batch Renamer
 *
 *     Copyright (C) 2023
 *
 *     This file is part of Dr.Kodi.
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

import drkodi.SelectedData;
import drkodi.config.AppConfig;
import drkodi.data.movie.MediaFilesFileSizeMovieTaskExecutor;
import drkodi.data.movie.Movie;
import drkodi.ui.control.GenresBox2;
import drkodi.ui.control.KodiOpenAndSaveButtonsBox;
import drkodi.util.DrRenameUtil;
import drrename.commons.RenamingPath;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.converter.IntegerStringConverter;
import lombok.extern.slf4j.Slf4j;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.Executor;

@Slf4j
@Component
@FxmlView("/fxml/EditView.fxml")
public class EditViewController extends DebuggableController implements Initializable {

    private final Executor executor;

    private final SelectedData selectedData;

    private final AppConfig appConfig;

    @FXML
    public VBox detailsTableRoot;

    @FXML
    public GridPane detailsTable;

    @FXML
    public TextField movieTitleTextField;

    @FXML
    public TextField movieOriginalTitleTextField;

    @FXML
    public TextField movieYearTextField;

    @FXML
    public HBox genresBox;

    @FXML
    public HBox buttonsBox;

    @FXML
    public HBox fileSizeBox;

    private Stage stage;

    //

    private final Label fileSizeLabel;


    class MediaFilesListener implements ListChangeListener<RenamingPath> {





            @Override
            public void onChanged(Change<? extends RenamingPath> c) {
                while(c.next()){

                }
                new MediaFilesFileSizeMovieTaskExecutor(selectedData.getSelectedMovie(), c.getList().stream().map(RenamingPath::getOldPath).toList(), executor, selectedData.getSelectedMovie().getRunningTasksList(), fileSizeLabel).execute();
            }

    }
    private final MediaFilesListener mediaFilesListener;

    public EditViewController(AppConfig appConfig, Executor executor, SelectedData selectedData, AppConfig appConfig1) {
        super(appConfig);
        this.executor = executor;
        this.selectedData = selectedData;
        this.appConfig = appConfig1;
        this.mediaFilesListener = new MediaFilesListener();
        fileSizeLabel = new Label();
        log.debug("New {} controller created", this);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        super.initialize(url, resourceBundle);
        this.stage = new Stage();
        this.stage.setTitle("Edit Details");
        this.stage.setScene(new Scene(detailsTableRoot));
        FlowPane fileSizePane = new FlowPane();
        fileSizePane.setHgap(4);
        fileSizePane.setVgap(4);
        fileSizeLabel.getStyleClass().add("file-size-box");
        fileSizePane.getChildren().add(UiUtil.applyDebug(fileSizeLabel, appConfig));
        this.fileSizeBox.getChildren().add(UiUtil.applyDebug(fileSizePane, appConfig));

        if(selectedData.getSelectedMovie() != null) {

            buttonsBox.getChildren().setAll(UiUtil.applyDebug(new KodiOpenAndSaveButtonsBox(selectedData.getSelectedMovie()), appConfig));

            movieTitleTextField.setText(selectedData.getSelectedMovie().getMovieTitle());
            movieOriginalTitleTextField.setText(selectedData.getSelectedMovie().getMovieOriginalTitle());
            movieYearTextField.setText(selectedData.getSelectedMovie() != null ? selectedData.getSelectedMovie().getMovieYear().toString() : null);
            genresBox.getChildren().setAll(new GenresBox2(selectedData.getSelectedMovie()));





        }
        selectedData.selectedMovieProperty().addListener(new ChangeListener<Movie>() {
            @Override
            public void changed(ObservableValue<? extends Movie> observable, Movie oldValue, Movie newValue) {
                if(oldValue != null){

                    movieTitleTextField.textProperty().unbindBidirectional(oldValue.movieTitleProperty());
                    movieOriginalTitleTextField.textProperty().unbindBidirectional(oldValue.movieOriginalTitleProperty());
                    movieYearTextField.textProperty().unbindBidirectional(oldValue.movieYearProperty());

                    oldValue.getMediaFiles().removeListener(mediaFilesListener);
                    fileSizeLabel.setText(DrRenameUtil.formatSize(0));
                }
                if(newValue != null){

                    buttonsBox.getChildren().setAll(UiUtil.applyDebug(new KodiOpenAndSaveButtonsBox(selectedData.getSelectedMovie()), appConfig));

                    movieTitleTextField.textProperty().bindBidirectional(newValue.movieTitleProperty());
                    movieOriginalTitleTextField.textProperty().bindBidirectional(newValue.movieOriginalTitleProperty());
                    movieYearTextField.textProperty().bindBidirectional(newValue.movieYearProperty(), new IntegerStringConverter());
                    genresBox.getChildren().setAll(new GenresBox2(selectedData.getSelectedMovie()));


                    // Initially load file size if media files have been collected already
                    new MediaFilesFileSizeMovieTaskExecutor(newValue, newValue.getMediaFiles().stream().map(RenamingPath::getOldPath).toList(), executor, newValue.getRunningTasksList(), fileSizeLabel).execute();

                    // Wait for media files to be available and update file size label
                    newValue.getMediaFiles().addListener(mediaFilesListener);


                }

            }
        });



        log.debug("{} initialized", this);
    }





    @Override
    protected Parent[] getUiElementsForRandomColor() {
        return new Parent[]{detailsTable, fileSizeBox};
    }



    public void show() {
        stage.show();
    }
}
