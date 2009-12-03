package me.instcode.gis.map.player;

import java.awt.Image;

public class Waiter implements Screen {
	protected boolean done = false;

	public boolean isDone() {
		return done;
	}

	public void setRenderedImage(Image image) {
		done = true;
	}
}
