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

import drrename.kodi.FixableStatusChecker;
import drrename.kodi.MovieDbClientFactory;
import drrename.kodi.WarningsConfig;
import drrename.kodi.nfo.MovieDbLookupTreeItemValue;
import drrename.RenamingPath;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executor;

@Slf4j
@RequiredArgsConstructor
public class KodiAddChildItemsTask extends Task<Void> {

    private final List<? extends FilterableKodiTreeItem> itemValues;

    private final Executor executor;

    private final WarningsConfig warningsConfig;

    private final MovieDbClientFactory movieDbClientFactory;

    @Override
    protected Void call() throws Exception {

        int cnt = 0;
        for(FilterableKodiTreeItem itemValue : itemValues){
            if (isCancelled()) {
                updateMessage("Cancelled");
                log.info("Cancelled");
                break;
            }
            var childItems = buildChildItems(itemValue.getValue().getRenamingPath());
            updateProgress(++cnt, itemValues.size());
            Platform.runLater(() -> childItems.forEach(childItem -> itemValue.getSourceChildren().add(childItem)));
        }

        return null;
    }

    List<FilterableKodiTreeItem> buildChildItems(RenamingPath renamingPath) {
        List<FilterableKodiTreeItem> treeItems = new ArrayList<>();
        List<KodiTreeItemValue<?>> treeItemValues = Arrays.asList(
                new MovieDbLookupTreeItemValue(renamingPath, executor, movieDbClientFactory, warningsConfig),
                new NfoFileNameTreeItemValue(renamingPath, executor, warningsConfig),
                new MediaFileNameTreeItemValue(renamingPath, executor, warningsConfig),
                new NfoFileContentMovieNameTreeItemValue(renamingPath, executor, warningsConfig)
        );

        for (KodiTreeItemValue<?> v : treeItemValues) {
            treeItems.add(new FilterableKodiTreeItem(v, null));
            executeItemValue(v);
        }

        return treeItems;
    }

    <R> void executeItemValue(KodiTreeItemValue<R> itemValue) {
        var fixableStatusChecker = new FixableStatusChecker<>(itemValue);
        fixableStatusChecker.setOnFailed(itemValue::defaultTaskFailed);
        fixableStatusChecker.setOnSucceeded(event -> statusCheckerSucceeded(itemValue, event));
        fixableStatusChecker.run();
    }

    <R> void statusCheckerSucceeded(KodiTreeItemValue<R> itemValue, WorkerStateEvent event) {
        itemValue.updateStatus((R) event.getSource().getValue());
    }
}
