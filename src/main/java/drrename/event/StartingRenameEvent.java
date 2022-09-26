package drrename.event;

import java.util.UUID;

public record StartingRenameEvent(UUID uuid) implements SynchronousEvent {

}
