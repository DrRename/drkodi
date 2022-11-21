package drrename.kodi;

import drrename.kodi.nfo.NfoFileCollector;
import drrename.kodi.nfo.NfoFileParser;
import drrename.kodi.ui.FilterableKodiRootTreeItem;
import drrename.kodi.ui.MovieTreeItemValue;
import drrename.kodi.ui.NfoFileTitleExtractor;
import drrename.RenamingPath;
import javafx.beans.Observable;
import javafx.concurrent.Task;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executor;

@RequiredArgsConstructor
@Getter
@Slf4j
class KodiToolsCollectTask extends Task<List<MovieTreeItemFilterable>> {

    private final Path directory;

    private final FilterableKodiRootTreeItem rootTreeItem;

    private final Executor executor;

    private final MovieDbClientFactory movieDbClientFactory;

    private final WarningsConfig warningsConfig;

    private final Observable[] extractor;

    private void verifyState() {
        Objects.requireNonNull(directory);
        if (!Files.isDirectory(directory)) {
            throw new IllegalArgumentException(directory.getFileName() + " is not a directory");
        }
        Objects.requireNonNull(rootTreeItem);
        Objects.requireNonNull(executor);
    }

    @Override
    protected List<MovieTreeItemFilterable> call() throws Exception {
        verifyState();
        List<MovieTreeItemFilterable> result = new ArrayList<>();
        var nfoFileParser = new NfoFileParser();
        log.debug("Taking a look at {}", directory);
        try (DirectoryStream<Path> ds = Files.newDirectoryStream(directory)) {

            for (Path path : ds) {
                if (isCancelled()) {
                    updateMessage("Cancelled");
                    log.info("Cancelled");
                    break;
                }
                if (Files.isDirectory(path)) {
                    if (path.getFileName().toString().startsWith("@")) {
                        log.debug("Ignoring {}", path);
                        continue;
                    }

                    var renamingPath = new RenamingPath(path);
                    var itemValue = new MovieTreeItemValue(renamingPath, executor, warningsConfig);

                    checkNfoName(path, nfoFileParser, itemValue);

                    var item = new MovieTreeItemFilterable(itemValue, extractor);
                    result.add(item);
                }
            }
        }
        result.sort(Comparator.comparing(o -> o.getValue().getRenamingPath().getMovieName()));
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
