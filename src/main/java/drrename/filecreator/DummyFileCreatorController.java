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

import drrename.config.AppConfig;
import drrename.event.DummyFileCreatorButtonCancelEvent;
import drrename.event.DummyFileCreatorButtonGoEvent;
import drrename.kodi.ui.ServiceStarter;
import drrename.ui.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.converter.NumberStringConverter;
import lombok.extern.slf4j.Slf4j;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ResourceBundle;

@Slf4j
@Component
@FxmlView("/fxml/DummyFileCreatorView.fxml")
public class DummyFileCreatorController extends DebuggableController implements Initializable, DrRenameController {

    // Spring injected //

    private final TabController tabController;

    private final FileCreatorService fileCreatorService;

    public BorderPane buttonPane;

    public GridPane inputGridPane;

    //

    // FXML injected //

    @FXML
    GoCancelButtonsComponentController goCancelButtonsComponentController;

    @FXML
    TextField filesCnt;

    @FXML
    TextField wordSeparator;

    @FXML
    Parent root;

    @FXML
    ProgressAndStatusGridPane progressAndStatusGridPane;

    // Default fields //

    private final FileCreatorServiceStarter serviceStarter;

    private Stage stage;

    private final Label progressStatusLabel;

    //

    // Nested classes //

    private class FileCreatorServiceStarter extends ServiceStarter<FileCreatorService> {

        public FileCreatorServiceStarter(FileCreatorService service) {
            super(service);
        }

        @Override
        protected void doInitService(FileCreatorService service) {
            service.setFileCnt((long) filesCnt.getTextFormatter().getValue());
            service.setDirectory(tabController.startDirectoryController.getInputPath());
            service.setWordSeparator(wordSeparator.getText());
            progressAndStatusGridPane.getProgressBar().progressProperty().bind(fileCreatorService.progressProperty());
        }
    }

    //

    public DummyFileCreatorController(TabController tabController, FileCreatorService fileCreatorService, AppConfig appConfig) {
        super(appConfig);
        this.tabController = tabController;
        this.fileCreatorService = fileCreatorService;
        this.serviceStarter = new FileCreatorServiceStarter(fileCreatorService);
        this.progressStatusLabel = new Label();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        super.initialize(url, resourceBundle);
        stage = new Stage();
        stage.setScene(new Scene(root));
        stage.initStyle(StageStyle.UTILITY);
        stage.setTitle("Dummy File Creator");
        TextFormatter<Number> textFormatter = new TextFormatter<>(new NumberStringConverter());
        wordSeparator.setText("_");
        filesCnt.setTextFormatter(textFormatter);
        goCancelButtonsComponentController.buttonGo.disableProperty().bind(fileCreatorService.runningProperty().or(tabController.startDirectoryController.readyProperty().not().or(filesCnt.textProperty().isEmpty())));
        goCancelButtonsComponentController.buttonCancel.disableProperty().bind(fileCreatorService.runningProperty().not());
        goCancelButtonsComponentController.setButtonCancelActionEventFactory(DummyFileCreatorButtonCancelEvent::new);
        goCancelButtonsComponentController.setButtonGoActionEventFactory(DummyFileCreatorButtonGoEvent::new);
        if (!getAppConfig().isDebug())
            progressAndStatusGridPane.getProgressBar().visibleProperty().bind(fileCreatorService.runningProperty());

        progressAndStatusGridPane.getProgressStatusBox().getChildren().add(progressStatusLabel);

    }

    @Override
    protected Parent[] getUiElementsForRandomColor() {
        return new Parent[]{inputGridPane, buttonPane, progressAndStatusGridPane, progressAndStatusGridPane.getProgressStatusBox()};
    }

    public void show() {
        stage.show(); //(3)
    }

    @EventListener
    public void onButtonGo(DummyFileCreatorButtonGoEvent event) {
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
        cancelCurrentOperation();
    }

    private void startService() {

        serviceStarter.startService();
    }


    @Override
    public void cancelCurrentOperation() {
        fileCreatorService.cancel();
    }

    @Override
    public void clearView() {

    }

    @Override
    public void updateInputView() {

    }
}
