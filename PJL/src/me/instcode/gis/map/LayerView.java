package me.instcode.gis.map;

import java.awt.Graphics;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JComponent;

import me.instcode.gis.map.layer.Layer;

public class LayerView extends JComponent {

	private static final long serialVersionUID = 1L;

	/**
	 * This variable holds the order of each layer
	 */
	private List<Layer> layers = new ArrayList<Layer>();
	
	/**
	 * This map holds all the layers by their names for fast looking up
	 */
	private Map<String, Layer> map = new HashMap<String, Layer>();
	
	/**
	 * Add a layer identified by its name
	 * 
	 * @param name
	 * @param layer
	 */
	public void addLayer(String name, Layer layer) {
		layers.add(layer);
		map.put(name, layer);
	}
	
	/**
	 * Remove a layer by its name
	 * 
	 * @param name
	 */
	public void removeLayer(String name) {
		Layer layer = map.remove(name);
		layers.remove(layer);
	}
	
	/**
	 * Pick up a layer by the given name
	 * 
	 * @param name
	 * @return
	 */
	public Layer getLayer(String name) {
		return map.get(name);
	}
	
	/**
	 * Get all the layers.
	 * 
	 * @return
	 */
	public List<Layer> getLayers() {
		return layers;
	}
	
	@Override
	public void paint(Graphics g) {
		for (Layer layer : layers) {
			if (layer.isVisible()) {
				layer.draw(g);
			}
		}
	}
}
