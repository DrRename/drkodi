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

import drrename.ui.FilterableTreeItem;
import javafx.application.Platform;

import java.nio.file.Path;
import java.util.concurrent.Executor;

public class KodiLevel2TreeItem extends FilterableTreeItem<KodiTreeItemContent> {

    protected final CheckService checkService;

    protected final Executor executor;

    protected final Path moviePath;

    public KodiLevel2TreeItem(Path moviePath, CheckService checkService, Executor executor) {
        super(new KodiLevel2TreeItemContent(moviePath));
        getValue().setTreeItem(this);
        this.checkService = checkService;
        this.executor = executor;
        this.moviePath = moviePath;
        addLevel3Items();
    }

    protected KodiLevel2TreeItemContent getValueInternal(){
        return (KodiLevel2TreeItemContent) getValue();
    }

    protected void addLevel3Items() {
        executor.execute(this::doAddLevel3Items);
    }

    protected void doAddLevel3Items() {
        var item = new KodiLevel3TreeItem(new KodiLevel3TreeItemContent(checkService.calculate(moviePath)));
        Platform.runLater(() -> getSourceChildren().add(item));
    }
}
