package com.github.drrename;

import com.kerner1000.drrename.RenamingBean;
import org.springframework.context.ApplicationEvent;

public class FileEntryEvent extends ApplicationEvent {
    public FileEntryEvent(RenamingBean bean) {
        super(bean);
    }
}
