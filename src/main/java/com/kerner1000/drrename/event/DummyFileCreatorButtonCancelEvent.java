package com.kerner1000.drrename.event;

import javafx.event.ActionEvent;
import org.springframework.context.ApplicationEvent;

public class DummyFileCreatorButtonCancelEvent extends ApplicationEvent {

    public DummyFileCreatorButtonCancelEvent(ActionEvent actionEvent) {
        super(actionEvent);
    }

    public ActionEvent getActionEvent() {
        return ((ActionEvent) getSource());
    }
}
