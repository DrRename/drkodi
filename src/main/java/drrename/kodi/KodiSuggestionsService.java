package drrename.kodi;

import drrename.config.AppConfig;
import drrename.kodi.data.Movie;
import javafx.concurrent.Task;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.ResourceBundle;

@Component
public class KodiSuggestionsService extends KodiService<Void> {

    private final MovieDbQuerier2 querier2;

    public KodiSuggestionsService(AppConfig appConfig, ResourceBundle resourceBundle, MovieDbQuerier2 querier2) {
        super(appConfig, resourceBundle);
        this.querier2 = querier2;
    }

    @Override
    public void setElements(List<? extends Movie> elements) {
        super.setElements(elements);
    }

    @Override
    protected Task<Void> createTask() {
        return new KodiSuggestionsTask(getAppConfig(), getResourceBundle(), getElements(), getExecutor(), querier2);
    }
}
