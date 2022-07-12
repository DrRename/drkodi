package com.kerner1000.drrename.event;

import javafx.event.ActionEvent;
import org.springframework.context.ApplicationEvent;

public interface JavaFXActionEventFactory extends EventFactory<ActionEvent> {

    JavaFXActionEvent buildEvent(ActionEvent actionEvent);
}
