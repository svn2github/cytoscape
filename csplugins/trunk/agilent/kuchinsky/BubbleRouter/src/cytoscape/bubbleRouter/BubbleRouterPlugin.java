package cytoscape.bubbleRouter;

import giny.model.Node;
import giny.view.NodeView;

import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import javax.swing.JWindow;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.undo.AbstractUndoableEdit;

import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;
import cytoscape.groups.CyGroup;
import cytoscape.groups.CyGroupManager;
import cytoscape.groups.CyGroupViewer;
import cytoscape.plugin.CytoscapePlugin;
import cytoscape.plugin.PluginInfo;
import cytoscape.util.undo.CyUndo;
import cytoscape.view.CyNetworkView;
import cytoscape.view.CytoscapeDesktop;
import ding.view.DGraphView;
import ding.view.InnerCanvas;

public class BubbleRouterPlugin extends CytoscapePlugin implements
		MouseListener, MouseMotionListener, PropertyChangeListener,
		CyGroupViewer {

	protected InnerCanvas canvas;

	protected JPanel cyAnnPanel;

	boolean dragging; // This is set to true when a drag begins, and to false

	// AJK: 12/24/06 BEGIN
	// flags for moving and stretching of region
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

	private int onEdge = NOT_ON_EDGE;

	private int edgeTolerance = 5; // number of pixels 'cushion' for edge

	private Cursor savedCursor;

	private LayoutRegion changedCursorRegion = null;

	// need to store region being stretched and edge being stretched because
	// mouse movement may get ahead of the stretching.
	private LayoutRegion regionToStretch = null;

	private int edgeBeingStretched = NOT_ON_EDGE;

	// selection
	// AJK: 12/24/06 END

	// when released

	boolean handlerStarted = false;

	// when it ends. The value is checked by mouseReleased()
	// and mouseDragged().

	int startx, starty; // The location of the mouse when the dragging started.

	int mousex, mousey; // The location of the mouse during dragging.

	/**
	 * the mouse press location for the drop point
	 */
	protected Point2D startPoint;

	/**
	 * point used in tracking mouse movement
	 */
	protected Point2D nextPoint;

	// AJK: 12/01/06
	/**
	 * for popup menu
	 */
	// public static String REGION_INFO = "Region Info";
	public static String DELETE_REGION = "Delete Region";

	// public static String REROUTE_REGION = "Reroute Region";
	public static String LAYOUT_REGION = "Layout Region";

	// AJK: 02/20/07 delete all regions
	public static String DELETE_ALL_REGIONS = "Delete All Regions";

	// KH: 03/14/07
	public static String BUBBLE_HELP = "Interactive Layout Help";

	public static String ITEM_HELP = "Online Help";

	public static String UNCROSS_EDGES = "Uncross Edges";

	private JPopupMenu menu = new JPopupMenu("Layout Region");

	private JWindow toolTip = new JWindow(new JFrame());

	private JLabel labelRegionInfo = new JLabel(" ");

	private static final int NOT_IN_AREA = -1;

	private static final int IN_AREA = 1;

	private boolean displayInfo = false;

	// JMenuItem regionInfoItem = new JMenuItem(REGION_INFO);

	LayoutRegion pickedRegion = null;

	private List boundedNodeViews;

	public static final String viewerName = "bubbleRouter";

	// private static CyGroupViewer groupViewer = null;
	private static GroupPanel groupPanel = null;

	public static final double VERSION = 1.0;

	// State values
	public static final int SELECTED = 1;

	public static final int UNSELECTED = 2;

	/**
	 * 
	 */
	public BubbleRouterPlugin() {

		Cytoscape.getDesktop().getSwingPropertyChangeSupport()
				.addPropertyChangeListener(
						CytoscapeDesktop.NETWORK_VIEW_FOCUSED, this);

		Cytoscape.getDesktop().getSwingPropertyChangeSupport()
				.addPropertyChangeListener(
						CytoscapeDesktop.NETWORK_VIEW_CREATED, this);
		// AJK: 12/01/06 BEGIN
		// addMouseListener to canvas; add popup menu
		canvas = ((DGraphView) Cytoscape.getCurrentNetworkView()).getCanvas();
		((DGraphView) Cytoscape.getCurrentNetworkView()).getCanvas()
				.addMouseListener(this);

		// AP: 2/25/07 add edge uncross to context menu
		JMenuItem uncrossEdgesItem = new JMenuItem(UNCROSS_EDGES);
		JMenuItem deleteRegionItem = new JMenuItem(DELETE_REGION);
		JMenuItem layoutRegionItem = new JMenuItem(LAYOUT_REGION);
		JMenuItem helpItem = new JMenuItem(ITEM_HELP);
		uncrossEdgesItem
				.setToolTipText("Run edge minimization algorithm to reduce edge crossings");
		deleteRegionItem.setToolTipText("Delete region from canvas");
		layoutRegionItem
				.setToolTipText("Run layout algorithm on all nodes associated with region");
		helpItem.setToolTipText("Open online help");
		RegionPopupActionListener popupActionListener = new RegionPopupActionListener();
		deleteRegionItem.addActionListener(popupActionListener);
		layoutRegionItem.addActionListener(popupActionListener);
		uncrossEdgesItem.addActionListener(popupActionListener);
		// menu.add(regionInfoItem);
		// menu.addSeparator();
		menu.add(uncrossEdgesItem);
		menu.add(layoutRegionItem);
		menu.add(deleteRegionItem);
		menu.add(helpItem);
		menu.setVisible(false);
		// AJK: 12/01/06 END

		// AJK: 02/20/07 BEGIN
		// Delete All Regions
		JMenuItem deleteAllRegionsItem = new JMenuItem(
				BubbleRouterPlugin.DELETE_ALL_REGIONS);
		DeleteAllRegionsActionListener deleteAllRegionsListener = new DeleteAllRegionsActionListener();
		deleteAllRegionsItem.addActionListener(deleteAllRegionsListener);
		Cytoscape.getDesktop().getCyMenus().getLayoutMenu().add(
				deleteAllRegionsItem);
		// AJK: 02/20/07 END

		// KH: 03/14/07 BEGIN
		// Add BubbleRouter Help to menu

		JMenuItem getBubbleHelp = new JMenuItem(BubbleRouterPlugin.BUBBLE_HELP);
		GetBubbleHelpListener getBubbleHelpListener = new GetBubbleHelpListener();
		getBubbleHelp.addActionListener(getBubbleHelpListener);
		Cytoscape.getDesktop().getCyMenus().getHelpMenu().add(new JSeparator());
		Cytoscape.getDesktop().getCyMenus().getHelpMenu().add(getBubbleHelp);
		helpItem.addActionListener(getBubbleHelpListener);

		// KH: 03/14/07 END

		// AJK: 12/28/06 save cursor for restoration after move/stretch
		savedCursor = Cytoscape.getDesktop().getCursor();

		// AJK: 1/2/07 add edge cross minimization functionality
		// UnCrossAction uncross = new UnCrossAction();
		// Cytoscape.getDesktop().getCyMenus().addAction(uncross);

		// MainPluginAction mpa = new MainPluginAction();
		// mpa.initializeBubbleRouter();

		// Add our interface to CytoPanel 1
		groupPanel = new GroupPanel(this);
		Cytoscape.getDesktop().getCytoPanel(SwingConstants.WEST).add("Regions",
				groupPanel);
		// We want to listen for graph perspective changes (primarily
		// SELECT/UNSELECT)
//		Cytoscape.getCurrentNetworkView()
//				.addGraphViewChangeListener(groupPanel);

		// Register with CyGroup
		CyGroupManager.registerGroupViewer(this);
		// this.groupViewer = this; // this makes it easier to get at from inner
		// classes
		System.out.println("BubbleRouter " + VERSION + " initialized");

	}

	public void mouseDragged(MouseEvent e) {
		// If a dragging operation is in progress, get the new
		// values for mousex and mousey, and repaint.

		onEdge = calculateOnEdge(e.getPoint(), pickedRegion);
		// .out.println ("OnEdge = " + onEdge);
		if (e.isShiftDown() && !dragging) {
			dragging = true;
			startx = e.getX();
			starty = e.getY();
			startPoint = e.getPoint();
		}

		else if (dragging) {
			mousex = e.getX();
			mousey = e.getY();
			nextPoint = e.getPoint();
		}
		// AJK: 12/24/06 BEGIN
		// moving and stretching
		// TODO: refactor to remove redundancies
		else if ((onEdge == NOT_ON_EDGE) && (!moving) && (!stretching)
				&& (pickedRegion != null)) {
			moving = true;
			stretching = false;
			startx = e.getX();
			starty = e.getY();
			startPoint = e.getPoint();

			// System.out.println("setting cursor from: "
			// + Cytoscape.getDesktop().getCursor());
			// recursiveSetCursor(Cytoscape.getDesktop(), Cursor
			// .getPredefinedCursor(Cursor.HAND_CURSOR));
			// Cytoscape.getDesktop().setCursor(
			// Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

			// Cytoscape.getCurrentNetworkView().getComponent().setCursor(
			// Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			// System.out.println("setting cursor to: "
			// + Cytoscape.getDesktop().getCursor());
			// System.out.println("Region start point = " + pickedRegion.getX1()
			// + "," + pickedRegion.getY1());
			e.consume(); // don't have canvas draw drag rect
			((DGraphView) Cytoscape.getCurrentNetworkView()).getCanvas()
					.setSelecting(false);
			return; // don't have canvas draw drag rect
		} else if (moving) {
			mousex = e.getX();
			mousey = e.getY();
			nextPoint = e.getPoint();
			if (pickedRegion != null) {
				pickedRegion.setX1(pickedRegion.getX1() + mousex - startx);
				pickedRegion.setY1(pickedRegion.getY1() + mousey - starty);
				// AJK: 01/09/07: use double coordinates to avoid roundoff
				// pickedRegion.setBounds((int) pickedRegion.getX1(),
				// (int) pickedRegion.getY1(), (int) pickedRegion.getW1(),
				// (int) pickedRegion.getH1());
				pickedRegion.setBounds(pickedRegion.getX1(), pickedRegion
						.getY1(), pickedRegion.getW1(), pickedRegion.getH1());

				NodeViewsTransformer.transform(boundedNodeViews, pickedRegion
						.getBounds());
				// System.out.println ("Region start point set to = " +
				// pickedRegion.getX1() + "," + pickedRegion.getY1());
				Cytoscape.getCurrentNetworkView().redrawGraph(true, true);
				pickedRegion.repaint();
			}
			startx = mousex; // reset mouse point for continuing drag
			starty = mousey;
			e.consume(); // don't have canvas draw drag rect
			((DGraphView) Cytoscape.getCurrentNetworkView()).getCanvas()
					.setSelecting(false);
			return;

		}

		if ((onEdge != NOT_ON_EDGE) && !stretching) {
			stretching = true;
			((DGraphView) Cytoscape.getCurrentNetworkView()).getCanvas()
					.setSelecting(false);
			startx = e.getX();
			starty = e.getY();
			startPoint = e.getPoint();
			regionToStretch = pickedRegion;
			edgeBeingStretched = onEdge;
		} else if (stretching) {
			stretchRegion(regionToStretch, edgeBeingStretched, e);
		}
		// AJK: 12/26/06 END

		// just rely on canvas to draw drag rect
		((DGraphView) Cytoscape.getCurrentNetworkView()).getCanvas()
				.mouseDragged(e);
	}

	public void mouseReleased(MouseEvent e) {
		// End the dragging operation, if one is in progress. Draw
		// the final figure, if any onto the off-screen canvas, so
		// it becomes a permanent part of the image.
		if (dragging) {
			dragging = false;

			drawRectRegion();
		} else {
		}
		// AJK: 12/24/06 BEGIN
		// reset all flags
		moving = false;
		dragging = false;
		onEdge = NOT_ON_EDGE;
		stretching = false;
		regionToStretch = null;
		edgeBeingStretched = NOT_ON_EDGE;
		((DGraphView) Cytoscape.getCurrentNetworkView()).getCanvas()
				.setSelecting(true);
		// if (pickedRegion != null)
		// {
		// pickedRegion.setCursor(savedCursor);
		// }
		recursiveSetCursor(Cytoscape.getDesktop(), Cursor
				.getPredefinedCursor(Cursor.DEFAULT_CURSOR));

		// AJK: 12/24/06 END
	}

	public void mouseExited(MouseEvent e) {
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mousePressed(MouseEvent e) {
		if (e.getButton() == MouseEvent.BUTTON3) {
			setRegionSelection(e);
			processRegionContextMenu(e);
		} else {
			menu.setVisible(false);
			// TODO: refactor processRegionMousePressEvent
			// AJK: 02/20/07 process selection/deselection of region
			setRegionSelection(e);
		}
	}

	private void initToolTip() {
		labelRegionInfo.setOpaque(true);
		labelRegionInfo.setBackground(UIManager.getColor("ToolTip.background"));
		labelRegionInfo.setText(boundedNodeViews.size() + " of "
				+ pickedRegion.getNodeViews().size() + " nodes in "
				+ pickedRegion.getRegionAttributeValue().toString());
		toolTip.add(labelRegionInfo);
	}

	public void mouseMoved(MouseEvent e) {
		LayoutRegion overRegion = LayoutRegionManager.getPickedLayoutRegion(e
				.getPoint());
		int hoveringOnEdge = calculateOnEdge(e.getPoint(), overRegion);
		int hoveringInArea = calculateInArea(e.getPoint(), overRegion);
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

		if (hoveringOnEdge == NOT_ON_EDGE) {
			if (changedCursorRegion != null) {
				recursiveSetCursor(Cytoscape.getDesktop(), Cursor
						.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
				changedCursorRegion = null;
			}
		} else {
			if ((overRegion != null) && (changedCursorRegion != overRegion)) {
				recursiveSetCursor(Cytoscape.getDesktop(), Cursor
						.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
				// changedCursorRegion.setCursor(changedCursorRegion.getSavedCursor());
				changedCursorRegion = overRegion;
				setResizeCursor(changedCursorRegion, hoveringOnEdge);
			}
		}
	}

	// AJK: 02/20/07 BEGIN
	// Selection/Deselection of a region on mouse click or mouse press

	public void mouseClicked(MouseEvent e) {
		setRegionSelection(e);
	}

	public void setRegionSelection(MouseEvent e) {
		// AJK: 12/24/06 BEGIN
		// set picked region
		LayoutRegion oldPickedRegion = pickedRegion;
		pickedRegion = LayoutRegionManager.getPickedLayoutRegion(e.getPoint());
		if ((oldPickedRegion != null) && (oldPickedRegion != pickedRegion)) {
			oldPickedRegion.setSelected(false);
			oldPickedRegion.repaint();
			unselect(oldPickedRegion);
		}
		if (pickedRegion != null) {
			pickedRegion.setSelected(true);
			pickedRegion.repaint();
			select(pickedRegion);

			boundedNodeViews = NodeViewsTransformer.bounded(pickedRegion
					.getNodeViews(), pickedRegion.getBounds());

			// Initialize Region Info Tool Tip
			initToolTip();
		}

	}

	// AJK: 02/20/07 END
	// PropertyChange support

	public void propertyChange(PropertyChangeEvent e) {

		if (e.getPropertyName().equals(CytoscapeDesktop.NETWORK_VIEW_FOCUSED)) {
			((DGraphView) Cytoscape.getCurrentNetworkView()).getCanvas()
					.addMouseListener(this);
			((DGraphView) Cytoscape.getCurrentNetworkView()).getCanvas()
					.addMouseMotionListener(this);
			Cytoscape.getCurrentNetworkView().addGraphViewChangeListener(
					groupPanel);
		}
	}

	// AJK: 12/01/06 BEGIN

	protected void processRegionContextMenu(MouseEvent event) {

		if (pickedRegion == null) {
			menu.setVisible(false);
			return;
		}

		if (pickedRegion.getRegionAttributeValue() != null) {

			menu.setLabel(pickedRegion.getRegionAttributeValue().toString());

			// AP 03.18.07: Region Info in Context Menu
			// regionInfoItem.setToolTipText(boundedNodeViews.size() +
			// " of " + pickedRegion.getNodeViews().size() + " nodes in " +
			// pickedRegion.getRegionAttributeValue().toString());
			//			
			// JMenuItem infoNodeCount = new JMenuItem(boundedNodeViews.size() +
			// " of " + pickedRegion.getNodeViews().size() + " nodes");
			// JMenuItem infoRegionName = new JMenuItem("in " +
			// pickedRegion.getRegionAttributeValue().toString());
			// regionInfoSubmenu.add(infoNodeCount);
			// regionInfoSubmenu.add(infoRegionName);
			// infoNodeCount.setEnabled(false);
			// infoRegionName.setEnabled(false);
		}

		menu.setLocation(event.getX()
				+ Cytoscape.getDesktop().getNetworkPanel().getWidth(), event
				.getY()
				+ Cytoscape.getDesktop().getCyMenus().getMenuBar().getHeight()
				+ Cytoscape.getDesktop().getCyMenus().getToolBar().getHeight());

		// ((DGraphView) Cytoscape.getCurrentNetworkView()).getCanvas().getY());
		// Display PopupMenu
		menu.show(((DGraphView) Cytoscape.getCurrentNetworkView()).getCanvas(),
				event.getX() - 3, event.getY() + 1);
		// menu.setVisible(true);
	}

	// AJK: 12/01/06 END
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

	// AJK: 12/24/06 BEGIN
	// for stretching
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
			} else {
				return LEFT;
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
			} else {
				return RIGHT;
			}
		} else if ((pt.getY() >= region.getY1() - edgeTolerance)
				&& (pt.getY() <= region.getY1() + edgeTolerance)) {
			return TOP;
		} else if ((pt.getY() >= region.getY1() + region.getH1()
				- edgeTolerance)
				&& (pt.getY() <= region.getY1() + region.getH1()
						+ edgeTolerance)) {
			return BOTTOM;
		} else {
			return NOT_ON_EDGE;
		}
	}

	// not working

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

	private void stretchRegion(LayoutRegion region, int whichEdge,
			MouseEvent event) {
		// System.out.println ("Stretching region: " + region + " on edge " +
		// whichEdge);
		// if ((region == null) || (whichEdge == NOT_ON_EDGE)) {
		// return;
		// }
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
				// System.out.println("stetching from edge: " + whichEdge);
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
				;
				regionToStretch
						.setW1(regionToStretch.getW1() + mousex - startx);
			}
		}
		// AJK: use double coordinates to avoid roundoff
		// regionToStretch.setBounds((int) regionToStretch.getX1(), (int)
		// regionToStretch
		// .getY1(), (int) regionToStretch.getW1(), (int) regionToStretch
		// .getH1());
		regionToStretch.setBounds(regionToStretch.getX1(), regionToStretch
				.getY1(), regionToStretch.getW1(), regionToStretch.getH1());
		NodeViewsTransformer.transform(boundedNodeViews, regionToStretch
				.getBounds());
		// System.out.println ("Region start point set to = " +
		// regionToStretch.getX1() + "," + regionToStretch.getY1());
		Cytoscape.getCurrentNetworkView().redrawGraph(true, true);
		regionToStretch.repaint();
		startx = mousex; // reset mouse point for continuing drag
		starty = mousey;
		event.consume(); // don't have canvas draw drag rect
	}

	// AJK: 12/24/06 END
	// Upon mouse release, calculate rectangular dimensions, create LayoutRegion
	// object, and send region to LayoutRegionManager, add to a prefab canvas
	void drawRectRegion() {
		if (startx != mousex && starty != mousey) {
			int x, y; // Top left corner of the rectangle.
			int w, h; // Width and height of the rectangle.
			// x,y,w,h must be computed from the coordinates
			// of the two corner points.
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

			// Create LayoutRegion object
			LayoutRegion region = new LayoutRegion(x, y, w, h);

			// if value is selected by user, i.e., not cancelled
			if (region.getRegionAttributeValue() != null
					&& !region.getRegionAttributeValue().toString()
							.contentEquals("[]")) {

				// AJK: 12/02/06 BEGIN
				// consolidate adding to region list and adding to canvas
				LayoutRegionManager.addRegion(
						Cytoscape.getCurrentNetworkView(), region);

				// // Add region to list of regions for this view
				// LayoutRegionManager.addRegionForView(Cytoscape
				// .getCurrentNetworkView(), region);
				//
				// // Grab ArbitraryGraphicsCanvas (a prefab canvas) and add the
				// // layout region
				// DGraphView view = (DGraphView) Cytoscape
				// .getCurrentNetworkView();
				// DingCanvas backgroundLayer = view
				// .getCanvas(DGraphView.Canvas.BACKGROUND_CANVAS);
				// backgroundLayer.add(region);
				//				
				// AJK 12/02/06 END

				// Cytoscape.getCurrentNetworkView().updateView();
			}
		}

	}

	public static final String REGION_X_ATT = "__Region_x";

	public static final String REGION_Y_ATT = "__Region_y";

	public static final String REGION_W_ATT = "__Region_w";

	public static final String REGION_H_ATT = "__Region_h";

	private static final String REGION_NAME_ATT = "__Region_name";

