package me.instcode.gis.map.tools;

import java.awt.Point;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;

import me.instcode.gis.map.MapView;
import me.instcode.gis.map.layer.RectLayer;

public abstract class RegionSelectionTool extends AbstractMapTool {
	private static final String RECT_DRAWING_LAYER = "RectDrawingLayer";
	
	private Point startPoint;
	private boolean isCanceled = true;
	protected RectLayer rectLayer = new RectLayer();

	public RegionSelectionTool(String name) {
		super(name);
	}
	
	public void handleInputEvent(InputEvent event) {
		MapView view = (MapView) event.getSource();
		switch (event.getID()) {
		case MouseEvent.MOUSE_PRESSED:
		{
			MouseEvent mouseEvent = (MouseEvent)event;
			isCanceled = (mouseEvent.getButton() == MouseEvent.BUTTON3);
			Point point = mouseEvent.getPoint();
			rectLayer.setRect(point.x, point.y, 0, 0);
			if (!isCanceled) {
				startPoint = point;
				view.addLayer(RECT_DRAWING_LAYER, rectLayer);
			}
			else {
				view.repaint();
			}
			break;
		}

		case MouseEvent.MOUSE_DRAGGED:
		{
			if (isCanceled) {
				return;
			}
			MouseEvent mouseEvent = (MouseEvent)event;
			Point point = mouseEvent.getPoint();
			int x = point.x > startPoint.x ? startPoint.x : point.x;
			int y = point.y > startPoint.y ? startPoint.y : point.y;
			rectLayer.setRect(x, y, Math.abs(point.x - startPoint.x), Math.abs(point.y - startPoint.y));
			view.repaint();
			break;
		}
		
		case MouseEvent.MOUSE_RELEASED:
		{
			view.removeLayer(RECT_DRAWING_LAYER);
			if (!isCanceled) {
				performAction((MouseEvent)event);
			}
			view.repaint();
			break;
		}
		
		default:
			super.handleInputEvent(event);
		}
	}

	protected abstract void performAction(MouseEvent event);
}
