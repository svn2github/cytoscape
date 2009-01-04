package org.genmapp.cellularlayout;

import giny.model.Node;
import giny.view.NodeView;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;
import cytoscape.view.CyNetworkView;

public class Region {

	// shape and parameters from template
	private String shape; // Line, Arc, Oval, Rectangle
	private Color color;
	private double centerX;
	private double centerY;
	private double width; 
	private double height;
	private double rotation;
	private String attValue;

	// additional parameters not from template
	private String attName;
	private List<String> nestedAttValues; // values represented by attValue
	private CyNetworkView myView;
	private List<NodeView> nodeViews;
	private int nodeCount;
	private int columns;
	private boolean visibleBorder;

	public Region(String shape, String color, double centerX, double centerY,
			double width, double height, double rotation, String attValue) {

		this.shape = shape;
		this.color = Color.decode(color); // decode hexadecimal string
		this.centerX = centerX;
		this.centerY = centerY;
		this.width = width;
		this.height = height;
		this.rotation = rotation;
		this.attValue = attValue;
		RegionManager.addRegion(this.attValue, this);

		// nested terms based on Nathan's GO tree analysis
		if (this.attValue.equals("extracellular region"))
			nestedAttValues = Arrays.asList("extracellular region", "secreted");
		else if (this.attValue.equals("plasma membrane"))
			nestedAttValues = Arrays.asList("plasma membrane", "cell wall");
		else if (this.attValue.equals("cytoplasm"))
			nestedAttValues = Arrays.asList("cytoplasm", "intracellular");
		else if (this.attValue.equals("nucleus"))
			nestedAttValues = Arrays.asList("nucleus", "nucleolus",
					"nuclear membrane");
		else
			nestedAttValues = Arrays.asList(this.attValue);

		// additional parameters
		this.attName = "BasicCellularComponents"; // hard-coded, for now
		this.myView = Cytoscape.getCurrentNetworkView();

		// and node views
		this.nodeViews = populateNodeViews();
		this.nodeCount = this.nodeViews.size() +1;
		this.columns = (int) Math.sqrt(this.nodeCount);
	}

	private List<NodeView> populateNodeViews() {
		CyAttributes attribs = Cytoscape.getNodeAttributes();
		Iterator it = Cytoscape.getCurrentNetwork().nodesIterator();
		List<NodeView> nvList = new ArrayList<NodeView>();
		while (it.hasNext()) {
			Node node = (Node) it.next();
			String val = null;
			String terms[] = new String[1];
			// add support for parsing List type attributes
			if (attribs.getType(attName) == CyAttributes.TYPE_SIMPLE_LIST) {
				List valList = attribs.getListAttribute(node.getIdentifier(),
						attName);
				// iterate through all elements in the list
				if (valList != null && valList.size() > 0) {
					terms = new String[valList.size()];
					for (int i = 0; i < valList.size(); i++) {
						Object o = valList.get(i);
						terms[i] = o.toString();
					}
				}
				val = join(terms);
			} else {
				String valCheck = attribs.getStringAttribute(node
						.getIdentifier(), attName);
				if (valCheck != null && !valCheck.equals("")) {
					val = valCheck;
				}
			}

			// loop through elements in array below and match
			if ((!(val == null) && (!val.equals("null")) && (val.length() > 0))) {
				for (Object o : nestedAttValues) {
					if (val.indexOf(o.toString()) >= 0) {
						nvList.add(Cytoscape.getCurrentNetworkView()
								.getNodeView(node));
					}

				}
			} else if (nestedAttValues.get(0).equals("unassigned")) {
				nvList.add(Cytoscape.getCurrentNetworkView().getNodeView(node));
			}

		}
		return nvList;
	}

