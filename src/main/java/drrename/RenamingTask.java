package drrename;

import drrename.event.FileRenamedEvent;
import drrename.event.StartingRenameEvent;
import drrename.model.RenamingEntry;
import javafx.concurrent.Task;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;

import java.nio.file.Path;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
public class RenamingTask extends Task<Void> {

	private final List<RenamingEntry> elements;
	private final RenamingStrategy strategy;
	private final AppConfig config;
	private final ApplicationEventPublisher applicationEventPublisher;

	@Override
	protected Void call() throws InterruptedException {

		long cnt = 0;
		UUID uuid = UUID.randomUUID();
		applicationEventPublisher.publishEvent(new StartingRenameEvent(uuid));
		for (final RenamingEntry b : elements) {
			if (Thread.currentThread().isInterrupted())
				throw new InterruptedException("Cancelled");
			if (b.willChange()) {
				Path p = b.rename(strategy);
				if(!b.getOldPath().equals(p)){
					applicationEventPublisher.publishEvent(new FileRenamedEvent(uuid, b));
				}
			}
			if (config.isDebug())
				Thread.sleep(config.getLoopDelayMs());
			updateProgress(cnt++, elements.size());
		}
		return null;
	}
}
