package drrename.ui.mainview.controller;

import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ResourceBundle;

@RequiredArgsConstructor
@Slf4j
@Component
@FxmlView("/fxml/FileListComponent.fxml")
public class FileListComponentController implements Initializable, ApplicationListener<ApplicationEvent> {

    public ListView content1;
    public ListView content2;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    @Override
    public void onApplicationEvent(ApplicationEvent event) {

    }
}
