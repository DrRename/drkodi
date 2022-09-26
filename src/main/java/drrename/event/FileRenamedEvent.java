package drrename.event;

import drrename.model.RenamingBean;
import org.springframework.context.ApplicationEvent;

import java.nio.file.Path;

public class FileRenamedEvent extends ApplicationEvent {

    public FileRenamedEvent(RenamingBean source) {
        super(source);
    }

    @Override
    public RenamingBean getSource() {
        return (RenamingBean) super.getSource();
    }
}
