package me.instcode.model;

import me.instcode.event.ModifyEvent;


public class RowDataChangeEvent extends ModifyEvent {
	/**
	 * Event about a node has been added to model.
	 */
	public static final int ROW_DATA_ADDED_CHANGE = 0;
	
	/**
	 * Event about a node has been removed from model.
	 */
	public static final int ROW_DATA_REMOVED_CHANGE = 1;
	
	/**
	 * Event about one or more node's attributes have
	 * been modified. 
	 */
	public static final int ROW_DATA_MODIFIED_CHANGE = 2;
	
	/**
	 * Event about one or more rows have been removed from
	 * the model. The data will be a list of row indices
	 * which were removed. Put a "this" object if all items
	 * were removed. 
	 */
	public static final int ROWS_REMOVED_CHANGE = 3;

	/**
	 * Create instance
	 */
	public RowDataChangeEvent(Object source, Object data, int type) {
		super(source, data, type);
	}
}
