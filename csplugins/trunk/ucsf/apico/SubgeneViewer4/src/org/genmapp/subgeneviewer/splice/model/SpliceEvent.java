package org.genmapp.subgeneviewer.splice.model;

import giny.view.NodeView;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.List;

import javax.swing.JComponent;

import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.groups.CyGroup;
import cytoscape.view.CyNetworkView;
import ding.view.DGraphView;
import ding.view.DingCanvas;
import ding.view.ViewportChangeListener;

@SuppressWarnings("serial")
public class SpliceEvent extends JComponent implements ViewportChangeListener {

	private double x1 = Double.NaN;

	private double y1 = Double.NaN;

	private double w1 = Double.NaN;

	private double h1 = Double.NaN;

	private double nodeX1;

	private double nodeY1;

	private double nodeW1;

	private double nodeH1;

	public static int spliceHeight = 50;

	/**
	 * ref to our buffered region
	 */
	private BufferedImage image;

	/**
	 * list of nodes associated with a layout region based on
	 * regionAttributeValue
	 */
	private List<NodeView> nodeViews;

	private CyNetworkView myView;

	private CyGroup myGroup = null;

	/**
	 * For accommodating pan and zoom of InnerCanvas
	 */
	private double currentZoom = Double.NaN;

	private double currentCenterX = Double.NaN, currentCenterY = Double.NaN;

	private boolean viewportSet = false;

	private int viewportWidth;

	private int viewportHeight;

	private String _splice_from;

	private String _splice_to;

	private int _units; // width in units of feature nodes

	private String _annotation;

	/**
	 * Constructs an object to graphically represent the exon, intron or
	 * untranslated region associated with each feature node.
	 * 
	 * @param name
	 * @param view
	 * @param id
	 * @param type
	 * @param units
	 * @param constitutive
	 * @param start
	 * @param annotation
	 */
	public SpliceEvent(String from, String to, CyNetworkView view) {
		super();
		_splice_from = from;
		_splice_to = to;
		myView = view;

		SpliceRegion region_from = getRegionById(_splice_from);
		SpliceRegion region_to = getRegionById(_splice_to);

		double from_x = region_from.getBounds().getMaxX();
		double from_y = region_from.getBounds().getMinY();
		double to_x = region_to.getBounds().getMinX();

		double h = (double) spliceHeight;
		double w = to_x - from_x + 2;
		double x = from_x - 1;
		double y = from_y - h + 0.25;

		setBounds(x, y, w, h, true);

		((DGraphView) myView).addViewportChangeListener(this);
	}

	/**
	 * Convoluted code to get Region by id for processing Splice Events
	 * 
	 * @param id
	 * @return
	 */
	public SpliceRegion getRegionById(String id) {
		SpliceRegion match = null;
		for (SpliceRegion region : SpliceRegion.regionList) {
			if (region.getId().equalsIgnoreCase(id)) {
				match = region;
			}
		}
		return match;
	}

