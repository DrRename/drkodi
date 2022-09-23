package drrename;

import drrename.event.FileEntryEvent;
import drrename.model.RenamingBean;
import javafx.application.Platform;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
public class EntriesService implements ApplicationListener<FileEntryEvent> {

    private ListProperty<RenamingBean> entries;

    @PostConstruct
    public void init(){
        this.entries = new SimpleListProperty<>(FXCollections.observableArrayList());
    }

    public ObservableList<RenamingBean> getEntries() {
        return entries.get();
    }

    @Override
    public void onApplicationEvent(FileEntryEvent event) {
        entries.add(event.getSource());
    }
}
