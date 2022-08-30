package drrename.kodi;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.nio.file.Path;
import java.util.List;

@RequiredArgsConstructor
@Data
public class SubdirResult {

    private final Path movie;

    private final List<Path> subdirs;
}
