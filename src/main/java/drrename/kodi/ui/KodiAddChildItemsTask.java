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

import drrename.DrRenameTask;
import drrename.RenamingPath;
import drrename.Tasks;
import drrename.config.AppConfig;
import drrename.kodi.FixableStatusChecker;
import drrename.kodi.MovieDbClientFactory;
import drrename.kodi.WarningsConfig;
import drrename.kodi.nfo.MovieDbLookupTreeItemValue;
import javafx.application.Platform;
import javafx.concurrent.WorkerStateEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.Executor;

@Slf4j
public class KodiAddChildItemsTask extends DrRenameTask<Void> {

    private final List<? extends FilterableKodiTreeItem> itemValues;

    private final Executor executor;

    private final WarningsConfig warningsConfig;

    private final MovieDbClientFactory movieDbClientFactory;

    public KodiAddChildItemsTask(AppConfig config, ResourceBundle resourceBundle, List<? extends FilterableKodiTreeItem> itemValues, Executor executor, WarningsConfig warningsConfig, MovieDbClientFactory movieDbClientFactory) {
        super(config, resourceBundle);
        this.itemValues = itemValues;
        this.executor = executor;
        this.warningsConfig = warningsConfig;
        this.movieDbClientFactory = movieDbClientFactory;
    }


    @Override
    protected Void call() throws Exception {

        log.debug("Starting");
        int cnt = 0;
        for (FilterableKodiTreeItem itemValue : itemValues) {
            if (isCancelled()) {
                log.debug("Cancelled");
                updateMessage(String.format(getResourceBundle().getString(Tasks.MESSAGE_CANCELLED)));
                break;
            }
            var childItems = buildChildItems(itemValue.getValue().getRenamingPath());
            updateProgress(++cnt, itemValues.size());
            Platform.runLater(() -> childItems.forEach(childItem -> itemValue.getSourceChildren().add(childItem)));
            if (getConfig().isDebug()) {
                Thread.sleep(getConfig().getLoopDelayMs());
            }
        }
        log.debug("Finished");
        updateMessage(null);
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
        // execute synchronously on current background thread
        fixableStatusChecker.run();
    }

    @SuppressWarnings("unchecked")
    <R> void statusCheckerSucceeded(KodiTreeItemValue<R> itemValue, WorkerStateEvent event) {
        itemValue.updateStatus((R) event.getSource().getValue());
    }
}
