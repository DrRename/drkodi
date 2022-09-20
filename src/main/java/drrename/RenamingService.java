package drrename;

import drrename.model.RenamingBean;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor
@Component
@Slf4j
public class RenamingService extends Service<Void>  {

	private final AppConfig appConfig;

	private List<RenamingBean> events;
	private RenamingStrategy strategy;



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

		return new RenamingTask(Objects.requireNonNull(events), Objects.requireNonNull(strategy), appConfig);
	}
}
