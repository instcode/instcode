package me.instcode.gis.map.layer;

import java.awt.Rectangle;
import java.awt.event.MouseEvent;

import me.instcode.gis.map.tools.RegionSelectionTool;

public class DomainTool extends RegionSelectionTool {
	private static final String NAME = "domain";

	public DomainTool() {
		super(NAME);
	}
	
	@Override
	protected void performAction(MouseEvent event) {
		Rectangle region = rectLayer.getRect();
		if (region.height == 0 || region.width == 0) {
			return;
		}
		/*final MapView view = (MapView) event.getSource();
		Point2D topleft = view.screenToGeo(new Point(region.x, region.y));
		Point2D botright = view.screenToGeo(new Point(region.x + region.width, region.y + region.height));
		//FIXME Should restrict max/min lon/lat.
		final Rectangle2D domain = Etopo.createDomain(topleft.getX(), botright.getX(), botright.getY(), topleft.getY());
		view.getLayer("domain")).setDomain(domain);*/
		//view.setCursor(Cursor.getPredefinedCursor(Cursor.S_RESIZE_CURSOR));
		//view.setCursor(Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR));
	}
}
