package org.genmapp.golayout;

import giny.model.Node;
import giny.view.NodeView;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javax.swing.JComponent;

import org.pathvisio.view.ShapeRegistry;

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
	private List<NodeView> filteredNodeViews = new ArrayList<NodeView>();
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
		if (width < 1) // handle extreme case
			this.width = 1;
		if (height < 1) // handle extreme case
			this.height = 1;
		this.area = (int) (width * height);
		this.zorder = zorder;
		this.rotation = rotation;
		this.attValue = attValue;
		RegionManager.addRegion(this.attValue, this);

		// synonym terms based on Nathan's GO tree analysis
		if (this.attValue.equals("extracellular region"))
			nestedAttValues = Arrays.asList("extracellular region",
					"extracellular space", "secreted");
		else if (this.attValue.equals("mitochondrion"))
			nestedAttValues = Arrays.asList("mitochondrion",
					"mitochondrion lumen");
		else if (this.attValue.equals("endoplasmic reticulum"))
			nestedAttValues = Arrays.asList("endoplasmic reticulum",
					"Golgi apparatus");
		else if (this.attValue.equals("plasma membrane"))
			nestedAttValues = Arrays.asList("plasma membrane", "cell wall");
		else if (this.attValue.equals("cytoplasm"))
			nestedAttValues = Arrays.asList("cytoplasm", "intracellular",
					"cytosol", "vacuole", "lysosome", "peroxisome");
		else if (this.attValue.equals("nucleus"))
			nestedAttValues = Arrays.asList("nucleus", "nucleolus",
					"nuclear membrane", "nucleoplasm");
		else if (this.attValue.equals("unassigned"))
			nestedAttValues = Arrays.asList("unassigned", "cellular_component");
		else
			nestedAttValues = Arrays.asList(this.attValue);

		// additional parameters
		this.attName = "annotation.GO CELLULAR_COMPONENT"; // hard-coded, for
															// now

		this.nodeViews = populateNodeViews();
		this.nodeCount = this.nodeViews.size();
		this.columns = (int) Math.sqrt(this.nodeCount);
		this.freeCenterX = centerX;
		this.freeCenterY = centerY;
		this.freeWidth = width;
		this.freeHeight = height;

		// define free area inside of ovals
		if (this.shape == "Oval") {
			Double x = 0.0d;
			Double y = 0.0d;
			Double a = 0.0d;
			Double b = 0.0d;

			if (width > height) {
				a = width / 2;
				b = height / 2;
			} else {
				a = height / 2;
				b = width / 2;
			}

			// TODO: adapt equations to handle rotation (phi)
			// x=h+a(cos t)(cos phi) - b(sin t)(sin phi)
			// y=k+b(sin t)(cos phi) +a(cos t)(sin phi)
			x = centerX + a * Math.cos(Math.PI / 4);
			y = centerY + b * Math.cos(Math.PI / 4);

			this.freeWidth = Math.abs(x - centerX) * 2;
			this.freeHeight = Math.abs(y - centerY) * 2;
		}

		// adjust area for line shapes
		if (this.shape == "Line") {
			this.area = 0; // treat as 1D object
			// this will force lines shapes to top of drawing order
		}

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
						break; // stop searching after first hit
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
				(getRegionRight() - getRegionLeft()),
				(getRegionBottom() - getRegionTop()));
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

	public Rectangle2D.Double getFreeVRectangle() {
		return new Rectangle2D.Double(getFreeLeft(), getFreeTop(),
				(getFreeRight() - getFreeLeft()),
				(getFreeBottom() - getFreeTop()));
	}

	// public java.awt.Shape getFreeVOutline() {
	// Rectangle2D.Double r = getFreeVRectangle();
	// r.width = r.width + 2;
	// r.height = r.height + 2;
	// AffineTransform f = new AffineTransform();
	// f.rotate(this.rotation, getFreeCenterX(), getFreeCenterY());
	// java.awt.Shape outline = f.createTransformedShape(r);
	// return outline;
	// }

	public void doPaint(Graphics2D g2d) {

		Rectangle b = relativeToBounds(viewportTransform(getVRectangle()))
				.getBounds();

		Color fillcolor = Color.blue;
		Color fillColor = new Color(fillcolor.getRed(), fillcolor.getGreen(),
				fillcolor.getBlue(), TRANSLUCENCY_LEVEL);
		Color linecolor = this.color;

		int sw = 1;
		int x = b.x;
		int y = b.y;
		int w = b.width - sw - 1;
		int h = b.height - sw - 1;
		int cx = x + w / 2;
		int cy = y + h / 2;

		// TODO
		// s = new Rectangle(x, y, w, h);
		if (this.shape == "Line") {
			Point2D src = new Point2D.Double(this.centerX, this.centerY);
			Point2D trgt = new Point2D.Double(this.width, this.height);
			Point2D srcT = new Point2D.Double();
			Point2D trgtT = new Point2D.Double();

			AffineTransform t = new AffineTransform();
			t.transform(src, srcT);
			t.transform(trgt, trgtT);

			g2d.drawLine((int) srcT.getX(), (int) srcT.getY(), (int) trgtT
					.getX(), (int) trgtT.getY());

		} else { // Rectangle, Oval
			java.awt.Shape s = null;

			s = ShapeRegistry.getShape(this.shape, x, y, w, h);

			AffineTransform t = new AffineTransform();
			t.rotate(this.rotation, cx, cy);
			s = t.createTransformedShape(s);

			// TODO
			// g2d.setColor(fillcolor);
			// g2d.fill(s);

			g2d.setColor(linecolor);
			g2d.setStroke(new BasicStroke());
			g2d.draw(s);

			// region label
//			int xLabelOffset = 5;
//			int yLabelOffset = 15;
			// //TODO: debugging free region
//			Rectangle fb = relativeToBounds(
//					viewportTransform(getFreeVRectangle())).getBounds();
//			int fsw = 1;
//			int fx = fb.x;
//			int fy = fb.y;
//			int fw = fb.width - fsw - 1;
//			int fh = fb.height - fsw - 1;
//			int fcx = fx + fw / 2;
//			int fcy = fy + fh / 2;
//
//			java.awt.Shape fs = null;
//
//			fs = ShapeRegistry.getShape(this.shape, fx, fy, fw, fh);
//
//			AffineTransform ft = new AffineTransform();
//			ft.rotate(this.rotation, fcx, fcy);
//			fs = ft.createTransformedShape(fs);
//
//			g2d.setColor(Color.gray);
//			Font font = new Font("Arial", Font.PLAIN, 10);
//			g2d.setFont(font);
//
//			g2d.draw(fs);

//			g2d.setColor(Color.DARK_GRAY);
//			g2d.setStroke(new BasicStroke());
//			g2d.drawString(this.attValue, xLabelOffset, yLabelOffset);
		}

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
					&& currentY > r.getRegionTop()
					&& currentY < r.getRegionBottom()) {
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
	
	public void removeFilteredNodeView(NodeView nv){
		this.filteredNodeViews.remove(nv);
	}
	public void addFilteredNodeView(NodeView nv){
		this.filteredNodeViews.add(nv);
	}
	public List<NodeView> getFilteredNodeViews() {
		return filteredNodeViews;
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
		if (this.shape == "Line") {
			return (Math.min(this.centerX, this.width));
		} else { // Rectangle, Oval
			return (this.centerX - this.width / 2);
		}
	}

	/**
	 * @return
	 */
	public double getRegionTop() {
		if (this.shape == "Line") {
			return (Math.min(this.centerY, this.height));
		} else { // Rectangle, Oval
			return (this.centerY - this.height / 2);
		}
	}

	/**
	 * @return
	 */
	public double getRegionRight() {
		if (this.shape == "Line") {
			return (Math.max(this.centerX, this.width));
		} else { // Rectangle, Oval
			return (this.centerX + this.width / 2);
		}
	}

	/**
	 * @return
	 */
	public double getRegionBottom() {
		if (this.shape == "Line") {
			return (Math.max(this.centerY, this.height));
		} else { // Rectangle, Oval
			return (this.centerY + this.height / 2);
		}
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

	public double getFreeLeft() {
		return (freeCenterX - freeWidth / 2);
	}

	public double getFreeRight() {
		return (freeCenterX + freeWidth / 2);
	}

	public double getFreeTop() {
		return (freeCenterY - freeHeight / 2);
	}

	public double getFreeBottom() {
		return (freeCenterY + freeHeight / 2);
	}

	public double getLineLength() {
		return (Math.sqrt(Math.pow((this.width - this.centerX), 2)
				+ Math.pow((this.height - this.centerY), 2)));
	}

	public double getFreeLength() {
		return (Math.sqrt(Math.pow((freeWidth - freeCenterX), 2)
				+ Math.pow((freeHeight - freeCenterY), 2)));
	}

	public void setLineLength(Double l) {
		Double oldLength = getLineLength();
		Double halfDiff = (l - oldLength) / 2;
		Double ratio = halfDiff / oldLength;
		Double x = (this.width - this.centerX) * ratio;
		Double y = (this.height - this.centerY) * ratio;
		if (this.centerX < this.width) {
			setCenterX((this.centerX - x));
			setRegionWidth((this.width + x));
		} else {
			setCenterX((this.centerX + x));
			setRegionWidth((this.width - x));
		}
		if (this.centerY < this.height) {
			setCenterY((this.centerY - y));
			setRegionHeight((this.height + y));
		} else {
			setCenterY((this.centerY + y));
			setRegionHeight((this.height - y));
		}
	}

	public void setFreeLength(Double l) {
		Double oldLength = getFreeLength();
		Double halfDiff = (l - oldLength) / 2;
		Double ratio = halfDiff / oldLength;
		Double x = (this.freeWidth - this.freeCenterX) * ratio;
		Double y = (this.freeHeight - this.freeCenterY) * ratio;
		if (this.freeCenterX < this.freeWidth) {
			setFreeCenterX((this.freeCenterX - x));
			setFreeWidth((this.freeWidth + x));
		} else {
			setFreeCenterX((this.freeCenterX + x));
			setFreeWidth((this.freeWidth - x));
		}
		if (this.freeCenterY < this.freeHeight) {
			setFreeCenterY((this.freeCenterY - y));
			setFreeHeight((this.freeHeight + y));
		} else {
			setFreeCenterY((this.freeCenterY + y));
			setFreeHeight((this.freeHeight - y));
		}
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
	 * @param overlappingRegions
	 *            the overlappingRegions to set
	 */
	public void setOverlappingRegions(Region r) {
		this.overlappingRegions.add(r);
	}

}