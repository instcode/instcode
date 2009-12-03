package me.instcode.gis.map.tools;

import java.awt.Rectangle;
import java.awt.event.MouseEvent;

import me.instcode.gis.map.MapView;

public class ZoomTool extends RegionSelectionTool {
	public static final String NAME = "zoom";
	
	public ZoomTool() {
		super(NAME);
	}

	@Override
	protected void performAction(MouseEvent event) {
		MapView view = (MapView) event.getSource();

		Rectangle region = rectLayer.getRect();
		if (region.width > 10 && region.height > 10) {
			view.zoomTo(rectLayer.getRect());
		}
	}
}
