package me.instcode.gis.map.player;

/**
 * Listen to all animation events
 * 
 */
public interface AnimationListener {
	
	/**
	 * @param event An event in {@link AnimationPlayer} event list. 
	 */
	void notifyEvent(int event);

}
