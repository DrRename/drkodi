package drrename.event;

import drrename.model.RenamingEntry;
import lombok.Data;

import java.util.Collections;
import java.util.List;
import java.util.UUID;


@Data
public class NewRenamingEntryEvent {

   private final UUID uuid;

   private final List<RenamingEntry> renamingEntries;

   public NewRenamingEntryEvent(UUID uuid, List<RenamingEntry> renamingEntries) {
      this.uuid = uuid;
      this.renamingEntries = renamingEntries;
   }

   public NewRenamingEntryEvent(UUID uuid, RenamingEntry renamingEntries) {
      this(uuid, Collections.singletonList(renamingEntries));
   }
}
