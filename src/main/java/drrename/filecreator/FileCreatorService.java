/*
 *     Dr.Rename - A Minimalistic Batch Renamer
 *
 *     Copyright (C) 2022
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Affero General Public License as
 *     published by the Free Software Foundation, either version 3 of the
 *     License, or (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Affero General Public License for more details.
 *
 *     You should have received a copy of the GNU Affero General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package drrename.filecreator;

import drrename.DrRenameService;
import drrename.Tasks;
import drrename.config.AppConfig;
import javafx.concurrent.Task;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Random;
import java.util.ResourceBundle;


@Slf4j
@Setter
@Component
public class FileCreatorService extends DrRenameService<Void> {

    private String wordSeparator;

    private long fileCnt;

    private Path directory;

    public FileCreatorService(AppConfig appConfig, ResourceBundle resourceBundle) {
        super(appConfig, resourceBundle);
    }

    @Override
    protected Task<Void> createTask() {
        return new Task<>() {
            @Override
            protected Void call() throws Exception {
                log.debug("Starting");
                for (int i = 1; i <= fileCnt; i++) {
                    if (isCancelled()) {
                        log.debug("Cancelled");
                        updateMessage(String.format(getResourceBundle().getString(Tasks.MESSAGE_CANCELLED)));
                        break;
                    }
                    String randomName = generateRandomName();
                    Path p = Paths.get(directory.toString(), randomName);
                    Files.createFile(p);
                    updateProgress(i, fileCnt);
                    if (getAppConfig().isDebug()) {
                        try {
                            Thread.sleep(getAppConfig().getLoopDelayMs());
                        } catch (InterruptedException e) {
                            if (isCancelled()) {
                                log.debug("Cancelled");
                                updateMessage(String.format(getResourceBundle().getString(Tasks.MESSAGE_CANCELLED)));
                                break;
                            }
                        }
                    }
                }
                log.debug("Completed");
                updateMessage(null);
                return null;
            }
        };
    }

    private String generateRandomName() {
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        long wordCnt = getAppConfig().getWordCnt();
        for (int i = 1; i < wordCnt; i++) {
            String word = generateRandomWord();
            sb.append(word);
            if (i < wordCnt - 1) {
                int numSeparators = random.nextInt(4);
                sb.append(String.valueOf(wordSeparator).repeat(numSeparators));
            }
        }
        sb.append(".").append(generateRandomFileExtension());
        return sb.toString();
    }

    private String generateRandomWord() {
        return RandomStringUtils.randomAlphanumeric(1, 10);

    }

    private String generateRandomFileExtension() {
        return RandomStringUtils.randomAlphabetic(3, 4).toLowerCase();

    }
}
