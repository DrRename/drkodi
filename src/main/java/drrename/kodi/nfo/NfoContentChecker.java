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

import drrename.model.nfo.NfoRoot;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Slf4j
public abstract class NfoContentChecker extends AbstractNfoFileChecker {

    public NfoFileCheckResultType checkFile(String movieName, Path nfoFile){
        if(Files.notExists(nfoFile)){
            return NfoFileCheckResultType.NO_FILE;
        }
        if(!Files.isReadable(nfoFile)){
            return NfoFileCheckResultType.EXCEPTION;
        }
        if(!Files.isRegularFile(nfoFile)){
            return NfoFileCheckResultType.NOT_A_FILE;
        }
        try {
            NfoRoot xmlModel = new NfoFileParser().parse(nfoFile);
            if(xmlModel == null || (xmlModel.getMovie() == null && xmlModel.getUrl() == null)){
                return NfoFileCheckResultType.INVALID_FILE;
            }
            if(xmlModel.getMovie() == null && xmlModel.getUrl() != null){
                return NfoFileCheckResultType.URL_ONLY_FILE;
            }
            return doCheckNfoFile(nfoFile.getParent(), xmlModel);
        } catch (IOException e) {
            log.debug(e.getLocalizedMessage());
            return NfoFileCheckResultType.EXCEPTION;
        }
    }

    protected abstract NfoFileCheckResultType doCheckNfoFile(Path moviePath, NfoRoot xmlModel) throws IOException;
}
