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

import drrename.kodi.NfoRoot;

import java.nio.file.Path;

public class NfoContentTitleChecker extends NfoContentChecker {

    @Override
    protected NfoFileCheckResultType doCheckNfoFile(Path moviePath, NfoRoot xmlModel) {
        if(xmlModel.getMovie().getTitle() == null){
            return NfoFileCheckResultType.MISSING_TITLE;
        }
        if(verifyTitle(moviePath, xmlModel)){
            return NfoFileCheckResultType.VALID_TITLE;
        }
        return NfoFileCheckResultType.INVALID_TITLE;
    }

    public static boolean verifyTitle(Path moviePath, NfoRoot xmlFileContent) {
        return verifyTitle(moviePath.getFileName().toString(), xmlFileContent);
    }

    public static boolean verifyTitle(String movieTitle, NfoRoot xmlFileContent) {
        return xmlFileContent != null && xmlFileContent.getMovie() != null && xmlFileContent.getMovie().getTitle() != null && verifyTitle(movieTitle, xmlFileContent.getMovie().getTitle());
    }

    public static boolean verifyTitle(String movieTitle, String nfoMovieTitle) {
        String substring = movieTitle;
        if(movieTitle.contains("(")){
            substring = movieTitle.substring(0, movieTitle.indexOf(" ("));
        }
        return substring.equals(nfoMovieTitle);
    }
}
