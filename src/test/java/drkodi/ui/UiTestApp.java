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

import drkodi.LoadImageTask;
import drkodi.SearchResultDtoMapper;
import drkodi.SearchResultDtoMapperImpl;
import drkodi.config.AppConfig;
import drkodi.data.*;
import drkodi.data.movie.Movie;
import drkodi.normalization.*;
import drkodi.ui.config.KodiUiConfig;
import drkodi.ui.control.KodiBox;
import drrename.commons.RenamingPath;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class UiTestApp extends Application {

    static Executor executor = Executors.newSingleThreadExecutor();

    SearchResultDtoMapper mapper = new SearchResultDtoMapperImpl();

    private SearchResultToMovieMapper searchResultToMovieMapper = new SearchResultToMovieMapperImpl(new ImageDataMapper());
    List<Movie> data = List.of(new Movie(new RenamingPath(Paths.get("src/test/resources/kodi/Reference Movie (2000)")),mapper,executor, null, new FolderNameWarningNormalizer(new FolderNameCompareNormalizerConfiguration()), new MovieTitleSearchNormalizer(new MovieTitleSearchNormalizerConfiguration()), searchResultToMovieMapper, new MovieTitleWriteNormalizer(new MovieTitleWriteNormalizerConfiguration())),new Movie(new RenamingPath(Paths.get("src/test/resources/kodi/Reference Movie (2000)")),mapper, executor, null, new FolderNameWarningNormalizer(new FolderNameCompareNormalizerConfiguration()), new MovieTitleSearchNormalizer(new MovieTitleSearchNormalizerConfiguration()), searchResultToMovieMapper, new MovieTitleWriteNormalizer(new MovieTitleWriteNormalizerConfiguration())));

    ListView<KodiBox> listView;

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setScene(buildScene());
        primaryStage.setMinWidth(400);
        primaryStage.setMinHeight(400);
        primaryStage.show();
        fillUi();
    }

    private Scene buildScene() {
        listView = new ListView<>();
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
        Scene scene = new Scene(listView);
        scene.getStylesheets().add("/css/general.css");
        scene.getStylesheets().add("/css/light-theme.css");
        return scene;
    }

    private void fillUi() throws Exception {
        for(Movie element : data){
            element.getSearchResults().addAll(buildSearchResults());
            Platform.runLater(() -> listView.getItems().add(new KodiBox(element, new AppConfig(), new KodiUiConfig())));
        }
    }

    private List<SearchResult> buildSearchResults() throws Exception {
        List<SearchResult> result = new ArrayList<>();
        SearchResult searchResult = new SearchResult();
        searchResult.setTitle("Hans Dampf");
        searchResult.setReleaseDate(1111);
        searchResult.setPlot("hhhhhaasdasdasd ad aa sd as   lasdkd  asldksdklsd  saldk");
        var task = new LoadImageDataTask(Paths.get("src/test/resources/kodi/Reference Movie (2000)/folder.jpg"));
        searchResult.setImageData(task.call());
        var task2 = new LoadImageTask(searchResult.getImageData());
        searchResult.setImage(task2.call());
        result.add(searchResult);
        result.add(searchResult);
        return result;
    }

    public static void main(String[] args) {
        Application.launch(args);
    }
}
