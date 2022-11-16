/*
 *     Dr.Rename - A Minimalistic Batch Renamer
 *
 *     Copyright (C) 2022
 *
 *     This file is part of Dr.Rename.
 *
 *     You can redistribute it and/or modify it under the terms of the GNU Affero
 *     General Public License as published by the Free Software Foundation, either
 *     version 3 of the License, or (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful, but WITHOUT
 *     ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 *     FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License
 *     for more details.
 *
 *     You should have received a copy of the GNU Affero General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package drrename.util;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class DrRenameUtil {

    public static <T> T[] concatenate(T[] a, T[] b) {
        if (a == null) {
            return b;
        }
        if (b == null) {
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

    public static <T> List<T> getSubList(List<T> list, int size) {
        if (list == null || list.size() <= size) {
            return list;
        }
        return list.subList(0, size);
    }

    /**
     * <p>
     * Checks, whether the provided path points to a case-sensitive file system or not.
     * </p>
     * <p>
     * It does so by creating two temp-files in the provided  path and comparing them. Note that you need to have write
     * access to the provided path.
     *
     * @param pathToFileSystem path to the file system to test
     * @param tmpFileName      name of the temp file that is created
     * @return {@code true}, if the provided path points to a case-sensitive file system; {@code false} otherwise
     * @throws IOException if IO operation fails
     */
    public static boolean fileSystemIsCaseSensitive(Path pathToFileSystem, String tmpFileName) throws IOException {

        Path a = Files.createFile(pathToFileSystem.resolve(Paths.get(tmpFileName.toLowerCase())));
        Path b = null;
        try {
            b = Files.createFile(pathToFileSystem.resolve(Paths.get(tmpFileName.toUpperCase())));
        } catch (FileAlreadyExistsException e) {
            return false;
        } finally {
            Files.deleteIfExists(a);
            if (b != null) Files.deleteIfExists(b);
        }
        return true;
    }
}
