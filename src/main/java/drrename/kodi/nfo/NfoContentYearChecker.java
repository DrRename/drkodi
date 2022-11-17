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

import java.io.IOException;
import java.nio.file.Path;

public class NfoContentYearChecker extends NfoContentChecker {

    @Override
    protected NfoFileCheckResultType doCheckNfoFile(Path moviePath, NfoRoot xmlModel) throws IOException {
        if(xmlModel.getMovie().getYear() == null){
            return NfoFileCheckResultType.MISSING_YEAR;
        }
        if(verifyYear(moviePath, xmlModel)){
            return NfoFileCheckResultType.VALID_YEAR;
        }
        return NfoFileCheckResultType.INVALID_YEAR;
    }

    private boolean verifyYear(Path moviePath, NfoRoot xmlFileContent) {
        return xmlFileContent != null &&
                xmlFileContent.getMovie() != null &&
                xmlFileContent.getMovie().getYear() != null &&
                moviePath.getFileName().toString().endsWith("(" + xmlFileContent.getMovie().getYear() + ")");
    }



}
