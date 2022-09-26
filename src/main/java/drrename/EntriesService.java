package drrename;

import drrename.event.*;
import drrename.model.RenamingBean;
import javafx.application.Platform;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.WritableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

@RequiredArgsConstructor
@Slf4j
@Service
public class EntriesService {

    static final String LOADED = "mainview.status.loaded.text";

    static final String WILL_RENAME = "mainview.status.willrename.text";

    static final String RENAMED = "mainview.status.renamed.text";

    private final AsyncEntriesService asyncEntriesService;

    private final ListFilesService listFilesService;

    private final PreviewService previewService;

    private final RenamingService renamingService;

    private final ResourceBundle resourceBundle;

    private final AppConfig appConfig;

    private final ListProperty<RenamingBean> entriesProperty = new SimpleListProperty<>(FXCollections.observableArrayList());

    private final StringProperty statusLoaded = new SimpleStringProperty();

    private final StringProperty statusLoadedFileTypes = new SimpleStringProperty();

    private final StringProperty statusWillRename = new SimpleStringProperty();

    private final StringProperty statusWillRenameFileTypes = new SimpleStringProperty();

    private final StringProperty statusRenamed = new SimpleStringProperty();

    private final StringProperty statusRenamedFileTypes = new SimpleStringProperty();

    private final List<Future<?>> runningAsyncTask = new CopyOnWriteArrayList<>();

    private static class CounterLock {
        private final AtomicInteger loadedCount = new AtomicInteger();
        private final AtomicInteger willRenameCount = new AtomicInteger();
        private final AtomicInteger renamedCount = new AtomicInteger();

        synchronized int incrementAngGetLoadedCount() {
            return loadedCount.incrementAndGet();
        }

        synchronized int incrementAndGetWillRenameCount() {
            return willRenameCount.incrementAndGet();
        }

        synchronized int incrementAndGetRenamedCount() {
            return renamedCount.incrementAndGet();
        }

        synchronized void resetAllCounters(ResetType resetType) {
            switch (resetType){
                case LOAD -> {
                    loadedCount.set(0);
                    willRenameCount.set(0);
                    renamedCount.set(0);
                }
                case PREVIEW -> {
                    willRenameCount.set(0);
                    renamedCount.set(0);
                }
                case RENAME -> {
                    renamedCount.set(0);
                }
            }
        }
    }

    private final CounterLock counterLock = new CounterLock();

    @PostConstruct
    public void init() {
        listFilesService.runningProperty().addListener((observable, oldValue, newValue) -> log.debug("{} running state changed to {}", listFilesService, newValue));
        previewService.runningProperty().addListener((observable, oldValue, newValue) -> log.debug("{} running state changed to {}", previewService, newValue));
        renamingService.runningProperty().addListener((observable, oldValue, newValue) -> log.debug("{} running state changed to {}", renamingService, newValue));
    }

    public void calculateLoading(long number) {
        calculateStatusFast(statusLoaded, LOADED, number);
    }

    public void calculateWillRename(long number) {
        calculateStatusFast(statusWillRename, WILL_RENAME, number);
    }

    public void calculateRenamed(long number) {
        calculateStatusFast(statusRenamed, RENAMED, number);
    }

    private void calculateStatusFast(WritableValue<String> statusProperty, String text, long number) {
        Platform.runLater(() -> statusProperty.setValue(String.format(resourceBundle.getString(text), number)));
    }

    private enum ResetType {
        LOAD, PREVIEW, RENAME
    }

    private void reset(ResetType resetType)  {
        if (appConfig.isDebug()) {
            log.debug("Reset called on thread {}, waiting..", Thread.currentThread());
            try {
                Thread.sleep(appConfig.getResetDelayMs());
            } catch (InterruptedException e) {
                // ignore 'sleep interrupted'
            }
        }
        for (Future<?> f : runningAsyncTask) {
            f.cancel(true);
            try {
                // wait for completion
                f.get();
            } catch (InterruptedException e) {
                // ignore
            } catch (ExecutionException e) {
                log.error(e.getLocalizedMessage(), e);
            }
        }
        runningAsyncTask.clear();
        asyncEntriesService.reset();
        counterLock.resetAllCounters(resetType);
        Platform.runLater(() -> resetStati(resetType));
        if (appConfig.isDebug()) {
            log.debug("Reset called on thread {} finished, waiting..", Thread.currentThread());
            try {
                Thread.sleep(appConfig.getResetDelayMs());
            } catch (InterruptedException e) {
                // ignore 'sleep interrupted'
            }
        }
    }

    private void resetStati(ResetType resetType) {
        switch (resetType){
            case LOAD -> {
                statusLoaded.set(null);
                statusLoadedFileTypes.set(null);
                statusWillRename.set(null);
                statusWillRenameFileTypes.set(null);
                statusRenamed.set(null);
                statusRenamedFileTypes.set(null);
            }
            case PREVIEW -> {
                statusWillRename.set(null);
                statusWillRenameFileTypes.set(null);
                statusRenamed.set(null);
                statusRenamedFileTypes.set(null);
            }
            case RENAME -> {
                statusRenamed.set(null);
                statusRenamedFileTypes.set(null);
            }
        }
    }

    @EventListener
    public void onLoadingStartEvent(StartingListFilesEvent event) {
        log.debug("New loading on thread {}", Thread.currentThread());
        synchronized (counterLock){
            reset(ResetType.LOAD);
        }
    }

    @EventListener
    public void onPreviewStartEvent(StartingPreviewEvent event) {
        log.debug("New preview on thread {}", Thread.currentThread());
        synchronized (counterLock){
            reset(ResetType.PREVIEW);
        }
    }

    @EventListener
    public void onRenameStartEvent(StartingRenameEvent event) {
        log.debug("New rename on thread {}", Thread.currentThread());
        synchronized (counterLock){
            reset(ResetType.RENAME);
        }
    }

    @EventListener
    public void onFileEntryEvent(FileEntryEvent event) {
        Platform.runLater(() -> entriesProperty.add(event.getRenamingBean()));
        calculateLoading(counterLock.incrementAngGetLoadedCount());
        var future = asyncEntriesService.calculateStatusLoadingFileTypes(statusLoadedFileTypes, event.getRenamingBean());
        runningAsyncTask.add(future);
    }

    @EventListener
    public void onFilePreviewEvent(FilePreviewEvent event) {
        calculateWillRename(counterLock.incrementAndGetWillRenameCount());
        var future = asyncEntriesService.calculateStatusWillRenameFileTypes(statusWillRenameFileTypes, event.getSource());
        runningAsyncTask.add(future);
    }

    @EventListener
    public void onFileRenamedEvent(FileRenamedEvent event) {
        calculateRenamed(counterLock.incrementAndGetRenamedCount());
        var future = asyncEntriesService.calculateStatusRenamedFileTypes(statusRenamedFileTypes, event.getSource());
        runningAsyncTask.add(future);
    }

    public ObservableList<RenamingBean> getEntries() {
        return entriesProperty.get();
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
