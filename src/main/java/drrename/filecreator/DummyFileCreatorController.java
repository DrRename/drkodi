/*
 *     Dr.Rename - A Minimalistic Batch Renamer
 *
 *     Copyright (C) 2022
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Affero General Public License as
 *     published by the Free Software Foundation, either version 3 of the
 *     License, or (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Affero General Public License for more details.
 *
 *     You should have received a copy of the GNU Affero General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package drrename.filecreator;

import drrename.event.DummyFileCreatorButtonCancelEvent;
import drrename.event.DummyFileCreatorButtonGoEvent;
import drrename.ui.mainview.GoCancelButtonsComponentController;
import drrename.ui.mainview.StartDirectoryComponentController;
import drrename.ui.mainview.controller.TabController;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.converter.NumberStringConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ResourceBundle;

@RequiredArgsConstructor
@Slf4j
@Component
@FxmlView("/fxml/DummyFileCreator.fxml")
public class DummyFileCreatorController implements Initializable {

    private final TabController tabController;

    public GoCancelButtonsComponentController goCancelButtonsComponentController;

    public TextField filesCnt;

    public TextField wordSeparator;

    public ProgressBar progressBar;

    public Parent root;

    private final FileCreatorService fileCreatorService;

    private Stage stage;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        stage = new Stage();
        stage.setScene(new Scene(root));
        stage.initStyle(StageStyle.UTILITY);
        stage.setTitle("Dummy File Creator");
        TextFormatter<Number> textFormatter = new TextFormatter<>(new NumberStringConverter());
        wordSeparator.setText("_");
        filesCnt.setTextFormatter(textFormatter);
        goCancelButtonsComponentController.buttonGo.disableProperty().bind(fileCreatorService.runningProperty().or(tabController.startDirectoryComponentController.readyProperty().not().or(filesCnt.textProperty().isEmpty())));
        goCancelButtonsComponentController.buttonCancel.disableProperty().bind(fileCreatorService.runningProperty().not());
        goCancelButtonsComponentController.setButtonCancelActionEventFactory( DummyFileCreatorButtonCancelEvent::new);
        goCancelButtonsComponentController.setButtonGoActionEventFactory(DummyFileCreatorButtonGoEvent::new);
        progressBar.visibleProperty().bind(fileCreatorService.runningProperty());

    }

    public void show() {
        stage.show(); //(3)
    }

    @EventListener
    public void onButtonGo(DummyFileCreatorButtonGoEvent event){
        handleDummyFileCreatorButtonGo(event.getActionEvent());
    }

    @EventListener
    public void onButtonCancel(DummyFileCreatorButtonCancelEvent event){
        handleDummyFileCreatorButtonCancel(event.getActionEvent());
    }

    public void handleDummyFileCreatorButtonGo(ActionEvent actionEvent) {
        startService();
    }

    public void handleDummyFileCreatorButtonCancel(ActionEvent actionEvent) {
        cancelService();
    }

    private void startService() {
        log.debug("Starting service {}" ,fileCreatorService);
        fileCreatorService.cancel();
        fileCreatorService.reset();
        fileCreatorService.setFileCnt((long) filesCnt.getTextFormatter().getValue());
        fileCreatorService.setDirectory(tabController.startDirectoryComponentController.getInputPath());
        fileCreatorService.setWordSeparator(wordSeparator.getText());
        progressBar.progressProperty().bind(fileCreatorService.progressProperty());
        fileCreatorService.start();
    }

    private void cancelService(){
        fileCreatorService.cancel();
    }


}
