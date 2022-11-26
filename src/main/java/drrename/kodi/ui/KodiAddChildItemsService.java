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

package drrename.kodi.ui;

import drrename.DrRenameService;
import drrename.config.AppConfig;
import drrename.kodi.MovieDbClientFactory;
import drrename.kodi.WarningsConfig;
import javafx.concurrent.Task;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.Executor;

@Component
@Setter
public class KodiAddChildItemsService extends DrRenameService<Void> {

    static final String MESSAGE = "kodi.add-child-items";

    private List<? extends FilterableKodiTreeItem> itemValues;

    private WarningsConfig warningsConfig;

    private final MovieDbClientFactory movieDbClientFactory;

    public KodiAddChildItemsService(AppConfig appConfig, ResourceBundle resourceBundle, MovieDbClientFactory movieDbClientFactory) {
        super(appConfig, resourceBundle);
        this.movieDbClientFactory = movieDbClientFactory;
    }

    @Override
    protected Task<Void> createTask() {
        return new KodiAddChildItemsTask(getAppConfig(), getResourceBundle(), itemValues, getExecutor(),warningsConfig,movieDbClientFactory);
    }
}
