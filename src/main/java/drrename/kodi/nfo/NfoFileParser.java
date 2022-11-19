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

package drrename.kodi.nfo;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.deser.DeserializationProblemHandler;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import drrename.model.nfo.NfoRoot;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.MalformedInputException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

@Slf4j
public class NfoFileParser {

    private final XmlMapper xmlMapper;

    public NfoFileParser() {
        xmlMapper = new XmlMapper();
        xmlMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        xmlMapper.addHandler(new DeserializationProblemHandler() {
            @Override
            public boolean handleUnknownProperty(DeserializationContext ctxt, JsonParser p, JsonDeserializer<?> deserializer, Object beanOrClass, String propertyName) throws IOException {
                if (beanOrClass instanceof NfoRoot nfoRoot) {
                    String valueAsString = p.readValueAs(String.class);
                    try {
                        URL url = new URL(valueAsString);
                        url.toURI();
                        nfoRoot.setUrl(valueAsString);
                        return true;
                    } catch (Exception e) {
                        return false;
                    }
                }
                return super.handleUnknownProperty(ctxt, p, deserializer, beanOrClass, propertyName);
            }
        });
    }

    public NfoRoot parse(Path filePath) throws IOException {
        long lineCount;
        try (Stream<String> stream = Files.lines(filePath, Charset.forName("UTF-8"))) {
            lineCount = stream.filter(s -> !s.isBlank()).count();
        }catch (Exception e){
            log.debug("Cannot count lines, reason: {}", e.getLocalizedMessage());
            if(e.getCause() instanceof MalformedInputException){
                return null;
            }
            throw new IOException(e);
        }
        if(lineCount == 1){
            NfoRoot root = new NfoRoot();
            root.setUrl(Files.readString(filePath));
            return root;
        }
        String content = "<NfoRoot>" + String.join("", Files.readAllLines(filePath)) + "</NfoRoot>";

        try {
            return xmlMapper.readValue(content, NfoRoot.class);
        } catch (MismatchedInputException e) {
            log.debug(e.getLocalizedMessage());
            return null;
        }
    }
}
