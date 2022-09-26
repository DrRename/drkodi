package drrename.event;

import org.springframework.context.ApplicationEvent;

import java.util.UUID;

public class StartingListFilesEvent extends ApplicationEvent implements SynchronousEvent {

    public StartingListFilesEvent(Object source) {
        super(source);
    }

    @Override
    public UUID getSource() {
        return (UUID) super.getSource();
    }
}
