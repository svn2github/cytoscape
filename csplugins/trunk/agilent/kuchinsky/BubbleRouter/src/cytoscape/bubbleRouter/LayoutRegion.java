package cytoscape.bubbleRouter;

import giny.model.Node;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.JComponent;

import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;

public class LayoutRegion extends JComponent {

	/**
	 * Translucency level of region
	 */
	private static final int TRANSLUCENCY_LEVEL = (int) (255 * .10);

	private double x1;

	private double y1;

	private double w1;

	private double h1;

	/**
	 * index into color array
	 */
	private static int colorIndex = -1;

	/**
	 * possible colors for layout regions
	 */
	private final Color[] colors = new Color[] { Color.red, Color.green,
			Color.blue, Color.orange, Color.cyan, Color.magenta, Color.darkGray };

	private Paint paint;

	/**
	 * ref to our buffered region
	 */
	private BufferedImage image;

	// variables for attribute and value selection
	/**
	 * name of the selected attribute field
	 */
	private static String attributeName = null;

	/**
	 * unique list of values for attributeName
	 */
	private static Object[] attributeValues = null;

	/**
	 * particular value associated with a layout region
	 */
	public static Object regionAttributeValue = null;

	/**
	 * list of nodes associated with a layout region based on
	 * regionAttributeValue
	 */
	private List nodeViews;

	/**
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 */
	public LayoutRegion(double x, double y, double width, double height) {
		super();

		// init member vars
		selectRegionAttributeValue();
		// setbounds must come before populate nodeviews
		setBounds((int) x, (int) y, (int) width, (int) height);
		nodeViews = populateNodeViews();

		// determine color of layout region
		colorIndex = (++colorIndex % colors.length == 0) ? 0 : colorIndex;
		this.paint = colors[colorIndex];
		
	}

	/**
	 * Empty Constructor
	 * 
	 */
	public LayoutRegion() {
		super();

		nodeViews = new ArrayList();

	}

	/**
	 * @return Returns the h1.
	 */
	public double getH1() {
		return h1;
	}

	/**
	 * @param h1
	 *            The h1 to set.
	 */
	public void setH1(double h1) {
		this.h1 = h1;
	}

	/**
	 * @return Returns the w1.
	 */
	public double getW1() {
		return w1;
	}

	/**
	 * @param w1
	 *            The w1 to set.
	 */
	public void setW1(double w1) {
		this.w1 = w1;
	}

	/**
	 * @return Returns the x1.
	 */
	public double getX1() {
		return x1;
	}

	/**
	 * @param x1
	 *            The x1 to set.
	 */
	public void setX1(double x1) {
		this.x1 = x1;
	}

	/**
	 * @return Returns the y1.
	 */
	public double getY1() {
		return y1;
	}

	/**
	 * @param y1
	 *            The y1 to set.
	 */
	public void setY1(double y1) {
		this.y1 = y1;
	}

	/**
	 * @return Returns the paint.
	 */
	public Paint getPaint() {
		return paint;
	}

	/**
	 * @param paint
	 *            The paint to set.
	 */
	public void setPaint(Paint paint) {
		this.paint = paint;
	}

	/**
	 * @return Returns the nodeViews.
	 */
	public List getNodeViews() {
		return nodeViews;
	}

	/**
	 * @param list
	 *            The nodeViews to set.
	 */
	public void setNodeViews(List list) {
		this.nodeViews = list;
	}

	/**
	 * Prompt user to select and attribute name and value
	 * 
	 * @return
	 */
	public static void selectRegionAttributeValue() {

		// Use Ethan's QuickFind dialog for attribute selection
		// TODO: modify dialog to provide value selection as well
		// and perhaps an all-value "brick" layout too
//		if (attributeName == null) {
			new QuickFindConfigDialog();
//		}

//		Object s = JOptionPane.showInputDialog(Cytoscape.getDesktop(),
//				"Assign a value to this region", "Bubble Router",
//				JOptionPane.PLAIN_MESSAGE, null, attributeValues,
//				attributeValues[0]);
//		return s;
	}

	/**
	 * Used by QuickFindConfigDialog
	 * 
	 * @param newAttributeKey
	 */
	public static void setAttributeName(String newAttributeKey) {
		attributeName = newAttributeKey;
		System.out.println("Attribute name selected: " + attributeName);
	}

	/**
	 * Could be used to display attribute name associated with region 
	 * and/or to initialize selection dialog when changing attribute name
	 */
	public String getAttributeName() {
		return attributeName;
	}

