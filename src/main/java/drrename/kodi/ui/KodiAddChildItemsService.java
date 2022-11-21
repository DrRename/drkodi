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

import drrename.kodi.MovieDbClientFactory;
import drrename.kodi.WarningsConfig;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.concurrent.Executor;

@org.springframework.stereotype.Service
@Setter
@RequiredArgsConstructor
public class KodiAddChildItemsService extends Service<Void> {

    private List<? extends FilterableKodiTreeItem> itemValues;

    private final Executor executor;

    private WarningsConfig warningsConfig;

    private final MovieDbClientFactory movieDbClientFactory;

    @Override
    protected Task<Void> createTask() {
        return new KodiAddChildItemsTask(itemValues, executor, warningsConfig, movieDbClientFactory);
    }
}
