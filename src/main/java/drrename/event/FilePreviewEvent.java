package drrename.event;

import drrename.model.RenamingEntry;
import org.springframework.context.ApplicationEvent;

public class FilePreviewEvent extends ApplicationEvent {

    public FilePreviewEvent(RenamingEntry source) {
        super(source);
    }

    @Override
    public RenamingEntry getSource() {
        return (RenamingEntry) super.getSource();
    }
}
