package drrename;

import drrename.model.RenamingBean;
import javafx.application.Platform;
import javafx.beans.value.WritableValue;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.Tika;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.util.Collection;
import java.util.Collections;
import java.util.ResourceBundle;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

@RequiredArgsConstructor
@Slf4j
@Service
public class AsyncEntriesService {

    static final String LOADED = "mainview.status.loaded.filetypes.text";

    static final String WILL_RENAME = "mainview.status.willrename.filetypes.text";

    static final String RENAMED = "mainview.status.renamed.filetypes.text";

    private final ResourceBundle resourceBundle;
    private final AtomicInteger imageCountLoading = new AtomicInteger();
    private final AtomicInteger videoCountLoading = new AtomicInteger();
    private final AtomicInteger imageCountWillRename = new AtomicInteger();
    private final AtomicInteger videoCountWillRename = new AtomicInteger();
    private final AtomicInteger imageCountRenamed = new AtomicInteger();
    private final AtomicInteger videoCountRenamed = new AtomicInteger();

    public synchronized void reset(){
        imageCountLoading.set(0);
        videoCountLoading.set(0);
        imageCountWillRename.set(0);
        videoCountWillRename.set(0);
        imageCountRenamed.set(0);
        videoCountRenamed.set(0);
    }

    @Async
    public synchronized Future<Void> calculateStatusLoadingFileTypes(WritableValue<String> statusProperty, RenamingBean entry){
        return calculateStatusLoadingFileTypes(statusProperty, Collections.singleton(entry));
    }

    @Async
    public synchronized Future<Void> calculateStatusLoadingFileTypes(WritableValue<String> statusProperty, Collection<? extends RenamingBean> entries){
        return calculateStatusSlow(LOADED, statusProperty, entries, imageCountLoading, videoCountLoading);
    }

    @Async
    public synchronized Future<Void> calculateStatusWillRenameFileTypes(WritableValue<String> statusProperty, RenamingBean entry){
        return calculateStatusWillRenameFileTypes(statusProperty, Collections.singleton(entry));
    }

    @Async
    public synchronized Future<Void> calculateStatusWillRenameFileTypes(WritableValue<String> statusProperty, Collection<? extends RenamingBean> entries){
        return calculateStatusSlow(WILL_RENAME, statusProperty, entries, imageCountWillRename, videoCountWillRename);
    }

    @Async
    public synchronized Future<Void> calculateStatusRenamedFileTypes(WritableValue<String> statusProperty, RenamingBean entry){
        return calculateStatusRenamedFileTypes(statusProperty, Collections.singleton(entry));
    }

    @Async
    public synchronized Future<Void> calculateStatusRenamedFileTypes(WritableValue<String> statusProperty, Collection<? extends RenamingBean> entries){
        return calculateStatusSlow(RENAMED, statusProperty, entries, imageCountRenamed, videoCountRenamed);
    }


    private Future<Void> calculateStatusSlow(String text, WritableValue<String> statusProperty, Collection<? extends RenamingBean> entries, AtomicInteger imageCounter, AtomicInteger videoCounter){

        for(RenamingBean b : entries){
            if(Files.isRegularFile(b.getOldPath())) {
                try {
                    if (Thread.currentThread().isInterrupted()) {
                        break;
                    }
                    String mimeType = new Tika().detect(b.getOldPath());
                    if (mimeType.startsWith("image")) {
                        imageCounter.getAndIncrement();
                    } else if (mimeType.startsWith("video")) {
                        videoCounter.getAndIncrement();
                    }
                } catch (IOException ex) {
                    log.error(ex.getLocalizedMessage(), ex);
                }
            }
        }
        if(!Thread.currentThread().isInterrupted()){
            Platform.runLater(() -> statusProperty.setValue(String.format(resourceBundle.getString(text), imageCounter.get(), videoCounter.get())));
        }
        return new AsyncResult<>(null);
    }
}
