package drrename.ui.service;

import drrename.DrRenameTask;
import drrename.RenamingControl;
import drrename.Tasks;
import drrename.config.AppConfig;
import drrename.strategy.RenamingStrategy;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.ResourceBundle;

@Slf4j
public class PreviewTask extends DrRenameTask<Void> {

    private final List<RenamingControl> beans;

    private final RenamingStrategy renamingStrategy;

    public PreviewTask(AppConfig config, ResourceBundle resourceBundle, List<RenamingControl> beans, RenamingStrategy renamingStrategy) {
        super(config, resourceBundle);
        this.beans = beans;
        this.renamingStrategy = renamingStrategy;
    }

    @Override
    protected Void call() throws Exception {

        log.debug("Starting");
        updateMessage(String.format(getResourceBundle().getString(PreviewService.LOADING_PREVIEVS)));
        long cnt = 0;
        for (final RenamingControl p : beans) {
            if (isCancelled()) {
                log.debug("Cancelled");
                updateMessage(String.format(getResourceBundle().getString(Tasks.MESSAGE_CANCELLED)));
                break;
            }
            String newName = p.preview(renamingStrategy);
            updateProgress(cnt++, beans.size());
            if (getAppConfig().isDebug()) {
                try {
                    Thread.sleep(getAppConfig().getLoopDelayMs());
                } catch (InterruptedException e) {
                    if (isCancelled()) {
                        log.debug("Cancelled");
                        updateMessage(String.format(getResourceBundle().getString(Tasks.MESSAGE_CANCELLED)));
                        break;
                    }
                }
            }
        }
        log.debug("Finished");
        updateMessage(null);
        return null;
    }
}
