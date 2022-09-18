package drrename.event;

import javafx.event.ActionEvent;

public class MainViewButtonCancelEvent extends JavaFXActionEvent {

    public MainViewButtonCancelEvent(ActionEvent actionEvent) {
        super(actionEvent);
    }

    public ActionEvent getActionEvent() {
        return ((ActionEvent) getSource());
    }
}
