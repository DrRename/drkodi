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
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;

class SettingsProviderTest {

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void doLoad(){
        var provider = new SettingsProvider(new ObjectMapper(), new ScheduledThreadPoolExecutor(1));
        var result = provider.doLoad(Paths.get("src/test/resources/settings/test-settings-01.json"));
        assertNotNull(result);
        assertEquals(new Settings(), result);
    }

    @Test
    void save() throws IOException {
        var provider = new SettingsProvider(new ObjectMapper(), new ScheduledThreadPoolExecutor(1));
        provider.save(new Settings(), Paths.get("src/test/resources/settings/test-settings-02.json"));
        Files.deleteIfExists(Paths.get("src/test/resources/settings/test-settings-02.json"));
    }
}