package com.kerner1000.drrename.event;

import javafx.event.ActionEvent;
import org.springframework.context.ApplicationEvent;

public interface EventFactory<T> {

    <T extends ApplicationEvent> T buildEvent(ActionEvent actionEvent);
}
