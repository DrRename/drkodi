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

package drkodi.nfo;

import drkodi.NfoMovieRoot;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.channels.ClosedByInterruptException;
import java.nio.file.Path;

@Slf4j
@RequiredArgsConstructor
public abstract class AbstractNfoFileExtractor {

    private final NfoFileParser nfoFileParser;

    public String parseNfoFile(Path nfoFile){
        try {
            var xmlModel = nfoFileParser.parse(nfoFile);
            if (xmlModel != null && xmlModel.getMovie() != null)
                return parseNfoModel(xmlModel);
        } catch (ClosedByInterruptException e){
            // it's ok.
        } catch (IOException e) {
            log.error(e.getLocalizedMessage(), e);
        }
        return null;
    }

    protected abstract String parseNfoModel(NfoMovieRoot xmlModel);
}
