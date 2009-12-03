package me.instcode.rcp.node.model;

public class RowDataChangeEvent {
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
	
	private Object data;
	private int type;
	
	/**
	 * Create instance
	 */
	public RowDataChangeEvent(Object data, int type) {
		this.data = data;
		this.type = type;
	}

	/**
	 * @return The carried object
	 */
	public Object getData() {
		return data;
	}
	
	/**
	 * @return The event type
	 */
	public int getType() {
		return type;
	}
}
