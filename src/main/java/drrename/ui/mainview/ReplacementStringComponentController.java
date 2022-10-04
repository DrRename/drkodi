package drrename.ui.mainview;

import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ResourceBundle;

@RequiredArgsConstructor
@Slf4j
@Component
@Scope("prototype")
@FxmlView("/fxml/ReplacementStringComponent.fxml")
public class ReplacementStringComponentController implements Initializable, ApplicationListener<ApplicationEvent> {
    public TextField textFieldReplacementStringFrom;
    public TextField textFieldReplacementStringTo;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    @Override
    public void onApplicationEvent(ApplicationEvent event) {

    }
}
