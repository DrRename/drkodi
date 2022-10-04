package drrename.ui.service;

import drrename.RenamingStrategy;
import drrename.config.AppConfig;
import drrename.model.RenamingEntry;
import drrename.ui.service.RenamingTask;
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

    private List<RenamingEntry> events;
    private RenamingStrategy strategy;

    public List<RenamingEntry> getEvents() {

        return events;
    }

    public void setEvents(final List<RenamingEntry> events) {

        this.events = events;
    }

    public RenamingStrategy getStrategy() {

        return strategy;
    }

    public void setStrategy(final RenamingStrategy strategy) {

        this.strategy = strategy;
    }

    @Override
    protected Task<List<RenamingEntry>> createTask() {

        return new RenamingTask(events, strategy, appConfig, applicationEventPublisher);
    }
}
