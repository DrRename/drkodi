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

package drkodi.ui;

import drkodi.NfoFileCheckResult;
import drkodi.WarningsConfig;
import drkodi.nfo.NfoContentTitleChecker;
import drkodi.nfo.NfoFileTitleExtractor;
import drrename.commons.RenamingPath;
import lombok.extern.slf4j.Slf4j;

import java.nio.file.Path;
import java.util.concurrent.Executor;

@Slf4j
public class NfoFileContentMovieNameTreeItemValue extends NfoFileContentTreeItemValue {

    public NfoFileContentMovieNameTreeItemValue(RenamingPath moviePath, Executor executor, WarningsConfig warningsConfig) {
        super(moviePath, executor, warningsConfig);
    }

    @Override
    public NfoFileCheckResult checkStatus() {
        return new NfoContentTitleChecker().checkDir(getRenamingPath().getOldPath());
    }

    protected String getInfoFromNfo(Path nfoFile){
        return new NfoFileTitleExtractor(getNfoFileParser()).parseNfoFile(nfoFile);
    }

    @Override
    public String getHelpText() {
        return null;
    }

    @Override
    public String getIdentifier() {
        return "NFO File Content Movie Name";
    }
}
