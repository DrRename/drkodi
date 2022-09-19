package drrename;

import drrename.model.RenamingBean;
import javafx.concurrent.Task;

import java.util.List;

public class RenamingTask extends Task<Void> {

	private final List<RenamingBean> elements;
	private final RenamingStrategy strategy;

	public RenamingTask(final List<RenamingBean> elements, final RenamingStrategy strategy) {

		super();
		this.elements = elements;
		this.strategy = strategy;
	}

	@Override
	protected Void call() throws InterruptedException {

		for (final RenamingBean b : elements) {
			if (Thread.currentThread().isInterrupted())
				throw new InterruptedException("Cancelled");
			if (!b.isFiltered() && b.willChange()) {
				b.rename(strategy);
			}
		}
		return null;
	}
}
