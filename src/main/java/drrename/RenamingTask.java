package drrename;

import drrename.event.FileRenamedEvent;
import drrename.event.StartingListFilesEvent;
import drrename.event.StartingRenameEvent;
import drrename.model.RenamingBean;
import javafx.concurrent.Task;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;

import java.nio.file.Path;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
public class RenamingTask extends Task<Void> {

	private final List<RenamingBean> elements;
	private final RenamingStrategy strategy;
	private final AppConfig config;
	private final ApplicationEventPublisher applicationEventPublisher;

	@Override
	protected Void call() throws InterruptedException {

		long cnt = 0;
		UUID uuid = UUID.randomUUID();
		applicationEventPublisher.publishEvent(new StartingRenameEvent(uuid));
		for (final RenamingBean b : elements) {
			if (Thread.currentThread().isInterrupted())
				throw new InterruptedException("Cancelled");
			if (!b.isFiltered() && b.willChange()) {
				Path p = b.rename(strategy);
				if(!b.getOldPath().equals(p)){
					applicationEventPublisher.publishEvent(new FileRenamedEvent(b));
				} else {
					int wait = 0;
				}
				updateProgress(cnt++, elements.size());
				if (config.isDebug())
					Thread.sleep(config.getLoopDelayMs());
			}
		}
		return null;
	}
}
