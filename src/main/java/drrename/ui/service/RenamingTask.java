package drrename.ui.service;

import drrename.DrRenameTask;
import drrename.Entries;
import drrename.RenamingControl;
import drrename.Tasks;
import drrename.config.AppConfig;
import drrename.strategy.RenamingStrategy;
import javafx.application.Platform;
import lombok.extern.slf4j.Slf4j;

import java.nio.file.Path;
import java.util.List;
import java.util.ResourceBundle;

@Slf4j
public class RenamingTask extends DrRenameTask<Void> {

	private final List<RenamingControl> elements;

	private final RenamingStrategy strategy;



	private final Entries entries;

	public RenamingTask(AppConfig config, ResourceBundle resourceBundle, List<RenamingControl> elements, RenamingStrategy strategy, Entries entries) {
		super(config, resourceBundle);
		this.elements = elements;
		this.strategy = strategy;
		this.entries = entries;
	}


	@Override
	protected Void call() throws InterruptedException {

		log.debug("Starting");
		updateMessage(String.format(getResourceBundle().getString(RenamingService.RENAMING_FILES)));
		long cnt = 0;
		for (final RenamingControl b : elements) {
			if (isCancelled()) {
				log.debug("Cancelled");
				updateMessage(String.format(getResourceBundle().getString(Tasks.MESSAGE_CANCELLED)));
				break;
			}
			if (b.isWillChange()) {
				Path p = b.rename(strategy);
				if (!b.getOldPath().equals(p)) {
					handleEntry(b);
				}
			}
			updateProgress(++cnt, elements.size());
			if (getConfig().isDebug())
				Thread.sleep(getConfig().getLoopDelayMs());

		}
		log.debug("Finished");
		updateMessage(null);
		return null;
	}

	private void handleEntry(RenamingControl b) {
		Platform.runLater(() -> entries.getEntriesRenamed().add(b));
	}
}
