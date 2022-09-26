package drrename.event;

import drrename.model.RenamingBean;
import lombok.Data;
import org.springframework.context.ApplicationEvent;

import java.util.UUID;


@Data
public class FileEntryEvent {

   private final UUID uuid;

   private final RenamingBean renamingBean;


}
