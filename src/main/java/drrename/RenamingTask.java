package drrename;

import drrename.model.RenamingBean;
import javafx.concurrent.Task;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class RenamingTask extends Task<Void> {

	private final List<RenamingBean> elements;
	private final RenamingStrategy strategy;
	private final AppConfig config;


	@Override
	protected Void call() throws InterruptedException {

		long cnt = 0;
		for (final RenamingBean b : elements) {
			if (Thread.currentThread().isInterrupted())
				throw new InterruptedException("Cancelled");
			if (!b.isFiltered() && b.willChange()) {
				b.rename(strategy);
				updateProgress(cnt++, elements.size());
				if (config.isDebug())
					Thread.sleep(config.getLoopDelayMs());
			}
		}
		return null;
	}
}
