package drrename.kodi;

import drrename.*;
import drrename.config.AppConfig;
import drrename.kodi.data.Movie;
import drrename.kodi.data.StaticMovieData;
import drrename.kodi.ui.FilterableKodiRootTreeItem;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.nio.file.Files;
import java.util.Comparator;
import java.util.ResourceBundle;
import java.util.concurrent.Executor;

@Getter
@Slf4j
class KodiToolsCollectTask extends DrRenameTask<ObservableList<StaticMovieData>> {

    private final Entries entries;

//    private final FilterableKodiRootTreeItem rootTreeItem;

    private final Executor executor;

//    private final MovieDbClientFactory movieDbClientFactory;

    private final WarningsConfig warningsConfig;

    private final Observable[] extractor;

    private final SearchResultMapper mapper;

    private final MovieDbQuerier2 movieDbQuerier2;

    public KodiToolsCollectTask(AppConfig config, ResourceBundle resourceBundle, Entries entries, FilterableKodiRootTreeItem rootTreeItem, Executor executor/*, MovieDbClientFactory movieDbClientFactory*/, WarningsConfig warningsConfig, Observable[] extractor, SearchResultMapper mapper, MovieDbQuerier2 movieDbQuerier2) {
        super(config, resourceBundle);
        this.entries = entries;
//        this.rootTreeItem = rootTreeItem;
        this.executor = executor;
//        this.movieDbClientFactory = movieDbClientFactory;
        this.warningsConfig = warningsConfig;
        this.extractor = extractor;
        this.mapper = mapper;
        this.movieDbQuerier2 = movieDbQuerier2;
    }

    @Override
    protected ObservableList<StaticMovieData> call() throws Exception {
        log.debug("Starting");
        updateMessage(String.format(getResourceBundle().getString(KodiCollectService.MESSAGE)));
        ObservableList<StaticMovieData> result = FXCollections.observableArrayList();

        int cnt = 0;
        for (RenamingPath renamingPath : entries.getEntriesFiltered()) {
            if (isCancelled()) {
                log.debug("Cancelled");
                updateMessage("Cancelled.");
                break;
            }
            if(!Files.isDirectory(renamingPath.getOldPath())){
                continue;
            }
            StaticMovieData staticMovieData = new Movie(renamingPath, mapper, getExecutor(), movieDbQuerier2);
            result.add(staticMovieData);
            updateProgress(++cnt, entries.getEntriesFiltered().size());
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

        result.sort(Comparator.comparing(kodiElement -> kodiElement.getRenamingPath().getOldPath()));
        updateMessage(null);
        log.debug("Finished");
        return result;
    }

//    private void checkNfoName(Path path, NfoFileParser nfoFileParser, MovieTreeItemValue itemValue) throws IOException {
//        var nfoFiles = new NfoFileCollector().collectNfoFiles(path);
//        if(!nfoFiles.isEmpty()){
//            Path firstNfoFile = nfoFiles.get(0);
//            String movieNameFromNfoFile = new NfoFileTitleExtractor(nfoFileParser).parseNfoFile(firstNfoFile);
//            if(StringUtils.isNotBlank(movieNameFromNfoFile)){
//                itemValue.setMovieNameFromNfo(movieNameFromNfoFile);
//            }
//        }
//    }
}
