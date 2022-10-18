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

package drrename.kodi.treeitem.content;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import drrename.kodi.treeitem.KodiTreeItem;
import drrename.kodi.treeitem.content.check.CheckService;
import drrename.kodi.treeitem.content.check.NfoFileContentCheckResult;
import drrename.model.NfoFileXmlModel;
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
public class NfoFileContentCheckService extends CheckService<NfoFileContentCheckResult> {

    @Override
    public KodiTreeItem buildChildItem(NfoFileContentCheckResult checkResult) {
        return new KodiTreeItem(new NfoFileContentTreeItemContent(checkResult));
    }

    public NfoFileContentCheckResult checkPath(Path path) throws IOException {
        XmlMapper mapper = new XmlMapper();
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

        try (DirectoryStream<Path> ds = Files.newDirectoryStream(path)) {
            for (Path child : ds) {
                String extension = FilenameUtils.getExtension(child.getFileName().toString());
                if (Files.isRegularFile(child) && "nfo".equalsIgnoreCase(extension)) {
                    // we look only at the first file found
                    try {
                        NfoFileXmlModel xmlFileContent = mapper.readValue(child.toFile(), NfoFileXmlModel.class);
                        if (!verifyTitle(path, xmlFileContent)) {
                            return new NfoFileContentCheckResult("NFO title mismatch", child, true);
                        }
                        if (!verifyYear(path, xmlFileContent)) {
                            return new NfoFileContentCheckResult("NFO year mismatch", child, true);
                        }
                        if (!verifyCoverFront(path, xmlFileContent)) {
                            return new NfoFileContentCheckResult("NFO front-cover not readable", child, true);
                        }
                        return new NfoFileContentCheckResult("XML NFO", child, false);
                    } catch (JsonParseException e) {
                        log.debug("{} for {}", e.getLocalizedMessage(), child);
                        try {
                            String content = Files.readString(child);
                            if (content == null) {
                                return new NfoFileContentCheckResult("Empty NFO", child, true);
                            }
                            if (content.contains("imdb")) {
                                return new NfoFileContentCheckResult("Single line NFO (imdb)", child, false);
                            } else {
                                return new NfoFileContentCheckResult("Unknown NFO content", child, true);
                            }
                        } catch (MalformedInputException ee) {
                            log.debug("{} for {}", ee.getLocalizedMessage(), child);
                            log.debug("{} for path {}", ee.getLocalizedMessage(), path);
                            return new NfoFileContentCheckResult("Invalid NFO content", child, true);
                        }
                    }
                }
            }
            return new NfoFileContentCheckResult("No NFO file", null, true);
        }
    }


    private boolean verifyYear(Path moviePath, NfoFileXmlModel xmlFileContent) {
        return xmlFileContent.getYear() != null && moviePath.getFileName().toString().endsWith("(" + xmlFileContent.getYear() + ")");
    }

    private boolean verifyTitle(Path moviePath, NfoFileXmlModel xmlFileContent) {
        return xmlFileContent.getTitle() != null && moviePath.getFileName().toString().startsWith(xmlFileContent.getTitle());
    }

    private boolean verifyCoverFront(Path moviePath, NfoFileXmlModel xmlFileContent) {
        if (xmlFileContent.getArt() == null || xmlFileContent.getArt().getPoster() == null) {
            return false;
        }
        Path coverFront = moviePath.resolve(xmlFileContent.getArt().getPoster());
        return Files.isRegularFile(coverFront) && Files.isReadable(coverFront);
    }
}
