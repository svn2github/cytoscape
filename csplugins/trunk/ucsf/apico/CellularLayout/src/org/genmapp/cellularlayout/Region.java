package org.genmapp.cellularlayout;

import giny.model.Node;
import giny.view.NodeView;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javax.swing.JComponent;

import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;
import ding.view.DGraphView;
import ding.view.InnerCanvas;
import ding.view.ViewportChangeListener;

public class Region extends JComponent implements ViewportChangeListener {

	// shape and parameters from template
	private String shape; // Line, Arc, Oval, Rectangle
	private Color color;
	private double centerX;
	private double centerY;
	private double width;
	private double height;
	private int zorder;
	private double rotation;
	private String attValue;

	// additional parameters not from template
	private String attName;
	private List<String> nestedAttValues; // values represented by attValue
	private List<NodeView> nodeViews;
	private int nodeCount;
	private int columns;
	private int area;
	private boolean visibleBorder;

	// dimensions of free, non-overlapping space available for nodes
	private double freeCenterX;
	private double freeCenterY;
	private double freeWidth;
	private double freeHeight;
	private List<Region> regionsOverlapped = new ArrayList<Region>();
	private List<Region> overlappingRegions = new ArrayList<Region>();

	// graphics
	protected DGraphView dview = (DGraphView) Cytoscape.getCurrentNetworkView();
	private static final int TRANSLUCENCY_LEVEL = (int) (255 * .10);

