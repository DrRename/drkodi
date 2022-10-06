package drrename.service;

import drrename.config.AppConfig;
import drrename.event.FileRenamedEvent;
import drrename.event.NewRenamingEntryEvent;
import drrename.model.RenamingEntry;
import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.Executor;
import java.util.function.Predicate;

@Slf4j
@Service
public class EntriesService {

    static final String LOADED = "mainview.status.loaded.text";

    static final String LOADED_TYPES = "mainview.status.loaded.filetypes.text";

    static final String WILL_RENAME = "mainview.status.willrename.text";

    static final String WILL_RENAME_TYPES = "mainview.status.willrename.filetypes.text";

    static final String RENAMED = "mainview.status.renamed.text";

    static final String RENAMED_TYPES = "mainview.status.renamed.filetypes.text";

    private final ResourceBundle resourceBundle;

    private final AppConfig appConfig;

    private final ListProperty<RenamingEntry> entries;

    private final ListProperty<RenamingEntry> entriesFiltered;

    private final FilteredList<RenamingEntry> entriesWillRename;

    private final FilteredList<RenamingEntry> loadedImageEntries;

    private final FilteredList<RenamingEntry> loadedVideosEntries;

    private final FilteredList<RenamingEntry> willRenameImageEntries;

    private final FilteredList<RenamingEntry> willRenameVideosEntries;

    private final ListProperty<RenamingEntry> entriesRenamed;

    private final FilteredList<RenamingEntry> renamedImageEntries;

    private final FilteredList<RenamingEntry> renamedVideosEntries;

    private final StringProperty statusLoaded = new SimpleStringProperty();

    private final StringProperty statusLoadedFileTypes = new SimpleStringProperty();

    private final StringProperty statusWillRename = new SimpleStringProperty();

    private final StringProperty statusWillRenameFileTypes = new SimpleStringProperty();

    private final StringProperty statusRenamed = new SimpleStringProperty();

    private final StringProperty statusRenamedFileTypes = new SimpleStringProperty();

    private final BooleanProperty filterHiddenFiles = new SimpleBooleanProperty();

    private final BooleanProperty filterDirectories = new SimpleBooleanProperty();

    private final BooleanProperty showOnlyChainging = new SimpleBooleanProperty();

    private final Predicate<RenamingEntry> entriesFilteredDefaultPredicate = e -> true;

    private final static Predicate<RenamingEntry> isImage = e -> e.getFileType() != null && e.getFileType().contains("image");

    private final static Predicate<RenamingEntry> isVideo = e -> e.getFileType() != null && e.getFileType().contains("video");

    private final static Predicate<RenamingEntry> noHidden = e -> {
        try {
            return !Files.isHidden(e.getOldPath());
        } catch (IOException ex) {
            log.error(ex.getLocalizedMessage(), ex);
            Platform.runLater(() -> e.exceptionProperty().set(ex));
            return false;
        }
    };

    private final static Predicate<RenamingEntry> noDirectories = e -> !Files.isDirectory(e.getOldPath());

    private final static Predicate<RenamingEntry> onlyChanging = RenamingEntry::willChange;

    public EntriesService(ResourceBundle resourceBundle, AppConfig appConfig, Executor executor) {
        this.resourceBundle = resourceBundle;
        this.appConfig = appConfig;
        entries = new SimpleListProperty<>(FXCollections.observableArrayList(item -> new Observable[]{item.newPathProperty(), item.exceptionProperty(), item.fileTypeProperty(), item.filteredProperty(), item.willChangeProperty()}));
        entriesFiltered = new SimpleListProperty<>(new FilteredList<>(entries, entriesFilteredDefaultPredicate));
        entriesRenamed = new SimpleListProperty<>(FXCollections.observableArrayList());
        entriesWillRename = new FilteredList<>(entriesFiltered, RenamingEntry::willChange);
        loadedImageEntries = new FilteredList<>(entriesFiltered, isImage);
        loadedVideosEntries = new FilteredList<>(entriesFiltered, isVideo);
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

        entriesFiltered.sizeProperty().addListener((observable, oldValue, newValue) -> statusLoaded.setValue(String.format(resourceBundle.getString(LOADED), newValue)));

        entriesWillRename.addListener((ListChangeListener<RenamingEntry>) c -> statusWillRename.setValue(String.format(resourceBundle.getString(WILL_RENAME), c.getList().size())));

        entriesRenamed.sizeProperty().addListener((observable, oldValue, newValue) -> statusRenamed.setValue(String.format(resourceBundle.getString(RENAMED), newValue)));

        loadedImageEntries.addListener((ListChangeListener<RenamingEntry>) c -> updateLoadedFileTypesLabel());

        loadedVideosEntries.addListener((ListChangeListener<RenamingEntry>) c -> updateLoadedFileTypesLabel());

        willRenameImageEntries.addListener((ListChangeListener<RenamingEntry>) c -> updateWillRenameFileTypesLabel());

        willRenameVideosEntries.addListener((ListChangeListener<RenamingEntry>) c -> updateWillRenameFileTypesLabel());

        renamedImageEntries.addListener((ListChangeListener<RenamingEntry>) c -> updateRenamedFileTypesLabel());

        renamedVideosEntries.addListener((ListChangeListener<RenamingEntry>) c -> updateRenamedFileTypesLabel());

        filterHiddenFiles.addListener((observable, oldValue, newValue) -> ((FilteredList<RenamingEntry>)entriesFiltered.get()).setPredicate(getCombinedPredicate()));
        filterDirectories.addListener((observable, oldValue, newValue) -> ((FilteredList<RenamingEntry>)entriesFiltered.get()).setPredicate(getCombinedPredicate()));
        showOnlyChainging.addListener((observable, oldValue, newValue) -> ((FilteredList<RenamingEntry>)entriesFiltered.get()).setPredicate(getCombinedPredicate()));
    }

    private Predicate<? super RenamingEntry> getCombinedPredicate() {
        Predicate<RenamingEntry> resultPredicate = null;
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

    @EventListener
    public void onFileEntryEvent(NewRenamingEntryEvent event) {
        var hans = new ArrayList<>(event.getRenamingEntries());
        Platform.runLater(() -> entries.addAll(hans));
    }

    @EventListener
    public void onFileRenamedEvent(FileRenamedEvent event) {
        var hans = new ArrayList<>(event.getRenamedEntries());
        Platform.runLater(() -> entriesRenamed.addAll(hans));
    }

    // Getter / Setter

    public ObservableList<RenamingEntry> getEntries() {
        return entries.get();
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

    public boolean isFilterHiddenFiles() {
        return filterHiddenFiles.get();
    }

    public BooleanProperty filterHiddenFilesProperty() {
        return filterHiddenFiles;
    }

    public void setFilterHiddenFiles(boolean filterHiddenFiles) {
        this.filterHiddenFiles.set(filterHiddenFiles);
    }

    public FilteredList<RenamingEntry> getEntriesFiltered() {
        return (FilteredList<RenamingEntry>) entriesFiltered.get();
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

    public List<RenamingEntry> getEntriesRenamed() {
        return entriesRenamed;
    }
}
