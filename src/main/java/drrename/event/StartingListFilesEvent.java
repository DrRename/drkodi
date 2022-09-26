package drrename.event;

import lombok.Data;

import java.util.UUID;
public record StartingListFilesEvent(UUID uuid) implements SynchronousEvent {

}
