package drrename;

import drrename.event.StageReadyEvent;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.stage.Stage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.rgielen.fxweaver.core.FxWeaver;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.ResourceBundle;

@RequiredArgsConstructor
@Slf4j
@Component
public class PrimaryStageInitializer implements ApplicationListener<StageReadyEvent> {

    private final FxWeaver fxWeaver;

    private final UiConfig uiConfig;

    private final ResourceBundle resourceBundle;

    @Override
    public void onApplicationEvent(StageReadyEvent event) {
        Platform.runLater(() -> {
            Stage stage = event.getStage();
            Scene scene = new Scene(fxWeaver.loadView(MainController.class, resourceBundle), uiConfig.getInitialWidth(), uiConfig.getInitialHeight());
            scene.getStylesheets().add("css/root.css");
            stage.setTitle(uiConfig.getAppTitle());
            stage.setScene(scene);
            stage.show();
        });

    }
}
