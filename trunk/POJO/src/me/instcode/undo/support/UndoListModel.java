package me.instcode.undo.support;

import java.util.Vector;

import javax.swing.AbstractListModel;

public class UndoListModel<T> extends AbstractListModel {
	private Vector<T> items = new Vector<T>();
	private int limit = 0;

	public int getSize() {
		return items.size();
	}

	/**
	 * Set limit number of undo/redo items
	 * 
	 * @param limit
	 */
	public void setLimit(int limit) {
		this.limit = limit;
	}

	/**
	 * insert an item to the begining of list
	 * 
	 * @param object
	 */
	public void addFirst(T object) {
		if (items.size() == limit)
			items.removeElementAt(limit - 1);
		items.add(0, object);
		this.fireIntervalAdded(this, 0, 0);
	}

	/**
	 * Remove all element in model
	 */
	public void removeAll() {
		int nIndex = items.size();
		items.removeAllElements();
		this.fireIntervalRemoved(this, 0, nIndex);
	}

	/**
	 * Remove elements from 0 to index
	 * 
	 * @param index
	 */
	public void removeTo(int index) {
		for (int i = 0; i < index; i++)
			items.remove(0);
		this.fireIntervalRemoved(this, 0, index);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.ListModel#getElementAt(int)
	 */
	public T getElementAt(int index) {
		return items.get(index);
	}
}