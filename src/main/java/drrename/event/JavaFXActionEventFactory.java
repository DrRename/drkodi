package drrename.event;

import javafx.event.ActionEvent;

public interface JavaFXActionEventFactory extends EventFactory<ActionEvent> {

    JavaFXActionEvent buildEvent(ActionEvent actionEvent);
}
