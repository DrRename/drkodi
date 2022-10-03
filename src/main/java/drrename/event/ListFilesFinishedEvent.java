package drrename.event;

import java.util.UUID;

public class ListFilesFinishedEvent extends SynchronousUuidEvent {

    public ListFilesFinishedEvent(UUID uuid) {
        super(uuid);
    }

    public ListFilesFinishedEvent() {
    }
}
