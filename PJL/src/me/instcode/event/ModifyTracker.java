package me.instcode.event;

import me.instcode.event.subcribing.SubscribingStrategy;

/**
 * A Facade pattern which uses to fire data modified event to
 * all register listener
 */
public class ModifyTracker {

	private SubscribingStrategy strategy;
	
	public void setStrategy(SubscribingStrategy strategy) {
		this.strategy = strategy;
	}
	
	public void register(Object interest, ModifyListener listener) {
		if (strategy != null) {
			strategy.register(interest, listener);
		}
	}
	
	public void unregister(Object interest, ModifyListener listener) {
		if (strategy != null) {
			strategy.unregister(interest, listener);
		}
	}

	public void fireModifyEvent(ModifyEvent event) {
		if (strategy == null) {
			return;
		}
		ModifyListener[] listeners = strategy.getListeners(event);
		for (ModifyListener listener : listeners) {
			listener.dataModified(event);
		}
	}
}
