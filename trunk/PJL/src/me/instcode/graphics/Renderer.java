package me.instcode.graphics;

import java.awt.Image;
import java.io.File;

public interface Renderer {

	File getFile();

	boolean render();

	Image renderedFrame();

}
