package org.cytoscape.groups.results;

// System imports
import giny.view.NodeView;

import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.GridBagConstraints;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Point2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import metaNodePlugin2.model.MetaNode;

//import metaNodePlugin2.model.MetaNode;

import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.util.CyNetworkNaming;
import cytoscape.view.CyNetworkView;
import cytoscape.view.CytoscapeDesktop;

//Cytoscape group system imports
import cytoscape.groups.CyGroup;
import cytoscape.groups.CyGroupViewer;
import cytoscape.groups.CyGroupViewer.ChangeType;

import cytoscape.groups.CyGroupManager;
import cytoscape.groups.CyGroupChangeListener;

import cytoscape.view.cytopanels.CytoPanel;
import cytoscape.view.cytopanels.CytoPanelState;
import org.cytoscape.groups.results.LayoutRegion;
import ding.view.DGraphView;

/**
 * The GroupPanel is the implementation for the Cytopanel that presents the
 * group list mechanism to the user.
 */
public class GroupHandler implements MouseListener, MouseMotionListener, PropertyChangeListener,
		CyGroupChangeListener, CyGroupViewer {

	public String viewerName = "moduleFinderViewer";
	private static boolean registeredWithGroupPanel = false;

	// Controlling variables
	public static boolean multipleEdges = false;
	public static boolean recursive = true;
	
	private Method updateMethod = null;
	private CyGroupViewer namedSelectionViewer = null;
	
	// State values
	public static final int EXPANDED = 1;
	public static final int COLLAPSED = 2;

	private int viewID = 0;
	
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
	 * String used to compare against os.name System property -
	 * to determine if we are running on Windows platform.
	 */
	static final String MAC_OS_ID = "mac";

	
	public GroupHandler() {
		super();

		CyGroupManager.addGroupChangeListener(this);
		
		//register groupViewer
		CyGroupManager.registerGroupViewer(this);
		
		// Listen for Network View Focus
		Cytoscape.getDesktop().getSwingPropertyChangeSupport()
				.addPropertyChangeListener(
						CytoscapeDesktop.NETWORK_VIEW_FOCUSED, this);

		// Listen for Network View Destruction
		Cytoscape.getDesktop().getSwingPropertyChangeSupport()
				.addPropertyChangeListener(
						CytoscapeDesktop.NETWORK_VIEW_DESTROYED, this);

		// Listen for Network View creation
		Cytoscape.getDesktop().getSwingPropertyChangeSupport()
				.addPropertyChangeListener(
						CytoscapeDesktop.NETWORK_VIEW_CREATED, this);

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
			//Cytoscape.getCurrentNetworkView().addGraphViewChangeListener(
			//		groupPanel);
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
				//preloadCellularComponents();
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
		if ( LayoutRegionManager.getNumRegionsForView
						(Cytoscape.getCurrentNetworkView()) < 1) { return; }

		onEdge = calculateOnEdge(e.getPoint(), pickedRegion);
		
		if (!dragging) {
			dragging = true;
			startx = e.getX();
			starty = e.getY();
			startPoint = e.getPoint();
		} else if (dragging) {
			mousex = e.getX();
			mousey = e.getY();
			nextPoint = e.getPoint();
		} 

		if ((onEdge == NOT_ON_EDGE) && (!moving) && (!stretching)
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
				NodeViewsTransformer.transform(pickedRegion.getNodeViews(), pickedRegion
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
			//stretchRegion(regionToStretch, edgeBeingStretched, e);
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
		if (
				(LayoutRegionManager.getNumRegionsForView
						(Cytoscape.getCurrentNetworkView()) < 1)) { return; }
		
		if (dragging) {
			dragging = false;
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
/*
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
*/
	}

	/**
	 * Captures clicks on regions for selection and right-clicks on regions to
	 * trigger context menu.
	 * 
	 * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
	 */
	public void mousePressed(MouseEvent e) {
		
		if ((e.getButton() == MouseEvent.BUTTON3) ||
				(e.isControlDown() && (isMacPlatform()))) {
			setRegionSelection(e);
			//processRegionContextMenu(e);
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

	public void setRegionSelection(MouseEvent e) {
		oldPickedRegion = pickedRegion;
		pickedRegion = (LayoutRegion) LayoutRegionManager.getPickedLayoutRegion(e.getPoint());

		if (pickedRegion == null) {			
			// user clicked on outside of any region, un-select selected region
			List regionList = LayoutRegionManager.getRegionListForView(Cytoscape
					.getCurrentNetworkView());

			for (int i=0; i< regionList.size(); i++){
				LayoutRegion theRegion = (LayoutRegion) regionList.get(i);
				if (theRegion.isSelected()){
					theRegion.setSelected(false);
				}
			}
			return;
		}

		// user clicked on a region, select it
		pickedRegion.setSelected(true);
		
		// unselect old region
		List regionList = LayoutRegionManager.getRegionListForView(Cytoscape
				.getCurrentNetworkView());
		if ((oldPickedRegion != null) && (oldPickedRegion != pickedRegion)
				&& (regionList.contains(oldPickedRegion))) {
			oldPickedRegion.setSelected(false);
			oldPickedRegion.repaint();			
			System.out.println("Unselect old region");

		}

		// select new region
		//if (pickedRegion != null) {
		//	pickedRegion.setSelected(true);
		//	pickedRegion.repaint();
		//	select(pickedRegion);

			// define NodeView bounded by region
		//	boundedNodeViews = NodeViewsTransformer.bounded(pickedRegion
		//			.getNodeViews(), pickedRegion.getBounds());

			// initialize region info tool tip
			//initToolTip();
		//}

	}
	
	
	/**
	 * Perform the action associated with an unselect menu selection
	 */
	
	/*
	private void unselect(LayoutRegion region) {
		
		System.out.println("unselect()");

		CyGroup group = region.getMyGroup();
		
		if (group != null)
		{
			group.setState(UNSELECTED);
		}
	}
*/
	/**
	 * Routine which determines if we are running on mac platform
	 *
	 * @return boolean
	 */
	private boolean isMacPlatform() {
		String os = System.getProperty("os.name");

		return os.regionMatches(true, 0, MAC_OS_ID, 0, MAC_OS_ID.length());
	}


	//required by CyGroupChangeListener
	public void groupChanged(CyGroup group, CyGroupChangeListener.ChangeType change) { 
		if ( change == CyGroupChangeListener.ChangeType.GROUP_CREATED ) {
			//System.out.println("\nGroupHandler: GROUP_CREATE");
			
			//Create LayoutRegion object
			LayoutRegion layoutRegion = new LayoutRegion(group);
			
		} else if ( change == CyGroupChangeListener.ChangeType.GROUP_DELETED ) {
			//System.out.println("GroupHandler: GROUP_DELETED");
		} else if ( change == CyGroupChangeListener.ChangeType.GROUP_MODIFIED ) {
			//System.out.println("GroupHandler: GROUP_MODIFIED");
		} else {
			System.err.println("unsupported change Group ChangeType");
		}
	}
	
	//
	// These are required by the CyGroupViewer interface
	/**
	 * Return the name of our viewer
	 *
	 * @return viewer name
	 */
	public String getViewerName() { return viewerName; }
	

	/**
	 * This is called when a new group has been created that
	 * we care about.  If we weren't building our menu each
	 * time, this would be used to update the list of groups
	 * we present to the user.
	 *
	 * @param group the CyGroup that was just created
	 */
	public void groupCreated(CyGroup group) { 

		System.out.println("groupCreated()");
	}

	/**
	 * This is called when a new group has been created that
	 * we care about.  This version of the groupCreated
	 * method is called by XGMML and provides the CyNetworkView
	 * that is in the process of being created.
	 *
	 * @param group the CyGroup that was just created
	 * @param view the CyNetworkView that is being created
	 */
	public void groupCreated(CyGroup group, CyNetworkView myview) { 
		System.out.println("groupCreated() Apple");

	}

	/**
	 * This is called when a group we care about is about to 
	 * be deleted.  If we weren't building our menu each
	 * time, this would be used to update the list of groups
	 * we present to the user.
	 *
	 * @param group the CyGroup that will be deleted
	 */
	public void groupWillBeRemoved(CyGroup group) {
		System.out.println("groupWillBeRemoved()");
	}


	
	/**
	 * This is called when a group we care about has been
	 * changed (usually node added or deleted).
	 *
	 * @param group the CyGroup that has changed
	 * @param node the CyNode that caused the change
	 * @param change the change that occured
	 */
	public void groupChanged(CyGroup group, CyNode node, CyGroupViewer.ChangeType change) { 
		System.out.println("groupChanged()");
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

	
}
