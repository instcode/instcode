package me.instcode.model;

import me.instcode.event.ModifyListener;
import me.instcode.event.ModifyTracker;
import me.instcode.event.ModifyTrackerAwareness;

/**
 * <p>
 * This is an implementation of a common row based data
 * structure which stores data row by row. The implementation
 * also supports notifying modification via event/listeners
 * mechanism.
 * <p>
 * Note: Not thread-safe.
 * 
 * @author khoanguyen
 *
 */
public abstract class StructuredModel<T> implements ModifyTrackerAwareness {
	private ModifyTracker tracker;

	public StructuredModel(ModifyTracker tracker) {
		setModifyTracker(tracker);
	}

	@Override
	public void setModifyTracker(ModifyTracker tracker) {
		this.tracker = tracker;
	}
	
	@Override
	public ModifyTracker getModifyTracker() {
		return tracker;
	}
	
	/**
	 * Add the given data to model
	 * @param data
	 */
	public abstract void add(T item);
	
	/**
	 * Remove the given data from model
	 * @param data
	 */
	public abstract void remove(T item);
	
	/**
	 * Clear the model
	 */
	public abstract void clear();

	/**
	 * @param listener
	 */
	public abstract void register(ModifyListener listener);
	
	/**
	 * @param listener
	 */
	public abstract void unregister(ModifyListener listener);
	
	/**
	 * Fire a change event to all listeners.
	 * @param event
	 */
	protected void fireChanged(RowModelChangeEvent event) {
		if (tracker != null) {
			tracker.fireModifyEvent(event);
		}
	}

	protected void fireChanged(Object data, int eventType) {
		fireChanged(new RowModelChangeEvent(this, data, eventType));
	}
}
