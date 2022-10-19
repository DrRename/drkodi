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

package drrename.kodi.treeitem.content.check;

import drrename.kodi.treeitem.content.KodiTreeItemContent;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MultipleSpacesTreeItemContent extends CheckResultTreeItemContent<MultipleSpacesCheckResult> {

    private boolean warning;

    private final Path path;

    public MultipleSpacesTreeItemContent(Path path) {
        super(null);
        this.path = path;
        Pattern p = Pattern.compile("\\s+");
        Matcher m = p.matcher(path.getFileName().toString());
        warning = m.find();
        setFixable(hasWarning());
    }

    @Override
    public boolean hasWarning() {
        return warning;
    }

    public void setWarning(boolean warning) {
        this.warning = warning;
    }

    public void fix() throws FixFailedException {
        try {
            Path targetPath = path.getParent().resolve(Path.of(path.getFileName().toString().replaceAll("\\s+", " ")));
            Files.move(path, targetPath);
            setFixable(false);
            setWarning(false);
        } catch (IOException e) {
            throw new FixFailedException(e);
        }
    }

    @Override
    public String toString() {
        return hasWarning() ? "Multiple spaces in path" : "No multiple spaces in path";
    }
}
