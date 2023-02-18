package drkodi.ui;

import drkodi.event.StageReadyEvent;
import drkodi.ui.config.UiConfig;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.rgielen.fxweaver.core.FxWeaver;
import org.apache.commons.lang3.SystemUtils;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.ResourceBundle;

@RequiredArgsConstructor
@Slf4j
@Component
public class PrimaryStageInitializer {

    private final FxWeaver fxWeaver;

    private final UiConfig uiConfig;

    private final ResourceBundle resourceBundle;

    private Scene mainScene;

    @EventListener
    public void onApplicationEvent(StageReadyEvent event) {
        Platform.runLater(() -> {
            Stage stage = event.stage();
            mainScene = new Scene(fxWeaver.loadView(MainViewController.class, resourceBundle), uiConfig.getInitialWidth(), uiConfig.getInitialHeight());
            mainScene.getStylesheets().add("/css/general.css");
            stage.setTitle(uiConfig.getAppTitle());
            stage.setScene(mainScene);
            stage.getIcons().setAll(SystemUtils.IS_OS_MAC ? List.of() : List.of(
                    new Image(PrimaryStageInitializer.class.getResource("/img/drkodi_16.png").toString()),
                    new Image(PrimaryStageInitializer.class.getResource("/img/drkodi_20.png").toString()),
                    new Image(PrimaryStageInitializer.class.getResource("/img/drkodi_24.png").toString()),
                    new Image(PrimaryStageInitializer.class.getResource("/img/drkodi_32.png").toString()),
                    new Image(PrimaryStageInitializer.class.getResource("/img/drkodi_40.png").toString()),
                    new Image(PrimaryStageInitializer.class.getResource("/img/drkodi_48.png").toString()),
                    new Image(PrimaryStageInitializer.class.getResource("/img/drkodi_64.png").toString()),
                    new Image(PrimaryStageInitializer.class.getResource("/img/drkodi_128.png").toString()),
                    new Image(PrimaryStageInitializer.class.getResource("/img/drkodi_256.png").toString()),
                    new Image(PrimaryStageInitializer.class.getResource("/img/drkodi_512.png").toString())));
            stage.show();
        });
    }

    public Scene getMainScene() {
        return mainScene;
    }
}
