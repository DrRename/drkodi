package drrename.event;

import java.util.Objects;
import java.util.UUID;

public class SynchronousUuidEvent implements SynchronousEvent {

    private final UUID uuid;

    public SynchronousUuidEvent(UUID uuid) {
        this.uuid = uuid;
    }

    SynchronousUuidEvent(){
        this(UUID.randomUUID());
    }

    public UUID getUuid() {
        return uuid;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName()+"{" +
                "uuid=" + uuid +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SynchronousUuidEvent that = (SynchronousUuidEvent) o;
        return uuid.equals(that.uuid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid);
    }
}
