package drrename.ui.mainview;

import drrename.event.JavaFXActionEventFactory;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ResourceBundle;

@RequiredArgsConstructor
@Slf4j
@Component
@Scope("prototype")
@FxmlView("/fxml/GoCancelButtonsComponent.fxml")
public class GoCancelButtonsComponentController implements Initializable, ApplicationListener<ApplicationEvent> {

    private final ConfigurableApplicationContext applicationContext;

    public Button buttonGo;
    public Button buttonCancel;

    private JavaFXActionEventFactory buttonGoActionEventFactory;

    private JavaFXActionEventFactory buttonCancelActionEventFactory;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        buttonGo.setDefaultButton(true);
        buttonGo.setDisable(true);

    }

    @Override
    public void onApplicationEvent(ApplicationEvent event) {

    }

    public void setButtonCancelActionEventFactory(JavaFXActionEventFactory buttonCancelActionEventFactory) {
        this.buttonCancelActionEventFactory = buttonCancelActionEventFactory;
    }

    public void setButtonGoActionEventFactory(JavaFXActionEventFactory buttonGoActionEventFactory) {
        this.buttonGoActionEventFactory = buttonGoActionEventFactory;
    }

    public void handleDummyFileCreatorButtonGo(ActionEvent actionEvent) {
        applicationContext.publishEvent(buttonGoActionEventFactory.buildEvent(actionEvent));
    }

    public void handleDummyFileCreatorButtonCancel(ActionEvent actionEvent) {
        applicationContext.publishEvent(buttonCancelActionEventFactory.buildEvent(actionEvent));
    }
}
