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

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class SubdirsCheckService extends CheckService<CheckResult> {

    @Override
    public CheckResult checkPath(Path path) throws IOException {
        if (getSubdirs(path).isEmpty())
            return new CheckResult("No sub-directories");
        return new CheckResult("Sub-directories found");
    }

    @Override
    public KodiTreeItem buildChildItem(CheckResult checkResult) {
        return new KodiTreeItem(new CheckResultTreeItemContent(checkResult));
    }

    static List<Path> getSubdirs(Path path) throws IOException {
        List<Path> subdirs = new ArrayList<>();
        try (DirectoryStream<Path> ds = Files.newDirectoryStream(path)) {
            for (Path child : ds) {
                if (Files.isDirectory(child)) {
                    subdirs.add(child.getFileName());
                }
            }
        }
        return subdirs;
    }
}
