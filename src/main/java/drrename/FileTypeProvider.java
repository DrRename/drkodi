package drrename;

import java.nio.file.Path;

public interface FileTypeProvider {

    String getFileType(Path path);
}
