package me.instcode.gis.map;

import java.awt.event.InputEvent;
import java.util.ArrayList;
import java.util.List;

import me.instcode.gis.map.tools.MapTool;

public class MapTools {
	
	private MapTool activeMapTool;
	private List<MapTool> mapTools = new ArrayList<MapTool>();

	public void handleEvent(InputEvent e) {
		if (activeMapTool != null) {
			activeMapTool.handleInputEvent(e);
		}
	}
	/**
	 * Add the given map tool to the current map component.
	 * 
	 * @param tool a MapTool to be added.
	 */
	public void addTool(MapTool tool) {
		mapTools.add(tool);
	}

	/**
	 * Remove the given map tool from the current map component.
	 * 
	 * @param tool a MapTool to be removed.
	 */
	public boolean removeTool(MapTool tool) {
		return mapTools.remove(tool);
	}

	/**
	 * Set the given map tool to be active and ready to use.
	 * 
	 * @param tool a MapTool to be active.
	 */
	public void setActiveTool(MapTool tool) {
		activeMapTool = tool;
	}

	/**
	 * Get current active map tool.
	 * 
	 * @return Active map tool.
	 */
	public MapTool getActiveTool() {
		return activeMapTool;
	}

	/**
	 * Get all map tools that associate to this map component.
	 * 
	 * @return All internal map tools.
	 */
	public List<MapTool> getMapTools() {
		return mapTools;
	}
}
