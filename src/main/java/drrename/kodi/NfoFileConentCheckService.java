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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;

import java.io.IOException;
import java.nio.charset.MalformedInputException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;

@RequiredArgsConstructor
@Slf4j
public class NfoFileConentCheckService implements CheckService {

    private final Path moviePath;

    @Override
    public NfoFileContentCheckResult calculate(Path path) {
        XmlMapper mapper = new XmlMapper();
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

        try (DirectoryStream<Path> ds = Files.newDirectoryStream(path)) {
            for (Path child : ds) {
                String extension = FilenameUtils.getExtension(child.getFileName().toString());
                if (Files.isRegularFile(child) && "nfo".equalsIgnoreCase(extension)) {
                    // we look only at the first file found
                    try {
                        NfoFileXmlModel xmlFileContent = mapper.readValue(child.toFile(), NfoFileXmlModel.class);
                        log.debug("XML content: {}", xmlFileContent);
                        if(!verifyTitle(xmlFileContent)){
                            return new NfoFileContentCheckResult("NFO title mismatch", true, child);
                        }
                        if(!verifyYear(xmlFileContent)){
                            return new NfoFileContentCheckResult("NFO year mismatch", true, child);
                        }
                        if(!verifyCoverFront(xmlFileContent)){
                            return new NfoFileContentCheckResult("NFO front-cover not readable", true, child);
                        }
                        return new NfoFileContentCheckResult("XML NFO", false, child);
                    } catch (JsonParseException e) {
                        log.debug("Not an XML file, check only one line for URL");
                        try {
                            String content = Files.readString(child);
                            if (content == null) {
                                return new NfoFileContentCheckResult("Empty NFO", true, child);
                            }
                            log.debug("File content: {}", content);
                            if (content.contains("imdb")) {
                                return new NfoFileContentCheckResult("Single line NFO (imdb)", false, child);
                            } else {
                                return new NfoFileContentCheckResult("Unknown NFO content (" + content + ")", true, child);
                            }
                        } catch (MalformedInputException ee) {
                            log.debug(ee.getLocalizedMessage(), ee);
                            log.error("Invalid nfo file {}", child);
                        }
                    }
                }
            }
        } catch (IOException e) {
            log.error(e.getLocalizedMessage(), e);
            return new NfoFileContentCheckResult(e.getLocalizedMessage(), true, null);
        }
        return new NfoFileContentCheckResult("No NFO file", true, null);
    }



    private boolean verifyYear(NfoFileXmlModel xmlFileContent) {
        return xmlFileContent.year != null && moviePath.getFileName().toString().endsWith("(" + xmlFileContent.year + ")");
    }

    private boolean verifyTitle(NfoFileXmlModel xmlFileContent) {
        return xmlFileContent.title != null && moviePath.getFileName().toString().startsWith(xmlFileContent.title);
    }

    private boolean verifyCoverFront(NfoFileXmlModel xmlFileContent) {
        Path coverFront = moviePath.resolve(xmlFileContent.art.poster);
        return Files.isRegularFile(coverFront) && Files.isReadable(coverFront);
    }
}
