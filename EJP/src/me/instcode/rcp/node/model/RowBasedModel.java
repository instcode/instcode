package me.instcode.rcp.node.model;

import java.util.ArrayList;
import java.util.List;

/**
 * This is an implementation of a common row based data
 * structure which stores data row by row. The implementation
 * also supports notifying modification via event/listeners
 * mechanism.
 *
 * @author dcsnxk
 *
 */
public abstract class RowBasedModel<T> {
	private List<RowDataChangeListener> listeners = new ArrayList<RowDataChangeListener>();
	private List<T> list = new ArrayList<T>();

	/**
	 * Add the given data to model
	 * @param data
	 */
	public void add(T item) {
		if (list.add(item)) {
			fireRowDataAdded(item);
		}
	}
	
	/**
	 * Remove the given data from model
	 * @param data
	 */
	public void remove(T item) {
		if (list.remove(item)) {
			fireRowDataRemoved(item);
		}
	}

	/**
	 * Remove the given items from model
	 * @param data
	 */
	public void remove(T[] items) {
		throw new UnsupportedOperationException("Not implemented.");
	}
	
	/**
	 * Get all data inside
	 * @return
	 */
	public Object[] getAll() {
		return list.toArray(new Object[list.size()]);
	}

	/**
	 * Add the given node change event listener to listeners
	 * @param listener
	 */
	public void addRowDataChangeListener(RowDataChangeListener listener) {
		listeners.add(listener);
	}
	
	/**
	 * Remove the given node change event listener from listeners.
	 * 
	 * @param listener
	 * @return
	 */
	public boolean removeRowDataChangeListener(RowDataChangeListener listener) {
		return listeners.remove(listener);
	}

	/**
	 * Fire a change event to all listeners.
	 * @param event
	 */
	protected void fireRowDataChanged(RowDataChangeEvent event) {
		for (RowDataChangeListener listener : listeners) {
			listener.rowDataChanged(event);
		}
	}
	
	/**
	 * Fire an event to listeners when a node is added.
	 * @param data
	 */
	protected void fireRowDataAdded(Object data) {
		RowDataChangeEvent event = new RowDataChangeEvent(data, RowDataChangeEvent.ROW_DATA_ADDED_CHANGE);
		fireRowDataChanged(event);
	}
	
	/**
	 * Fire an event to listeners when a node is removed.
	 * @param data
	 */
	protected void fireRowDataRemoved(Object data) {
		RowDataChangeEvent event = new RowDataChangeEvent(data, RowDataChangeEvent.ROW_DATA_REMOVED_CHANGE);
		fireRowDataChanged(event);
	}

	/**
	 * Fire an event to listeners when node's attributes are changed.
	 * @param data
	 */
	protected void fireRowDataModified(Object data) {
		RowDataChangeEvent event = new RowDataChangeEvent(data, RowDataChangeEvent.ROW_DATA_MODIFIED_CHANGE);
		fireRowDataChanged(event);
	}
}
