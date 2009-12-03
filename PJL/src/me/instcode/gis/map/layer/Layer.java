package me.instcode.gis.map.layer;

import java.awt.Graphics;

public abstract class Layer {

	private boolean isVisible;
	
	public Layer() {
		this.setVisible(true);
	}
	
	public void setVisible(boolean isVisible) {
		this.isVisible = isVisible;
	}
	
	public boolean isVisible() {
		return isVisible;
	}

	public abstract void draw(Graphics g);
}
