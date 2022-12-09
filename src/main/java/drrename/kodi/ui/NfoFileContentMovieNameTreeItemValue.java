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

package drrename.kodi.ui;

import drrename.commons.RenamingPath;
import drrename.kodi.FixFailedException;
import drrename.kodi.NfoFileCheckResult;
import drrename.kodi.WarningsConfig;
import drrename.kodi.nfo.NfoContentTitleChecker;
import drrename.kodi.nfo.NfoFileParser;
import drrename.kodi.nfo.NfoFileTitleExtractor;
import drrename.util.DrRenameUtil;
import javafx.scene.Node;
import javafx.scene.control.Button;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.SystemUtils;

import java.nio.file.Path;
import java.util.Objects;
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
