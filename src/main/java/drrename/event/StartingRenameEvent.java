package drrename.event;

import java.util.UUID;

public class StartingRenameEvent extends SynchronousUuidEvent {

    public StartingRenameEvent(UUID uuid) {
        super(uuid);
    }

    public StartingRenameEvent() {
    }
}
