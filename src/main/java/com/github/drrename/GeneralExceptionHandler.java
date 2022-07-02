package com.github.drrename;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import lombok.extern.slf4j.Slf4j;
import net.rgielen.fxweaver.core.FxWeaver;
import org.springframework.stereotype.Component;

import java.io.PrintWriter;
import java.io.StringWriter;

@Slf4j
@Component
public class GeneralExceptionHandler {

    private static class Handler implements Thread.UncaughtExceptionHandler {
        @Override
        public void uncaughtException(Thread t, Throwable e) {
            log.error("Uncaught exception on thread {}", t, e);
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Uncaught exception");
        alert.setHeaderText(e.toString());
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            alert.setContentText(sw.toString());
            Platform.runLater(alert::showAndWait);
        }
    }

    private final FxWeaver fxWeaver;

    public GeneralExceptionHandler(FxWeaver fxWeaver){
        this.fxWeaver = fxWeaver;
        Thread.setDefaultUncaughtExceptionHandler(new Handler());
    }
}
