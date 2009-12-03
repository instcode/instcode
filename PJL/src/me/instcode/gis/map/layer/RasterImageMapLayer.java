package me.instcode.gis.map.layer;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import me.instcode.gis.map.MapView;
import me.instcode.gis.map.handler.MapEvent;
import me.instcode.gis.map.handler.MapEventListener;

public class RasterImageMapLayer extends Layer implements MapEventListener {

	private Image image;
	/**
	 * This viewport refers to a portion of the original map image.
	 * The layer uses this boundary to render that portion to the view. 
	 */
	private Rectangle viewport = new Rectangle();
	
	/**
	 * This variable refers to the boundary of the display screen.
	 */
	private Rectangle screen;
	
	private Point minimapPosition;
	private Rectangle minimapViewport;
	private BufferedImage minimapImage;
	private boolean minimapVisible;
	
	public RasterImageMapLayer(Image image) {
		this.image = image;
	}

	public void draw(Graphics g) {
		if (screen == null) {
			return;
		}
		g.drawImage(image,
				screen.x, screen.y, screen.width, screen.height,
				viewport.x, viewport.y,
				viewport.x + viewport.width, viewport.y + viewport.height, null);
		renderMinimap(g);
	}

	private void renderMinimap(Graphics g) {
		if (minimapImage != null && minimapVisible) {
			// Instead of rendering the minimap directly from the map data, we
			// render it to a BufferedImage in advance. This introduces some
			// advantages:
			// - We only have to compute some parameters once (width, height)
			// - In the future, if we want to replace the minimap image, we can
			// do it easy by replacing the BufferedImage...
			// - By default, Swing already uses back buffer technique which
			// renders everything in background, but what if we want to port 
			// this code to another graphics lib? =))
			g.drawImage(minimapImage, minimapPosition.x, minimapPosition.y, null);
			g.drawRect(minimapViewport.x, minimapViewport.y, minimapViewport.width, minimapViewport.height);
		}
	}

	public boolean isMinimapVisible() {
		return minimapVisible;
	}
	
	public void setMinimap(boolean visible) {
		minimapVisible = visible;
	}
	
	/**
	 * Enable minimap at specific position with its size
	 * 
	 * @param position
	 * @param scale
	 */
	public void setMinimap(Point position, double scale) {
		int width = image.getWidth(null);
		int height = image.getHeight(null);
		
		minimapPosition = position;
		minimapImage = new BufferedImage((int)(scale * width), (int)(scale * height), BufferedImage.TYPE_INT_ARGB);
		minimapImage.getGraphics().drawImage(image,
				0, 0, minimapImage.getWidth(), minimapImage.getHeight(),
				0, 0, width, height, null);
		minimapViewport = new Rectangle();
		calculateMinimapViewport(viewport);
		setMinimap(true);
	}

	public void handleMapEvent(MapEvent event) {
		MapView mapView = (MapView)event.getSource();
		
		switch (event.getType()) {
		case MapEvent.SCREEN_SIZE_CHANGED:
			this.screen = new Rectangle(mapView.getSize());
			// Fall through!!!

		case MapEvent.ZOOM_SCALE_CHANGED:
			// Fall through!!!
			
		case MapEvent.VIEW_PORT_CHANGED:
			viewport.setBounds(mapView.getViewport());
			calculateMinimapViewport(viewport);
			break;
		
		}
	}

	/**
	 * Precompute the rectangle of the minimap viewport to be painted in next draw request.
	 */
	private void calculateMinimapViewport(Rectangle viewport) {
		double scale = minimapImage.getHeight() / (double)image.getHeight(null);
		minimapViewport.x = minimapPosition.x + (int) (viewport.x * scale);
		minimapViewport.y = minimapPosition.y + (int) (viewport.y * scale);
		minimapViewport.width = (int) (viewport.width * scale);
		minimapViewport.height = (int) (viewport.height * scale);
	}
}
