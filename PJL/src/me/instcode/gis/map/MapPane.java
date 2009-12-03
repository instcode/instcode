package me.instcode.gis.map;

import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import javax.swing.AbstractAction;
import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import me.instcode.gis.Configuration;
import me.instcode.gis.Constants;
import me.instcode.gis.map.handler.InputEventListener;
import me.instcode.gis.map.handler.MapEvent;
import me.instcode.gis.map.handler.MapEventListener;
import me.instcode.gis.map.layer.DomainTool;
import me.instcode.gis.map.layer.Layer;
import me.instcode.gis.map.layer.RasterImageMapLayer;
import me.instcode.gis.map.tools.MapTool;
import me.instcode.gis.map.tools.PanTool;
import me.instcode.gis.map.tools.ZoomTool;

public class MapPane extends JPanel implements MapEventListener {

	public static final String RASTER_IMAGE_MAP_LAYER = "map";
	public static final String DOMAIN_LAYER = "domain";
	public static final String GAUGE_LAYER = "gauge";
	public static final String FAULT_BOXES_LAYER = "fault";
	
	private static final String DOMAIN_ACTION = "Domain";
	private static final String ZOOM_ACTION = "Zoom";
	private static final String PAN_ACTION = "Pan";
	
	private static final NumberFormat LON_LAT_FORMAT = new DecimalFormat("0.0000");
	private ButtonGroup buttonGroup = new ButtonGroup();
	private JToolBar toolBar;
	private MapView mapView;
	private JLabel status;
	private JSlider zoomSlider;
	
	private MapTools mapTools;

	class InputCaptureLayer extends Layer implements InputEventListener {

		public InputCaptureLayer() {
			setVisible(false);
		}
		
		@Override
		public void draw(Graphics g) {
		}

		public void handleInputEvent(InputEvent event) {
			MapView view = (MapView) event.getSource();
			switch (event.getID()) {

			case MouseEvent.MOUSE_MOVED:
				Point point = ((MouseEvent)event).getPoint();
				Point2D screenToGeo = view.screenToGeo(point);
				status.setText(LON_LAT_FORMAT.format(screenToGeo.getX()) + ", " + LON_LAT_FORMAT.format(screenToGeo.getY()));
				break;
			}			
		}
	}
	/**
	 * Create the application
	 */
	public MapPane() {
		super(new BorderLayout());
		createContents();
	}

	/**
	 * Initialize the contents of the frame
	 */
	private void createContents() {
		add(createToolbar(), BorderLayout.NORTH);
		mapView = createMapView();
		add(mapView, BorderLayout.CENTER);
		status = new JLabel(" ");
		add(status, BorderLayout.SOUTH);
	}

	private MapView createMapView() {
		BufferedImage mapImage = Constants.readImageFromJar(Configuration.getInstance().getMapImagePath());
		Rectangle2D mapBounds = Configuration.getInstance().getMapBound();
		GeoMapModel mapModel = new GeoMapModel(mapImage, mapImage.getWidth(), mapImage.getHeight(), mapBounds);
		
		MapView mapView = new MapView(mapModel, mapTools);
		mapModel.addListener(mapView);
		mapModel.addListener(this);
		final RasterImageMapLayer mapLayer = new RasterImageMapLayer(mapModel.getMapData());
		mapView.addLayer("InputLayer", new InputCaptureLayer());
		mapView.addLayer(RASTER_IMAGE_MAP_LAYER, mapLayer);
		mapLayer.setMinimap(new Point(10, 10), 0.07);
		return mapView;
	}

	private JToolBar createToolbar() {
		mapTools = new MapTools();
		toolBar = new JToolBar();
		addMapTool(PAN_ACTION, KeyStroke.getKeyStroke(KeyEvent.VK_M, 0, true),
				Constants.createIcon(Constants.SCROLL_TOOL_ICON_PRESSED), new PanTool());
		addMapTool(ZOOM_ACTION, KeyStroke.getKeyStroke(KeyEvent.VK_Z, 0, true),
				Constants.createIcon(Constants.ZOOM_IN_TOOL_ICON), new ZoomTool());
		addMapTool(DOMAIN_ACTION, KeyStroke.getKeyStroke(KeyEvent.VK_D, 0, true),
				Constants.createIcon(Constants.ZOOM_OUT_TOOL_ICON), new DomainTool());
		getActionMap().get(PAN_ACTION).actionPerformed(null);
		
		zoomSlider = new JSlider(1, 200, 10);
		zoomSlider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				if (zoomSlider.getValueIsAdjusting()) {
					double scale = zoomSlider.getValue() / 10.0;
					mapView.getMapModel().setZoomScale(scale);
				}
			}
		});
		toolBar.add(zoomSlider);

		final JButton resetButton = new JButton();
		resetButton.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				mapView.getMapModel().setZoomScale(new Point2D.Double(0, 0), 1.0);
			}
		});
		resetButton.setText("Reset");
		toolBar.add(resetButton);
		return toolBar;
	}
	
	private void addMapTool(String actionName, KeyStroke keyStroke, Icon icon, final MapTool tool) {
		final JToggleButton button = new JToggleButton();
		button.setAction(new AbstractAction(actionName, icon) {
			public void actionPerformed(ActionEvent e) {
				buttonGroup.setSelected(button.getModel(), true);
				mapTools.setActiveTool(tool);
			}
		});
		mapTools.addTool(tool);
		toolBar.add(button);
		buttonGroup.add(button);
		getInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(keyStroke, actionName);
		getActionMap().put(actionName, button.getAction());
	}

	public void handleMapEvent(MapEvent event) {
		MapModel map = (MapModel) event.getSource();
		zoomSlider.setValue((int) (map.getZoomScale() * 10.0));
	}

	public MapView getMapView() {
		return mapView;
	}
}
