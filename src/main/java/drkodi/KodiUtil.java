/*
 *     Dr.Rename - A Minimalistic Batch Renamer
 *
 *     Copyright (C) 2022
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Affero General Public License as
 *     published by the Free Software Foundation, either version 3 of the
 *     License, or (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Affero General Public License for more details.
 *
 *     You should have received a copy of the GNU Affero General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package drkodi;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import drkodi.data.movie.MovieData;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class KodiUtil {

    public static String buildFolderNameString(String movieTitle, Integer movieYear){
        if(movieYear == null){
            return movieTitle;
        }
        return movieTitle + " (" + movieYear + ")";
    }

    public static String getMovieNameFromDirectoryName(String directoryName){
        if(directoryName == null){
            return null;
        }
        if(directoryName.contains("(")) {
            return directoryName.substring(0, directoryName.indexOf("(")).trim();
        }
        return directoryName;
    }

    public static Integer getMovieYearFromDirectoryName(String directoryName) {
        if(directoryName == null){
            return null;
        }
        Pattern p = Pattern.compile("\\(\\d+\\)");
        Matcher m = p.matcher(directoryName);
        if(m.find()) {
            return Integer.parseInt(m.group().replaceAll("\\(", "").replaceAll("\\)", ""));
        }
        return null;
    }

    public static Path getImagePathFromNfo(Path nfoFile) throws IOException {
        XmlMapper mapper = new XmlMapper();
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        try {
            NfoMovie xmlFileContent = mapper.readValue(nfoFile.toFile(), NfoMovie.class);
            if(xmlFileContent.getArt() != null && xmlFileContent.getArt().getPoster() != null)
                return nfoFile.getParent().resolve(xmlFileContent.getArt().getPoster());
        } catch (JsonParseException e) {
            log.debug("Failed to deserialize image path from {})", nfoFile);
        }
        return null;
    }


    public static boolean isEmpty(Path path) throws IOException {
        if (Files.isDirectory(path)) {
            try (DirectoryStream<Path> directory = Files.newDirectoryStream(path)) {
                return !directory.iterator().hasNext();
            }
        }
        return false;
    }

    public static Path getDefaultNfoPath(MovieData movieData){
        return getDefaultNfoPath(movieData.getType(), movieData.getRenamingPath().getOldPath());
    }

    public static Path getDefaultNfoPath(MovieData.Type type, Path movieDirectory){
        if(MovieData.Type.MOVIE.equals(type)) {
            return Paths.get(movieDirectory.toString(), "movie.nfo");
        }
        else if(MovieData.Type.TV_SERIES.equals(type)){
            return Paths.get(movieDirectory.toString(), "tvshow.nfo");
        } else
            throw new RuntimeException("Type not set");

    }

    public static Path getDefaultImagePath(MovieData movieData){
        return getDefaultImagePath(movieData.getRenamingPath().getOldPath());
    }

    public static Path getDefaultImagePath(Path moviePath){
        return Paths.get(moviePath.toString(), "folder" + ".jpg");
    }

    public static List<Path> getSubdirs(Path path) throws IOException {
        List<Path> subdirs = new ArrayList<>();
        try (DirectoryStream<Path> ds = Files.newDirectoryStream(path)) {
            for (Path child : ds) {
                if (Files.isDirectory(child)) {
                    subdirs.add(child);
                }
            }
        }
        return subdirs;
    }

    public static List<Path> findAllVideoFiles(Path directory, int maxDepth) throws IOException {
        List<Path> result = new ArrayList<>();
        if (Files.isDirectory(directory)) {
            try (DirectoryStream<Path> ds = Files.newDirectoryStream(directory)) {
                int depth = 0;
                result.addAll(descend(ds,  ++depth, maxDepth));
            }
        }
        return result;
    }

    static List<Path> descend(DirectoryStream<Path> directory, int currentDepth, int maxDepth) throws IOException {
        List<Path> result = new ArrayList<>();
        for (Path child : directory) {
            if (Files.isRegularFile(child)) {
                String extension = FilenameUtils.getExtension(child.getFileName().toString());
                if ("sub".equalsIgnoreCase(extension) || "idx".equalsIgnoreCase(extension)) {
//                        log.debug("Ignore sub/ idx files for now");
                    continue;
                }
                var mediaType = new FileTypeByMimeProvider().getFileType(child);
                if (mediaType.startsWith("video")) {
                    result.add(child);
                }
            } else if(Files.isDirectory(child) && ++currentDepth <= maxDepth){
                try (DirectoryStream<Path> ds = Files.newDirectoryStream(child)) {
                    result.addAll(descend(ds,  currentDepth, maxDepth));
                }
            }
        }
        return result;
    }
}
