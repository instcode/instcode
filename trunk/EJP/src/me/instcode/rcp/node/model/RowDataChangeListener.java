package me.instcode.rcp.node.model;

public interface RowDataChangeListener {
	
	/**
	 * Handle changes from model
	 * 
	 * @param event
	 */
	public void rowDataChanged(RowDataChangeEvent event);
}
