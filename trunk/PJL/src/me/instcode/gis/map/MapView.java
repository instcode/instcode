package me.instcode.gis.map;

import java.awt.AWTEvent;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ComponentEvent;
import java.awt.event.InputEvent;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import me.instcode.gis.map.handler.InputEventListener;
import me.instcode.gis.map.handler.MapEvent;
import me.instcode.gis.map.handler.MapEventListener;
import me.instcode.gis.map.layer.Layer;

/**
 * This implements a simple map viewer using layering technique.
 * Each layer will be drawn in order so that the top most layer
 * will be drawn last.<br>
 * <br>
 * To receive notification from the viewer, a visible layer should
 * implement appropriate listeners. Currently, this viewer supports
 * 2 types of listener: one is {@link MapEventListener} & one is
 * {@link InputEventListener}.<br>
 * <br>
 * 
 *
 */
public class MapView extends LayerView implements MapAPI, MapEventListener {

	private static final long serialVersionUID = 1L;
	private MapModel model;
	private MapTools mapTools;
	
	/**
	 * Viewport is a rectangle that's holding a visible region of
	 * the map in pixels. This will be used to plot that specific
	 * region to screen, not the whole map.
	 */
	private Rectangle viewport = new Rectangle();
	
	public MapView(MapModel map, MapTools mapTools) {
		this.mapTools = mapTools;
		this.model = map;
		this.enableEvents(AWTEvent.MOUSE_EVENT_MASK | AWTEvent.MOUSE_MOTION_EVENT_MASK | AWTEvent.MOUSE_WHEEL_EVENT_MASK | AWTEvent.COMPONENT_EVENT_MASK);
	}

	public MapModel getMapModel() {
		return model;
	}

	public void moveTo(int x, int y) {
		Point2D viewpoint = screenToGeo(new Point(x, y));
		model.setViewpoint(viewpoint);
	}

	public void offset(int dx, int dy) {
		if (model.getZoomScale() == 0) {
			return;
		}
		Rectangle2D bounds = model.getBounds();
		double geodx = dx * bounds.getWidth() / (model.getWidth() * model.getZoomScale());
		double geody = dy * bounds.getHeight() / (model.getHeight() * model.getZoomScale());
		model.move(geodx, geody);
	}
	
	public void moveTo(Point point) {
		moveTo(point.x, point.y);
	}

	public void zoomTo(Rectangle region) {
		Dimension screen = getSize();
		double dx = Math.abs(region.getWidth()) / model.getZoomScale();
		double dy = Math.abs(region.getHeight()) / model.getZoomScale();
		double scale = Math.min(screen.getWidth() / dx, screen.getHeight() / dy);
		Point center = new Point((int)region.getCenterX(), (int)region.getCenterY());
		model.setZoomScale(screenToGeo(center), scale);
	}
	
	public void zoomTo(Point point, boolean zoomIn) {
		Point2D zoomPoint = screenToGeo(point);
		Rectangle2D bounds = model.getBounds();
		if (zoomPoint.getX() > bounds.getMaxX() || zoomPoint.getX() < bounds.getMinX()
				|| zoomPoint.getY() > bounds.getMaxY() || zoomPoint.getY() < bounds.getMinY()) {
			return;
		}
		Dimension screen = getSize();
		double zoomScale = model.getZoomScale();
		double scale = zoomScale + (zoomIn ? 0.25 : -0.25) ;
		double coeff = (scale - zoomScale) / (scale * zoomScale);
		double centerX = viewport.x + point.x * coeff + screen.getWidth() / scale / 2.0;
		double centerY = viewport.y  + point.y * coeff + screen.getHeight() / scale / 2.0;
		Point2D viewpoint = mapToGeo(new Point(round(centerX), round(centerY)));
		model.setZoomScale(viewpoint, scale);
	}

	/**
	 * This is similar to Math.round function but it also concerns
	 * about the sign of the input value. It actually rounds the
	 * value to the nearest one (may smaller or greater).
	 * 
	 * @param value
	 * @return
	 */
	private int round(double value) {
		if (value < 0) {
			return (int) Math.floor(value - 0.5);
		}
		return (int) Math.floor(value + 0.5);
	}

