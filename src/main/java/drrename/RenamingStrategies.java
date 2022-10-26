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
        var renamingConfig = new SimpleRenamingConfig();
        renamingStrategies.add(new SimpleReplaceRenamingStrategy(resourceBundle, renamingConfig));
        renamingStrategies.add(new RegexReplaceRenamingStrategy(resourceBundle, renamingConfig));
        renamingStrategies.add(new CleanupStrategy(resourceBundle, renamingConfig));
        renamingStrategies.add(new MediaMetadataRenamingStrategy(resourceBundle, renamingConfig));
        renamingStrategies.add(new ToLowerCaseRenamingStrategy(resourceBundle, renamingConfig));
        renamingStrategies.add(new SpaceToCamelCaseRenamingStrategy(resourceBundle, renamingConfig));
        renamingStrategies.add(new UnhideStrategy(resourceBundle, renamingConfig));
        renamingStrategies.add(new ExtensionFromMimeStrategy(resourceBundle, renamingConfig));
        renamingStrategies.add(new CapitalizeFirstStrategy(resourceBundle, renamingConfig));
    }

    @Override
    public Iterator<RenamingStrategy> iterator() {
        return renamingStrategies.iterator();
    }
}
