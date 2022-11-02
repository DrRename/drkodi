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

package drrename.kodi;

import drrename.kodi.nfo.NfoFileNameChecker;
import drrename.kodi.nfo.NfoFileNameFixer;
import drrename.model.RenamingPath;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Getter
@Slf4j
public class NfoFileNameIssue extends FxKodiIssue<NfoFileNameCheckResult> {

    private NfoFileNameCheckResult checkResult;

    public NfoFileNameIssue(RenamingPath moviePath) {
        super(moviePath);
    }

    @Override
    public NfoFileNameCheckResult checkStatus() {
//        log.debug("Triggering check status on thread {}", Thread.currentThread());
        return new NfoFileNameChecker().checkDir(getRenamingPath().getOldPath());
    }

    @Override
    public void fix(NfoFileNameCheckResult result) throws FixFailedException {
//        log.debug("Triggering fixing on thread {}", Thread.currentThread());
        new NfoFileNameFixer(result).fix(getMovieName());
    }

    @Override
    public void updateStatus(NfoFileNameCheckResult result) {
        this.checkResult = result;

    }

    @Override
    public String getHelpText() {
        return null;
    }

    @Override
    public String getIdentifier() {
        return "NFO File Name";
    }
}
