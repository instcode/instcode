package me.instcode.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import me.instcode.event.ModifyTracker;

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
public abstract class TableModel<T> extends StructuredModel<T> {
	private List<T> list = new ArrayList<T>();

	public TableModel(ModifyTracker tracker) {
		super(tracker);
	}

	@Override
	public void add(T item) {
		if (list.add(item)) {
			fireRowDataAdded(item);
		}
	}
	
	@Override
	public void remove(T item) {
		if (list.remove(item)) {
			fireRowDataRemoved(item);
		}
	}
	
	@Override
	public void clear() {
		list.clear();
		fireChanged(this, RowModelChangeEvent.MODEL_RESET);
	}
	
	/**
	 * Remove the given items from model
	 * @param items
	 */
	public void remove(Object[] items) {
		if (list.remove(Arrays.asList(items))) {
			fireChanged(items, RowModelChangeEvent.ROWS_REMOVED);
		}
	}
	
	/**
	 * Get all item inside
	 * @return
	 */
	public Object[] getAll() {
		return list.toArray(new Object[list.size()]);
	}

	/**
	 * Fire an event to listeners when a node is added.
	 * @param data
	 */
	protected void fireRowDataAdded(Object data) {
		fireChanged(data, RowModelChangeEvent.ROW_ADDED);
	}
	
	/**
	 * Fire an event to listeners when a node is removed.
	 * @param data
	 */
	protected void fireRowDataRemoved(Object data) {
		fireChanged(data, RowModelChangeEvent.ROW_REMOVED);
	}
}
