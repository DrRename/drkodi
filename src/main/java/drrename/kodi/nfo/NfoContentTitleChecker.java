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

import drrename.Strings;
import drrename.model.nfo.NfoRoot;

import java.io.IOException;
import java.nio.file.Path;

public class NfoContentTitleChecker  extends NfoContentChecker {
    @Override
    protected NfoFileContentType doCheckNfoFile(Path moviePath, NfoRoot xmlModel) throws IOException {
        if(xmlModel.getMovie().getTitle() == null){
            return NfoFileContentType.MISSING_TITLE;
        }
        if(verifyTitle(moviePath, xmlModel)){
            return NfoFileContentType.VALID_TITLE;
        }
        return NfoFileContentType.INVALID_TITLE;
    }

    static boolean verifyTitle(Path moviePath, NfoRoot xmlFileContent) {
        return xmlFileContent != null && xmlFileContent.getMovie() != null && xmlFileContent.getMovie().getTitle() != null && Strings.startsWithIgnoreCase(moviePath.getFileName().toString(),getSimplifiedName(xmlFileContent.getMovie().getTitle()));
    }

    private static String getSimplifiedName(String title) {
        // NFO title may be more specific, e.g. might contain punctuation
        return title.replaceAll(",", "");
    }
}
