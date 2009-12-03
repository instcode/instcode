package me.instcode.gis;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

public class Constants {
	static final String DEFAULT_TUNAMI_N2_EXECUTABLE_FILE = "tunami_n2.v0.2.exe";
	static final String DEFAULT_GAUGES_TXT = "gauges.txt";
	static final String DEFAULT_PARA_TXT = "para.txt";
	static final String DEFAULT_JDBC_PASSWORD = "password";
	static final String DEFAULT_JDBC_USERNAME = "username";
	static final String DEFAULT_JDBC_URL = "jdbc:postgresql://localhost/tsunami";
	static final String DEFAULT_JDBC_DRIVER = "org.postgresql.Driver";
	static final String CONFIG_CONFIG_PROPERTIES = "config.properties";
	
	public static final String DEFAULT_MAP_IMAGE_PATH = "maps/relevantAreaMap_small.png";
	public static final String WORLD_MAP_IMAGE_PATH = "maps/world.png";
	public static final String ZOOM_IN_TOOL_ICON = "/icons/zoomIn.png";
	public static final String ZOOM_OUT_TOOL_ICON = "/icons/zoomOut.png";
	public static final String ZOOM_TOOL_ICON_PRESSED = "/icons/zoomInPressed1.png";
	public static final String SCROLL_TOOL_ICON = "/icons/hand.png";
	public static final String SCROLL_TOOL_ICON_PRESSED = "/icons/handPressed.png";
	public static final String RUN_ICON = "/icons/play.png";
	public static final String STOP_ICON = "/icons/stop.png";
	public static final String PLAY_ICON = "/icons/play.png";
	public static final String PAUSE_ICON = "/icons/pause.png";
	public static final String STEP_FORWARD_ICON = "/icons/stepForward.png";
	public static final String STEP_BACKWARD_ICON = "/icons/stepBackward.png";
	public static final String UNDO_ICON = "/icons/undo.png";
	public static final String REDO_ICON = "/icons/redo.png";
	public static final String DEFAULT_ETOPO2_FILE = "./data/etopo2.bin";

	public static BufferedImage readImageFromJar(String resourceName) {
		try {
			InputStream is = Constants.class.getResourceAsStream(resourceName);

			return ImageIO.read(is);

		} catch (Exception ex) {
			ex.printStackTrace();
			return new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
		}
	}

	public static ImageIcon createIcon(String resourceName) {
		try {
			InputStream is = Constants.class.getResourceAsStream(resourceName);

			Image img = ImageIO.read(is);

			return new ImageIcon(img);
		} catch (Exception ex) {
			ex.printStackTrace();
			return new ImageIcon();
		}
	}

}
