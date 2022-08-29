package drrename.kodi;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.nio.file.*;

@RequiredArgsConstructor
@Slf4j
@Getter
@Setter
@Component
public class KodiService extends Service<KodiCheckResult> {

    private Path directory;

    @Override
    protected Task<KodiCheckResult> createTask() {
        return new KodiTask( directory);
    }

}
