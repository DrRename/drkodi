package drrename.ui.service;

import drrename.strategy.RenamingStrategy;
import drrename.config.AppConfig;
import drrename.model.RenamingControl;
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
public class RenamingService extends Service<List<RenamingControl>> {

    private final AppConfig appConfig;

    private final ApplicationEventPublisher applicationEventPublisher;

    private List<RenamingControl> renamingEntries;
    private RenamingStrategy strategy;

    public List<RenamingControl> getRenamingEntries() {

        return renamingEntries;
    }

    public void setRenamingEntries(final List<RenamingControl> renamingEntries) {

        this.renamingEntries = renamingEntries;
    }

    public RenamingStrategy getStrategy() {

        return strategy;
    }

    public void setStrategy(final RenamingStrategy strategy) {

        this.strategy = strategy;
    }

    @Override
    protected Task<List<RenamingControl>> createTask() {

        return new RenamingTask(renamingEntries, strategy, appConfig, applicationEventPublisher);
    }
}
