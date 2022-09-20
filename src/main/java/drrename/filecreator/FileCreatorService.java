package drrename.filecreator;

import drrename.AppConfig;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Random;

@RequiredArgsConstructor
@Slf4j
@Getter
@Setter
@Component
public class FileCreatorService extends Service<Void> {

    private final AppConfig config;

    private String wordSeparator;
    private long fileCnt;
    private Path directory;

    @Override
    protected Task<Void> createTask() {
        return new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                for (int i = 1; i <= fileCnt; i++) {
                    if (Thread.interrupted()) {
                        break;
                    }
                    String randomName = generateRandomName();
                    Path p = Paths.get(directory.toString(), randomName);
                    Files.createFile(p);
                    if (config.isDebug())
                        Thread.sleep(config.getLoopDelayMs());
                    updateProgress(i, fileCnt);
                }
                return null;
            }
        };
    }

    private String generateRandomName() {
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        long wordCnt = config.getWordCnt();
        for (int i = 1; i < wordCnt; i++) {
            String word = generateRandomWord();
            sb.append(word);
            if (i < wordCnt - 1) {
                int numSeparators = random.nextInt(4);
//                log.debug("Number of separators: {}", numSeparators);
                for (int j = 0; j < numSeparators; j++) {
                    sb.append(wordSeparator);
                }
            }
        }
        sb.append("." + generateRandomFileExtension());
        return sb.toString();
    }

    private String generateRandomWord() {
        return RandomStringUtils.randomAlphanumeric(1, 10);

    }

    private String generateRandomFileExtension() {
        return RandomStringUtils.randomAlphabetic(3, 4).toLowerCase();

    }
}
