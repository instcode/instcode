package me.instcode.gis.map;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Point2D;

public interface MapAPI {
	
	/**
	 * Convert a (lon, lat) point to current map position.
	 *  
	 * @param point Geometry point (lon, lat).
	 * @return Screen coordinates.
	 */
	public Point geoToMap(Point2D point);
	
	/**
	 * Convert a point on map to (longitude, latitude) position.
	 *   
	 * @param point Map position to be converted.
	 * @return Geometry point (lon, lat).
	 */
	public Point2D mapToGeo(Point point);
	
	/**
	 * Convert a (lon, lat) point to current screen position.
	 * 
	 * @param point Geometry point to be converted.
	 * @return
	 */
	public Point geoToScreen(Point2D point);
	
	/**
	 * Convert a point on current screen to (lon, lat) in the world map.
	 *  
	 * @param point Screen point to be converted.
	 * @return
	 */
	public Point2D screenToGeo(Point point);
	
	/**
	 * Zoom the viewport to the give rectangle on the map.
	 * 
	 * @param region
	 * 		The region to be zoomed to.
	 */
	public void zoomTo(Rectangle region);
	
	/**
	 * Zoom to that specific point. That point will be retain
	 * its position on the screen. 
	 * 
	 * @param point
	 * @param zoomIn
	 */
	public void zoomTo(Point point, boolean zoomIn);
	
	/**
	 * Move the current viewpoint to the given point on screen. 
	 * 
	 * @param point
	 * 		The point on screen to be moved to.
	 */
	public void moveTo(Point point);
	
	/**
	 * Move the current viewport to the given location on screen.
	 * 
	 * @param dx x position
	 * @param dy y position
	 */
	public void moveTo(int x, int y);
	
	/**
	 * Move the current viewport by the given offsets
	 * 
	 * @param dx
	 * @param dy
	 */
	public void offset(int dx, int dy);
}
