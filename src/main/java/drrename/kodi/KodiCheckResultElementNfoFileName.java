package drrename.kodi;


import lombok.*;
import org.apache.commons.io.FilenameUtils;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;

@Getter
public class KodiCheckResultElementNfoFileName extends KodiCheckResultElement<KodiCheckResultElementNfoFileName.NfoFileNameType> {

    public enum NfoFileNameType {NO_FILE, MOVIE_NAME, DEFAULT_NAME, MULTIPLE_FILES, INVALID_NAME}

    public KodiCheckResultElementNfoFileName(String movieName, NfoFileNameType nfoFileNameType) {
        super(KodiCheckResult.Type.NFO_FILE_NAME, movieName, nfoFileNameType);
    }

    static KodiCheckResultElementNfoFileName parse(Path path) throws IOException {
        String movieName = path.getFileName().toString();
        NfoFileNameType nfoFileNameTypeResult = parse2(path);
        return new KodiCheckResultElementNfoFileName(movieName, nfoFileNameTypeResult);
    }

    static NfoFileNameType parse2(Path path) throws IOException {
        String movieName = path.getFileName().toString();
        NfoFileNameType result = null;
        try (DirectoryStream<Path> ds = Files.newDirectoryStream(path)) {
            for (Path child : ds) {
                String extension = FilenameUtils.getExtension(child.getFileName().toString());
                if (Files.isRegularFile(child) && "nfo".equalsIgnoreCase(extension)) {
                    if(result != null){
                        return NfoFileNameType.MULTIPLE_FILES;
                    }
                    result = handleFile(movieName, child);
                }
            }
        }
        return NfoFileNameType.NO_FILE;
    }

    private static NfoFileNameType handleFile(String movieName, Path child) {
        String childName = child.getFileName().toString();
        if ("movie.nfo".equalsIgnoreCase(childName)) {
            return NfoFileNameType.DEFAULT_NAME;
        } else if (FilenameUtils.getBaseName(childName).equalsIgnoreCase(movieName)) {
            return NfoFileNameType.MOVIE_NAME;
        } else {
            return NfoFileNameType.INVALID_NAME;
        }
    }

}
