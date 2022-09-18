package drrename.event;

import javafx.event.ActionEvent;

public class DummyFileCreatorButtonCancelEvent extends JavaFXActionEvent {

    public DummyFileCreatorButtonCancelEvent(ActionEvent actionEvent) {
        super(actionEvent);
    }

    public ActionEvent getActionEvent() {
        return ((ActionEvent) getSource());
    }
}
