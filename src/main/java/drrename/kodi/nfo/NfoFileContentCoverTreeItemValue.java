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

import drrename.model.RenamingPath;
import lombok.extern.slf4j.Slf4j;

import java.nio.file.Path;
import java.util.concurrent.Executor;

@Slf4j
public class NfoFileContentCoverTreeItemValue extends NfoFileContentTreeItemValue {

    public NfoFileContentCoverTreeItemValue(RenamingPath path, Executor executor) {
        super(path, false, executor);
    }

    @Override
    protected String updateIdentifier() {
        return "NFO Poster";
    }

    @Override
    protected NfoFileContentType parseNfoFile(Path child) {
        var checker = new NfoContentCoverChecker();
        var result = checker.checkNfoFile(child);
        setNfoFiles(checker.getNfoFiles());
        return result;
    }
}
