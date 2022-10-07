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

package drrename;

import drrename.strategy.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.ResourceBundle;
import java.util.Set;

@RequiredArgsConstructor
@Component
public class RenamingStrategies implements Iterable<RenamingStrategy> {

    private final Set<RenamingStrategy> renamingStrategies = new LinkedHashSet<>();

    private final ResourceBundle resourceBundle;

    @PostConstruct
    public void init(){
        renamingStrategies.add(new SimpleReplaceRenamingStrategy(resourceBundle));
        renamingStrategies.add(new RegexReplaceRenamingStrategy(resourceBundle));
        renamingStrategies.add(new CleanupStrategy(resourceBundle));
        renamingStrategies.add(new MediaMetadataRenamingStrategy(resourceBundle));
        renamingStrategies.add(new ToLowerCaseRenamingStrategy(resourceBundle));
        renamingStrategies.add(new SpaceToCamelCaseRenamingStrategy(resourceBundle));
        renamingStrategies.add(new UnhideStrategy(resourceBundle));
        renamingStrategies.add(new ExtensionFromMimeStrategy(resourceBundle));
        renamingStrategies.add(new CapitalizeFirstStrategy(resourceBundle));
    }

    @Override
    public Iterator<RenamingStrategy> iterator() {
        return renamingStrategies.iterator();
    }
}
