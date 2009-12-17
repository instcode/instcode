package me.instcode.event;

public interface ModifyTrackerAwareness {
	/**
	 * @param tracker
	 */
	public void setModifyTracker(ModifyTracker tracker);
	
	/**
	 * @return
	 */
	public ModifyTracker getModifyTracker();
}
