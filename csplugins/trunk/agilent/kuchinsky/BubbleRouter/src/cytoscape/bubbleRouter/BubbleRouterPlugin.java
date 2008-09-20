package cytoscape.bubbleRouter;

import giny.model.Node;
import giny.view.NodeView;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Point2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

import javax.swing.AbstractAction;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import javax.swing.JWindow;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.undo.AbstractUndoableEdit;

import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;
import cytoscape.data.readers.CyAttributesReader;
import cytoscape.groups.CyGroup;
import cytoscape.groups.CyGroupManager;
import cytoscape.groups.CyGroupViewer;
import cytoscape.plugin.CytoscapePlugin;
import cytoscape.util.undo.CyUndo;
import cytoscape.view.CyNetworkView;
import cytoscape.view.CytoscapeDesktop;
import ding.view.DGraphView;

/**
 * BubbleRouterPlugin is the main plugin class, setting layout and context menu
 * items, defining mouse and selection events, as well as layout region drawing,
 * group creation and import from xGMML.
 * 
 * @author Allan Kuchinsky, Alexander Pico, Kristina Hanspers.
 * 
 */
public class BubbleRouterPlugin extends CytoscapePlugin implements
		MouseListener, MouseMotionListener, PropertyChangeListener,
		CyGroupViewer {

	/**
	 * A region is created by dragging the mouse while shift is pressed. Set to
	 * TRUE when dragging begins and to FALSE when it ends. The value is checked
	 * by mouseReleased() and mouseDragged().
	 */
	boolean dragging;

	int startx, starty; // X,y of mouse when started.

	protected Point2D startPoint; // Point of mouse when started.

	int mousex, mousey; // X,y of mouse during dragging.

	protected Point2D nextPoint; // Point of mouse during dragging.

	/**
	 * flags for moving and stretching of region
	 */
	private boolean moving = false;

	private boolean stretching = false;

	private static final int NOT_ON_EDGE = -1;

	private static final int TOP = 1;

	private static final int BOTTOM = 2;

	private static final int LEFT = 3;

	private static final int RIGHT = 4;

	private static final int TOP_LEFT = 5;

	private static final int TOP_RIGHT = 6;

	private static final int BOTTOM_LEFT = 7;

	private static final int BOTTOM_RIGHT = 8;

	private static Point2D[] _undoOffsets;

	private static Point2D[] _redoOffsets;

	/**
	 * Array of NodeViews
	 */
	private static NodeView[] _nodeViews;

	/**
	 * Flags for region selection and resize. Need to store region being
	 * stretched and edge being stretched because mouse movement may get ahead
	 * of the stretching.
	 */
	private int onEdge = NOT_ON_EDGE;

	private int edgeBeingStretched = NOT_ON_EDGE;

	private int edgeTolerance = 5; // number of 'cushion' pixels for edge

	private int whichEdge = NOT_ON_EDGE;

	private LayoutRegion regionToStretch = null;

	private LayoutRegion pickedRegion = null;

	private LayoutRegion oldPickedRegion = null;

	public static final int SELECTED = 1;

	public static final int UNSELECTED = 2;

	private List<NodeView> boundedNodeViews = new ArrayList<NodeView>();

	boolean handlerStarted = false;

	/**
	 * For Region Context Menu
	 */
	private JPopupMenu menu = new JPopupMenu("Layout Region");

	public static String DELETE_REGION = "Delete Region";

	public static String LAYOUT_REGION = "Layout Region";

	public static String ITEM_HELP = "Online Help";

	public static String UNCROSS_EDGES = "Uncross Edges";

	public static String MOVE_FORWARD = "Bring Forward";

	public static String MOVE_BACKWARD = "Send Backward";

	public static String MOVE_TO_FRONT = "Bring To Front";

	public static String MOVE_TO_BACK = "Send To Back";

	public static String ORDER = "Order";

	public static String COLOR = "Color";

	/**
	 * For Cytoscape Menu
	 */
	public static String DELETE_ALL_REGIONS = "Delete All Regions";

	public static String BUBBLE_HELP = "Bubble Router Help";

	/**
	 * For Region Label and MouseOver-sensitive Display
	 */
	private JLabel labelRegionInfo = new JLabel(" ");

	private JWindow toolTip = new JWindow(new JFrame());

	private static final int NOT_IN_AREA = -1;

	private static final int IN_AREA = 1;

	/**
	 * Parameters for CyGroups
	 */
	public static final String viewerName = "bubbleRouter";

	private static GroupPanel groupPanel = null;

	public static final double VERSION = 1.0;

	public static final String REGION_X_ATT = "__Region_x";

	public static final String REGION_Y_ATT = "__Region_y";

	public static final String REGION_W_ATT = "__Region_w";

	public static final String REGION_H_ATT = "__Region_h";

	private static final String REGION_NAME_ATT = "__Region_name";

	private static final String REGION_COLORINT_ATT = "__Region_colorInt";

	private static final String REGION_NETWORK_ATT = "__Region_network";

	private int viewID = 0;

	/**
	 * Constructor
	 * 
	 * The BubbleRouter plugin does not run from the plugin menu. It is
	 * available whenever there is a network view.
	 */
	public BubbleRouterPlugin() {

		// initialization statement printed during plugin loading
		System.out.println("BubbleRouter " + VERSION + " initialized");

		// Listen for Network View Focus
		Cytoscape.getDesktop().getSwingPropertyChangeSupport()
				.addPropertyChangeListener(
						CytoscapeDesktop.NETWORK_VIEW_FOCUSED, this);

		// Listen for Network View Destruction
		Cytoscape.getDesktop().getSwingPropertyChangeSupport()
				.addPropertyChangeListener(
						CytoscapeDesktop.NETWORK_VIEW_DESTROYED, this);

		// Listen for Network View Destruction
		Cytoscape.getDesktop().getSwingPropertyChangeSupport()
				.addPropertyChangeListener(
						CytoscapeDesktop.NETWORK_VIEW_CREATED, this);

		/**
		 * Build Context Menus per Region
		 */
		// Context menu items
		JMenuItem uncrossEdgesItem = new JMenuItem(UNCROSS_EDGES);
		JMenuItem deleteRegionItem = new JMenuItem(DELETE_REGION);
		JMenuItem layoutRegionItem = new JMenuItem(LAYOUT_REGION);
		JMenu orderRegionSubmenu = new JMenu(ORDER);
		JMenu colorRegionSubmenu = new JMenu(COLOR);
		JMenuItem forwardRegionItem = new JMenuItem(MOVE_FORWARD);
		JMenuItem backwardRegionItem = new JMenuItem(MOVE_BACKWARD);
		JMenuItem frontRegionItem = new JMenuItem(MOVE_TO_FRONT);
		JMenuItem backRegionItem = new JMenuItem(MOVE_TO_BACK);
		JMenuItem redItem = new JMenuItem("Red");
		JMenuItem greenItem = new JMenuItem("Green");
		JMenuItem blueItem = new JMenuItem("Blue");
		JMenuItem orangeItem = new JMenuItem("Orange");
		JMenuItem cyanItem = new JMenuItem("Cyan");
		JMenuItem magentaItem = new JMenuItem("Magenta");
		JMenuItem dgrayItem = new JMenuItem("Black");
		JMenuItem helpItem = new JMenuItem(ITEM_HELP);

		// Tool tips per item
		uncrossEdgesItem
				.setToolTipText("Run edge minimization algorithm to reduce edge crossings");
		deleteRegionItem.setToolTipText("Delete region");
		layoutRegionItem
				.setToolTipText("Run layout algorithm on all nodes associated with region");
		orderRegionSubmenu.setToolTipText("Move regions forward and back");
		colorRegionSubmenu.setToolTipText("Choose color for region border");
		helpItem.setToolTipText("Open online help");

		// Popup action listeners per item
		RegionPopupActionListener popupActionListener = new RegionPopupActionListener();
		uncrossEdgesItem.addActionListener(popupActionListener);
		deleteRegionItem.addActionListener(popupActionListener);
		layoutRegionItem.addActionListener(popupActionListener);
		forwardRegionItem.addActionListener(popupActionListener);
		backwardRegionItem.addActionListener(popupActionListener);
		frontRegionItem.addActionListener(popupActionListener);
		backRegionItem.addActionListener(popupActionListener);
		redItem.addActionListener(popupActionListener);
		greenItem.addActionListener(popupActionListener);
		blueItem.addActionListener(popupActionListener);
		orangeItem.addActionListener(popupActionListener);
		cyanItem.addActionListener(popupActionListener);
		magentaItem.addActionListener(popupActionListener);
		dgrayItem.addActionListener(popupActionListener);
		helpItem.addActionListener(popupActionListener);

		// Add items to context menu
		orderRegionSubmenu.add(frontRegionItem);
		orderRegionSubmenu.add(forwardRegionItem);
		orderRegionSubmenu.add(backwardRegionItem);
		orderRegionSubmenu.add(backRegionItem);
		colorRegionSubmenu.add(redItem);
		colorRegionSubmenu.add(greenItem);
		colorRegionSubmenu.add(blueItem);
		colorRegionSubmenu.add(orangeItem);
		colorRegionSubmenu.add(cyanItem);
		colorRegionSubmenu.add(magentaItem);
		colorRegionSubmenu.add(dgrayItem);
		menu.add(uncrossEdgesItem);
		menu.add(layoutRegionItem);
		menu.add(deleteRegionItem);
		menu.add(orderRegionSubmenu);
		menu.add(colorRegionSubmenu);
		menu.add(helpItem);
		menu.setVisible(false);
		
		/**
		 * Cytoscape Menu Items: Layout > Delete All Regions Help > Bubble
		 * Router Help
		 */
		// 
		JMenuItem getBubbleHelp = new JMenuItem(BUBBLE_HELP);
		JMenuItem deleteAllRegionsItem = new JMenuItem(DELETE_ALL_REGIONS);

		getBubbleHelp
				.setToolTipText("Open online help for Bubble Router plugin");
		deleteAllRegionsItem.setToolTipText("Delete all Bubble Router regions");

		GetBubbleHelpListener getBubbleHelpListener = new GetBubbleHelpListener();
		getBubbleHelp.addActionListener(getBubbleHelpListener);
		DeleteAllRegionsActionListener deleteAllRegionsListener = new DeleteAllRegionsActionListener();
		deleteAllRegionsItem.addActionListener(deleteAllRegionsListener);

		Cytoscape.getDesktop().getCyMenus().getHelpMenu().add(new JSeparator());
		Cytoscape.getDesktop().getCyMenus().getHelpMenu().add(getBubbleHelp);
		Cytoscape.getDesktop().getCyMenus().getLayoutMenu().add(
				deleteAllRegionsItem);

		/**
		 * Add Group Interface to CytoPanel1 for Regions
		 */
		groupPanel = new GroupPanel(this);
		Cytoscape.getDesktop().getCytoPanel(SwingConstants.WEST).add("Regions",
				groupPanel);
		CyGroupManager.registerGroupViewer(this);

		// Load .na file(s) from jar
		// preloadCellularComponents(); 
	}

	/**
	 * Loads Node Attribute files packaged in the jar in the data directory
	 * 
	 */
	private void preloadCellularComponents() {
		try {
			String path = this.getClass().getResource("data/").toString();
			System.out.println("path: " + path);
			path = path.substring(path.indexOf("jar:file:") + 9, path
					.indexOf("cytoscape/bubbleRouter/data"));
			System.out.println("path: " + path);
			path = path.replace("!", "");

			Enumeration files;
			try {
				JarFile jar = new JarFile(path);
				files = jar.entries();
			} catch (Exception e) {
				// An error at this point probably means we don't have
				// any data files in our jar
				return;
			}

			while (files.hasMoreElements()) {
				ZipEntry entry = (ZipEntry) files.nextElement();
				if (entry.getName().contains("data")
						&& entry.getName().contains(".na")) {
					String name = entry.getName().substring(
							entry.getName().indexOf("data/"),
							entry.getName().length());
					System.out.println("Loading node attribute file: " + name);
					InputStream is = this.getClass().getResourceAsStream(name);

					// System.out.println("InputStream = " + is);
					// try {
					// System.out.println(" available bytes = "
					// + is.available());
					// } catch (IOException ex) {
					// ex.printStackTrace();
					// }

					try {
						InputStreamReader reader = new InputStreamReader(is);
						CyAttributesReader.loadAttributes(Cytoscape
								.getNodeAttributes(), reader);
						// Cytoscape.firePropertyChange(
						// Cytoscape.ATTRIBUTES_CHANGED, null, null);
					} catch (Exception ex) {
						ex.printStackTrace();
						throw new IllegalArgumentException(
								"Failure loading node attribute data: " + is
										+ "  because of:" + ex.getMessage());
					}
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Adds listeners: Mouse Mouse Motion Graph View Change
	 * 
	 * @see cytoscape.plugin.CytoscapePlugin#propertyChange(java.beans.PropertyChangeEvent)
	 */
	public void propertyChange(PropertyChangeEvent e) {

		if (e.getPropertyName().equals(CytoscapeDesktop.NETWORK_VIEW_FOCUSED)) {
			((DGraphView) Cytoscape.getCurrentNetworkView()).getCanvas()
					.addMouseListener(this);
			((DGraphView) Cytoscape.getCurrentNetworkView()).getCanvas()
					.addMouseMotionListener(this);
			Cytoscape.getCurrentNetworkView().addGraphViewChangeListener(
					groupPanel);
		}
		if (e.getPropertyName().equals(CytoscapeDesktop.NETWORK_VIEW_DESTROYED)) {
			CyNetworkView view = (CyNetworkView) e.getNewValue();
			LayoutRegionManager.removeAllRegionsForView(view);
			LayoutRegionManager.removeViewId(view);
		}
		if (e.getPropertyName().equals(CytoscapeDesktop.NETWORK_VIEW_CREATED)) {

			// If this is a new session; the first view-id mapping
			if (LayoutRegionManager.getViewIdMapSize() == 0) {
				// Load .na file(s) from jar
				preloadCellularComponents();
				// Reset counter
				viewID = 1;
			} else {
				viewID++;
			}

			CyNetworkView view = (CyNetworkView) e.getNewValue();
			LayoutRegionManager.setViewIdMap(view, viewID);
		}
	}

	/**
	 * Captures dragging with SHIFT pressed for region drawing, as well as using
	 * drag to move and stretch a region.
	 * 
	 * @see java.awt.event.MouseMotionListener#mouseDragged(java.awt.event.MouseEvent)
	 */
	public void mouseDragged(MouseEvent e) {
		
		// exit right away if we aren't working with a region
		if (!e.isShiftDown() && 
				(LayoutRegionManager.getNumRegionsForView
						(Cytoscape.getCurrentNetworkView()) < 1)) { return; }
		
		onEdge = calculateOnEdge(e.getPoint(), pickedRegion);

		if (e.isShiftDown() && !dragging) {
			dragging = true;
			startx = e.getX();
			starty = e.getY();
			startPoint = e.getPoint();
		} else if (dragging) {
			mousex = e.getX();
			mousey = e.getY();
			nextPoint = e.getPoint();
		} else if ((onEdge == NOT_ON_EDGE) && (!moving) && (!stretching)
				&& (pickedRegion != null)) {
			moving = true;
			stretching = false;
			startx = e.getX();
			starty = e.getY();
			startPoint = e.getPoint();

			// Don't draw rectangle or make selection during move
			((DGraphView) Cytoscape.getCurrentNetworkView()).getCanvas()
					.setSelecting(false);
			((DGraphView) Cytoscape.getCurrentNetworkView())
					.disableNodeSelection();
			((DGraphView) Cytoscape.getCurrentNetworkView())
					.disableEdgeSelection();
		} else if (moving) {
			mousex = e.getX();
			mousey = e.getY();
			nextPoint = e.getPoint();
			if (pickedRegion != null) {
				pickedRegion.setX1(pickedRegion.getX1() + mousex - startx);
				pickedRegion.setY1(pickedRegion.getY1() + mousey - starty);
				pickedRegion.setBounds(pickedRegion.getX1(), pickedRegion
						.getY1(), pickedRegion.getW1(), pickedRegion.getH1());
				NodeViewsTransformer.transform(boundedNodeViews, pickedRegion
						.getBounds());
				//not needed and significantly slows move performance
				//Cytoscape.getCurrentNetworkView().redrawGraph(true, true);
				pickedRegion.repaint();
			}

			// reset mouse point for continuing drag
			startx = mousex;
			starty = mousey;
		} else if ((onEdge != NOT_ON_EDGE) && !stretching) {
			stretching = true;
			startx = e.getX();
			starty = e.getY();
			startPoint = e.getPoint();
			regionToStretch = pickedRegion;
			edgeBeingStretched = onEdge;

			// Don't draw rectangle or make selection during stretch
			
			((DGraphView) Cytoscape.getCurrentNetworkView()).getCanvas()
					.setSelecting(false);
			((DGraphView) Cytoscape.getCurrentNetworkView())
					.disableNodeSelection();
			((DGraphView) Cytoscape.getCurrentNetworkView())
					.disableEdgeSelection();
		} else if (stretching) {
			stretchRegion(regionToStretch, edgeBeingStretched, e);
		}
	}

	/**
	 * Captures release after SHIFT+dragging and calls for the region to be
	 * drawn. Method is also used to reset flags and setting after any mouse
	 * release event.
	 * 
	 * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
	 */
	public void mouseReleased(MouseEvent e) {
		
		// exit right away if we aren't working with a region
		if (!e.isShiftDown() && 
				(LayoutRegionManager.getNumRegionsForView
						(Cytoscape.getCurrentNetworkView()) < 1)) { return; }
		
		if (dragging) {
			dragging = false;
			drawRectRegion();
		}

		// reset all flags
		moving = false;
		dragging = false;
		onEdge = NOT_ON_EDGE;
		stretching = false;
		regionToStretch = null;
		edgeBeingStretched = NOT_ON_EDGE;
		((DGraphView) Cytoscape.getCurrentNetworkView()).getCanvas()
				.setSelecting(true);
		((DGraphView) Cytoscape.getCurrentNetworkView()).enableNodeSelection();
		((DGraphView) Cytoscape.getCurrentNetworkView()).enableEdgeSelection();
		recursiveSetCursor(Cytoscape.getDesktop(), Cursor
				.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
	}

	/**
	 * Captures mouse over events to trigger cursor changes, flags and tool
	 * tips.
	 * 
	 * @see java.awt.event.MouseMotionListener#mouseMoved(java.awt.event.MouseEvent)
	 */
	public void mouseMoved(MouseEvent e) {

		// mouse over edge of region or not: change cursor
		int hoveringOnEdge = calculateOnEdge(e.getPoint(), pickedRegion);
		if (hoveringOnEdge == NOT_ON_EDGE) {
			if (whichEdge != NOT_ON_EDGE) {
				recursiveSetCursor(Cytoscape.getDesktop(), Cursor
						.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
				whichEdge = NOT_ON_EDGE;
			}
		} else {
			if ((pickedRegion != null) && (whichEdge != hoveringOnEdge)) {
				recursiveSetCursor(Cytoscape.getDesktop(), Cursor
						.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
				whichEdge = hoveringOnEdge;
				setResizeCursor(pickedRegion, hoveringOnEdge);
			}
		}

		// mouse over label area within region: show tool tip
		int hoveringInArea = calculateInArea(e.getPoint(), pickedRegion);
		if (hoveringInArea == IN_AREA) {
			toolTip.pack();
			Point pt = new Point(e.getX(), e.getY());
			SwingUtilities.convertPointToScreen(pt, ((DGraphView) Cytoscape
					.getCurrentNetworkView()).getCanvas());
			toolTip.setLocation((int) pt.getX(), (int) pt.getY()
					- toolTip.getHeight());
			toolTip.setVisible(true);
			toolTip.repaint();
		} else {
			toolTip.setVisible(false);
		}

	}

	/**
	 * Captures clicks on regions for selection and right-clicks on regions to
	 * trigger context menu.
	 * 
	 * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
	 */
	public void mousePressed(MouseEvent e) {
		if (e.getButton() == MouseEvent.BUTTON3) {
			setRegionSelection(e);
			processRegionContextMenu(e);
		} else {
			setRegionSelection(e);
		}
	}

	public void mouseClicked(MouseEvent e) {
		// handled by mousePressed
	}

	public void mouseExited(MouseEvent e) {
	}

	public void mouseEntered(MouseEvent e) {
	}

	/**
	 * This method adds tool tip info to the region label
	 */
	private void initToolTip() {
		labelRegionInfo.setOpaque(true);
		labelRegionInfo.setBackground(UIManager.getColor("ToolTip.background"));
		labelRegionInfo.setText(boundedNodeViews.size() + " of "
				+ pickedRegion.getNodeViews().size() + " nodes in "
				+ pickedRegion.getRegionAttributeValue().toString());
		toolTip.add(labelRegionInfo);
	}

	/**
	 * Sets pickedRegion, remembers oldPickedRegion, defined NodeViews bounded
	 * by pickedRegion.
	 * 
	 * @param e
	 */
	public void setRegionSelection(MouseEvent e) {
		oldPickedRegion = pickedRegion;
		pickedRegion = LayoutRegionManager.getPickedLayoutRegion(e.getPoint());

		// unselect old region
		List regionList = LayoutRegionManager.getRegionListForView(Cytoscape
				.getCurrentNetworkView());
		if ((oldPickedRegion != null) && (oldPickedRegion != pickedRegion)
				&& (regionList.contains(oldPickedRegion))) {
			oldPickedRegion.setSelected(false);
			oldPickedRegion.repaint();
			unselect(oldPickedRegion);
		}

		// select new region
		if (pickedRegion != null) {
			pickedRegion.setSelected(true);
			pickedRegion.repaint();
			select(pickedRegion);

			// define NodeView bounded by region
			boundedNodeViews = NodeViewsTransformer.bounded(pickedRegion
					.getNodeViews(), pickedRegion.getBounds());

			// initialize region info tool tip
			initToolTip();
		}

	}

	/**
	 * Display context menu relative to mouse position.
	 * 
	 * @param event
	 */
	protected void processRegionContextMenu(MouseEvent event) {
		if (pickedRegion != null) {
			menu.show(((DGraphView) Cytoscape.getCurrentNetworkView())
					.getCanvas(), event.getX() - 3, event.getY() + 1);
		} else {
			menu.setVisible(false);
		}
	}

	/**
	 * Determines whether cursor is in label area of region.
	 * 
	 * @param pt
	 * @param region
	 * @return
	 */
	private int calculateInArea(Point2D pt, LayoutRegion region) {
		if (pickedRegion == null || region == null || region != pickedRegion) {
			return NOT_IN_AREA;
		}
		if ((pt.getX() >= region.getX1() + 5)
				&& (pt.getX() <= region.getX1()
						+ 15
						+ (region.getRegionAttributeValue().toString().length() * 8))
				&& (pt.getY() >= region.getY1() + 5)
				&& (pt.getY() <= region.getY1() + 20)) {
			return IN_AREA;
		} else {
			return NOT_IN_AREA;
		}
	}

	/**
	 * Determines which edge of region the cursor is over
	 * 
	 * @param pt
	 * @param region
	 * @return
	 */
	private int calculateOnEdge(Point2D pt, LayoutRegion region) {
		if (region == null) {
			return NOT_ON_EDGE;
		}
		if ((pt.getX() >= region.getX1() - edgeTolerance)
				&& (pt.getX() <= region.getX1() + edgeTolerance)) {
			// at left
			if ((pt.getY() >= region.getY1() - edgeTolerance)
					&& (pt.getY() <= region.getY1() + edgeTolerance)) {
				return TOP_LEFT;
			} else if ((pt.getY() >= region.getY1() + region.getH1()
					- edgeTolerance)
					&& (pt.getY() <= region.getY1() + region.getH1()
							+ edgeTolerance)) {
				return BOTTOM_LEFT;
			} else if ((pt.getY() >= region.getY1())
					&& (pt.getY() <= region.getY1() + region.getH1())) {
				return LEFT;
			} else {
				return NOT_ON_EDGE;
			}
		} else if ((pt.getX() >= region.getX1() + region.getW1()
				- edgeTolerance)
				&& (pt.getX() <= region.getX1() + region.getW1()
						+ edgeTolerance)) {
			// at right
			if ((pt.getY() >= region.getY1() - edgeTolerance)
					&& (pt.getY() <= region.getY1() + edgeTolerance)) {
				return TOP_RIGHT;
			} else if ((pt.getY() >= region.getY1() + region.getH1()
					- edgeTolerance)
					&& (pt.getY() <= region.getY1() + region.getH1()
							+ edgeTolerance)) {
				return BOTTOM_RIGHT;
			} else if ((pt.getY() >= region.getY1())
					&& (pt.getY() <= region.getY1() + region.getH1())) {
				return RIGHT;
			} else {
				return NOT_ON_EDGE;
			}
		} else if ((pt.getX() >= region.getX1())
				&& (pt.getX() <= region.getX1() + region.getW1())) {

			// at top
			if ((pt.getY() >= region.getY1() - edgeTolerance)
					&& (pt.getY() <= region.getY1() + edgeTolerance)) {
				return TOP;
			}

			// at bottom
			else if ((pt.getY() >= region.getY1() + region.getH1()
					- edgeTolerance)
					&& (pt.getY() <= region.getY1() + region.getH1()
							+ edgeTolerance)) {
				return BOTTOM;
			} else {
				return NOT_ON_EDGE;
			}
		} else {
			return NOT_ON_EDGE;
		}
	}

	/**
	 * Changes the cursor to reflect which resize function is active.
	 * 
	 * @param region
	 * @param whichEdge
	 */
	private void setResizeCursor(LayoutRegion region, int whichEdge) {
		if (whichEdge == TOP) {
			recursiveSetCursor(Cytoscape.getDesktop(), Cursor
					.getPredefinedCursor(Cursor.N_RESIZE_CURSOR));
		} else if (whichEdge == BOTTOM) {
			recursiveSetCursor(Cytoscape.getDesktop(), Cursor
					.getPredefinedCursor(Cursor.S_RESIZE_CURSOR));
		} else if (whichEdge == LEFT) {
			recursiveSetCursor(Cytoscape.getDesktop(), Cursor
					.getPredefinedCursor(Cursor.W_RESIZE_CURSOR));
		} else if (whichEdge == RIGHT) {
			recursiveSetCursor(Cytoscape.getDesktop(), Cursor
					.getPredefinedCursor(Cursor.E_RESIZE_CURSOR));
		} else if (whichEdge == TOP_LEFT) {
			recursiveSetCursor(Cytoscape.getDesktop(), Cursor
					.getPredefinedCursor(Cursor.NW_RESIZE_CURSOR));
		} else if (whichEdge == TOP_RIGHT) {
			recursiveSetCursor(Cytoscape.getDesktop(), Cursor
					.getPredefinedCursor(Cursor.NE_RESIZE_CURSOR));
		} else if (whichEdge == BOTTOM_LEFT) {
			recursiveSetCursor(Cytoscape.getDesktop(), Cursor
					.getPredefinedCursor(Cursor.SW_RESIZE_CURSOR));
		} else if (whichEdge == BOTTOM_RIGHT) {
			recursiveSetCursor(Cytoscape.getDesktop(), Cursor
					.getPredefinedCursor(Cursor.SE_RESIZE_CURSOR));
		}
	}

	/**
	 * Actually changes the cursor.
	 * 
	 * @param comp
	 * @param cursor
	 */
	private void recursiveSetCursor(Container comp, Cursor cursor) {
		comp.setCursor(cursor);
		Component children[] = comp.getComponents();
		for (int i = 0; i < children.length; i++) {
			children[i].setCursor(cursor);
			if (children[i] instanceof Container) {
				recursiveSetCursor((Container) children[i], cursor);
			}
		}
	}

	/**
	 * Performs resize functions on the region.
	 * 
	 * @param region
	 * @param whichEdge
	 * @param event
	 */
	private void stretchRegion(LayoutRegion region, int whichEdge,
			MouseEvent event) {
		mousex = event.getX();
		mousey = event.getY();
		nextPoint = event.getPoint();

		if (whichEdge == TOP) {
			if (mousey < (regionToStretch.getY1() + regionToStretch.getH1())) {
				regionToStretch.setY1(mousey);
				regionToStretch
						.setH1(regionToStretch.getH1() + starty - mousey);
			}
		} else if (whichEdge == BOTTOM) {
			if (mousey > regionToStretch.getY1()) {
				regionToStretch
						.setH1(regionToStretch.getH1() + mousey - starty);
			}
		} else if (whichEdge == LEFT) {
			if (mousex < (regionToStretch.getX1() + regionToStretch.getW1())) {
				regionToStretch.setX1(mousex);
				regionToStretch
						.setW1(regionToStretch.getW1() + startx - mousex);
			}
		} else if (whichEdge == RIGHT) {
			if (mousex > regionToStretch.getX1()) {
				regionToStretch
						.setW1(regionToStretch.getW1() + mousex - startx);
			}
		} else if (whichEdge == TOP_LEFT) {
			if ((mousey < (regionToStretch.getY1() + regionToStretch.getH1()))
					&& (mousex < (regionToStretch.getX1() + regionToStretch
							.getW1()))) {
				regionToStretch.setY1(mousey);
				regionToStretch
						.setH1(regionToStretch.getH1() + starty - mousey);
				regionToStretch.setX1(mousex);
				regionToStretch
						.setW1(regionToStretch.getW1() + startx - mousex);
			}
		} else if (whichEdge == TOP_RIGHT) {
			if ((mousey < (regionToStretch.getY1() + regionToStretch.getH1()))
					&& (mousex > regionToStretch.getX1())) {
				regionToStretch.setY1(mousey);
				regionToStretch
						.setH1(regionToStretch.getH1() + starty - mousey);
				regionToStretch
						.setW1(regionToStretch.getW1() + mousex - startx);
			}
		} else if (whichEdge == BOTTOM_LEFT) {
			if ((mousey > regionToStretch.getY1())
					&& (mousex < (regionToStretch.getX1() + regionToStretch
							.getW1()))) {
				regionToStretch
						.setH1(regionToStretch.getH1() + mousey - starty);
				regionToStretch.setX1(mousex);
				regionToStretch
						.setW1(regionToStretch.getW1() + startx - mousex);
			}
		} else if (whichEdge == BOTTOM_RIGHT) {
			if ((mousey > regionToStretch.getY1())
					&& (mousex > regionToStretch.getX1())) {
				regionToStretch
						.setH1(regionToStretch.getH1() + mousey - starty);
				regionToStretch
						.setW1(regionToStretch.getW1() + mousex - startx);
			}
		}
		regionToStretch.setBounds(regionToStretch.getX1(), regionToStretch
				.getY1(), regionToStretch.getW1(), regionToStretch.getH1());
		NodeViewsTransformer.transform(boundedNodeViews, regionToStretch
				.getBounds());
		Cytoscape.getCurrentNetworkView().redrawGraph(true, true);
		regionToStretch.repaint();

		// reset mouse point for continuing drag
		startx = mousex;
		starty = mousey;
	}

	/**
	 * Upon mouse release, calculate rectangular dimensions, create LayoutRegion
	 * object, and send region to LayoutRegionManager, add to a prefab canvas
	 */
	void drawRectRegion() {
		if (startx != mousex && starty != mousey) {

			/**
			 * x,y,w,h must be computed from the coordinates of the two corner
			 * points.
			 */
			int x, y; // Top left corner of the rectangle.
			int w, h; // Width and height of the rectangle.
			if (mousex > startx) {
				x = startx;
				w = mousex - startx;
			} else {
				x = mousex;
				w = startx - mousex;
			}
			if (mousey > starty) {
				y = starty;
				h = mousey - starty;
			} else {
				y = mousey;
				h = starty - mousey;
			}

			// if the drawn region is unreasonably small, then reject
			if ((w < 20) || (h < 20)) {
				JOptionPane
						.showMessageDialog(Cytoscape.getDesktop(),
								"This region is too small to fit anything.  Please draw a larger region.");
				return;
			}

			/**
			 * Use Ethan's QuickFind dialog for attribute selection.
			 * 
			 * Returns region attribute name and values.
			 */
			new BRQuickFindConfigDialog(x, y, w, h);
		}
	}

	/**
	 * Create a new group.
	 */
	public static void newGroup(LayoutRegion region, int viewID) {
		List<NodeView> currentNodeViews = region.getNodeViews();
		List<CyNode> currentNodes = new ArrayList<CyNode>();
		for (NodeView cnv : currentNodeViews) {
			currentNodes.add((CyNode) cnv.getNode());
		}
		String groupName = region.getRegionAttributeValue().toString() + "_"
				+ viewID;
		CyGroup group = CyGroupManager.createGroup(groupName, currentNodes,
				viewerName);
		region.setMyGroup(group);
		group.setState(SELECTED);
		groupPanel.groupCreated(group);

		CyNode groupNode = group.getGroupNode();
		CyAttributes attributes = Cytoscape.getNodeAttributes();
		attributes.setAttribute(groupNode.getIdentifier(), REGION_NAME_ATT,
				groupName);
		attributes.setAttribute(groupNode.getIdentifier(), REGION_COLORINT_ATT,
				region.getColorIndex());
		attributes.setAttribute(groupNode.getIdentifier(), REGION_NETWORK_ATT,
				Cytoscape.getCurrentNetwork().getIdentifier());

		// Set Region Variables to Hidden
		attributes.setUserVisible(REGION_NAME_ATT, false);
		attributes.setUserVisible(REGION_COLORINT_ATT, false);
		attributes.setUserVisible(REGION_X_ATT, false);
		attributes.setUserVisible(REGION_Y_ATT, false);
		attributes.setUserVisible(REGION_W_ATT, false);
		attributes.setUserVisible(REGION_H_ATT, false);
		attributes.setUserVisible(REGION_NETWORK_ATT, false);

	}

	/**
	 * Return the name of our Group viewer
	 * 
	 * @return viewer name
	 */
	public String getViewerName() {
		return viewerName;
	}

	/**
	 * This is called when a new group has been created that we care about. If
	 * we weren't building our menu each time, this would be used to update the
	 * list of groups we present to the user.
	 * 
	 * @param group
	 *            the CyGroup that was just created
	 */
	public void groupCreated(CyGroup group) {
		this.groupCreated(group, null);
	}

	/**
	 * Generates layout region from xGMML
	 * 
	 * @see cytoscape.groups.CyGroupViewer#groupCreated(cytoscape.groups.CyGroup,
	 *      cytoscape.view.CyNetworkView)
	 */
	public void groupCreated(CyGroup group, CyNetworkView cnv) {
		System.out.println("Building Layout Region from xGMML");
		CyNode groupNode = group.getGroupNode();
		CyNetworkView myView = cnv;
		myView.hideGraphObject(myView.getNodeView(groupNode));
		CyAttributes attributes = Cytoscape.getNodeAttributes();
		Double x = attributes.getDoubleAttribute(groupNode.getIdentifier(),
				REGION_X_ATT);
		Double y = attributes.getDoubleAttribute(groupNode.getIdentifier(),
				REGION_Y_ATT);
		Double w = attributes.getDoubleAttribute(groupNode.getIdentifier(),
				REGION_W_ATT);
		Double h = attributes.getDoubleAttribute(groupNode.getIdentifier(),
				REGION_H_ATT);
		String nameString = attributes.getStringAttribute(groupNode
				.getIdentifier(), REGION_NAME_ATT);

		nameString = nameString.replace("[", "");
		nameString = nameString.replace("]_", ","); // mark viewID
		String[] nameArray = nameString.split(",");
		ArrayList<Object> name = new ArrayList<Object>();
		for (int j = 0; j < (nameArray.length - 1); j++) { // skip viewID
			name.add(nameArray[j].trim());
		}
		Iterator<Node> nodes = myView.getNetwork().nodesIterator();
		List<NodeView> nv = new ArrayList<NodeView>();
		List<CyNode> groupNodes = group.getNodes();
		while (nodes.hasNext()) {
			Node n = nodes.next();
			NodeView myNodeView = myView.getNodeView(n);
			if (groupNodes.contains(n)) {
				nv.add(myNodeView);
			}
		}
		int color = attributes.getIntegerAttribute(groupNode.getIdentifier(),
				REGION_COLORINT_ATT);

		String networkTarget = attributes.getStringAttribute(groupNode
				.getIdentifier(), REGION_NETWORK_ATT);

		// See if this is the network we want
		if (!networkTarget.equals(myView.getNetwork().getIdentifier())) {
			// No, return without actually building the region
			return;
		}

		group.setState(SELECTED);
		groupPanel.groupCreated(group);

		// Set Region Variables to Hidden
		attributes.setUserVisible(REGION_NAME_ATT, false);
		attributes.setUserVisible(REGION_COLORINT_ATT, false);
		attributes.setUserVisible(REGION_X_ATT, false);
		attributes.setUserVisible(REGION_Y_ATT, false);
		attributes.setUserVisible(REGION_W_ATT, false);
		attributes.setUserVisible(REGION_H_ATT, false);
		attributes.setUserVisible(REGION_NETWORK_ATT, false);

		// Create Region
		new LayoutRegion(x, y, w, h, name, nv, color, myView, group);
	}

	/**
	 * This is called when a group we care about is about to be deleted. If we
	 * weren't building our menu each time, this would be used  to update the
	 * list of groups we present to the user.
	 * 
	 * @param group
	 *            the CyGroup that will be deleted
	 */
	public void groupWillBeRemoved(CyGroup group) {
		groupPanel.groupRemoved(group);
	}

	/**
	 * This is called when a region is deleted and the corresponding group needs
	 * to be removed.
	 * 
	 * @param region
	 */
	public static void groupWillBeRemoved(LayoutRegion region) {
		CyGroup group = region.getMyGroup();
		CyGroupManager.removeGroup(group);
		groupPanel.groupRemoved(group);
	}

	/**
	 * This is called when a group we care about is changed.
	 * 
	 * @param group
	 *            the CyGroup that has changed
	 * @param node
	 *            the CyNode that caused the change
	 * @param change
	 *            the change that occured
	 */
	public void groupChanged(CyGroup group, CyNode node, ChangeType change) {
		// At some point, this should be a little more granular. Do we really
		// need to rebuild the tree when we have a simple node addition/removal?
		groupPanel.groupChanged(group);
	}

	/**
	 * Perform the action associated with a select menu selection
	 */
	private void select(LayoutRegion region) {
		CyGroup group = CyGroupManager.getCyGroup(Cytoscape.getCyNode(region
				.getRegionAttributeValue().toString()
				+ "_"
				+ LayoutRegionManager.getIdForView(Cytoscape
						.getCurrentNetworkView())));
		if (group != null) 
		{
			group.setState(SELECTED);
		}
	}

	/**
	 * Perform the action associated with an unselect menu selection
	 */
	private void unselect(LayoutRegion region) {
		CyGroup group = CyGroupManager.getCyGroup(Cytoscape.getCyNode(region
				.getRegionAttributeValue().toString()
				+ "_"
				+ LayoutRegionManager.getIdForView(Cytoscape
						.getCurrentNetworkView())));
		if (group != null)
		{
			group.setState(UNSELECTED);
		}
	}

	/**
	 * This class gets attached to the menu item. Bubble Router does not have a
	 * Plugin Menu action.
	 */
	@SuppressWarnings("serial")
	public class MainPluginAction extends AbstractAction {

		// The constructor sets the text that should appear on the menu item.
		public MainPluginAction() {
			super("Interactive Layout");
		}

		// This method is called when the user selects the menu item.
		public void actionPerformed(ActionEvent ae) {
		}

		public void initializeBubbleRouter() {
		}

	}

	/**
	 * This class listens for actions from the popup menu, it is responsible for
	 * performing actions related to destroying and creating views, and
	 * destroying the network.
	 */
	class RegionPopupActionListener implements ActionListener {

		/**
		 * Based on the action event, destroy or create a view, or destroy a
		 * network
		 */
		@SuppressWarnings("serial")
		public void actionPerformed(ActionEvent ae) {
			String label = ((JMenuItem) ae.getSource()).getText();
			// Figure out the appropriate action
			if ((label == DELETE_REGION) && (pickedRegion != null)) {
				// confirm deletion
				int confirm = JOptionPane.showConfirmDialog(Cytoscape
						.getDesktop(),
						"Do you really want to delete this layout region?");
				if (confirm == JOptionPane.YES_OPTION) {
					LayoutRegionManager.removeRegion(Cytoscape
							.getCurrentNetworkView(), pickedRegion);
					System.out.println("Region \""
							+ pickedRegion.getRegionAttributeValue().toString()
							+ "\" deleted");
					pickedRegion = null;
				}

			} else if ((label == LAYOUT_REGION) && (pickedRegion != null)) {

				pickedRegion.setBounds(pickedRegion.getX1(), pickedRegion
						.getY1(), pickedRegion.getW1(), pickedRegion.getH1());

				// set up for Undo/Redo
				List myNodeViews = pickedRegion.getNodeViews();
				_nodeViews = new NodeView[myNodeViews.size()];
				_undoOffsets = new Point2D[myNodeViews.size()];
				_redoOffsets = new Point2D[myNodeViews.size()];
				for (int j = 0; j < _nodeViews.length; j++) {
					_nodeViews[j] = (NodeView) myNodeViews.get(j);
				}

				NodeViewsTransformer.transform(pickedRegion.getNodeViews(),
						pickedRegion.getBounds());

				Cytoscape.getCurrentNetworkView().redrawGraph(true, true);
				pickedRegion.repaint();
				System.out.println("Region layout updated");

				// collect NodeViews bounded by current region
				boundedNodeViews = NodeViewsTransformer.bounded(pickedRegion
						.getNodeViews(), pickedRegion.getBounds());

				UnCrossAction.unCross(boundedNodeViews, false);
				// boolean=false indicates that UnCrossAction is not the top
				// level
				// caller, does not need undo/redo logic

				for (int m = 0; m < _nodeViews.length; m++) {
					_redoOffsets[m] = _nodeViews[m].getOffset();
				}

				CyUndo.getUndoableEditSupport().postEdit(
						new AbstractUndoableEdit() {

							public String getPresentationName() {
								return "Layout Region";
							}

							public String getRedoPresentationName() {
								return "Redo: Layout region";
							}

							public String getUndoPresentationName() {
								return "Undo: Layout region";
							}

							public void redo() {
								NodeView nv;
								for (int m = 0; m < _nodeViews.length; m++) {
									nv = (NodeView) _nodeViews[m];
									nv.setOffset(_redoOffsets[m].getX(),
											_redoOffsets[m].getY());
								}
							}

							public void undo() {
								NodeView nv;
								for (int m = 0; m < _nodeViews.length; m++) {
									nv = (NodeView) _nodeViews[m];
									nv.setOffset(_undoOffsets[m].getX(),
											_undoOffsets[m].getY());
								}
							}
						});

			} else if ((label == UNCROSS_EDGES) && (pickedRegion != null)) {
				// uncross edges of nodes selected only within a region
				UnCrossAction.unCross(boundedNodeViews, true);
				System.out.println("Edges uncrossed");
			} else if (label == ITEM_HELP) {
				String helpURL = "http://www.genmapp.org/BubbleRouter/manual.htm";
				cytoscape.util.OpenBrowser.openURL(helpURL);
			} else if ((label == MOVE_TO_FRONT) && (pickedRegion != null)) {
				((DGraphView) Cytoscape.getCurrentNetworkView()).getCanvas(
						LayoutRegionManager.REGION_CANVAS).setComponentZOrder(
						pickedRegion, 0);
				Cytoscape.getCurrentNetworkView().redrawGraph(true, true);
			} else if ((label == MOVE_TO_BACK) && (pickedRegion != null)) {
				((DGraphView) Cytoscape.getCurrentNetworkView()).getCanvas(
						LayoutRegionManager.REGION_CANVAS).setComponentZOrder(
						pickedRegion, ((DGraphView) Cytoscape
								.getCurrentNetworkView())
								.getCanvas(
										LayoutRegionManager.REGION_CANVAS)
								.getComponentCount() - 1);
				Cytoscape.getCurrentNetworkView().redrawGraph(true, true);
			} else if ((label == MOVE_FORWARD) && (pickedRegion != null)) {
				if (((DGraphView) Cytoscape.getCurrentNetworkView()).getCanvas(
						LayoutRegionManager.REGION_CANVAS).getComponentZOrder(
						pickedRegion) > 0) {
					((DGraphView) Cytoscape.getCurrentNetworkView())
							.getCanvas(LayoutRegionManager.REGION_CANVAS)
							.setComponentZOrder(
									pickedRegion,
									((DGraphView) Cytoscape
											.getCurrentNetworkView())
											.getCanvas(
													LayoutRegionManager.REGION_CANVAS)
											.getComponentZOrder(pickedRegion) - 1);
				}
				Cytoscape.getCurrentNetworkView().redrawGraph(true, true);
			} else if ((label == MOVE_BACKWARD) && (pickedRegion != null)) {
				if (((DGraphView) Cytoscape.getCurrentNetworkView()).getCanvas(
						LayoutRegionManager.REGION_CANVAS).getComponentZOrder(
						pickedRegion) < ((DGraphView) Cytoscape
						.getCurrentNetworkView()).getCanvas(
						LayoutRegionManager.REGION_CANVAS).getComponentCount()) {
					((DGraphView) Cytoscape.getCurrentNetworkView())
							.getCanvas(LayoutRegionManager.REGION_CANVAS)
							.setComponentZOrder(
									pickedRegion,
									((DGraphView) Cytoscape
											.getCurrentNetworkView())
											.getCanvas(
													LayoutRegionManager.REGION_CANVAS)
											.getComponentZOrder(pickedRegion) + 1);
				}
				Cytoscape.getCurrentNetworkView().redrawGraph(true, true);
			} else if (label == "Red" && pickedRegion != null) {
				pickedRegion.setPaint(Color.red);
				pickedRegion.repaint();
			} else if (label == "Green" && pickedRegion != null) {
				pickedRegion.setPaint(Color.green);
				pickedRegion.repaint();
			} else if (label == "Blue" && pickedRegion != null) {
				pickedRegion.setPaint(Color.blue);
				pickedRegion.repaint();
			} else if (label == "Orange" && pickedRegion != null) {
				pickedRegion.setPaint(Color.orange);
				pickedRegion.repaint();
			} else if (label == "Cyan" && pickedRegion != null) {
				pickedRegion.setPaint(Color.cyan);
				pickedRegion.repaint();
			} else if (label == "Magenta" && pickedRegion != null) {
				pickedRegion.setPaint(Color.magenta);
				pickedRegion.repaint();
			} else if (label == "Black" && pickedRegion != null) {
				pickedRegion.setPaint(Color.darkGray);
				pickedRegion.repaint();
			} else {
				// throw an exception here?
				System.err.println("No action associated with that menu selection!");
			}
		}
	}

	/**
	 * This class prompts the user for confirmation and, if confirmed, deletes
	 * all of the regions from the network view.
	 */
	class DeleteAllRegionsActionListener implements ActionListener {

		/**
		 * Based on the action event, destroy or create a view, or destroy a
		 * network
		 */
		public void actionPerformed(ActionEvent ae) {

			int confirm = JOptionPane.showConfirmDialog(Cytoscape.getDesktop(),
					"Do you really want to delete all layout regions?");
			if (confirm == JOptionPane.YES_OPTION) {
				LayoutRegionManager.removeAllRegionsForView(Cytoscape
						.getCurrentNetworkView());
				Cytoscape.getCurrentNetworkView().redrawGraph(true, true);
			}
		}
	}

	/**
	 * This class direct a browser to the help manual web page.
	 */
	class GetBubbleHelpListener implements ActionListener {
		private String helpURL = "http://www.genmapp.org/BubbleRouter/manual.htm";

		public void actionPerformed(ActionEvent ae) {
			cytoscape.util.OpenBrowser.openURL(helpURL);
		}

	}

	/**
	 * Plugin Info for the Plugin Manager has been commented out and registered
	 * with Cytoscape Plugins page:
	 * http://tocai.ucsd.edu/plugins/pluginsubmit.php
	 * http://cytoscape.org/plugins/plugins.xml
	 */
	// public PluginInfo getPluginInfoObject() {
	// PluginInfo Info = new PluginInfo();
	// Info.setName("BubbleRouter"); // name can be anything
	// Info.setDescription("Attribute-based layout using interactive regions");
	// Info.setCategory("Functional Enrichment");
	// Info.setPluginVersion(1.0);
	// Info.setCytoscapeVersion("2.5");
	// Info.setProjectUrl("http://conklinwolf.ucsf.edu/genmappwiki/Bubble_Router_Plugin");
	// Info.addAuthor("Allan Kuchinsky", "Agilent");
	// Info.addAuthor("Alex Pico", "UCSF");
	// Info.addAuthor("Kristina Hanspers", "UCSF");
	// return Info;
	// }
}
