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

package drrename.kodi;

import drrename.model.RenamingPath;
import drrename.strategy.RegexReplaceRenamingStrategy;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Executor;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class MultipleSpacesTreeItemValue extends KodiTreeItemValue {

    public MultipleSpacesTreeItemValue(RenamingPath path, Executor executor) {
        super(path, true, executor);
        updateStatus();
    }

    @Override
    protected String updateIdentifier() {
        return "Movie Name Multiple Spaces";
    }

    @Override
    protected String updateMessage(Boolean newValue) {
        return isWarning() ? "Multiple spaces in path" : "No multiple spaces in path";
    }

    protected boolean calculateWarning() {
        return test(getRenamingPath().getMovieName());
    }

    public boolean test(String fileName) {
        Pattern p = Pattern.compile("\\s{2,}");
        Matcher m = p.matcher(fileName);
        return m.find();
    }

    protected void updateStatus() {
        setWarning(calculateWarning());
        setCanFix(isWarning());
    }

    public void fix() {
        getRenamingPath().rename(new RegexReplaceRenamingStrategy(null, null).setReplacementStringFrom("\\s+").setReplacementStringTo(" "));
    }


}
