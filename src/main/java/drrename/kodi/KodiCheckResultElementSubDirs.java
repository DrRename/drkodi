package drrename.kodi;

import lombok.*;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@Getter
public class KodiCheckResultElementSubDirs extends KodiCheckResultElement<SubdirResult> {

    public KodiCheckResultElementSubDirs(String movieName, SubdirResult subdirs) {
        super(KodiCheckResult.Type.SUB_DIRS, movieName, subdirs);
    }

    static KodiCheckResultElementSubDirs parse(Path path) throws IOException {
        String movieName = path.getFileName().toString();
        List<Path> subdirs = parse2(path);
        return new KodiCheckResultElementSubDirs(movieName, new SubdirResult(path, subdirs));
    }

    static List<Path> parse2(Path path) throws IOException {
        List<Path> subdirs = new ArrayList<>();
        try (DirectoryStream<Path> ds = Files.newDirectoryStream(path)) {
            for (Path child : ds) {
                if (Files.isDirectory(child)) {
                    subdirs.add(child.getFileName());
                }
            }
        }
        return subdirs;
    }
}
