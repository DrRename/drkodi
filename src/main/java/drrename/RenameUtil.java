package drrename;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.stream.Collectors;

public class RenameUtil {

    public static Path rename(final Path file, String newFileName) throws IOException {
        return Files.move(file, file.resolveSibling(newFileName));
    }

    public static String stackTraceToString(Throwable e) {
        return Arrays.stream(e.getStackTrace())
                .map(StackTraceElement::toString)
                .collect(Collectors.joining("\n"));
    }
}