	/**
	 * @return Returns the h1.
	 */
	public double getH1() {
		if (Double.isNaN(h1)) {
			// if imported node coords from xGMML/Groups, then transform
			Point2D[] corners = new Point2D[] {
					new Point2D.Double(nodeX1, nodeY1),
					new Point2D.Double(nodeX1 + nodeW1, nodeY1 + nodeH1) };
			AffineTransform xfrm = ((DGraphView) myView).getCanvas()
					.getAffineTransform();
			Point2D[] newCorners = new Point2D[2];
			xfrm.transform(corners, 0, newCorners, 0, 2);
			h1 = (newCorners[1].getY() - newCorners[0].getY());

		}
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
		if (Double.isNaN(w1)) {
			// if imported node coords from xGMML/Groups, then transform
			Point2D[] corners = new Point2D[] {
					new Point2D.Double(nodeX1, nodeY1),
					new Point2D.Double(nodeX1 + nodeW1, nodeY1 + nodeH1) };
			AffineTransform xfrm = ((DGraphView) myView).getCanvas()
					.getAffineTransform();
			Point2D[] newCorners = new Point2D[2];
			xfrm.transform(corners, 0, newCorners, 0, 2);
			w1 = (newCorners[1].getX() - newCorners[0].getX());

		}
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
		if (Double.isNaN(x1)) {
			// if imported node coords from xGMML/Groups, then transform
			Point2D[] topLeft = new Point2D[] { new Point2D.Double(nodeX1,
					nodeY1) };
			AffineTransform xfrm = ((DGraphView) myView).getCanvas()
					.getAffineTransform();
			Point2D[] newTopLeft = new Point2D[1];
			xfrm.transform(topLeft, 0, newTopLeft, 0, 1);
			x1 = (newTopLeft[0].getX());

		}
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
		if (Double.isNaN(y1)) {
			// if imported node coords from xGMML/Groups, then transform
			Point2D[] topLeft = new Point2D[] { new Point2D.Double(nodeX1,
					nodeY1) };
			AffineTransform xfrm = ((DGraphView) myView).getCanvas()
					.getAffineTransform();
			Point2D[] newTopLeft = new Point2D[1];
			xfrm.transform(topLeft, 0, newTopLeft, 0, 1);
			y1 = (newTopLeft[0].getY());

		}

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
	 * @return Returns the nodeViews.
	 */
	public List<NodeView> getNodeViews() {
		return nodeViews;
	}

	/**
	 * @param list
	 *            The nodeViews to set.
	 */
	public void setNodeViews(List<NodeView> list) {
		this.nodeViews = list;
	}

	/**
	 * Our implementation of ViewportChangeListener.
	 */
	public void resetViewportMappings() {
		viewportSet = false;

	}

	/**
	 * recalculate bounds of region when canvas is panned, zoomed, resized
	 * equations are
	 * 
	 * if (newZoom == zoom) then just a translation newX1 = x1 + newXCenter -
	 * xCenter; else (x1 - xCenter) / zoom = (newX1 - newXCenter) / newZoom
	 * known is x1, xCenter, zoom, newXCenter, newZoom, unknown is newX1 newX1 =
	 * newXCenter + ((newZoom / zoom) * (x1 - xCenter)) and if deltaZoom =
	 * newZoom / zoom and deltaX = x1 - xCenter then newX1 = newXCenter +
	 * deltaZoom * deltaX
	 * 
	 */
	public void viewportChanged(int w, int h, double newXCenter,
			double newYCenter, double newScaleFactor) {

		Double vpX = this.getX1();
		Double vpY = this.getY1();
		Double vpW = this.getW1();
		Double vpH = this.getH1();

		// first time initialization of zoom and centerpoint, if needed
		if (!viewportSet) {
			viewportSet = true;
			currentZoom = newScaleFactor;
			currentCenterX = newXCenter;
			currentCenterY = newYCenter;
			viewportWidth = w;
			viewportHeight = h;
		}

		double deltaZoom = newScaleFactor / currentZoom;
		double deltaX = vpX - (0.5 * w);
		double deltaY = vpY - (0.5 * h);

		if ((deltaZoom > 0.999999) && (deltaZoom < 1.000001)
				&& (viewportWidth == w) && (viewportHeight == h))
		// we are just panning
		{
			this.setX1(vpX + (currentCenterX - newXCenter) * newScaleFactor);
			this.setY1(vpY + (currentCenterY - newYCenter) * newScaleFactor);
		} else if ((viewportWidth != w) || (viewportHeight != h)) { // we are
			// resizing viewport
			this.setX1(vpX + (0.5 * (w - viewportWidth)));
			this.setY1(vpY + (0.5 * (h - viewportHeight)));

		} else // we are zooming
		{
			this.setW1(vpW * deltaZoom);
			this.setH1(vpH * deltaZoom);

			deltaX *= deltaZoom;
			deltaY *= deltaZoom;

			vpX = (0.5 * w) + deltaX;
			vpY = (0.5 * h) + deltaY;

			// do whatever translation is necessary
			this.setX1(vpX + (currentCenterX - newXCenter) * newScaleFactor);
			this.setY1(vpY + (currentCenterY - newYCenter) * newScaleFactor);
		}

		currentZoom = newScaleFactor;
		currentCenterX = newXCenter;
		currentCenterY = newYCenter;
		viewportWidth = w;
		viewportHeight = h;

		// test with 'true'
		this.setBounds(this.getX1(), this.getY1(), this.getW1(), this.getH1(),
				true);
	}

	public void setBounds(double x, double y, double width, double height,
			boolean fromNode) {

		// pass up to JComponent
		super.setBounds((int) x, (int) y, (int) width, (int) height);

		// set member vars to node coords
		nodeX1 = x;
		nodeY1 = y;
		nodeW1 = width;
		nodeH1 = height;

		// our bounds have changed, create a new image with new size
		if ((width > 1) && (height > 1)) {
			image = new BufferedImage((int) width, (int) height,
					BufferedImage.TYPE_INT_ARGB);
		}

	}

	// public void setBounds(double x, double y, double width, double height) {
	//
	// // make room for handles
	// super.setBounds(((int) x - (HANDLE_SIZE / 2)),
	// ((int) y - (HANDLE_SIZE / 2)), ((int) width + HANDLE_SIZE),
	// ((int) height + HANDLE_SIZE));
	//
	// // set member vars
	// this.x1 = x;
	// this.y1 = y;
	// this.w1 = width;
	// this.h1 = height;
	//
	// // our bounds have changed, create a new image with new size
	// if ((width > 0) && (height > 0)) {
	// // make room for handles
	// image = new BufferedImage(((int) width + HANDLE_SIZE),
	// ((int) height + HANDLE_SIZE), BufferedImage.TYPE_INT_ARGB);
	// }
	//
	// // update nodeView coordinates of Layout Region for Groups/xGMML export
	// Point2D[] corners = new Point2D[] { new Point2D.Double(x, y),
	// new Point2D.Double(x + width, y + height) };
	// try {
	// AffineTransform xfrm = ((DGraphView) myView).getCanvas()
	// .getAffineTransform().createInverse();
	// Point2D[] newCorners = new Point2D[2];
	// xfrm.transform(corners, 0, newCorners, 0, 2);
	// nodeX1 = (newCorners[0].getX());
	// nodeY1 = (newCorners[0].getY());
	// nodeW1 = (newCorners[1].getX() - newCorners[0].getX());
	// nodeH1 = (newCorners[1].getY() - newCorners[0].getY());
	//
	// // if (myGroup != null) {
	// // CyNode groupNode = this.myGroup.getGroupNode();
	// // CyAttributes attributes = Cytoscape.getNodeAttributes();
	// // attributes.setAttribute(groupNode.getIdentifier(),
	// // BubbleRouterPlugin.REGION_X_ATT, nodeX1);
	// // attributes.setAttribute(groupNode.getIdentifier(),
	// // BubbleRouterPlugin.REGION_Y_ATT, nodeY1);
	// // attributes.setAttribute(groupNode.getIdentifier(),
	// // BubbleRouterPlugin.REGION_W_ATT, nodeW1);
	// // attributes.setAttribute(groupNode.getIdentifier(),
	// // BubbleRouterPlugin.REGION_H_ATT, nodeH1);
	// //
	// // }
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// }

	public void paint(Graphics g) {

		// only paint if we have an image to paint onto
		if (image != null) {

			// set visable edge/rim color
			Color drawColor = Color.red;

			// image to draw
			Graphics2D image2D = image.createGraphics();

			// image2D.setPaint(_fillColor);
			//
			// image2D.fillRect(0, 0, image.getWidth(null), image
			// .getHeight(null));
			image2D.setPaint(drawColor);

			Line2D line = new Line2D.Double();
			line.setLine(0, image.getHeight(), image.getWidth(null)/2, 0);
			image2D.setPaint(drawColor);
			image2D.draw(line);
			line.setLine(image.getWidth(null)/2, 0, image.getWidth(), image
					.getHeight());
			image2D.setPaint(drawColor);
			image2D.draw(line);

			// image2D.fillRect(0, image.getHeight()/2 - 1,
			// image.getWidth(null), image.getHeight()/2 +1);
			// image2D.setStroke(new BasicStroke(1.0f));
			// image2D.drawLine(0, image.getHeight(),
			// image.getWidth(null)/2, 0);
			// image2D.drawLine(image.getWidth(null)/2, 0,
			// image.getWidth(null), image.getHeight());
			// image2D.setPaint(drawColor);
			// // image2D.drawRect(0, 0, image.getWidth(null), 1);
			// image2D.setStroke(new BasicStroke(1.0f, BasicStroke.CAP_SQUARE,
			// BasicStroke.JOIN_MITER, 10, new float[] { 4, 4 }, 0));
			// image2D.drawLine(0, image.getHeight() / 2,
			// image.getWidth(null), image.getHeight() / 2);

			((Graphics2D) g).drawImage(image, null, 0, 0);

		}

	}

	public void setMyView(CyNetworkView view) {
		this.myView = view;
	}

	public CyGroup getMyGroup() {
		return myGroup;
	}

	public void setMyGroup(CyGroup group) {
		myGroup = group;
	}

	// Copied from original Region class
	public int getUnits() {
		return _units;
	}

	public void setUnits(int units) {
		this._units = units;
	}

	public String getAnnotation() {
		return _annotation;
	}

	public void setAnnotation(String annotation) {
		this._annotation = annotation;
	}
}
