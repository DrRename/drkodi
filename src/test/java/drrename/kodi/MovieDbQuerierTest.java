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

package drrename.kodi;

import com.fasterxml.jackson.databind.ObjectMapper;
import drrename.Beans;
import drrename.SettingsProvider;
import drrename.config.TheMovieDbConfig;
import drrename.ui.config.UiConfig;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;
import java.util.ResourceBundle;
import java.util.concurrent.Executor;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@EnableConfigurationProperties
@ContextConfiguration(classes = { TheMovieDbConfig.class, Beans.class, UiConfig.class, SettingsProvider.class, ObjectMapper.class, Executor.class})
class MovieDbQuerierTest {

    @Autowired
    private MovieDbClient client;

    @Autowired
    private TheMovieDbConfig config;

    @Autowired
    private ResourceBundle resourceBundle;

    private MovieDbQuerier querier;

    @BeforeEach
    void setUp() {
        querier = new MovieDbQuerier(client, config, resourceBundle);
    }

    @AfterEach
    void tearDown() {
    }


    void query() throws IOException {
        var result = querier.query("Cinderella", 2015);
        System.out.println(result);
    }
}