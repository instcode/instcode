package me.instcode.gis.map;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

public interface MapModel {
	
	/**
	 * Get boundaries of the current map.
	 * 
	 * @return Bounds in lon, lat.
	 */
	public Rectangle2D getBounds();
	/**
	 * Get the center of the current viewport.
	 * 
	 * @return The viewpoint.
	 */
	public Point2D getViewPoint();

	/**
	 * Set the center of the current viewport 
	 * 
	 * @param x Longitude
	 * @param y Latitude
	 */
	public void setViewpoint(double x, double y);

	/**
	 * Set the center of the current viewport
	 * 
	 * @param viewpoint The viewpoint in (lon, lat)
	 */
	public void setViewpoint(Point2D viewpoint);

	/**
	 * Move the current viewport by the given offsets on lon/lat axes.
	 * 
	 * @param dx Offset on longitude axis
	 * @param dy Offset on latitude axis
	 */
	public void move(double dx, double dy);

	/**
	 * Get current zoom level.
	 * 
	 * @return The current zoom scale
	 */
	public double getZoomScale();

	/**
	 * Set zoom level.
	 * 
	 * @param zoomScale The zoom scale to be set.
	 */
	public void setZoomScale(double zoomScale);
	
	/**
	 * Zoom to specific viewpoint.
	 * 
	 * @param viewpoint
	 * @param zoomScale
	 */
	public void setZoomScale(Point2D viewpoint, double zoomScale);
	
	/**
	 * Get map width in pixel.
	 * 
	 * @return
	 */
	public int getWidth();
	
	/**
	 * Get map height in pixel.
	 * @return
	 */
	public int getHeight();
}