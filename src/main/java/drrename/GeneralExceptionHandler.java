package drrename;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ResourceBundle;

@Slf4j
@Component
public class GeneralExceptionHandler {

    private final static String ALERT_TITLE = "error.alert.title";

    private final ResourceBundle resourceBundle;

    private class Handler implements Thread.UncaughtExceptionHandler {
        @Override
        public void uncaughtException(Thread t, Throwable e) {
            log.error("Uncaught exception on thread {}", t, e);
            Platform.runLater(() -> showAlertDialog(e));
        }

        private void showAlertDialog(Throwable e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(String.format(resourceBundle.getString(ALERT_TITLE)));
            alert.setHeaderText(e.getLocalizedMessage());
            TextArea area = new TextArea(RenameUtil.stackTraceToString(e));
            alert.getDialogPane().setContent(area);
            area.setWrapText(true);
            area.setEditable(false);
            alert.setResizable(true);
            alert.getDialogPane().setPrefHeight(300);
            alert.getDialogPane().setPrefWidth(400);
            alert.showAndWait();
        }
    }

    public GeneralExceptionHandler(ResourceBundle resourceBundle) {
        this.resourceBundle = resourceBundle;
        Thread.setDefaultUncaughtExceptionHandler(new Handler());
    }
}
