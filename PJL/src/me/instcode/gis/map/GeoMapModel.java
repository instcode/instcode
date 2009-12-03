package me.instcode.gis.map;

import java.awt.Image;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

import me.instcode.gis.map.handler.MapEvent;
import me.instcode.gis.map.handler.MapEventListener;


public class GeoMapModel implements MapModel {
	private double zoomScale;
	private Point2D viewpoint;
	private Rectangle2D bounds;
	
	private int width, height;
	private Image image;
	private List<MapEventListener> listeners = new ArrayList<MapEventListener>();

	public GeoMapModel(Image image, int width, int height, Rectangle2D bounds) {
		this.image = image;
		this.width = width;
		this.height = height;
		this.bounds = bounds;
		this.zoomScale = 1;
		this.viewpoint = new Point2D.Double(0, 0);
	}

	public Image getMapData() {
		return image;
	}
	
	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}

	/**
	 * Add a MapEvent listener.
	 * 
	 * @param listener
	 */
	public void addListener(MapEventListener listener) {
		listeners.add(listener);
	}

	/**
	 * Remove the given listener from the list of listeners.
	 * 
	 * @param listener
	 * @return true if the listener is in the list.
	 */
	public boolean removeListener(MapEventListener listener) {
		return listeners.remove(listener);
	}
	
	public Rectangle2D getBounds() {
		return bounds;
	}
	
	public Point2D getViewPoint() {
		return viewpoint;
	}

	public void setViewpoint(double x, double y) {
		this.viewpoint.setLocation(x, y);
		fireMapEvent(new MapEvent(this, MapEvent.VIEW_PORT_CHANGED));
	}

	public void setViewpoint(Point2D viewpoint) {
		setViewpoint(viewpoint.getX(), viewpoint.getY());
	}

    public void move(double dx, double dy) {
    	setViewpoint(viewpoint.getX() + dx, viewpoint.getY() + dy);
    }

    public double getZoomScale() {
    	return zoomScale;
    }

    public void setZoomScale(double zoomScale) {
    	if (zoomScale <= 0) {
    		this.zoomScale = 0.1;
    	}
    	else if (zoomScale >= 20) {
    		this.zoomScale = 20;
    	}
    	else {
    		this.zoomScale = zoomScale;
    	}
    	fireMapEvent(new MapEvent(this, MapEvent.ZOOM_SCALE_CHANGED));
    }

	public void setZoomScale(Point2D viewpoint, double zoomScale) {
		this.viewpoint = viewpoint;
		setZoomScale(zoomScale);
	}
	
	protected void fireMapEvent(MapEvent event) {
		for (MapEventListener listener : listeners) {
			listener.handleMapEvent(event);
		}
	}
}
