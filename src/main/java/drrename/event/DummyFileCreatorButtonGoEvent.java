package drrename.event;

import javafx.event.ActionEvent;

public class DummyFileCreatorButtonGoEvent extends JavaFXActionEvent {

    public DummyFileCreatorButtonGoEvent(ActionEvent actionEvent) {
        super(actionEvent);
    }

    public ActionEvent getActionEvent() {
        return ((ActionEvent) getSource());
    }
}
