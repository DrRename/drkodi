package drkodi;

import drkodi.config.AppConfig;
import drkodi.data.Movie;
import javafx.concurrent.Task;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.ResourceBundle;

@Component
public class KodiSuggestionsServicePrototype extends KodiServicePrototype<Void> {

    private final MovieDbSearcher querier2;

    public KodiSuggestionsServicePrototype(AppConfig appConfig, ResourceBundle resourceBundle, MovieDbSearcher querier2) {
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
