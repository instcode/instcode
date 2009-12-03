package me.instcode.gis.map.player;

import java.awt.Image;

public interface Source {

	public abstract Image getFrame(int index);

	public abstract int getLength();

}