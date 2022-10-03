package drrename.event;

import lombok.Data;

import java.util.UUID;
public class StartingListFilesEvent extends SynchronousUuidEvent {

    public StartingListFilesEvent(UUID uuid) {
        super(uuid);
    }

    public StartingListFilesEvent() {
    }
}
