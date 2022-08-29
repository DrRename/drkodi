package drrename.kodi;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Getter
@Setter
@Component
public class KodiService extends Service<KodiCheckResult> {

    private Path directory;

    private KodiCheckResult kodiCheckResult;

    @Override
    protected Task<KodiCheckResult> createTask() {
        return new Task<KodiCheckResult>() {
            @Override
            protected KodiCheckResult call() throws Exception {
                KodiCheckResult result = new KodiCheckResult();
                try (DirectoryStream<Path> ds = Files.newDirectoryStream(directory)) {
                    for (Path path : ds) {
                        if(Thread.interrupted()){
                            log.info("Cancelling");
                            break;
                        }
//                        log.debug("Path {}", path);
                        if(Files.isDirectory(path)){
                            String movieName = path.getFileName().toString();
                            if(movieName.startsWith("@")){
                                log.info("Ignoring {}", path);
                                continue;
                            }
//                            log.debug("Movie name: {}", movieName);
                            var result2 = checkSubDirs(path);
                            if(!result2.getSubdirs().getSubdirs().isEmpty()) {
                                result.addResult(result2);
                            }
                            var result3 = checkForNfoFile(path);
                            if(KodiCheckResultElementNfoFile.NfoFile.INVALID_NAME.equals(result3) || KodiCheckResultElementNfoFile.NfoFile.NO_FILE.equals(result3))
                            result.addResult(new KodiCheckResultElementNfoFile(movieName, result3));
                        }
                    }
                }
                return result;
            }

            private KodiCheckResultElementSubDirs checkSubDirs(Path path) throws IOException {
                String movieName = path.getFileName().toString();
                List<Path> subdirs = checkSubDirs2(path);
                if(subdirs.isEmpty()){
                    return new KodiCheckResultElementSubDirs(movieName, new SubdirResult(path, subdirs));
                }
                return new KodiCheckResultElementSubDirs(movieName, new SubdirResult(path, subdirs));
            }

            private List<Path> checkSubDirs2(Path path) throws IOException {
                List<Path> subdirs = new ArrayList<>();
                String movieName = path.getFileName().toString();
                try (DirectoryStream<Path> ds = Files.newDirectoryStream(path)) {
                    for (Path child : ds) {
                        if(Files.isDirectory(child)){
                            subdirs.add(child.getFileName());
                            log.debug("{} has subdir: {}, empty: {}", movieName, child, isEmpty(child));
                        }
                    }
                }
                if(!subdirs.isEmpty())
                log.warn("{} has subdirs: {}", movieName, subdirs);
                return subdirs;
            }

            private KodiCheckResultElementNfoFile.NfoFile checkForNfoFile(Path path) throws IOException {
                String movieName = path.getFileName().toString();
                try (DirectoryStream<Path> ds = Files.newDirectoryStream(path)) {
                    for (Path child : ds) {
                        String extension = FilenameUtils.getExtension(child.getFileName().toString());
                        if(Files.isRegularFile(child) && "nfo".equalsIgnoreCase(extension)){
                            String childName = child.getFileName().toString();
                           if("movie.nfo".equalsIgnoreCase(childName)){
                               return KodiCheckResultElementNfoFile.NfoFile.DEFAULT_NAME;
                           }
                           else if(FilenameUtils.getBaseName(childName).equalsIgnoreCase(movieName)){
                               return KodiCheckResultElementNfoFile.NfoFile.MOVIE_NAME;
                           }
                           else {
                            log.warn("Invalid nfo file name for movie {}: {}", movieName, childName);
                            return KodiCheckResultElementNfoFile.NfoFile.INVALID_NAME;
                           }
                        }
                    }
                }
                log.warn("no nfo file for {}", movieName);
                return KodiCheckResultElementNfoFile.NfoFile.NO_FILE;
            }

            public static boolean isEmpty(Path path) throws IOException {
                if (Files.isDirectory(path)) {
                    try (DirectoryStream<Path> directory = Files.newDirectoryStream(path)) {
                        return !directory.iterator().hasNext();
                    }
                }
                return false;
            }
        };
    }
}
