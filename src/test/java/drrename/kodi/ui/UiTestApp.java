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

import drrename.RenamingPath;
import drrename.SearchResultMapper;
import drrename.SearchResultMapperImpl;
import drrename.config.AppConfig;
import drrename.kodi.LoadImageTask;
import drrename.kodi.data.*;
import drrename.kodi.ui.config.KodiUiConfig;
import drrename.kodi.ui.control.HansBox;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Bounds;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class UiTestApp extends Application {

    static Executor executor = Executors.newSingleThreadExecutor();

    SearchResultMapper mapper = new SearchResultMapperImpl();

    List<Movie> data = List.of(new Movie(new RenamingPath(Paths.get("src/test/resources/kodi/Reference Movie (2000)")),mapper,executor, null)/*,new Movie(new RenamingPath(Paths.get("src/test/resources/kodi/Reference Movie (2000)")),mapper, executor, null)*/);

    VBox view;

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setScene(buildScene());
        primaryStage.setMinWidth(400);
        primaryStage.setMinHeight(400);
        primaryStage.show();
        fillUi();
    }

    private Scene buildScene() {
        view = new VBox();
        view.setFillWidth(true);
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(view);
        scrollPane.viewportBoundsProperty().addListener(new ChangeListener<Bounds>() {
            @Override
            public void changed(ObservableValue<? extends Bounds> ov, Bounds oldBounds, Bounds bounds) {
                view.setPrefWidth(bounds.getWidth());
                view.setPrefHeight(bounds.getHeight());
            }
        });
        Scene scene = new Scene(scrollPane);
        scene.getStylesheets().add("/css/general.css");
        scene.getStylesheets().add("/css/light-theme.css");
        return scene;
    }

    private void fillUi() throws Exception {
        for(Movie element : data){
//            element.getSearchResults().addAll(buildSearchResults());
            Platform.runLater(() -> view.getChildren().add(new HansBox(element, executor, new AppConfig(), new KodiUiConfig())));
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
//        result.add(searchResult);
        return result;
    }

    public static void main(String[] args) {
        Application.launch(args);
    }
}
