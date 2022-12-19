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

package drkodi;

import drkodi.config.AppConfig;
import drkodi.data.dynamic.Movie;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.ResourceBundle;

@Slf4j
public class NfoTitleTask extends KodiTask {

    public NfoTitleTask(AppConfig appConfig, ResourceBundle resourceBundle, List<? extends Movie> elements, int indexOfAppearance) {
        super(appConfig, resourceBundle, elements, indexOfAppearance);
    }

    public NfoTitleTask(AppConfig appConfig, ResourceBundle resourceBundle, Movie element, int indexOfAppearance) {
        super(appConfig, resourceBundle, element, indexOfAppearance);
    }

    @Override
    protected void handleElement(Movie element) throws Exception {
        if(element.getNfoData() != null){
            if(!element.getMovieTitleFromFolder().equals(element.getMovieTitleFromNfo())){
                log.warn("NFO title mismatch for {}", element);
//               Platform.runLater(() ->  element.getWarnings().add(getIndexOfAppearance(), new KodiWarning("NFO title mismatch", null)));
            }
        }
    }


}
