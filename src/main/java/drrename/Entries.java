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

package drrename;

import drrename.config.AppConfig;
import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ResourceBundle;
import java.util.concurrent.Executor;
import java.util.function.Predicate;

@Slf4j
@Component
public class Entries {

    static final String LOADED = "mainview.status.loaded.text";

    static final String LOADED_TYPES = "mainview.status.loaded.filetypes.text";

    static final String WILL_RENAME = "mainview.status.willrename.text";

    static final String WILL_RENAME_TYPES = "mainview.status.willrename.filetypes.text";

    static final String RENAMED = "mainview.status.renamed.text";

    static final String RENAMED_TYPES = "mainview.status.renamed.filetypes.text";

    private final ResourceBundle resourceBundle;

    private final AppConfig appConfig;

    private final ListProperty<RenamingControl> entries;

    private final ListProperty<RenamingControl> entriesFiltered;

    private final FilteredList<RenamingControl> entriesWillRename;

    private final FilteredList<RenamingControl> loadedImageEntries;

    private final FilteredList<RenamingControl> loadedVideosEntries;

    private final FilteredList<RenamingControl> willRenameImageEntries;

    private final FilteredList<RenamingControl> willRenameVideosEntries;

    private final ListProperty<RenamingControl> entriesRenamed;

    private final FilteredList<RenamingControl> renamedImageEntries;

    private final FilteredList<RenamingControl> renamedVideosEntries;

    private final StringProperty statusLoaded = new SimpleStringProperty();

    private final StringProperty statusLoadedFileTypes = new SimpleStringProperty();

    private final StringProperty statusWillRename = new SimpleStringProperty();

    private final StringProperty statusWillRenameFileTypes = new SimpleStringProperty();

    private final StringProperty statusRenamed = new SimpleStringProperty();

    private final StringProperty statusRenamedFileTypes = new SimpleStringProperty();

    private final BooleanProperty filterHiddenFiles = new SimpleBooleanProperty();

    private final BooleanProperty filterDirectories = new SimpleBooleanProperty();

    private final BooleanProperty showOnlyChainging = new SimpleBooleanProperty();

    private final Predicate<RenamingControl> entriesFilteredDefaultPredicate = e -> true;

    private final static Predicate<RenamingControl> isImage = e -> e.getFileType() != null && e.getFileType().contains("image");

    private final static Predicate<RenamingControl> isVideo = e -> e.getFileType() != null && e.getFileType().contains("video");

    private final static Predicate<RenamingControl> noHidden = e -> {
        try {
            return !Files.isHidden(e.getOldPath());
        } catch (IOException ex) {
            log.error(ex.getLocalizedMessage(), ex);
            Platform.runLater(() -> e.exceptionProperty().set(ex));
            return false;
        }
    };

    private final static Predicate<RenamingControl> noDirectories = e -> !Files.isDirectory(e.getOldPath());

    private final static Predicate<RenamingControl> onlyChanging = RenamingControl::isWillChange;

    public Entries(ResourceBundle resourceBundle, AppConfig appConfig, Executor executor) {
        this.resourceBundle = resourceBundle;
        this.appConfig = appConfig;
        entries = new SimpleListProperty<>(FXCollections.observableArrayList(item -> new Observable[]{item.newPathProperty(), item.exceptionProperty(), item.fileTypeProperty(), item.filteredProperty(), item.willChangeProperty()}));
        entriesFiltered = new SimpleListProperty<>(new FilteredList<>(entries, entriesFilteredDefaultPredicate));
        entriesRenamed = new SimpleListProperty<>(FXCollections.observableArrayList());
        entriesWillRename = new FilteredList<>(entries, RenamingControl::isWillChange);
        loadedImageEntries = new FilteredList<>(entries, isImage);
        loadedVideosEntries = new FilteredList<>(entries, isVideo);
        willRenameImageEntries = new FilteredList<>(entriesWillRename, isImage);
        willRenameVideosEntries = new FilteredList<>(entriesWillRename, isVideo);
        renamedImageEntries = new FilteredList<>(entriesRenamed , isImage);
        renamedVideosEntries = new FilteredList<>(entriesRenamed, isVideo);

        log.debug("Configured with executor: {}", executor);
    }

    @PostConstruct
    public void init() {

        initListeners();

    }

