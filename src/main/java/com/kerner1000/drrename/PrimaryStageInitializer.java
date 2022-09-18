package com.kerner1000.drrename;

import com.kerner1000.drrename.event.StageReadyEvent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import net.rgielen.fxweaver.core.FxWeaver;
import org.springframework.boot.info.BuildProperties;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PrimaryStageInitializer implements ApplicationListener<StageReadyEvent> {

    private final FxWeaver fxWeaver;

    private final BuildProperties buildProperties;

    public PrimaryStageInitializer(FxWeaver fxWeaver, BuildProperties buildProperties) {
        this.fxWeaver = fxWeaver;
        this.buildProperties = buildProperties;
    }

    @Override
    public void onApplicationEvent(StageReadyEvent event) {
        Stage stage = event.getStage();
        Scene scene = new Scene(fxWeaver.loadView(MainController.class), 600, 600);
        scene.getStylesheets().add("css/root.css");
        stage.setTitle("Dr.Rename");
        stage.setScene(scene);
        stage.show();
    }
}
