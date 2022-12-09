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

import drrename.DrRenameTask;
import drrename.Tasks;
import drrename.config.AppConfig;
import drrename.kodi.data.Movie;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.ResourceBundle;

@Getter
public abstract class KodiTask extends DrRenameTask<Void> {

    protected final Logger log = LoggerFactory.getLogger(getClass());

    private final List<? extends Movie> elements;

    private  final int indexOfAppearance;

    public KodiTask(AppConfig appConfig, ResourceBundle resourceBundle, List<? extends Movie> elements, int indexOfAppearance) {
        super(appConfig, resourceBundle);
        this.elements = elements;
        this.indexOfAppearance = indexOfAppearance;
    }

    public KodiTask(AppConfig appConfig, ResourceBundle resourceBundle, Movie element, int indexOfAppearance) {
        this(appConfig, resourceBundle, List.of(element), indexOfAppearance);
    }

    @Override
    protected Void call() throws Exception {
//        log.debug("Starting");

        for(int i = 0; i < getElements().size(); i++){
            if (isCancelled()) {
                log.debug("Cancelled");
                updateMessage(String.format(getResourceBundle().getString(Tasks.MESSAGE_CANCELLED)));
                break;
            }
            Movie element = getElements().get(i);
            handleElement(element);
            updateProgress(i, getElements().size()-1);

            if (getAppConfig().isDebug()) {
                try {
                    Thread.sleep(getAppConfig().getLoopDelayMs());
                } catch (InterruptedException e) {
                    if (isCancelled()) {
                        log.debug("Cancelled");
                        updateMessage(String.format(getResourceBundle().getString(Tasks.MESSAGE_CANCELLED)));
                        break;
                    }
                }
            }
        }

        updateMessage(null);
//        log.debug("Finished");
        return null;
    }

    protected abstract void handleElement(Movie element) throws Exception;


}
