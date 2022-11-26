package drrename.ui.service;

import drrename.DrRenameService;
import drrename.Entries;
import drrename.RenamingControl;
import drrename.config.AppConfig;
import drrename.strategy.RenamingStrategy;
import javafx.concurrent.Task;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.ResourceBundle;



@Component
@Setter
@Slf4j
public class RenamingService extends DrRenameService<Void> {

    static final String RENAMING_FILES = "mainview.status.renaming_files";

    private final Entries entries;

    private List<RenamingControl> renamingEntries;

    private RenamingStrategy strategy;

    public RenamingService(AppConfig appConfig, ResourceBundle resourceBundle, Entries entries) {
        super(appConfig, resourceBundle);
        this.entries = entries;
    }

    @Override
    protected Task<Void> createTask() {

        return new RenamingTask(getAppConfig(), getResourceBundle(), renamingEntries, strategy, entries);
    }
}
