/*
 *     Dr.Rename - A Minimalistic Batch Renamer
 *
 *     Copyright (C) 2022
 *
 *     This file is part of Dr.Rename.
 *
 *     You can redistribute it and/or modify it under the terms of the GNU Affero
 *     General Public License as published by the Free Software Foundation, either
 *     version 3 of the License, or (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful, but WITHOUT
 *     ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 *     FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License
 *     for more details.
 *
 *     You should have received a copy of the GNU Affero General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package drrename;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.concurrent.Executor;

@Slf4j
@RequiredArgsConstructor
@Component
public class SettingsProvider {

    public static final String DEFAULT_SETTINGS_FILE_NAME = "drrename-settings.json";

    public static final String DEFAULT_SETTINGS_DIR_PARENT = System.getProperty("user.home");

    public static final String DEFAULT_SETTINGS_DIR = ".drrename";

    public static final Path DEFAULT_SETTINGS_PATH;

    static {
        DEFAULT_SETTINGS_PATH = Paths.get(DEFAULT_SETTINGS_DIR_PARENT, DEFAULT_SETTINGS_DIR, DEFAULT_SETTINGS_FILE_NAME);
    }

    private static final Path settingsPath = DEFAULT_SETTINGS_PATH;

    private final ObjectMapper objectMapper;

    private final Executor executor;

    public Settings load() {
        return doLoad(settingsPath);
    }

    Settings doLoad(Path settingsPath) {
        Settings settings = new Settings();
        try {
            settings = objectMapper.readValue(settingsPath.toFile(), Settings.class);
        } catch (IOException e) {
            log.debug("Failed to load settings from {} ({})", settingsPath, e.getLocalizedMessage());
        }
        return settings;
    }

    public void save(Settings settings) {
        executor.execute(() -> save(settings, settingsPath));
    }

    void save(Settings settings, Path settingsPath) {
        try {
            Files.createDirectories(settingsPath.getParent());
            Path tmpPath = settingsPath.resolveSibling(settingsPath.getFileName().toString() + ".tmp");
            objectMapper.writeValue(tmpPath.toFile(), settings);
            Files.move(tmpPath, settingsPath, StandardCopyOption.REPLACE_EXISTING);
            log.info("Settings saved to {}", settingsPath);
        } catch (IOException e) {
            log.error("Failed to save settings", e);
        }
    }
}
