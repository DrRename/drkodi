package drrename.ui.service;

import drrename.strategy.RenamingStrategy;
import drrename.config.AppConfig;
import drrename.model.RenamingEntry;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.List;


@RequiredArgsConstructor
@Component
@Slf4j
public class RenamingService extends Service<List<RenamingEntry>> {

    private final AppConfig appConfig;

    private final ApplicationEventPublisher applicationEventPublisher;

    private List<RenamingEntry> renamingEntries;
    private RenamingStrategy strategy;

    public List<RenamingEntry> getRenamingEntries() {

        return renamingEntries;
    }

    public void setRenamingEntries(final List<RenamingEntry> renamingEntries) {

        this.renamingEntries = renamingEntries;
    }

    public RenamingStrategy getStrategy() {

        return strategy;
    }

    public void setStrategy(final RenamingStrategy strategy) {

        this.strategy = strategy;
    }

    @Override
    protected Task<List<RenamingEntry>> createTask() {

        return new RenamingTask(renamingEntries, strategy, appConfig, applicationEventPublisher);
    }
}
