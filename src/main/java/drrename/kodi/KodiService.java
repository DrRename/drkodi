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

import drrename.DrRenameService;
import drrename.config.AppConfig;
import drrename.kodi.data.Movie;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.ResourceBundle;

@Getter
@Setter
public abstract class KodiService<T> extends DrRenameService<T> {

    private List<? extends Movie> elements;

    public KodiService(AppConfig appConfig, ResourceBundle resourceBundle) {
        super(appConfig, resourceBundle);
    }
}
