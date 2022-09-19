package drrename;

import java.util.List;
import java.util.Objects;

import javafx.concurrent.Service;
import javafx.concurrent.Task;

public class RenamingService extends Service<Void> {

	private List<RenamingBean> events;
	private RenamingStrategy strategy;

	public RenamingService() {

		super();
	}

	public List<RenamingBean> getEvents() {

		return events;
	}

	public void setEvents(final List<RenamingBean> events) {

		this.events = events;
	}

	public RenamingStrategy getStrategy() {

		return strategy;
	}

	public void setStrategy(final RenamingStrategy strategy) {

		this.strategy = strategy;
	}

	@Override
	protected Task<Void> createTask() {

		return new RenamingTask(Objects.requireNonNull(events), Objects.requireNonNull(strategy));
	}
}
