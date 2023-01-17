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

package drkodi.nfo;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.deser.DeserializationProblemHandler;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import drkodi.NfoMovieRoot;
import drkodi.NfoRoot;
import drkodi.NfoTvShowRoot;
import drkodi.data.movie.MovieData;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.URL;
import java.nio.channels.ClosedByInterruptException;
import java.nio.charset.MalformedInputException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

@Slf4j
public class NfoFileParser {

    private final XmlMapper xmlMapper;

    private final Class<? extends NfoRoot> clazz;

    public NfoFileParser(MovieData.Type type) {
        if(MovieData.Type.MOVIE.equals(type)){
            this.clazz = NfoMovieRoot.class;
        }
        else if(MovieData.Type.TV_SERIES.equals(type)){
            this.clazz = NfoTvShowRoot.class;
        }
        else {
            throw new RuntimeException("Unknown type");
        }
        xmlMapper = new XmlMapper();
//        xmlMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        xmlMapper.addHandler(new DeserializationProblemHandler() {
            @Override
            public boolean handleUnknownProperty(DeserializationContext ctxt, JsonParser p, JsonDeserializer<?> deserializer, Object beanOrClass, String propertyName) throws IOException {
                if (clazz.isAssignableFrom(beanOrClass.getClass())) {
                    String valueAsString = p.readValueAs(String.class);
                    try {
                        URL url = new URL(valueAsString);
                        url.toURI();
                        clazz.cast(beanOrClass).setUrl(valueAsString);
                        return true;
                    } catch (Exception e) {
                        log.debug(e.getLocalizedMessage(), e);
                        return false;
                    }
                }
                return super.handleUnknownProperty(ctxt, p, deserializer, beanOrClass, propertyName);
            }
        });
    }

    public NfoRoot parse(Path filePath) throws Exception {
        long lineCount;
        try (Stream<String> stream = Files.lines(filePath, StandardCharsets.UTF_8)) {
            lineCount = stream.filter(s -> !s.isBlank()).count();

        }catch (Exception e){
            log.debug("Cannot count lines, reason: {}", e.getLocalizedMessage());
            if(e.getCause() instanceof MalformedInputException || e.getCause() instanceof ClosedByInterruptException){
                return null;
            }
            throw new IOException(e);
        }
        if(lineCount == 1){
            NfoRoot root = clazz.getConstructor().newInstance();
            root.setUrl(Files.readString(filePath));
            return root;
        }
        String content = "<Test>" + String.join("", Files.readAllLines(filePath)).trim() + "</Test>";

        try {
            var result = xmlMapper.readValue(content, clazz);
            return result;
        } catch (MismatchedInputException e) {
            log.debug(e.getLocalizedMessage());
            return null;
        }
    }
}
