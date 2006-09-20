package cytoscape.bubbleRouter;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

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
						// when released

	boolean handlerStarted = false;

	// when it ends. The value is checked by mouseReleased()
	// and mouseDragged().

	int startx, starty; // The location of the mouse when the dragging started.

	int mousex, mousey; // The location of the mouse during dragging.

	protected static int count = 0;

	/**
	 * the mouse press location for the drop point
	 */
	protected Point2D startPoint;

	/**
	 * point used in tracking mouse movement
	 */
	protected Point2D nextPoint;

	private static String ATTRIBUTE_NAME = null;

	private static Object[] ATTRIBUTE_VALUES = null;

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
	}

	public void mouseExited(MouseEvent e) {
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mousePressed(MouseEvent e) {
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

	// Upon mouse release, calculate rectangular dimensions, create LayoutRegion
	// object, handle graphics, add to a prefab canvas, and route nodes to
	// region
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
			LayoutRegion region = new LayoutRegion(getRegionAttributeValue(),
					x, y, w, h);

			// if value is selected by user, i.e., not canceled
			if (region.getAttValue() != null) {

				// Add region to list of regions for this view
				LayoutRegionManager.addRegionForView(Cytoscape
						.getCurrentNetworkView(), region);

				// Handle graphics and create label object
				BufferedImage image = new BufferedImage(Cytoscape
						.getCurrentNetworkView().getComponent().getWidth(),
						Cytoscape.getCurrentNetworkView().getComponent()
								.getHeight(), BufferedImage.TYPE_INT_ARGB);

				Graphics2D g = (Graphics2D) image.createGraphics();

				float transluc = 0.1F;
				g.setComposite(AlphaComposite.getInstance(
						AlphaComposite.SRC_OVER, transluc));

				JLabel label = new JLabel(new ImageIcon(image));
				label.setBounds(0, 0, image.getWidth(), image.getHeight());

				// Grab ArbitraryGraphicsCanvas (a prefab canvas) and add label
				DGraphView view = (DGraphView) Cytoscape
						.getCurrentNetworkView();
				DingCanvas foregroundLayer = view
						.getCanvas(DGraphView.Canvas.FOREGROUND_CANVAS);
				foregroundLayer.add(label);
				foregroundLayer.setVisible(true);
				foregroundLayer.setOpaque(false);
				label.setVisible(true);
				label.setOpaque(false);

				// Direct call to LayoutRegion paint method seems improper, but
				// it works...
				region.paint(g);

				// Call router to send nodes to region
				region.populateNodeViews(ATTRIBUTE_NAME);
			}
		}

	} // end drawRectRegion()

	public static void setAttributeName(String newAttributeKey) {
		ATTRIBUTE_NAME = newAttributeKey;
		System.out.println("Attribute set to: " + ATTRIBUTE_NAME);
	}

	public static void setAttributeValues(Object[] objects) {
		ATTRIBUTE_VALUES = objects;
	}

	// Prompt user to select a value
	public Object getRegionAttributeValue() {

		// Use Ethan's QuickFind dialog for attribute selection
		// TODO: modify dialog to provide value selection as well
		// and perhaps an all-value "brick" layout too
		if (ATTRIBUTE_NAME == null) {
			new QuickFindConfigDialog();
		}

		Object s = JOptionPane.showInputDialog(Cytoscape.getDesktop(),
				"Assign a value to this region", "Bubble Router",
				JOptionPane.PLAIN_MESSAGE, null, ATTRIBUTE_VALUES,
				ATTRIBUTE_VALUES[0]);
		return s;
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

}
