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

package drrename.kodi;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import drrename.kodi.NfoFileXmlModel;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class KodiUtil {

    public static String getMovieNameFromDirectoryName(String directoryName){
        if(directoryName.contains("(")) {
            return directoryName.substring(0, directoryName.indexOf("(")).trim();
        }
        return directoryName;
    }

    public static Integer getMovieYearFromDirectoryName(String directoryName) {
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
            NfoFileXmlModel xmlFileContent = mapper.readValue(nfoFile.toFile(), NfoFileXmlModel.class);
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
}
