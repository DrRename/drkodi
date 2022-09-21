package drrename.event;

import javafx.event.ActionEvent;
import org.springframework.context.ApplicationEvent;

import java.time.Clock;

public class JavaFXActionEvent extends ApplicationEvent {

    public JavaFXActionEvent(ActionEvent actionEvent) {
        super(actionEvent);
    }

    public JavaFXActionEvent(ActionEvent actionEvent, Clock clock) {
        super(actionEvent, clock);
    }
}
