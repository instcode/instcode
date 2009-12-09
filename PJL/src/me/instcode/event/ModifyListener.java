package me.instcode.event;

public interface ModifyListener {
	
	/**
	 * Handle changes from model
	 * 
	 * @param event
	 */
	public void dataModified(ModifyEvent event);
}
