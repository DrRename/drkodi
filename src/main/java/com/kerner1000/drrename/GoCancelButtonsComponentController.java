package com.kerner1000.drrename;

import com.kerner1000.drrename.event.DummyFileCreatorButtonCancelEvent;
import com.kerner1000.drrename.event.DummyFileCreatorButtonGoEvent;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ResourceBundle;

@RequiredArgsConstructor
@Slf4j
@Component
@FxmlView("/fxml/GoCancelButtonsComponent.fxml")
public class GoCancelButtonsComponentController implements Initializable, ApplicationListener<ApplicationEvent> {

    private final ConfigurableApplicationContext applicationContext;

    public Button buttonGo;
    public Button buttonCancel;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        buttonGo.setDefaultButton(true);
        buttonGo.setDisable(true);

    }

    @Override
    public void onApplicationEvent(ApplicationEvent event) {

    }

    public void handleDummyFileCreatorButtonGo(ActionEvent actionEvent) {
        applicationContext.publishEvent(new DummyFileCreatorButtonGoEvent(actionEvent));
    }

    public void handleDummyFileCreatorButtonCancel(ActionEvent actionEvent) {
        applicationContext.publishEvent(new DummyFileCreatorButtonCancelEvent(actionEvent));
    }
}
