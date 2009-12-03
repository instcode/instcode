package me.instcode.gis.map.tools;

import java.awt.Point;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;

import me.instcode.gis.map.MapView;

public class PanTool extends AbstractMapTool {

	public static final String NAME = "pan";
	
	private Point startPoint;

	public PanTool() {
		super(NAME);
	}
	
	public void handleInputEvent(InputEvent event) {
		MapView view = (MapView) event.getSource();
		
		switch (event.getID()) {
		case MouseEvent.MOUSE_PRESSED:
			startPoint = ((MouseEvent)event).getPoint();
			break;

		case MouseEvent.MOUSE_DRAGGED:
			Point draggingPoint = ((MouseEvent)event).getPoint();
			int dx = -draggingPoint.x + startPoint.x;
			int dy = draggingPoint.y - startPoint.y;
			view.offset(dx, dy);
			startPoint = draggingPoint;
			break;
			
		default:
			super.handleInputEvent(event);
		}
	}
}