	public Region(String shape, String color, double centerX, double centerY,
			double width, double height, int zorder, double rotation,
			String attValue) {
		super();

		this.shape = shape;
		this.color = Color.decode(color); // decode hexadecimal string
		this.centerX = centerX;
		this.centerY = centerY;
		this.width = width;
		this.height = height;
		this.area = (int) (width * height);
		this.zorder = zorder;
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

		this.nodeViews = populateNodeViews();
		this.nodeCount = this.nodeViews.size();
		this.columns = (int) Math.sqrt(this.nodeCount);
		this.freeCenterX = centerX;
		this.freeCenterY = centerY;
		this.freeWidth = width;
		this.freeHeight = height;

		// graphics
		setBounds(getVOutline().getBounds());
		dview.addViewportChangeListener(this);

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

	// graphics
	public void setBounds(double x, double y, double width, double height) {
		setBounds((int) x, (int) y, (int) width, (int) height);
	}

	public final void paint(Graphics g) {
		Graphics2D g2d = (Graphics2D) g.create();
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
				RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		doPaint(g2d);
	}

	protected java.awt.Shape relativeToBounds(java.awt.Shape s) {
		Rectangle r = getBounds();
		AffineTransform f = new AffineTransform();
		f.translate(-r.x, -r.y);
		return f.createTransformedShape(s);
	}

	protected java.awt.Shape viewportTransform(java.awt.Shape s) {
		InnerCanvas canvas = dview.getCanvas();

		AffineTransform f = canvas.getAffineTransform();
		if (f != null)
			return f.createTransformedShape(s);
		else
			return s;
	}

	public void viewportChanged(int w, int h, double newXCenter,
			double newYCenter, double newScaleFactor) {
		InnerCanvas canvas = dview.getCanvas();

		AffineTransform f = canvas.getAffineTransform();

		if (f == null)
			return;

		java.awt.Shape outline = getVOutline();

		Rectangle b = outline.getBounds();
		Point2D pstart = f.transform(new Point2D.Double(b.x, b.y), null);
		setBounds(pstart.getX(), pstart.getY(), b.width * newScaleFactor,
				b.height * newScaleFactor);
	}

	public Rectangle2D.Double getVRectangle() {
		return new Rectangle2D.Double(getRegionLeft(), getRegionTop(),
				getRegionWidth(), getRegionHeight());
	}

	public java.awt.Shape getVOutline() {
		Rectangle2D.Double r = getVRectangle();
		r.width = r.width + 2;
		r.height = r.height + 2;
		AffineTransform f = new AffineTransform();
		f.rotate(this.rotation, getCenterX(), getCenterY());
		java.awt.Shape outline = f.createTransformedShape(r);
		return outline;
	}

	public void doPaint(Graphics2D g2d) {

		Rectangle b = relativeToBounds(viewportTransform(getVRectangle()))
				.getBounds();

		Color fillcolor = Color.blue;
		Color fillColor = new Color(fillcolor.getRed(), fillcolor.getGreen(),
				fillcolor.getBlue(), TRANSLUCENCY_LEVEL);
		Color linecolor = Color.black;
		if (!this.visibleBorder) {
			linecolor = Color.blue;
		}

		int sw = 1;
		int x = b.x;
		int y = b.y;
		int w = b.width - sw - 1;
		int h = b.height - sw - 1;
		int cx = x + w / 2;
		int cy = y + h / 2;

		java.awt.Shape s = null;

		// TODO
		s = new Rectangle(x, y, w, h);

		AffineTransform t = new AffineTransform();
		t.rotate(this.rotation, cx, cy);
		s = t.createTransformedShape(s);

		// TODO
		// g2d.setColor(fillcolor);
		// g2d.fill(s);

		g2d.setColor(linecolor);
		g2d.setStroke(new BasicStroke());
		g2d.draw(s);
	}

	/**
	 * identifies set of node views that overlap with provided region.
	 * 
	 * @param nodeViews
	 * @param from
	 * @return NodeViews within boundary of current region
	 */
	public static List bounded(List<NodeView> nodeViews, Region r) {
		List<NodeView> boundedNodeViews = new ArrayList<NodeView>();
		double currentX;
		double currentY;
		// first calculate the min/max x and y for the list of *relevant*
		// nodeviews
		Iterator<NodeView> it = nodeViews.iterator();
		while (it.hasNext()) {
			NodeView nv = it.next();
			currentX = nv.getXPosition();
			currentY = nv.getYPosition();
			if (currentX > r.getRegionLeft() && currentX < r.getRegionRight()
							&& currentY > r.getRegionTop() && currentY < r.getRegionBottom()) {
				boundedNodeViews.add(nv);
			}
		}

		return boundedNodeViews;
	}

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
	 * @return the width
	 */
	public double getRegionWidth() {
		return width;
	}

	/**
	 * @return the height
	 */
	public double getRegionHeight() {
		return height;
	}

	/**
	 * Note: for a Line, width == length, irrespective of orientation
	 * 
	 * @param width
	 *            the width to set
	 */
	public void setRegionWidth(double width) {
		this.width = width;
	}

	/**
	 * @param height
	 *            the height to set
	 */
	public void setRegionHeight(double height) {
		this.height = height;
	}

	/**
	 * @return
	 */
	public double getRegionLeft() {
		return (this.centerX - this.width / 2);
	}

	/**
	 * @return
	 */
	public double getRegionTop() {
		return (this.centerY - this.height / 2);
	}

	/**
	 * @return
	 */
	public double getRegionRight() {
		return (this.centerX + this.width / 2);
	}

	/**
	 * @return
	 */
	public double getRegionBottom() {
		return (this.centerY + this.height / 2);
	}

	/**
	 * @return the area
	 */
	public int getArea() {
		return area;
	}

	/**
	 * @param area
	 *            the area to set
	 */
	public void setArea(int area) {
		this.area = area;
	}

	/**
	 * @return the zorder
	 */
	public int getZorder() {
		return zorder;
	}

	/**
	 * @return the attValue
	 */
	public String getAttValue() {
		return attValue;
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

	/**
	 * @return the freeCenterX
	 */
	public double getFreeCenterX() {
		return freeCenterX;
	}

	/**
	 * @param freeCenterX
	 *            the freeCenterX to set
	 */
	public void setFreeCenterX(double freeCenterX) {
		this.freeCenterX = freeCenterX;
	}

	/**
	 * @return the freeCenterY
	 */
	public double getFreeCenterY() {
		return freeCenterY;
	}

	/**
	 * @param freeCenterY
	 *            the freeCenterY to set
	 */
	public void setFreeCenterY(double freeCenterY) {
		this.freeCenterY = freeCenterY;
	}

	/**
	 * @return the freeWidth
	 */
	public double getFreeWidth() {
		return freeWidth;
	}

	/**
	 * @param freeWidth
	 *            the freeWidth to set
	 */
	public void setFreeWidth(double freeWidth) {
		this.freeWidth = freeWidth;
	}

	/**
	 * @return the freeHeight
	 */
	public double getFreeHeight() {
		return freeHeight;
	}

	/**
	 * @param freeHeight
	 *            the freeHeight to set
	 */
	public void setFreeHeight(double freeHeight) {
		this.freeHeight = freeHeight;
	}

	/**
	 * returns list of regions that are overlapped by a given region
	 * 
	 * @return the regionsOverlapped
	 */
	public List<Region> getRegionsOverlapped() {
		return regionsOverlapped;
	}

	/**
	 * add to list of regions that are overlapped by a given region
	 * 
	 * @param regionsOverlapped
	 *            the regionsOverlapped to set
	 */
	public void setRegionsOverlapped(Region r) {
		this.regionsOverlapped.add(r);
	}

	/**
	 * @return the overlappingRegions
	 */
	public List<Region> getOverlappingRegions() {
		return overlappingRegions;
	}

	/**
	 * @param overlappingRegions the overlappingRegions to set
	 */
	public void setOverlappingRegions(Region r) {
		this.overlappingRegions.add(r);
	}

}