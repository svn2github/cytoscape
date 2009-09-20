package org.genmapp.golayout;

import giny.model.Node;
import giny.view.NodeView;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Stroke;
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
	private String shape;
	// restricted syntax on "Rectangle" and "Oval" for ShapeRegistry.getShape()
	public static final String COMPARTMENT_RECT = "Rectangle";
	public static final String COMPARTMENT_OVAL = "Oval";
	public static final String MEMBRANE_LINE = "Line";
	public static final String UKNOWN = "Vertical Divider";
	private Color color;
	private Color fillcolor;
	private double centerX;
	private double centerY;
	private double width;
	private double height;
	private int zorder;
	private double rotation;
	private String attValue;

	// additional parameters not from template
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
	private static final int OPACITY_LEVEL = (int) (255 * 1);
	
	public Region(String shape, String fillcolor, String color, double centerX,
			double centerY, double width, double height, int zorder,
			double rotation, String attValue) {
		super();

		this.shape = shape;
		if (color.matches("#[0-9a-zA-Z]{6}")) { // hexadecimal string
			this.color = Color.decode(color);
		} else {
			this.color = null; // e.g., "Transparent"
		}
		if (fillcolor.matches("#[0-9a-zA-Z]{6}")) { // hexadecimal string
			this.fillcolor = Color.decode(fillcolor);
		} else {
			this.fillcolor = null; // e.g., "Transparent"
		}
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

		//addition parameters
		this.nodeViews = CellAlgorithm.populateNodeViews(this);
		this.nodeCount = this.nodeViews.size();
		this.columns = (int) Math.sqrt(this.nodeCount);
		this.freeCenterX = centerX;
		this.freeCenterY = centerY;
		/*
		 * subtract proportional width and height for buffer around borders and
		 * region label
		 */
		this.freeWidth = width - 10
				* MFNodeAppearanceCalculator.FEATURE_NODE_WIDTH;
		this.freeHeight = height - 20
				* MFNodeAppearanceCalculator.FEATURE_NODE_HEIGHT;
		
		if (this.shape == MEMBRANE_LINE){
			// further shrink width to leave room for in-line label
			this.freeCenterX += 300;
			this.freeWidth -= 600;
		}

		// define free area inside of ovals
		if (this.shape == COMPARTMENT_OVAL) {
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

		// graphics
		setBounds(getVOutline().getBounds());
		dview.addViewportChangeListener(this);

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
		//hack! increase bounds to accommodate layered oval and rect drawing
		setBounds(pstart.getX() - 1, pstart.getY() - 1, (b.width + 2) * newScaleFactor,
				(b.height + 2) * newScaleFactor);
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

	/**
	 * @param g2d
	 */
	public void doPaint(Graphics2D g2d) {

		InnerCanvas canvas = dview.getCanvas();
		AffineTransform f = canvas.getAffineTransform();
		double affineScale = f.getScaleX();
		if (affineScale == 1.0) {
			return; // hack! to avoid region label bug
		}
		double scaledFontD = affineScale * 30;
		int scaledFont = 1;
		// protect again inverting matrix with zero value
		if (scaledFontD > 0.5) {
			scaledFont = (int) Math.round(scaledFontD);
		}

		Rectangle b = relativeToBounds(viewportTransform(getVRectangle()))
				.getBounds();

		int x = b.x;
		int y = b.y;
		int w = b.width - 2;
		int h = b.height - 2;
//		int cx = x + w / 2;
//		int cy = y + h / 2;
		int arcWidth = 25;
		int arcHeight = 25;

		if (this.shape == UKNOWN) { // draw vertical divider
			Point2D src = new Point2D.Double(x, y);
			Point2D trgt = new Point2D.Double(x, y + h);

			double scaledWidthD = affineScale * 6;
			float scaledWidth = (float) scaledWidthD;

			g2d.setColor(this.color);
			float dash[] = { 10.0f };
			g2d.setStroke(new BasicStroke(scaledWidth, BasicStroke.CAP_BUTT,
					BasicStroke.JOIN_MITER, 10.0f, dash, 0.0f));
			g2d.drawLine((int) src.getX(), (int) src.getY(), (int) trgt.getX(),
					(int) trgt.getY());

			double scaledOffsetD = affineScale * 30;
			int scaledOffset = 1;
			if (scaledOffsetD > 0.5) {
				scaledOffset = (int) Math.round(scaledOffsetD);
			}

			g2d.setColor(Color.black);
			Font font = new Font("Serif", Font.BOLD, scaledFont);
			g2d.setFont(font);
			g2d.drawString(this.attValue, scaledOffset, scaledOffset);
		} else if (this.shape == MEMBRANE_LINE) {
//			Point2D src = new Point2D.Double(x, y);
//			Point2D trgt = new Point2D.Double(x + w, y);
//
//			Point2D src2 = new Point2D.Double(x, y + h);
//			Point2D trgt2 = new Point2D.Double(x + w, y + h);

//			double scaledHeightD = affineScale * 6;
//			float scaledHeight = (float) scaledHeightD;
			
			g2d.setColor(this.fillcolor);
			g2d.fillRoundRect(x, y, w , h, h, h);
//			g2d.fillRoundRect(x, y + h, w , h, h, h);
			// outline rect
			g2d.setColor(this.color);
			g2d.setStroke(new BasicStroke(0.3f));
			g2d.drawRoundRect(x, y, w , h, h, h);
//			g2d.drawRoundRect(x, y + h, w , h, h, h);

//			double scaledWidthD = affineScale * 10;
//			float scaledWidth = (float) scaledWidthD;
//
//			g2d.setStroke(new BasicStroke(scaledWidth, BasicStroke.CAP_BUTT,
//					BasicStroke.JOIN_MITER, 10.0f, null, 0.0f));
//			g2d.drawLine((int) src.getX(), (int) src.getY(), (int) trgt.getX(),
//					(int) trgt.getY());
//			g2d.drawLine((int) src2.getX(), (int) src2.getY(), (int) trgt2
//					.getX(), (int) trgt2.getY());

			double scaledOffsetXD = affineScale * 30;
			int scaledOffsetX = 1;
			if (scaledOffsetXD > 0.5) {
				scaledOffsetX = (int) Math.round(scaledOffsetXD);
			}
			double scaledOffsetYD = affineScale * 20;
			int scaledOffsetY = 1;
			if (scaledOffsetYD > 0.5) {
				scaledOffsetY = (int) Math.round(scaledOffsetYD);
			}

			g2d.setColor(Color.black);
			Font font = new Font("Serif", Font.BOLD, scaledFont);
			g2d.setFont(font);
			g2d.drawString(this.attValue, scaledOffsetX, scaledOffsetY);

		} else if (this.shape == COMPARTMENT_RECT) {
//			java.awt.Shape s = null;
//
//			// Note restricted syntax for shape (e.g., "Rectangle"
//			s = ShapeRegistry.getShape(this.shape, x, y, w, h);
//
//			AffineTransform t = new AffineTransform();
//			t.rotate(this.rotation, cx, cy);
//			s = t.createTransformedShape(s);

			if (this.getRegionsOverlapped().size() > 0) {
				// background "shadow" rect
				g2d.setColor(this.color);
				g2d.fillRoundRect(x, y, w, h, arcWidth, arcHeight);
				// foreground rect
				g2d.setColor(this.fillcolor);
				g2d.fillRoundRect(x - 1, y - 1, w - 1, h - 1, arcWidth,
						arcHeight);
				// outline rect
				g2d.setColor(this.color);
				g2d.setStroke(new BasicStroke(0.3f));
				g2d.drawRoundRect(x - 1, y - 1 , w - 1, h - 1, arcWidth, arcHeight);

			}
			
			double scaledOffsetD = affineScale * 30;
			int scaledOffset = 1;
			if (scaledOffsetD > 0.5) {
				scaledOffset = (int) Math.round(scaledOffsetD);
			}

			g2d.setColor(Color.black);
			Font font = new Font("Serif", Font.BOLD, scaledFont);
			g2d.setFont(font);
			g2d.drawString(this.attValue, scaledOffset, scaledOffset);

		} else if (this.shape == Region.COMPARTMENT_OVAL) {
			// background "shadow" oval
			g2d.setColor(this.color);
			g2d.fillOval(x, y, w, h);
			// foreground oval
			g2d.setColor(this.fillcolor);
			g2d.fillOval(x - 1, y - 1, w - 1, h - 1);
			// outline oval
			g2d.setColor(this.color);
			g2d.setStroke(new BasicStroke(0.3f));
			g2d.drawOval(x - 1, y - 1 , w - 1, h - 1);
			
			// Note: the "8" is a function of font size
			double scaledOffsetXD = affineScale
					* (this.width / 2 - 8 * this.attValue.length());
			int scaledOffsetX = 1;
			if (scaledOffsetXD > 0.5) {
				scaledOffsetX = (int) Math.round(scaledOffsetXD);
			}
			double scaledOffsetYD = affineScale * 40;
			int scaledOffsetY = 1;
			if (scaledOffsetYD > 0.5) {
				scaledOffsetY = (int) Math.round(scaledOffsetYD);
			}

			g2d.setColor(Color.black);
			Font font = new Font("Serif", Font.BOLD, scaledFont);
			g2d.setFont(font);
			g2d.drawString(this.attValue, scaledOffsetX, scaledOffsetY);
		} else {

			// do nothing
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
		double bufferX = MFNodeAppearanceCalculator.FEATURE_NODE_WIDTH;
		double bufferY = MFNodeAppearanceCalculator.FEATURE_NODE_HEIGHT;

		/*
		 * first calculate the min/max x and y for the list ofrelevant nodeviews
		 */
		Iterator<NodeView> it = nodeViews.iterator();
		while (it.hasNext()) {
			NodeView nv = it.next();
			currentX = nv.getXPosition();
			currentY = nv.getYPosition();
			if (currentX > (r.getRegionLeft() - bufferX)
					&& currentX < (r.getRegionRight() + bufferX)
					&& currentY > (r.getRegionTop() - bufferY)
					&& currentY < (r.getRegionBottom() + bufferY)) {
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

	public void removeFilteredNodeView(NodeView nv) {
		this.filteredNodeViews.remove(nv);
	}

	public void addFilteredNodeView(NodeView nv) {
		this.filteredNodeViews.add(nv);
	}

	public List<NodeView> getFilteredNodeViews() {
		return filteredNodeViews;
	}

	/**
	 * @return the nestedAttValues
	 */
	public List<String> getNestedAttValues() {
		return nestedAttValues;
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

	public double getFreeLength() {
		return (Math.sqrt(Math.pow((freeWidth - freeCenterX), 2)
				+ Math.pow((freeHeight - freeCenterY), 2)));
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