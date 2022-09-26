package drrename.event;

import java.util.UUID;

public record StartingPreviewEvent(UUID uuid) implements SynchronousEvent {

}
