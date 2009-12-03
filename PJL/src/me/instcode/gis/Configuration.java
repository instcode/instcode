package me.instcode.gis;

import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.StringTokenizer;

public class Configuration {
	private static Configuration m_instance = null;
	private Properties m_props = null;

	private Configuration() {
		m_props = new Properties();
		try {
			InputStream inputStream = Configuration.class.getClassLoader().getResourceAsStream(
					Constants.CONFIG_CONFIG_PROPERTIES);
			if (inputStream == null) {
				inputStream = new FileInputStream(new File(Constants.CONFIG_CONFIG_PROPERTIES));
			}
			m_props.load(inputStream);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static Configuration getInstance() {
		if (m_instance == null) {
			m_instance = new Configuration();
		}
		return m_instance;
	}

	/**
	 * Boundary of the map in lon, lat.
	 * 
	 * @return Map boundary
	 */
	public Rectangle2D getMapBound() {
		String coordinates = m_props.getProperty("map.bound", "");
		StringTokenizer tokenizer = new StringTokenizer(coordinates, "() ,");
		Rectangle2D.Double bound = null;
		try {
			double[] points = new double[4];
			for (int i = 0; i < 4; i++) {
				points[i] = Double.parseDouble(tokenizer.nextToken());
			}
			bound = new Rectangle2D.Double(points[0], points[1], points[2] - points[0], points[3] - points[1]);
		}
		catch (Exception e) {
			bound = new Rectangle2D.Double(-180.017, -90.017, 360.034, 180.034);
		}
		return bound;
	}

	public String getMapImagePath() {
		return m_props.getProperty("map.image", Constants.DEFAULT_MAP_IMAGE_PATH);
	}

	public String getEtopoFile() {
		return m_props.getProperty("map.etopo", Constants.DEFAULT_ETOPO2_FILE);
	}
}