	/**
	 * generates comma-separated list as a single string
	 * 
	 * @param values
	 *            array
	 * @return string
	 */
	private static String join(String values[]) {
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < values.length; i++) {
			buf.append(values[i]);
			if (i < values.length - 1) {
				buf.append(", ");
			}
		}
		return buf.toString();
	}
	
	/**
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 
	public void setBounds(double x, double y, double width, double height) {

		// set member vars
		this.x1 = x;
		this.y1 = y;
		this.w1 = width;
		this.h1 = height;

		// our bounds have changed, create a new image with new size
		if ((width > 0) && (height > 0)) {
			// make room for handles
			image = new BufferedImage(((int) width + HANDLE_SIZE),
					((int) height + HANDLE_SIZE), BufferedImage.TYPE_INT_ARGB);
		}

		// update nodeView coordinates of Layout Region for Groups/xGMML export
		Point2D[] corners = new Point2D[] { new Point2D.Double(x, y),
				new Point2D.Double(x + width, y + height) };
		try {
			AffineTransform xfrm = ((DGraphView) getMyView()).getCanvas()
					.getAffineTransform().createInverse();
			Point2D[] newCorners = new Point2D[2];
			xfrm.transform(corners, 0, newCorners, 0, 2);
			nodeX1 = (newCorners[0].getX());
			nodeY1 = (newCorners[0].getY());
			nodeW1 = (newCorners[1].getX() - newCorners[0].getX());
			nodeH1 = (newCorners[1].getY() - newCorners[0].getY());

			if (myGroup != null) {
				CyNode groupNode = this.myGroup.getGroupNode();
				CyAttributes attributes = Cytoscape.getNodeAttributes();
				attributes.setAttribute(groupNode.getIdentifier(),
						BubbleRouterPlugin.REGION_X_ATT, nodeX1);
				attributes.setAttribute(groupNode.getIdentifier(),
						BubbleRouterPlugin.REGION_Y_ATT, nodeY1);
				attributes.setAttribute(groupNode.getIdentifier(),
						BubbleRouterPlugin.REGION_W_ATT, nodeW1);
				attributes.setAttribute(groupNode.getIdentifier(),
						BubbleRouterPlugin.REGION_H_ATT, nodeH1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
*/
	/**
	 * @param g
	 
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

			// set visable edge/rim color
			Color drawColor = new Color(currentColor.getRed(), currentColor
					.getGreen(), currentColor.getBlue());

			// image to draw
			Graphics2D image2D = image.createGraphics();

			// draw into the image
			Composite origComposite = image2D.getComposite();
			image2D
					.setComposite(AlphaComposite
							.getInstance(AlphaComposite.SRC));

			// leave space for handles
			// first clear background in case region has been deselected
			VisualStyle vs = Cytoscape.getVisualMappingManager()
					.getVisualStyle();
			GlobalAppearanceCalculator gCalc = vs
					.getGlobalAppearanceCalculator();
			Color backgroundColor = gCalc.getDefaultBackgroundColor();
			image2D.setPaint(backgroundColor);
			image2D.fillRect(0, 0, image.getWidth(), image.getHeight());
			image2D.setPaint(fillColor);
			image2D.fillRect(HANDLE_SIZE / 2, HANDLE_SIZE / 2, image
					.getWidth(null)
					- HANDLE_SIZE, image.getHeight(null) - HANDLE_SIZE);
			image2D.setColor(new Color(0, 0, 0, 255));
			if (regionAttributeValues != null) {
				image2D.drawString(regionAttributeValues.toString(), 20, 20);
			}

			image2D.setColor(drawColor);

			// leave space for handles
			// adds thickness to border
			image2D.drawRect(1 + (HANDLE_SIZE / 2), 1 + (HANDLE_SIZE / 2),
					image.getWidth(null) - 3 - HANDLE_SIZE, image
							.getHeight(null)
							- 3 - HANDLE_SIZE);
			// give border dimensionality
			image2D.draw3DRect(HANDLE_SIZE / 2, HANDLE_SIZE / 2, image
					.getWidth(null)
					- 1 - HANDLE_SIZE, image.getHeight(null) - 1 - HANDLE_SIZE,
					true);

			// draw handles for stretching if selected
			if (this.isSelected()) {
				drawHandles(image2D);
			}

			image2D.setComposite(origComposite);
			((Graphics2D) g).drawImage(image, null, 0, 0);

		}
	}
*/
	/**
	 * @return the nodeViews
	 */
	public List<NodeView> getNodeViews() {
		return nodeViews;
	}

	/**
	 * @param nodeViews
	 *            the nodeViews to set
	 */
	public void setNodeViews(List<NodeView> nodeViews) {
		this.nodeViews = nodeViews;
	}

	/**
	 * @return the visibleBorder
	 */
	public boolean isVisibleBorder() {
		return visibleBorder;
	}

	/**
	 * @param visibleBorder
	 *            the visibleBorder to set
	 */
	public void setVisibleBorder(boolean visibleBorder) {
		this.visibleBorder = visibleBorder;
	}

	/**
     * Note: for a Line, width == length, irrespective of orientation
	 * 
	 * @return the width
	 */
	public double getWidth() {
		return width;
	}

	/**
	 * @return the height
	 */
	public double getHeight() {
		return height;
	}

	/**
	 * @param centerX
	 *            the centerX to set
	 */
	public void setCenterX(double centerX) {
		this.centerX = centerX;
	}

	/**
	 * @param centerY
	 *            the centerY to set
	 */
	public void setCenterY(double centerY) {
		this.centerY = centerY;
	}

	/**
	 * Note: for a Line, width == length, irrespective of orientation
	 * 
	 * @param width
	 *            the width to set
	 */
	public void setWidth(double width) {
		this.width = width;
	}

	/**
	 * @param height
	 *            the height to set
	 */
	public void setHeight(double height) {
		this.height = height;
	}

	/**
	 * @return the attValue
	 */
	public String getAttValue() {
		return attValue;
	}

	/**
	 * @return the centerX
	 */
	public double getCenterX() {
		return centerX;
	}

	/**
	 * @return the centerY
	 */
	public double getCenterY() {
		return centerY;
	}

	/**
	 * @return the nodeCount
	 */
	public int getNodeCount() {
		return nodeCount;
	}

	/**
	 * @return the columns
	 */
	public int getColumns() {
		return columns;
	}

	/**
	 * @return the shape
	 */
	public String getShape() {
		return shape;
	}

}