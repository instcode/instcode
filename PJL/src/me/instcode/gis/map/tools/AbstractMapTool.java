package me.instcode.gis.map.tools;

import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

import me.instcode.gis.map.MapView;

public class AbstractMapTool implements MapTool {
	private String name;
	
	public AbstractMapTool(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public void handleInputEvent(InputEvent event) {
		MapView view = (MapView) event.getSource();
		
		switch (event.getID()) {
		case MouseEvent.MOUSE_WHEEL:
			MouseWheelEvent mouseWheelEvent = (MouseWheelEvent)event;
			boolean zoomIn = (mouseWheelEvent.getWheelRotation() < 0);
			view.zoomTo(mouseWheelEvent.getPoint(), zoomIn);
		}
	}
}