    private void initListeners() {

        entries.sizeProperty().addListener((observable, oldValue, newValue) -> statusLoaded.setValue(String.format(resourceBundle.getString(LOADED), newValue)));

        entriesWillRename.addListener((ListChangeListener<RenamingControl>) c -> statusWillRename.setValue(String.format(resourceBundle.getString(WILL_RENAME), c.getList().size())));

        entriesRenamed.sizeProperty().addListener((observable, oldValue, newValue) -> statusRenamed.setValue(String.format(resourceBundle.getString(RENAMED), newValue)));

        loadedImageEntries.addListener((ListChangeListener<RenamingControl>) c -> updateLoadedFileTypesLabel());

        loadedVideosEntries.addListener((ListChangeListener<RenamingControl>) c -> updateLoadedFileTypesLabel());

        willRenameImageEntries.addListener((ListChangeListener<RenamingControl>) c -> updateWillRenameFileTypesLabel());

        willRenameVideosEntries.addListener((ListChangeListener<RenamingControl>) c -> updateWillRenameFileTypesLabel());

        renamedImageEntries.addListener((ListChangeListener<RenamingControl>) c -> updateRenamedFileTypesLabel());

        renamedVideosEntries.addListener((ListChangeListener<RenamingControl>) c -> updateRenamedFileTypesLabel());

        filterHiddenFiles.addListener((observable, oldValue, newValue) -> ((FilteredList<RenamingControl>)entriesFiltered.get()).setPredicate(getCombinedPredicate()));
        filterDirectories.addListener((observable, oldValue, newValue) -> ((FilteredList<RenamingControl>)entriesFiltered.get()).setPredicate(getCombinedPredicate()));
        showOnlyChainging.addListener((observable, oldValue, newValue) -> ((FilteredList<RenamingControl>)entriesFiltered.get()).setPredicate(getCombinedPredicate()));
    }

    private Predicate<? super RenamingControl> getCombinedPredicate() {
        Predicate<RenamingControl> resultPredicate = null;
        if(isFilterHiddenFiles()){
            resultPredicate = noHidden;
        }
        if(isFilterDirectories()){
            resultPredicate = resultPredicate == null? noDirectories : resultPredicate.and(noDirectories);
        }
        if(isShowOnlyChainging()){
            resultPredicate = resultPredicate == null? onlyChanging : resultPredicate.and(onlyChanging);
        }
        return resultPredicate == null ? entriesFilteredDefaultPredicate : resultPredicate;
    }

    private void updateLoadedFileTypesLabel() {
        statusLoadedFileTypes.set(String.format(resourceBundle.getString(LOADED_TYPES), loadedImageEntries.size(), loadedVideosEntries.size()));
    }

    private void updateWillRenameFileTypesLabel() {
        statusWillRenameFileTypes.set(String.format(resourceBundle.getString(WILL_RENAME_TYPES), willRenameImageEntries.size(), willRenameVideosEntries.size()));
    }

    private void updateRenamedFileTypesLabel() {
        statusRenamedFileTypes.set(String.format(resourceBundle.getString(RENAMED_TYPES), renamedImageEntries.size(), renamedVideosEntries.size()));
    }

    // FX Getter / Setter //


    public ObservableList<RenamingControl> getEntries() {
        return entries.get();
    }

    public ListProperty<RenamingControl> entriesProperty() {
        return entries;
    }

    public void setEntries(ObservableList<RenamingControl> entries) {
        this.entries.set(entries);
    }

    public boolean isFilterHiddenFiles() {
        return filterHiddenFiles.get();
    }

    public BooleanProperty filterHiddenFilesProperty() {
        return filterHiddenFiles;
    }

    public void setFilterHiddenFiles(boolean filterHiddenFiles) {
        this.filterHiddenFiles.set(filterHiddenFiles);
    }

    public boolean isFilterDirectories() {
        return filterDirectories.get();
    }

    public BooleanProperty filterDirectoriesProperty() {
        return filterDirectories;
    }

    public void setFilterDirectories(boolean filterDirectories) {
        this.filterDirectories.set(filterDirectories);
    }

    public boolean isShowOnlyChainging() {
        return showOnlyChainging.get();
    }

    public BooleanProperty showOnlyChaingingProperty() {
        return showOnlyChainging;
    }

    public void setShowOnlyChainging(boolean showOnlyChainging) {
        this.showOnlyChainging.set(showOnlyChainging);
    }

    public ObservableList<RenamingControl> getEntriesFiltered() {
        return entriesFiltered.get();
    }

    public ListProperty<RenamingControl> entriesFilteredProperty() {
        return entriesFiltered;
    }

    public ObservableList<RenamingControl> getEntriesRenamed() {
        return entriesRenamed.get();
    }

    public ListProperty<RenamingControl> entriesRenamedProperty() {
        return entriesRenamed;
    }

    public String getStatusLoaded() {
        return statusLoaded.get();
    }

    public StringProperty statusLoadedProperty() {
        return statusLoaded;
    }

    public String getStatusLoadedFileTypes() {
        return statusLoadedFileTypes.get();
    }

    public StringProperty statusLoadedFileTypesProperty() {
        return statusLoadedFileTypes;
    }

    public String getStatusWillRename() {
        return statusWillRename.get();
    }

    public StringProperty statusWillRenameProperty() {
        return statusWillRename;
    }

    public String getStatusWillRenameFileTypes() {
        return statusWillRenameFileTypes.get();
    }

    public StringProperty statusWillRenameFileTypesProperty() {
        return statusWillRenameFileTypes;
    }

    public String getStatusRenamed() {
        return statusRenamed.get();
    }

    public StringProperty statusRenamedProperty() {
        return statusRenamed;
    }

    public String getStatusRenamedFileTypes() {
        return statusRenamedFileTypes.get();
    }

    public StringProperty statusRenamedFileTypesProperty() {
        return statusRenamedFileTypes;
    }
}
