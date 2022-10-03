package drrename.event;

import java.util.UUID;

public final class StartingPreviewEvent extends SynchronousUuidEvent {

    public StartingPreviewEvent(UUID uuid) {
        super(uuid);
    }

    public StartingPreviewEvent() {
    }
}
