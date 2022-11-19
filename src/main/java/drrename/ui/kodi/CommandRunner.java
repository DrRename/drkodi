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

package drrename.ui.kodi;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;

@Slf4j
public class CommandRunner {

    public void runCommand(String[] command) {
        log.debug("Running {}", Arrays.toString(command));
        try {
            Process process = Runtime.getRuntime().exec(command);
            final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                log.debug("Process out: {}", line);
            }
            final BufferedReader bufferedReader2 = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            String line2;
            while ((line2 = bufferedReader2.readLine()) != null) {
                log.debug("Process err: {}", line2);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
