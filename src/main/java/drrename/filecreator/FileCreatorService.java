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

import drrename.config.AppConfig;
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

    @Override
    public void reset() {
        super.reset();
        this.fileCnt = 0;
        this.directory = null;
        this.wordSeparator = null;
    }
}
