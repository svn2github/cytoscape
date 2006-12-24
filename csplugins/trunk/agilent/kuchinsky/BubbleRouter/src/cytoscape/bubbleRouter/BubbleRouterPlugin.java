package cytoscape.bubbleRouter;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Point2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.AbstractAction;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

import cytoscape.Cytoscape;
import cytoscape.plugin.CytoscapePlugin;
import cytoscape.view.CytoscapeDesktop;
import ding.view.DGraphView;
import ding.view.DingCanvas;
import ding.view.InnerCanvas;

public class BubbleRouterPlugin extends CytoscapePlugin implements
		MouseListener, MouseMotionListener, PropertyChangeListener {

	protected InnerCanvas canvas;

	protected JPanel cyAnnPanel;

	boolean dragging; // This is set to true when a drag begins, and to false
	
	// AJK: 12/24/06 BEGIN
	//     flags for moving and stretching of region
	private boolean moving = false;
	private boolean stretching = false;
	private boolean onEdge = false;
	private boolean onCorner = false;
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
	public static String DELETE_REGION = "Delete Region";

	JPopupMenu menu = new JPopupMenu("Layout Region");

	LayoutRegion pickedRegion = null;

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
		((DGraphView) Cytoscape.getCurrentNetworkView()).getCanvas()
				.addMouseListener(this);

		JMenuItem deleteRegionItem = new JMenuItem(this.DELETE_REGION);
		RegionPopupActionListener popupActionListener = new RegionPopupActionListener();
		deleteRegionItem.addActionListener(popupActionListener);
		menu.add(deleteRegionItem);
		menu.setVisible(false);
		// AJK: 12/01/06 END

		MainPluginAction mpa = new MainPluginAction();
		mpa.initializeBubbleRouter();

	}

	public void mouseDragged(MouseEvent e) {
		// If a dragging operation is in progress, get the new
		// values for mousex and mousey, and repaint.

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
		//    moving and stretching
		// TODO: refactor to remove redundancies
		else if ((!onEdge) && (!onCorner) && (!moving) && (pickedRegion != null))
		{
			moving = true;
			startx = e.getX();
			starty = e.getY();
			startPoint = e.getPoint();
			System.out.println ("Region start point = " + 
					pickedRegion.getX1() + "," + pickedRegion.getY1());
			e.consume(); // don't have canvas draw drag rect
			return; // don't have canvas draw drag rect
		}
		else if (moving)
		{
			mousex = e.getX();
			mousey = e.getY();
			nextPoint = e.getPoint();
			if (pickedRegion != null)
			{
				pickedRegion.setX1(pickedRegion.getX1() + mousex - startx);
				pickedRegion.setY1(pickedRegion.getY1() + mousey - starty);
				pickedRegion.setBounds((int) pickedRegion.getX1(),
						(int) pickedRegion.getY1(), 
						(int) pickedRegion.getW1(), 
						(int) pickedRegion.getH1()); 
				NodeViewsTransformer.transform(
						pickedRegion.getNodeViews(), pickedRegion.getBounds());
//				System.out.println ("Region start point set to = " + 
//						pickedRegion.getX1() + "," + pickedRegion.getY1());
				Cytoscape.getCurrentNetworkView().redrawGraph(true, true);
				pickedRegion.repaint();
			}
			startx = mousex;  // reset mouse point for continuing drag
			starty = mousey;
			e.consume(); // don't have canvas draw drag rect
			return; 
			
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
		//    reset all flags
		moving = false;
		dragging = false;
		onEdge = false;
		onCorner = false;
		stretching = false;
		// AJK: 12/24/06 END
	}

	public void mouseExited(MouseEvent e) {
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mousePressed(MouseEvent e) {
		if (e.getButton() == MouseEvent.BUTTON3) {
			processRegionMousePressEvent(e);
		}
		else 
		{
			menu.setVisible(false);
			// AJK: 12/24/06 BEGIN
			//    set picked region
			pickedRegion = LayoutRegionManager.getPickedLayoutRegion(e.getPoint());
			// TODO: refactor processRegionMousePressEvent
		}
	}

	public void mouseMoved(MouseEvent e) {
	}

	public void mouseClicked(MouseEvent e) {
	}

	public void propertyChange(PropertyChangeEvent e) {

		if (e.getPropertyName().equals(CytoscapeDesktop.NETWORK_VIEW_FOCUSED)) {
			((DGraphView) Cytoscape.getCurrentNetworkView()).getCanvas()
					.addMouseListener(this);
			((DGraphView) Cytoscape.getCurrentNetworkView()).getCanvas()
					.addMouseMotionListener(this);
		}
	}

	// AJK: 12/01/06 BEGIN

	protected void processRegionMousePressEvent(MouseEvent event) {

		pickedRegion = LayoutRegionManager.getPickedLayoutRegion(event
				.getPoint());
		if (pickedRegion == null) {
			menu.setVisible(false);
			return;
		}
		
		if (pickedRegion.getRegionAttributeValue() != null)
		{
			System.out.println("clicked on region: "
					+ pickedRegion.getRegionAttributeValue());
			
			menu.setLabel(pickedRegion.getRegionAttributeValue().toString());
			
		}


		menu.setLocation(event.getX()
				+ Cytoscape.getDesktop().getNetworkPanel().getWidth(), event
				.getY()
				+ Cytoscape.getDesktop().getCyMenus().getMenuBar().getHeight()
				+ Cytoscape.getDesktop().getCyMenus().getToolBar().getHeight());

		// ((DGraphView) Cytoscape.getCurrentNetworkView()).getCanvas().getY());
		// Display PopupMenu
		 menu.show(
	                ((DGraphView) Cytoscape.getCurrentNetworkView()).getCanvas(),
	                event.getX(),
	                event.getY());
//		menu.setVisible(true);
	}

	// AJK: 12/01/06 END

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
			if (region.getRegionAttributeValue() != null) {

				// AJK: 12/02/06 BEGIN
				// consolidate adding to region list and adding to canvas
				LayoutRegionManager.addRegion(Cytoscape
						.getCurrentNetworkView(), region);

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

			}
		}

	}

	/**
	 * This class gets attached to the menu item.
	 */
	public class MainPluginAction extends AbstractAction {
		/**
		 * The constructor sets the text that should appear on the menu item.
		 */
		public MainPluginAction() {
			super("Bubble Router");
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
			if ((label == DELETE_REGION) || (pickedRegion != null)) {
				System.out.println ("delete region: " + pickedRegion.getAttributeName());
				LayoutRegionManager.removeRegion(Cytoscape
						.getCurrentNetworkView(), pickedRegion);

			} // end of if ()
			else {
				// throw an exception here?
				System.err.println("Unexpected Region popup option");
			} // end of else
		}
	}
	// AJK: 12/01/06 END

}