	public Point geoToScreen(Point2D point) {
		Rectangle2D bounds = model.getBounds();
		double y = ((-point.getY() - bounds.getY()) * model.getHeight()) / bounds.getHeight() - viewport.y;
		double x = ((point.getX() - bounds.getX()) * model.getWidth()) / bounds.getWidth() - viewport.x;
		y *= model.getZoomScale();
		x *= model.getZoomScale();
		return new Point(round(x), round(y));
	}

	public Point2D screenToGeo(Point point) {
		Rectangle2D bounds = model.getBounds();
		double x = viewport.x + (point.getX() / model.getZoomScale());
		double y = viewport.y + (point.getY() / model.getZoomScale());
		x = (x * bounds.getWidth() / model.getWidth()) + bounds.getX();
		y = (y * bounds.getHeight() / model.getHeight()) + bounds.getY();
		return new Point2D.Double(x, -y);
	}
	
	public Point geoToMap(Point2D point) {
		Rectangle2D bounds = model.getBounds();
		double x = ((point.getX() - bounds.getX()) * model.getWidth()) / bounds.getWidth();
		double y = ((-point.getY() - bounds.getY()) * model.getHeight()) / bounds.getHeight();
		return new Point(round(x), round(y));
	}

	public Point2D mapToGeo(Point point) {
		Rectangle2D bounds = model.getBounds();
		double x = (point.x * bounds.getWidth() / model.getWidth()) + bounds.getX();
		double y = (point.y * bounds.getHeight() / model.getHeight()) + bounds.getY();
		return new Point2D.Double(x, -y);
	}
	
	@Override
	protected void processEvent(AWTEvent e) {
		super.processEvent(e);
		if (e.getID() == ComponentEvent.COMPONENT_RESIZED) {
			calculateViewportSize();
			fireMapEvent(new MapEvent(this, MapEvent.SCREEN_SIZE_CHANGED));
		}
		else {
			if (e instanceof InputEvent) {
				fireInputEvent((InputEvent) e);
			}
		}
	}
	
	/**
	 * Calculate viewport size due to changes of display size or map scale.
	 * Changes in viewport's size will affect to its position. Thus, this
	 * also updates viewport's position.
	 */
	private void calculateViewportSize() {
		Dimension screen = getSize();
		// Calculate viewport's size
		double scale = model.getZoomScale();
		double vpWidth = screen.getWidth() / scale;
		double vpHeight = screen.getHeight() / scale;
		Point2D viewpoint = model.getViewPoint();
		Rectangle2D bounds = model.getBounds();
		double x = ((viewpoint.getX() - bounds.getX()) * model.getWidth()) / bounds.getWidth();
		double y = ((-viewpoint.getY() - bounds.getY()) * model.getHeight()) / bounds.getHeight();
		viewport.setRect(round(x - vpWidth / 2), round(y - vpHeight / 2), vpWidth, vpHeight);
	}

	public Rectangle getViewport() {
		return viewport;
	}
	
	/**
	 * Fire a map event to all layers. Any layer which implements
	 * {@link MapEventListener} will receive the event.
	 * 
	 * @param event Map event to be sent 
	 */
	protected void fireMapEvent(MapEvent event) {
		for (Layer layer : getLayers()) {
			if (layer instanceof MapEventListener) {
				((MapEventListener)layer).handleMapEvent(event);
			}
		}
	}

	/**
	 * Fire an input event to all {@link InputEventListener} listeners.
	 * 
	 * @param event
	 */
	protected void fireInputEvent(InputEvent event) {
		mapTools.handleEvent(event);
		for (Layer layer : getLayers()) {
			if (layer instanceof InputEventListener) {
				((InputEventListener)layer).handleInputEvent(event);
			}
		}
	}
	
	public void handleMapEvent(MapEvent event) {
		switch (event.getType()) {
		case MapEvent.VIEW_PORT_CHANGED:
			calculateViewportSize();
			fireMapEvent(new MapEvent(this, MapEvent.VIEW_PORT_CHANGED));
			repaint();
			break;
			
		case MapEvent.ZOOM_SCALE_CHANGED:
			calculateViewportSize();
			fireMapEvent(new MapEvent(this, MapEvent.ZOOM_SCALE_CHANGED));
			repaint();
			break;
		}
	}

	public MapTools getMapTools() {
		return mapTools;
	}
}
