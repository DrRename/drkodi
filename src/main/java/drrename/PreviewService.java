package drrename;

import javafx.concurrent.Task;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executor;

@Component
public class PreviewService extends StrategyService<Void> {

    private final ApplicationEventPublisher applicationEventPublisher;

    public PreviewService(final Executor taskExecutor, ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
        setExecutor(taskExecutor);
    }

    @Override
    protected Task<Void> createTask() {

        return new PreviewTask(getFiles(), getRenamingStrategy(), applicationEventPublisher);
    }
}
