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

package drrename.kodi;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import static java.nio.file.StandardOpenOption.APPEND;

@Slf4j
public class NfoFileWriter {

    public void write(NfoRoot data, Path outFile) throws IOException {
        if(outFile == null){
            throw new NullPointerException("Path is null");
        }
        log.debug("Writing NFO data to {}", outFile);
        XmlMapper xmlMapper = getXmlMapper();

        Path tmpPath = outFile.resolveSibling(outFile.getFileName().toString() + ".tmp");
        log.debug("Writing to tmp path {} ", tmpPath);
        xmlMapper.writeValue(tmpPath.toFile(), data.movie);
        // append URL line
        if (data.url != null)
            Files.writeString(tmpPath,data.url,APPEND);
        log.debug("Moving from tmp path");
        Files.move(tmpPath, outFile, StandardCopyOption.REPLACE_EXISTING);
        log.info("NFO file written to {}", outFile);

    }

    private XmlMapper getXmlMapper() {
        XmlMapper xmlMapper = new XmlMapper();
        xmlMapper.enable(SerializationFeature.INDENT_OUTPUT);
        return xmlMapper;
    }
}
