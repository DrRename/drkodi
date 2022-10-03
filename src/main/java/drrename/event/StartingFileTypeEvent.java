package drrename.event;

import java.util.Objects;
import java.util.UUID;

public class StartingFileTypeEvent extends SynchronousUuidEvent {


    public StartingFileTypeEvent(UUID uuid) {
        super(uuid);
    }

    public StartingFileTypeEvent() {
    }
}
