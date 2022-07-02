package com.github.drrename;

import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class NewFileEntryListener implements ApplicationListener<FileEntryEvent> {

    @Override
    public void onApplicationEvent(FileEntryEvent event) {

    }
}
