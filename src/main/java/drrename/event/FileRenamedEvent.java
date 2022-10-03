package drrename.event;

import drrename.model.RenamingEntry;
import lombok.Data;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Data
public class FileRenamedEvent  {

    private final UUID uuid;

    private final List<RenamingEntry> renamedEntries;

    public FileRenamedEvent(UUID uuid, List<RenamingEntry> renamedEntries) {
        this.uuid = uuid;
        this.renamedEntries = renamedEntries;
    }

    public FileRenamedEvent(UUID uuid, RenamingEntry renamedEntries) {
        this(uuid, Collections.singletonList(renamedEntries));
    }
}
