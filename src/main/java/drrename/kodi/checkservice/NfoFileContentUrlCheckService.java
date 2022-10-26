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

package drrename.kodi.checkservice;

import drrename.kodi.MovieDbClientFactory;
import drrename.kodi.WarningsConfig;
import drrename.kodi.nfo.NfoFileContentUrlTreeItemValue;
import drrename.model.RenamingPath;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executor;

@Component
public class NfoFileContentUrlCheckService extends CheckService<NfoFileContentUrlTreeItemValue> {

    private final MovieDbClientFactory factory;

    public NfoFileContentUrlCheckService(MovieDbClientFactory factory) {

        this.factory = factory;
    }

    @Override
    public NfoFileContentUrlTreeItemValue checkPath(RenamingPath path, WarningsConfig warningsConfig, Executor executor) {
        return new NfoFileContentUrlTreeItemValue(path, executor, factory);
    }
}