	/**
	 * Used by QuickFindConfigDialog
	 * 
	 * @param objects
	 */
	public static void setAttributeValues(Object[] objects) {
		attributeValues = objects;
	}

	/**
	 * Used by BubbleRouterPlugin to verify that a value has been selected,
	 * i.e., that the user did NOT cancel the selection dialog
	 * 
	 * @return Returns the regionAttributeValue.
	 */
	public Object getRegionAttributeValue() {
		return regionAttributeValue;
	}

	/**
	 * Could be used to allow user control over changing a regions attribute
	 * value association
	 * 
	 * @param regionAttributeValue
	 */
	public static void setRegionAttributeValue(Object object) {
		regionAttributeValue = object;
		System.out.println("Attribute value selected: " + regionAttributeValue);

	}

	// select all nodeViews with specified attribute value for attribute
	public List populateNodeViews() {
		CyAttributes attribs = Cytoscape.getNodeAttributes();
		Iterator it = Cytoscape.getCurrentNetwork().nodesIterator();
		List selectedNodes = new ArrayList();
		while (it.hasNext()) {
			Cytoscape.getCurrentNetwork().unselectAllNodes();
			Node node = (Node) it.next();
			String val = attribs.getStringAttribute(node.getIdentifier(),
					attributeName);
			if (val != null) {
				if (val.equalsIgnoreCase(this.regionAttributeValue.toString())) {
					selectedNodes.add(node);
				}
			} else if (regionAttributeValue.equals("unassigned")) {
				selectedNodes.add(node);
			}
		}
		Cytoscape.getCurrentNetwork().setSelectedNodeState(selectedNodes, true);
		System.out.println("selected " + selectedNodes.size()
				+ " nodes for layout.");

		// If some nodes were select, then it's safe to run the hierarchical
		// layout
		if (selectedNodes.size() > 0) {
			HierarchicalLayoutListener hierarchicalListener = new HierarchicalLayoutListener();
			System.out.println("running hierarchical layout algorithm.");
			hierarchicalListener.actionPerformed(null);

			NodeViewsTransformer
					.transform(Cytoscape.getCurrentNetworkView()
							.getSelectedNodes(), new Rectangle2D.Double(x1, y1,
							w1, h1));

			Cytoscape.getCurrentNetworkView().redrawGraph(true, true);

			// Associate selected nodes with region
			return selectedNodes;
		} else {
			return null;
		}
	}

	public void setBounds(int x, int y, int width, int height) {
		super.setBounds(x, y, width, height);

		// set member vars
		this.x1 = x;
		this.y1 = y;
		this.w1 = width;
		this.h1 = height;

		// our bounds have changed, create a new image with new size
		if ((width > 0) && (height > 0)) {
			image = new BufferedImage(width, height,
					BufferedImage.TYPE_INT_ARGB);
		}
	}

	public void paint(Graphics g) {

		// only paint if we have an image to paint onto
		if (image != null) {

			// before anything, lets make sure we have a color
			Color currentColor = (paint instanceof Color) ? (Color) paint
					: null;
			if (currentColor == null) {
				System.out
						.println("LayoutRegion.paint(), currentColor is null");
				return;
			}

			// pick a base color for fill
			Color fillBaseColor = Color.blue;

			// set proper translucency for fill color
			Color fillColor = new Color(fillBaseColor.getRed(), fillBaseColor
					.getGreen(), fillBaseColor.getBlue(), TRANSLUCENCY_LEVEL);

			// set vaiable edge/rim color
			Color drawColor = new Color(currentColor.getRed(), currentColor
					.getGreen(), currentColor.getBlue());

			// image to draw
			Graphics2D image2D = image.createGraphics();

			// draw into the image
			Composite origComposite = image2D.getComposite();
			image2D
					.setComposite(AlphaComposite
							.getInstance(AlphaComposite.SRC));
			image2D.setPaint(fillColor);
			image2D.fillRect(0, 0, image.getWidth(null), image.getHeight(null));
			image2D.setColor(drawColor);
			// adds thickness to border
			image2D.drawRect(1, 1, image
					.getWidth(null) - 3, image.getHeight(null) - 3);
			//give border dimensionality
			image2D.draw3DRect(0, 0, image.getWidth(null) - 1, image
					.getHeight(null) - 1, true);
			image2D.setComposite(origComposite);
			((Graphics2D) g).drawImage(image, null, 0, 0);
		}
	}

}
