package drrename.ui.service;

import drrename.config.AppConfig;
import javafx.concurrent.Task;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ResourceBundle;


@Service
public class PreviewService extends StrategyService<Void> {

    static final String LOADING_PREVIEVS = "mainview.status.loading_previews";

    public PreviewService(AppConfig appConfig, ResourceBundle resourceBundle) {
        super(appConfig, resourceBundle);
    }

    @Override
    protected Task<Void> createTask() {

        return new PreviewTask(getAppConfig(), getResourceBundle(),getRenamingEntries(), getRenamingStrategy());
    }
}
