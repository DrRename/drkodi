package com.kerner1000.drrename;

import com.github.drrename.PreviewTask;
import com.kerner1000.drrename.StrategyService;
import javafx.concurrent.Task;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;

@Component
public class PreviewService extends StrategyService<Void> {

    public PreviewService(@Qualifier("low-priority-executor") final ExecutorService lowPrioExecutor) {
        setExecutor(lowPrioExecutor);
    }

    @Override
    protected Task<Void> createTask() {

        return new PreviewTask(getFiles(), getRenamingStrategy());
    }
}
