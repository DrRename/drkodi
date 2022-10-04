package drrename;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Slf4j
@Component
public class GeneralExceptionHandler {

    private final static String ALERT_TITLE = "error.alert.title";

    private final ResourceBundle resourceBundle;

    private class Handler implements Thread.UncaughtExceptionHandler {
        @Override
        public void uncaughtException(Thread t, Throwable e) {
            log.error("Uncaught exception on thread {}", t, e);
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(String.format(resourceBundle.getString(ALERT_TITLE)));
            alert.setHeaderText(e.toString());
            alert.setContentText(Arrays.stream(e.getStackTrace())
                    .map(StackTraceElement::toString)
                    .collect(Collectors.joining("\n")));
            Platform.runLater(alert::showAndWait);
        }
    }

    public GeneralExceptionHandler() {
        Thread.setDefaultUncaughtExceptionHandler(new Handler());
    }
}
