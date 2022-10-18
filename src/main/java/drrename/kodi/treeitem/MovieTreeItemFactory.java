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

package drrename.kodi.treeitem;

import drrename.kodi.treeitem.content.check.CheckServiceProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.util.concurrent.Executor;

@Slf4j
@RequiredArgsConstructor
@Service
public class MovieTreeItemFactory {

    private final Executor executor;

    private final CheckServiceProvider checkServiceProvider;

    public MovieTreeItem buildNew(Path moviePath){
        return triggerNfoFileNameCheck(new MovieTreeItem(moviePath));
    }

    private MovieTreeItem triggerNfoFileNameCheck(MovieTreeItem movieTreeItem) {
        executor.execute(() -> checkServiceProvider.getCheckServices().forEach(e -> e.addChildItem(movieTreeItem)));
        return movieTreeItem;
    }
}
