package drrename;

import lombok.extern.slf4j.Slf4j;
import org.apache.tika.Tika;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

@Slf4j
public class FileTypeByMimeProvider implements FileTypeProvider {

    public static String UNKNOWN = "n/a";

    public static String DIRECTORY = "directory";

    @Override
    public String getFileType(Path path) {
        final File file = path.toFile();
        if(Files.isDirectory(path)){
            return DIRECTORY;
        }
        try {
            Tika tika = new Tika();
            @SuppressWarnings("")
            var result = tika.detect(file);
            return result;
        } catch (Exception e) {
            log.error(e.getLocalizedMessage(), e);
            return UNKNOWN;
        }
    }
}
