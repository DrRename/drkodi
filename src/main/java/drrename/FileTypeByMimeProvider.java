package drrename;

import jodd.net.MimeTypes;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.tika.Tika;

import java.io.File;
import java.nio.file.Path;
import java.util.Arrays;

@Slf4j
public class FileTypeByMimeProvider implements FileTypeProvider {

    public static String UNKNOWN = "n/a";

    @Override
    public String getFileType(Path path) {
        final File file = path.toFile();
        try {
            Tika tika = new Tika();
            var result = tika.detect(file);
            if (result != null && !result.contains("image")) {
                int wait = 0;
            }
            return result;
        } catch (Exception e) {
            log.error(e.getLocalizedMessage(), e);
            return UNKNOWN;
        }
    }
}