//	private static final String REGION_NODEVIEWS_ATT = "__Region_nodeViews";

	private static final String REGION_COLORINT_ATT = "__Region_colorInt";

	/**
	 * Create a new group.
	 */
	public static void newGroup(LayoutRegion region) {
		List<NodeView> currentNodeViews = region.getNodeViews();
		List<CyNode> currentNodes = new ArrayList<CyNode>();
		for (NodeView cnv: currentNodeViews){
			currentNodes.add((CyNode) cnv.getNode());
		}
//		CyNetwork network = Cytoscape.getCurrentNetwork();
//		List<CyNode> currentNodes = new ArrayList(network.getSelectedNodes());
		String groupName = region.getRegionAttributeValue().toString();
		CyGroup group = CyGroupManager.createGroup(groupName, currentNodes,
				viewerName);
		region.setMyGroup(group);
		group.setState(SELECTED);
		groupPanel.groupCreated(group);

		CyNode groupNode = group.getGroupNode();
		CyAttributes attributes = Cytoscape.getNodeAttributes();
		attributes.setAttribute(groupNode.getIdentifier(), REGION_NAME_ATT,
				groupName);
//		attributes.setAttribute(groupNode.getIdentifier(),
//				REGION_NODEVIEWS_ATT, region.getNodeViews().toString());
		attributes.setAttribute(groupNode.getIdentifier(), REGION_COLORINT_ATT,
				region.getColorIndex());
	}

	/**
	 * Return the name of our viewer
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
	public void groupCreated(CyGroup group, CyNetworkView myView) {
		System.out.println("Building Layout Region from xGMML");
		CyNode groupNode = group.getGroupNode();
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
		nameString = nameString.replace("]", "");
		String[] nameArray = nameString.split(",");
		ArrayList<Object> name = new ArrayList<Object>();
		for (int j = 0; j < nameArray.length; j++) {
			name.add(nameArray[j]);
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
		group.getGroupNode().getRootGraph();

		LayoutRegion region = new LayoutRegion(x, y, w, h, name, nv, color,
				myView, group);
		LayoutRegionManager.addRegion(myView, region);
	}

	/**
	 * This is called when a group we care about is about to be deleted. If we
	 * weren't building our menu each time, this would be used to update the
	 * list of groups we present to the user.
	 * 
	 * @param group
	 *            the CyGroup that will be deleted
	 */
	public void groupWillBeRemoved(CyGroup group) {
		groupPanel.groupRemoved(group);
	}

	public static void groupWillBeRemoved(LayoutRegion region) {
		CyGroup group = region.getMyGroup();
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
				.getRegionAttributeValue().toString()));
		// List<CyNode> nodeList = group.getNodes();
		// Cytoscape.getCurrentNetwork().setSelectedNodeState(nodeList, true);
		// Cytoscape.getCurrentNetworkView().updateView();
		group.setState(SELECTED);
		// groupPanel.groupChanged(group);
	}

	/**
	 * Perform the action associated with an unselect menu selection
	 */
	private void unselect(LayoutRegion region) {
		CyGroup group = CyGroupManager.getCyGroup(Cytoscape.getCyNode(region
				.getRegionAttributeValue().toString()));
		// List<CyNode> nodeList = group.getNodes();
		// Cytoscape.getCurrentNetwork().setSelectedNodeState(nodeList, false);
		// Cytoscape.getCurrentNetworkView().updateView();
		group.setState(UNSELECTED);
		// groupPanel.groupChanged(group);
	}

	/**
	 * This class gets attached to the menu item.
	 */
	public class MainPluginAction extends AbstractAction {
		/**
		 * The constructor sets the text that should appear on the menu item.
		 */
		public MainPluginAction() {
			super("Interactive Layout");
		}

		/**
		 * This method is called when the user selects the menu item.
		 */
		public void actionPerformed(ActionEvent ae) {

		}

		public void initializeBubbleRouter() {

		}

	}

	// AJK: 12/01/06 BEGIN
	// popup action listener and, for context menu added to region

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
		public void actionPerformed(ActionEvent ae) {
			String label = ((JMenuItem) ae.getSource()).getText();
			// Figure out the appropriate action
			if ((label == DELETE_REGION) && (pickedRegion != null)) {
				// System.out.println("delete region: "
				// + pickedRegion.getAttributeName());

				// AJK: 03/17/2007 BEGIN
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
				}

			} // end of if ()
			else if ((label == LAYOUT_REGION) && (pickedRegion != null)) {

				// AJK: 01/09/06 use double coordinates to avoid roundoff errors
				// pickedRegion.setBounds((int) pickedRegion.getX1(),
				// (int) pickedRegion.getY1(), (int) pickedRegion.getW1(),
				// (int) pickedRegion.getH1());
				pickedRegion.setBounds(pickedRegion.getX1(), pickedRegion
						.getY1(), pickedRegion.getW1(), pickedRegion.getH1());

				// AJK: 03/16/07: BEGIN
				// set up for Undo/Redo

				List myNodeViews = pickedRegion.getNodeViews();
				Rectangle oldBounds = pickedRegion.getBounds();
				_nodeViews = new NodeView[myNodeViews.size()];
				_undoOffsets = new Point2D[myNodeViews.size()];
				_redoOffsets = new Point2D[myNodeViews.size()];

				// System.out.println("_nodeViews = " + _nodeViews + ", length =
				// "
				// + _nodeViews.length);
				for (int j = 0; j < _nodeViews.length; j++) {
					_nodeViews[j] = (NodeView) myNodeViews.get(j);
					// System.out.println("Getting offset for _nodeviews[" + j
					// + "], " + _nodeViews[j]);
					// System.out.println("Got offset "
					// + _nodeViews[j].getOffset());
					// _undoOffsets[j] = _nodeViews[j].getOffset();
				}

				/**
				 * for undo/redo of uncrossing
				 */

				NodeViewsTransformer.transform(pickedRegion.getNodeViews(),
						pickedRegion.getBounds());

				// AJK: 03/16/07 END

				Cytoscape.getCurrentNetworkView().redrawGraph(true, true);
				pickedRegion.repaint();
				System.out.println("Region layout updated");
				// AP 1.2.07
				// collect NodeViews bounded by current region
				boundedNodeViews = NodeViewsTransformer.bounded(pickedRegion
						.getNodeViews(), pickedRegion.getBounds());
				UnCrossAction.unCross(boundedNodeViews, false);
				// boolean indicates that UnCrossAction is not the top level
				// caller, does
				// not need undo/redo logic

				for (int m = 0; m < _nodeViews.length; m++) {
					_redoOffsets[m] = _nodeViews[m].getOffset();
				}

				CyUndo.getUndoableEditSupport().postEdit(
						new AbstractUndoableEdit() {
							// CytoscapeDesktop.addEdit(new
							// AbstractUndoableEdit() {

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
				// AP: 2/25/07 uncross edges of nodes selected only WITHIN a
				// region
				UnCrossAction.unCross(boundedNodeViews);
				System.out.println("Edges uncrossed");
			} else {
				// throw an exception here?
				System.err.println("Unexpected Region popup option");
			} // end of else
		}
	}

	// AJK: 12/01/06 END

	// AJK 02/20/07 BEGIN
	// Delete All Regions
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

	// KH: 03/14/07
	class GetBubbleHelpListener implements ActionListener {
		private String helpURL = "http://www.genmapp.org/InteractiveLayout/manual.htm";

		public void actionPerformed(ActionEvent ae) {
			cytoscape.util.OpenBrowser.openURL(helpURL);
		}

	}

	public PluginInfo getPluginInfoObject() {

		PluginInfo Info = new PluginInfo(); // can be created with a unique id,
		// but for updating this has to
		// match what the document contains
		// as an id

		Info.setName("BubbleRouter"); // name can be anything

		Info.setDescription("Attribute-based layout using interactive regions");

		Info.setCategory("Functional Enrichment"); // currently no categories
		// preset, but
		// this is an option

		Info.setPluginVersion(1.0);

		Info
				.setProjectUrl("http://conklinwolf.ucsf.edu/genmappwiki/Interactive_Layout_Plugin");

		Info.addAuthor("Allan Kuchinsky", "Agilent");

		Info.addAuthor("Alex Pico", "UCSF");
		Info.addAuthor("Kristina Hanspers", "UCSF");

		return Info;

	}

}
