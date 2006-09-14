package cytoscape.bubbleRouter;

import giny.view.NodeView;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Container;
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
import javax.swing.JComponent;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import cytoscape.Cytoscape;
import cytoscape.plugin.CytoscapePlugin;
import cytoscape.view.CyNetworkView;
import cytoscape.view.CytoscapeDesktop;
import ding.view.DGraphView;
import ding.view.InnerCanvas;

public class BubbleRouterPlugin extends CytoscapePlugin
    implements MouseListener, MouseMotionListener, PropertyChangeListener {

	protected InnerCanvas canvas;
	// AP 8/21
	protected JPanel cyAnnPanel;

	/* Some variables used during dragging. */
	boolean dragging; // This is set to true when a drag begins, and to false
	
	// AJK: 09/07/06 keep track of whether event handler has been started
	boolean handlerStarted = false;

	// when it ends. The value is checked by mouseReleased()
	// and mouseDragged().
	int startx, starty; // The location of the mouse when the dragging started.

	int mousex, mousey; // The location of the mouse during dragging.

	private static final int WIDTH = 400;

	private static final int HEIGHT = 400;
	
	protected static int count = 0;

	// AP 8/21 end
	/**
	 * the mouse press location for the drop point
	 */
	protected Point2D startPoint;

	/**
	 * point used in tracking mouse movement
	 */
	protected Point2D nextPoint;
	
	private static final String DEFAULT_ATTRIBUTE_NAME = "component";


	/**
	 * 
	 */
	public BubbleRouterPlugin () {
		
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
			System.out.println("AP: Mouse Drag + shift!");
			startx = e.getX();
			starty = e.getY();
			startPoint = e.getPoint();
		}

		else if (dragging) {
			// System.out.println("AP: Mouse Dragged New!");
			mousex = e.getX();
			mousey = e.getY();
			nextPoint = e.getPoint();
		}
		// just rely on canvas to draw drag rect
		((DGraphView) Cytoscape.getCurrentNetworkView()).getCanvas().mouseDragged(e);
//		canvas.mouseDragged(e);
	}

	public void mouseReleased(MouseEvent e) {
		// End the dragging operation, if one is in progress. Draw
		// the final figure, if any onto the off-screen canvas, so
		// it becomes a permanent part of the image.
		if (dragging) {
			dragging = false;
			// cyAnnPanel.repaint();
//			System.out.println("AP: Mouse Released from dragging!");

			drawFillFigure();

			count += 1;
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

		if (e.getPropertyName().equals(CytoscapeDesktop.NETWORK_VIEW_FOCUSED)) {   //added 8/17
		
		((DGraphView) Cytoscape.getCurrentNetworkView()).getCanvas().addMouseListener(this);
		((DGraphView) Cytoscape.getCurrentNetworkView()).getCanvas().addMouseMotionListener(this);
		}}
	
	void drawFillFigure() {
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
			// AP 8.30
			BufferedImage image = new BufferedImage(WIDTH, HEIGHT,
					BufferedImage.TYPE_INT_ARGB);
			Graphics2D g = (Graphics2D) image.createGraphics();

			float transluc = 0.1F;
			g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
					transluc));

			if (count % 2 == 0) { // is even
				g.setColor(Color.green);
			} else { // is odd
				g.setColor(Color.red);
			}
			g.fillRect(x, y, w, h);
			g.setColor(Color.black);
			g.drawRect(x, y, w, h);

			JLabel label = new JLabel(new ImageIcon(image));
			label.setBounds(0, 0, image.getWidth(), image.getHeight());
			Cytoscape.getCurrentNetworkView().getComponent();
			JComponent component = Cytoscape.getDesktop()
					.getNetworkViewManager().getComponentForView(
							Cytoscape.getCurrentNetworkView());
			Container layeredPane = ((JInternalFrame) component)
					.getLayeredPane();
			Integer cyAnnotationLayer = cytoscape.view.InternalFrameLayeredComponent.ANNOTATION_LAYER;
			layeredPane.add(label, cyAnnotationLayer);
			label.setOpaque(false);

			// AP 8.31
			LayoutRegion region = new LayoutRegion(getRegionAttributeValue(),
					x, y, w, h);
			// region.setPaint(g.getColor());
			LayoutRegionManager.addRegionForView(Cytoscape
					.getCurrentNetworkView(), region);
			// ADD: Call Router here
			region.populateNodeViews(this.DEFAULT_ATTRIBUTE_NAME);

		}

	} // end drawFillFigure()

	
	// AP 8.31 Controller Stuff
	// ADD: Prompt user via pulldown menu
	public Object getRegionAttributeValue() {
		Object[] possibilities = { "Nucleus", "Extracellular", "Cytosol",
				"Plasma membrane", "unassigned" };
		Object s = JOptionPane.showInputDialog(Cytoscape.getDesktop(),
				"Select a value or die", "Select a value",
				JOptionPane.PLAIN_MESSAGE, null, possibilities, "Cytosol");
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
			super("CyAnnotation Editor");
		}
		/**
		 * This method is called when the user selects the menu item.
		 */
		public void actionPerformed(ActionEvent ae) {
//			initializeCyAnnotationEditor();
		}
		public void initializeBubbleRouter () {
			
		}	

	}
	
}
