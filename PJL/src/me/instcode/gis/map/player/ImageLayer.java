package me.instcode.gis.map.player;

import java.awt.Graphics;
import java.awt.Image;

import me.instcode.gis.map.LayerView;
import me.instcode.gis.map.layer.Layer;

public class ImageLayer extends Layer implements Screen {

	private Image image;
	private LayerView view;

	public ImageLayer(LayerView view) {
		this(view, null);
	}
	
	public ImageLayer(LayerView view, Image image) {
		this.view = view;
		setRenderedImage(image);
	}

	public void draw(Graphics g) {
		g.drawImage(image, 0, 0, null);
	}

	public void setRenderedImage(Image image) {
		this.image = image;
		setVisible(image != null);
		if (isVisible()) {
			view.repaint();
		}
	}
}
