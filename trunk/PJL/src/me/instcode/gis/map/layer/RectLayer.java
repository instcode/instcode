package me.instcode.gis.map.layer;

import java.awt.BasicStroke;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Stroke;

public class RectLayer extends Layer {

	private static final Stroke DRAWING_STROKE = new BasicStroke(
			2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[] {9}, 0);

	private Rectangle tracker = new Rectangle(0, 0, 0, 0);

	@Override
	public void draw(Graphics g) {
		Stroke stroke = ((Graphics2D) g).getStroke();
		((Graphics2D) g).setStroke(DRAWING_STROKE);
		g.drawRect(tracker.x, tracker.y, tracker.width, tracker.height);
		((Graphics2D) g).setStroke(stroke);
	}

	public void setRect(int x, int y, int width, int height) {
		tracker.setBounds(x, y, width, height);
	}
	
	public Rectangle getRect() {
		return tracker;
	}
	
	public void clearRect() {
		tracker.setBounds(0, 0, 0, 0);
	}
}
