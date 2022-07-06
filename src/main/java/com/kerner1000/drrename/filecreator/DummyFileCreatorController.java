package com.kerner1000.drrename.filecreator;

import com.kerner1000.drrename.StartDirectoryComponentController;
import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.util.converter.NumberStringConverter;
import lombok.extern.slf4j.Slf4j;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.nio.file.Path;
import java.util.ResourceBundle;

@Slf4j
@Component
@FxmlView("/fxml/DummyFileCreatorController.fxml")
public class DummyFileCreatorController implements Initializable, ApplicationListener<ApplicationEvent> {

    public StartDirectoryComponentController startDirectoryComponentController;
    public TextField filesCnt;
    public TextField wordSeparator;
    public Button buttonGo;
    public Button buttonCancel;
    public ProgressBar progressBar;

    private FileCreatorService fileCreatorService;

    public DummyFileCreatorController(){

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        fileCreatorService = new FileCreatorService();
        TextFormatter<Number> textFormatter = new TextFormatter<>(new NumberStringConverter());
        wordSeparator.setText("_");
        filesCnt.setTextFormatter(textFormatter);
        buttonGo.setDefaultButton(true);
        buttonGo.setDisable(true);
        buttonGo.disableProperty().bind(fileCreatorService.runningProperty().or(startDirectoryComponentController.readyProperty().not()));
        buttonCancel.disableProperty().bind(fileCreatorService.runningProperty().not());
        progressBar.visibleProperty().bind(fileCreatorService.runningProperty());

    }

    private void updateInput(Path inputPath) {
    }

    @Override
    public void onApplicationEvent(ApplicationEvent event) {

    }

    public void handleDummyFileCreatorButtonGo(ActionEvent actionEvent) {
        startService();
    }

    private void startService() {
        fileCreatorService.reset();
        fileCreatorService.setFileCnt((long) filesCnt.getTextFormatter().getValue());
        fileCreatorService.setDirectory(startDirectoryComponentController.getInputPath());
        fileCreatorService.setWordSeparator(wordSeparator.getText());
        progressBar.progressProperty().bind(fileCreatorService.progressProperty());
        fileCreatorService.start();
    }

    private void cancelService(){
        fileCreatorService.cancel();
    }

    public void handleDummyFileCreatorButtonCancel(ActionEvent actionEvent) {
        cancelService();
    }
}
