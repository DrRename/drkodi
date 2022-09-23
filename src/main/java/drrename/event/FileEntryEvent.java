package drrename.event;

import drrename.model.RenamingBean;
import org.springframework.context.ApplicationEvent;

public class FileEntryEvent extends ApplicationEvent {
    public FileEntryEvent(RenamingBean bean) {
        super(bean);
    }

    @Override
    public RenamingBean getSource() {
        return (RenamingBean) super.getSource();
    }
}
