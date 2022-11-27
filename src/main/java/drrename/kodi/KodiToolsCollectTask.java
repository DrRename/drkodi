package drrename.kodi;

import drrename.DrRenameTask;
import drrename.Entries;
import drrename.RenamingPath;
import drrename.config.AppConfig;
import drrename.kodi.nfo.NfoFileCollector;
import drrename.kodi.nfo.NfoFileParser;
import drrename.kodi.ui.FilterableKodiRootTreeItem;
import drrename.kodi.ui.MovieTreeItemValue;
import drrename.kodi.nfo.NfoFileTitleExtractor;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.ResourceBundle;
import java.util.concurrent.Executor;

@Getter
@Slf4j
class KodiToolsCollectTask extends DrRenameTask<ObservableList<MovieTreeItemFilterable>> {

    private final Entries entries;

    private final FilterableKodiRootTreeItem rootTreeItem;

    private final Executor executor;

    private final MovieDbClientFactory movieDbClientFactory;

    private final WarningsConfig warningsConfig;

    private final Observable[] extractor;

    public KodiToolsCollectTask(AppConfig config, ResourceBundle resourceBundle, Entries entries, FilterableKodiRootTreeItem rootTreeItem, Executor executor, MovieDbClientFactory movieDbClientFactory, WarningsConfig warningsConfig, Observable[] extractor) {
        super(config, resourceBundle);
        this.entries = entries;
        this.rootTreeItem = rootTreeItem;
        this.executor = executor;
        this.movieDbClientFactory = movieDbClientFactory;
        this.warningsConfig = warningsConfig;
        this.extractor = extractor;
    }

    @Override
    protected ObservableList<MovieTreeItemFilterable> call() throws Exception {
        log.debug("Starting");
        updateMessage(String.format(getResourceBundle().getString(KodiCollectService.MESSAGE)));
        ObservableList<MovieTreeItemFilterable> result = FXCollections.observableArrayList();
        var nfoFileParser = new NfoFileParser();

        int cnt = 0;
        for (RenamingPath renamingPath : entries.getEntriesFiltered()) {
            if (isCancelled()) {
                log.debug("Cancelled");
                updateMessage("Cancelled.");
                break;
            }
            if (Files.isDirectory(renamingPath.getOldPath())) {
                var itemValue = new MovieTreeItemValue(renamingPath, executor, warningsConfig);
                checkNfoName(renamingPath.getOldPath(), nfoFileParser, itemValue);
                var item = new MovieTreeItemFilterable(itemValue, extractor);
                result.add(item);
            }
            updateProgress(++cnt, entries.getEntriesFiltered().size());
            if (getConfig().isDebug()) {
                Thread.sleep(getConfig().getLoopDelayMs());
            }
        }

        result.sort(Comparator.comparing(o -> o.getValue().getRenamingPath().getMovieName()));
        updateMessage(null);
        log.debug("Finished");
        return result;
    }

    private void checkNfoName(Path path, NfoFileParser nfoFileParser, MovieTreeItemValue itemValue) throws IOException {
        var nfoFiles = new NfoFileCollector().collectNfoFiles(path);
        if(!nfoFiles.isEmpty()){
            Path firstNfoFile = nfoFiles.get(0);
            String movieNameFromNfoFile = new NfoFileTitleExtractor(nfoFileParser).getTitleFromNfoFile(firstNfoFile);
            if(StringUtils.isNotBlank(movieNameFromNfoFile)){
                itemValue.setMovieNameFromNfo(movieNameFromNfoFile);
            }
        }
    }
}
