package com.github.drrename.event;

import com.kerner1000.drrename.RenamingStrategy;
import com.github.events1000.api.AbstractTransportingEvent;
import com.github.events1000.api.EventTopic;
import com.github.events1000.api.SynchronousEvent;
import com.github.events1000.impl.SimpleEventTopic;

public class AvailableRenamingStrategyEvent extends AbstractTransportingEvent<RenamingStrategy> implements SynchronousEvent {

	public static final EventTopic EVENT_TOPIC = new SimpleEventTopic("available-renaming-strategy");

	public AvailableRenamingStrategyEvent(final RenamingStrategy data) {

		super(EVENT_TOPIC, data);
	}
}
