package me.instcode.model;

import me.instcode.event.ModifyEvent;


public class RowModelChangeEvent extends ModifyEvent {
	/**
	 * Event about a node has been added to model.
	 */
	public static final int ROW_ADDED = 0;
	
	/**
	 * Event about a node has been removed from model.
	 */
	public static final int ROW_REMOVED = 1;
	
	/**
	 * Event about one or more node's attributes have
	 * been modified. 
	 */
	public static final int ROW_DATA_MODIFIED = 2;
	
	/**
	 * Event about one or more rows have been removed from
	 * the model. The data should be an array of row removed
	 * elements.
	 */
	public static final int ROWS_REMOVED = 3;

	/**
	 * Event about one or more rows have been added to 
	 * the model. The data should be an array of newly added
	 * elements. 
	 */
	public static final int ROWS_ADDED = 4;
	
	/**
	 * Event about the model has been reset.
	 */
	public static final int MODEL_RESET = 5;

	/**
	 * Create instance
	 */
	public RowModelChangeEvent(Object source, Object data, int type) {
		super(source, data, type);
	}
}
