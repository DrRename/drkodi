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
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import drkodi.NfoMovieRoot;
import drkodi.NfoRoot;
import drkodi.NfoTvShowRoot;
import drkodi.data.movie.MovieData;
import lombok.AllArgsConstructor;
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
public class NfoFileParser2 {


    public record ParseResult(NfoRoot nfoRoot, MovieData.Type type) {

    }

    public NfoFileParser2() {

    }

    public ParseResult parse(Path filePath) throws Exception {

        String firstLine;
        try (Stream<String> stream = Files.lines(filePath, StandardCharsets.UTF_8)) {
            firstLine = stream.findFirst().orElse("");
        }catch (Exception e){
            log.debug("Cannot read file", e);
            throw new IOException(e);
        }

        if("<movie>".equals(firstLine.toLowerCase().trim())){
            var try2 = new NfoFileParser(MovieData.Type.MOVIE).parse(filePath);
            return new ParseResult(try2, MovieData.Type.MOVIE);
        }

        if("<tvshow>".equals(firstLine.toLowerCase().trim())){
            var try1 = new NfoFileParser(MovieData.Type.TV_SERIES).parse(filePath);
            return new ParseResult(try1, MovieData.Type.TV_SERIES);
        }

        throw new IOException("Failed to determine type from " + firstLine);
    }
}
