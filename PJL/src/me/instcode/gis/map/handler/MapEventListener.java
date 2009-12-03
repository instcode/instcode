package me.instcode.gis.map.handler;

public interface MapEventListener {

	/**
	 * Handle a map event. The listener should check for event type
	 * before making any further actions.
	 * 
	 * @param event
	 * 		Event to be handled.
	 */
	public void handleMapEvent(MapEvent event);
}
