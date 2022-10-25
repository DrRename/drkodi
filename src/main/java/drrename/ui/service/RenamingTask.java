package drrename.ui.service;

import drrename.config.AppConfig;
import drrename.event.FileRenamedEvent;
import drrename.event.StartingRenameEvent;
import drrename.model.RenamingControl;
import drrename.strategy.RenamingStrategy;
import javafx.concurrent.Task;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class RenamingTask extends Task<List<RenamingControl>> {

	private final List<RenamingControl> elements;
	private final RenamingStrategy strategy;
	private final AppConfig config;
	private final ApplicationEventPublisher applicationEventPublisher;

	@Override
	protected List<RenamingControl> call() throws InterruptedException {

		List<RenamingControl> result = new ArrayList<>();
		var event = new StartingRenameEvent();
		applicationEventPublisher.publishEvent(event);
		long cnt = 0;
		for (final RenamingControl b : elements) {
			if (Thread.currentThread().isInterrupted())
				throw new InterruptedException("Cancelled");
			if (b.isWillChange()) {
				Path p = b.rename(strategy);
				if(!b.getOldPath().equals(p)){
					result.add(b);
					applicationEventPublisher.publishEvent(new FileRenamedEvent(event.getUuid(), b));


				}
			}
			if (config.isDebug())
				Thread.sleep(config.getLoopDelayMs());
			updateProgress(cnt++, elements.size());
		}
		return result;
	}
}
