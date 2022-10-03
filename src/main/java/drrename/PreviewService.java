package drrename;

import drrename.ui.service.PreviewTask;
import javafx.concurrent.Task;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.concurrent.Executor;

@Service
public class PreviewService extends StrategyService<Void> {

    private final ApplicationEventPublisher applicationEventPublisher;

    public PreviewService(final Executor taskExecutor, ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
        setExecutor(taskExecutor);
    }

    @Override
    protected Task<Void> createTask() {

        return new PreviewTask(getRenamingEntries(), getRenamingStrategy(), applicationEventPublisher);
    }
}
