package drrename.event;

import drrename.model.RenamingBean;
import org.springframework.context.ApplicationEvent;

import java.nio.file.Path;

public class FilePreviewEvent extends ApplicationEvent {

    public FilePreviewEvent(RenamingBean source) {
        super(source);
    }

    @Override
    public RenamingBean getSource() {
        return (RenamingBean) super.getSource();
    }
}
