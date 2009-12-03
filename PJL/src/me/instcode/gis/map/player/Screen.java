package me.instcode.gis.map.player;

import java.awt.Image;

/**
 * A virtual screen to display image
 * 
 */
public interface Screen {
	/**
	 * Update the screen's image
	 * 
	 * @param image The image to be displayed 
	 */
	public void setRenderedImage(Image image);
}
