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

import drrename.mime.FileTypeByMimeProvider;
import drrename.model.nfo.NfoRoot;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class NfoContentCoverChecker extends NfoContentChecker {
    @Override
    protected NfoFileCheckResultType doCheckNfoFile(Path moviePath, NfoRoot xmlModel) throws IOException {
        if(xmlModel.getMovie().getArt() == null || xmlModel.getMovie().getArt().getPoster() == null){
            return NfoFileCheckResultType.MISSING_POSTER;
        }
        if(verifyCoverFront(moviePath, xmlModel)){
            return NfoFileCheckResultType.VALID_POSTER;
        }
        return NfoFileCheckResultType.INVALID_POSTER;
    }

    static boolean verifyCoverFront(Path moviePath, NfoRoot xmlFileContent) {
        if (xmlFileContent == null || xmlFileContent.getMovie() == null || xmlFileContent.getMovie().getArt() == null || xmlFileContent.getMovie().getArt().getPoster() == null) {
            return false;
        }
        Path coverFront = moviePath.resolve(xmlFileContent.getMovie().getArt().getPoster());
        return Files.isRegularFile(coverFront) && Files.isReadable(coverFront) && new FileTypeByMimeProvider().getFileType(coverFront).startsWith("image");
    }
}
