package me.instcode.gis.map.handler;


public class MapEvent {

	public static final int SCREEN_SIZE_CHANGED = 0;
	public static final int VIEW_PORT_CHANGED = 1;
	public static final int ZOOM_SCALE_CHANGED = 2;
	
	private Object source;
	private int eventType;
	
	public MapEvent(Object source, int eventType) {
		this.source = source;
		this.eventType = eventType;
	}
	
	public int getType() {
		return eventType;
	}

	public Object getSource() {
		return source;
	}
}
