package com.kerner1000.drrename.event;

import javafx.event.ActionEvent;
import javafx.stage.Stage;
import org.springframework.context.ApplicationEvent;

public class DummyFileCreatorButtonGoEvent extends ApplicationEvent {

    public DummyFileCreatorButtonGoEvent(ActionEvent actionEvent) {
        super(actionEvent);
    }

    public ActionEvent getActionEvent() {
        return ((ActionEvent) getSource());
    }
}
