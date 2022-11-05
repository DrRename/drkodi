package drrename;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RenameUtil {

    public static <T> T[] concatenate(T[] a, T[] b) {
        if(a == null){
            return b;
        }
        if(b == null){
            return a;
        }
        int aLen = a.length;
        int bLen = b.length;

        @SuppressWarnings("unchecked")
        T[] c = (T[]) Array.newInstance(a.getClass().getComponentType(), aLen + bLen);
        System.arraycopy(a, 0, c, 0, aLen);
        System.arraycopy(b, 0, c, aLen, bLen);

        return c;
    }

    public static Path rename(final Path file, String newFileName) throws IOException {
        return Files.move(file, file.resolveSibling(newFileName));
    }

    public static void deleteRecursively(Path path) throws IOException {
        try (var dirStream = Files.walk(path)) {
            dirStream
                    .map(Path::toFile)
                    .sorted(Comparator.reverseOrder())
                    .forEach(File::delete);
        }
    }

    public static String stackTraceToString(Throwable e) {
        return Arrays.stream(e.getStackTrace())
                .map(StackTraceElement::toString)
                .collect(Collectors.joining("\n"));
    }

    public static <T> List<T> getSubList(List<T> list, int size){
        if(list == null || list.size() <= size){
            return list;
        }
        return list.subList(0, size);
    }
}
