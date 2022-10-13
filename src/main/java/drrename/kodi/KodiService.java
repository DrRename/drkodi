package drrename.kodi;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Getter
@Setter
@Component
public class KodiService extends Service<List<Path>> {

    private Path directory;

    @Override
    protected Task<List<Path>> createTask() {
        return new KodiTask(directory);
    }

}
