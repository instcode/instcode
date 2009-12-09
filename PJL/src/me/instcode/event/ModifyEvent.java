package me.instcode.event;

public class ModifyEvent {
	private Object source;
	private Object data;
	private int type;
	
	/**
	 * Create instance
	 */
	public ModifyEvent(Object source, Object data, int type) {
		this.source = source;
		this.data = data;
		this.type = type;
	}

	/**
	 * @return source of this event
	 */
	public Object getSource() {
		return source;
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
