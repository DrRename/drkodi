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
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Executor;

@Slf4j
public class SubdirsTreeItemValue extends KodiTreeItemValue {

    private final ListProperty<Path> subdirs;

    public SubdirsTreeItemValue(RenamingPath moviePath, Executor executor) {
        super(moviePath, false, executor);
        subdirs = new SimpleListProperty<>();
        init();
        updateStatus();
    }

    private void init(){
        subdirs.addListener((ListChangeListener<Path>) c -> {
            while(c.next()){
                setWarning(calculateWarning(c.getList()));
                setCanFix(isWarning());
            }
        });
    }

    @Override
    protected String updateIdentifier() {
        return "Subdirs";
    }

    @Override
    protected void updateStatus() {
        try {
            setSubdirs(FXCollections.observableArrayList(getSubdirs(getRenamingPath().getOldPath())));
        } catch (IOException e) {
            log.error(e.getLocalizedMessage(), e);
        }
    }

    protected List<Path> getSubdirs(Path path) throws IOException {
        List<Path> subdirs = new ArrayList<>();
        try (DirectoryStream<Path> ds = Files.newDirectoryStream(path)) {
            for (Path child : ds) {
                if (Files.isDirectory(child)) {
                    subdirs.add(child);
                }
            }
        }
        return subdirs;
    }

    protected List<Path> getChildFilesAndFolders(Path path) throws IOException {
        List<Path> subdirs = new ArrayList<>();
        try (DirectoryStream<Path> ds = Files.newDirectoryStream(path)) {
            for (Path child : ds) {
                    subdirs.add(child);
            }
        }
        return subdirs;
    }

    protected String countChildFilesAndFolders(Path path) {
        try {
            return Integer.toString(getChildFilesAndFolders(path).size());
        } catch (IOException e) {
            return e.getLocalizedMessage();
        }
    }

    private boolean calculateWarning(Collection<? extends Path> subdirs){
        return !subdirs.isEmpty();
    }

    @Override
    protected String updateMessage(Boolean newValue) {
        return newValue ? "Has subdirs: " + getSubdirs().stream().map(p -> p.getFileName() + "[" + countChildFilesAndFolders(p) + "]").toList() : "No subdirs";
    }

    @Override
    public void fix() throws FixFailedException {
        for(Path p : getSubdirs()){
            try (var dirStream = Files.walk(p)) {
                dirStream
                        .map(Path::toFile)
                        .sorted(Comparator.reverseOrder())
                        .forEach(File::delete);
            } catch (IOException e) {
                throw new FixFailedException(e);
            }
        }
    }



    // Getter / Setter //


    public ObservableList<Path> getSubdirs() {
        return subdirs.get();
    }

    public ListProperty<Path> subdirsProperty() {
        return subdirs;
    }

    public void setSubdirs(ObservableList<Path> subdirs) {
        this.subdirs.set(subdirs);
    }
}
