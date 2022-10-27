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

package drrename.kodi;

import drrename.MovieDbQuerier;
import drrename.MovieDbClient;
import drrename.MovieDbImagesClient;
import drrename.config.TheMovieDbConfig;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ResourceBundle;

@Component
public class MovieDbClientFactory {

    private final MovieDbClient client;

    private final MovieDbImagesClient imagesClient;

    private final TheMovieDbConfig config;

    private final ResourceBundle resourceBundle;

    public MovieDbClientFactory(MovieDbClient client, MovieDbImagesClient imagesClient, TheMovieDbConfig config, ResourceBundle resourceBundle) {
        this.client = client;
        this.imagesClient = imagesClient;
        this.config = config;
        this.resourceBundle = resourceBundle;
    }

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public MovieDbQuerier getNewMovieDbChecker() {
        return new MovieDbQuerier(client, config, resourceBundle);
    }

    public MovieDbImagesClient getImagesClient() {
        return imagesClient;
    }

    public TheMovieDbConfig getConfig() {
        return config;
    }
}
