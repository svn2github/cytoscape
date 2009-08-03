package org.cytoscape.groups.results;

import giny.model.Node;
import giny.view.NodeView;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.swing.JComponent;
import javax.swing.undo.AbstractUndoableEdit;

import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;
import cytoscape.groups.CyGroup;
import cytoscape.util.undo.CyUndo;
import cytoscape.view.CyNetworkView;
import cytoscape.visual.GlobalAppearanceCalculator;
import cytoscape.visual.NodeAppearance;
import cytoscape.visual.VisualPropertyType;
import cytoscape.visual.VisualStyle;
import cytoscape.visual.parsers.ObjectToString;
import ding.view.DGraphView;
import ding.view.DingCanvas;
import ding.view.ViewportChangeListener;

@SuppressWarnings("serial")
public class LayoutRegion extends JComponent implements ViewportChangeListener {

	/**
	 * Translucency level of region
	 */
	private static final int TRANSLUCENCY_LEVEL = (int) (255 * .10);

	private double x1 = Double.NaN;

	private double y1 = Double.NaN;

	private double w1 = Double.NaN;

	private double h1 = Double.NaN;

	private double nodeX1;

	private double nodeY1;

	private double nodeW1;

	private double nodeH1;

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

	/**
	 * name of the selected attribute field
	 */
	private String attributeName = null;

	/**
	 * particular value(s) associated with a layout region
	 */
	private ArrayList<Object> regionAttributeValues = new ArrayList<Object>();

	/**
	 * list of nodes associated with a layout region based on
	 * regionAttributeValue
	 */
	private List<NodeView> nodeViews;

	private CyNetworkView myView;

	private CyGroup myGroup = null;

	/**
	 * For undo/redo
	 */
	private Point2D[] _undoOffsets;

	private Point2D[] _redoOffsets;

	private NodeView[] _selectedNodeViews;

	private Point2D[] _undoExcludedOffsets;

	private Point2D[] _redoExcludedOffsets;

	private NodeView[] _excludedNodeViews;

	private LayoutRegion _thisRegion;

	private static final int HANDLE_SIZE = 8;

	private boolean selected = false;

	/**
	 * For accommodating pan and zoom of InnerCanvas
	 */
	private double currentZoom = Double.NaN;

	private double currentCenterX = Double.NaN, currentCenterY = Double.NaN;

	private boolean viewportSet = false;

	private int viewportWidth;

	private int viewportHeight;

	private List<NodeView> boundedNodeViews = new ArrayList<NodeView>();

	
	
	/**
	 * This is used to generate all the properties of the layout region object.
	 * 
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 */
	public LayoutRegion(CyGroup group) {
		super();

		this.myGroup = group;
				
		myView = Cytoscape.getCurrentNetworkView();
		// add ViewportChangeListener for accommodating pan/zoom
		((DGraphView) myView).addViewportChangeListener(this);
		// determine color of layout region
		colorIndex = (++colorIndex % colors.length == 0) ? 0 : colorIndex;
		this.paint = colors[colorIndex];
		this.setColorIndex(colorIndex);
		
		nodeViews = new ArrayList<NodeView>();
		Iterator<CyNode> it = myGroup.getNodeIterator();
		while (it.hasNext()){
			nodeViews.add(Cytoscape.getCurrentNetworkView().getNodeView(it.next()));
		}

		/**
		 * Note that coordinates are in terms of screen coordinates, not node
		 * coordinates. Use double coordinates, to avoid roundoff errors
		 * setBounds((int) x, (int) y, (int) width, (int) height);
		 */
		double[] region = getRectValues(myGroup);
		setBounds(region[0], region[1], region[2], region[3]);
		// add region to hashmap
		LayoutRegionManager.addRegion(myView, this);
	}

	/**
	 * Constructor for xGMML read-in using group node attributes
	 * 
	 */
	public LayoutRegion(double x, double y, double width, double height,
			ArrayList name, List<NodeView> nv, int color, CyNetworkView view,
			CyGroup group) {
		super();
		this.setRegionAttributeValue(name);
		setBounds(x, y, width, height, true);
		this.setNodeViews(nv);
		this.paint = colors[color];
		this.setColorIndex(color);
		myView = view;
		this.setMyGroup(group);
		((DGraphView) myView).addViewportChangeListener(this);
		LayoutRegionManager.addRegionFromFile(myView, this);
	}

	/**
	 * Empty Constructor
	 * 
	 */
	public LayoutRegion() {
		super();

		List<NodeView> nv = new ArrayList<NodeView>();
		this.setNodeViews(nv);

	}

	
	// Determine the screen coordinates of a region (rectangle:x,y,w,h),
	// which covers all nodes in a given group 
	private double[] getRectValues(CyGroup pGroup){

		Iterator it = pGroup.getNodeIterator();
		
		CyNode node = (CyNode) it.next();
		NodeView nv = Cytoscape.getCurrentNetworkView().getNodeView(node);
		
		double minX = Double.POSITIVE_INFINITY; 
		double maxX = Double.NEGATIVE_INFINITY; 
		double minY = Double.POSITIVE_INFINITY;
		double maxY = Double.NEGATIVE_INFINITY;
		
		double scaleFactor = Cytoscape.getCurrentNetworkView().getZoom();	

		DGraphView theView = (DGraphView) Cytoscape.getCurrentNetworkView();

		double w = theView.getComponent().getBounds().getWidth();
		double h = theView.getComponent().getBounds().getHeight();
		
		double xCenter = theView.getCenter().getX();
		double yCenter = theView.getCenter().getY();
		
		double deltaX=0, deltaY=0;
		
		while (it.hasNext()){
			node = (CyNode) it.next();
			nv = Cytoscape.getCurrentNetworkView().getNodeView(node);
		
			deltaX = nv.getWidth()/2 + nv.getBorderWidth();
			deltaY = nv.getHeight()/2 + nv.getBorderWidth();
			
			if (nv.getXPosition()-deltaX < minX){
				minX = nv.getXPosition()- deltaX;
			}
			if (nv.getXPosition()+deltaX > maxX){
				maxX = nv.getXPosition()+deltaX;
			}
			
			if (nv.getYPosition()-deltaY < minY){
				minY = nv.getYPosition()-deltaY;
			}
			if (nv.getYPosition()+deltaY > maxY){
				maxY = nv.getYPosition()+deltaY;
			}				
		}
				
		double[] rectValues = new double[4];
		
		rectValues[0] = w/2 - (xCenter - minX)*scaleFactor;
		rectValues[1] = h/2 - (yCenter - minY)*scaleFactor;
		rectValues[2] = (maxX - minX)*scaleFactor;
		rectValues[3] = (maxY - minY)*scaleFactor;

		return rectValues;
	}
	
	
	/**
	 * @return Color
	 */
	public Color getColor() {
		return colors[LayoutRegion.colorIndex];
	}

	/**
	 * @return colorIndex
	 */
	public int getColorIndex() {
		return LayoutRegion.colorIndex;
	}

	/**
	 * @param colorIndex
	 */
	public void setColorIndex(int colorIndex) {
		LayoutRegion.colorIndex = colorIndex;
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
	 * Could be used to display attribute name associated with region and/or to
	 * initialize selection dialog when changing attribute name
	 */
	public String getAttributeName() {
		return this.attributeName;
	}

	/**
	 * Could be used to display attribute name associated with region and/or to
	 * initialize selection dialog when changing attribute name
	 */
	public void setAttributeName(String attName) {
		attributeName = attName;
	}

	/**
	 * Returns the list of values associated with a region as an object.
	 * 
	 * e.g., "[value1, value2, value3]"
	 * 
	 * @return Returns the regionAttributeValue.
	 */
	public Object getRegionAttributeValue() {
		return this.regionAttributeValues;
	}

	/**
	 * Could be used to allow user control over changing a regions attribute
	 * value association
	 * 
	 * @param regionAttributeValue
	 */
	public void setRegionAttributeValue(ArrayList selected) {
		for (Object o : selected) {
			regionAttributeValues.add(o);
		}
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

		this.setBounds(this.getX1(), this.getY1(), this.getW1(), this.getH1());
	}

	/**
	 * Select all nodeViews with specified attribute value for attribute. Note:
	 * logic is symmetrical with BRQuickFindConfigDialog.addSortTableModel().
	 */
	

	public void populateNodeViews() {
		

		
	}

	public void setBounds(double x, double y, double width, double height,
			boolean fromNode) {

		// make room for handles
		super.setBounds(((int) x - (HANDLE_SIZE / 2)),
				((int) y - (HANDLE_SIZE / 2)), ((int) width + HANDLE_SIZE),
				((int) height + HANDLE_SIZE));

		// set member vars to node coords
		nodeX1 = x;
		nodeY1 = y;
		nodeW1 = width;
		nodeH1 = height;

		// our bounds have changed, create a new image with new size
		if ((width > 0) && (height > 0)) {
			// make room for handles
			image = new BufferedImage(((int) width + HANDLE_SIZE),
					((int) height + HANDLE_SIZE), BufferedImage.TYPE_INT_ARGB);
		}

	}

	

	public void setBounds(double x, double y, double width, double height) {

		
		// make room for handles
		super.setBounds(((int) x - (HANDLE_SIZE / 2)),
				((int) y - (HANDLE_SIZE / 2)), ((int) width + HANDLE_SIZE),
				((int) height + HANDLE_SIZE));

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

		/*
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
		*/	
		} catch (Exception e) {
			e.printStackTrace();
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

	/**
	 * add handles on the corners and edges of the region -- affordances for
	 * stretching
	 * 
	 */
	private void drawHandles(Graphics2D image2D) {

		// first fill in handles
		image2D.setColor(Color.white);

		// top left
		image2D.fillOval(0, 0, HANDLE_SIZE, HANDLE_SIZE);

		// top center
		image2D.fillOval(((int) (this.w1 / 2)), 0, HANDLE_SIZE, HANDLE_SIZE);
		// top right
		image2D.fillOval((int) this.w1, 0, HANDLE_SIZE, HANDLE_SIZE);

		// center left
		image2D.fillOval(0, ((int) (this.h1 / 2)), HANDLE_SIZE, HANDLE_SIZE);

		// center right
		image2D.fillOval((int) this.w1, ((int) (this.h1 / 2)), HANDLE_SIZE,
				HANDLE_SIZE);

		// bottom left
		image2D.fillOval(0, (int) this.h1, HANDLE_SIZE, HANDLE_SIZE);

		// bottom center
		image2D.fillOval(((int) (this.w1 / 2)), (int) this.h1, HANDLE_SIZE,
				HANDLE_SIZE);

		// bottom right
		image2D
				.fillOval((int) this.w1, (int) this.h1, HANDLE_SIZE,
						HANDLE_SIZE);

		// now draw outline of handle
		image2D.setColor(Color.black);
		// top left
		image2D.drawOval(0, 0, HANDLE_SIZE, HANDLE_SIZE);

		// top center
		image2D.drawOval(((int) (this.w1 / 2)), 0, HANDLE_SIZE, HANDLE_SIZE);
		// top right
		image2D.drawOval((int) this.w1, 0, HANDLE_SIZE, HANDLE_SIZE);

		// center left
		image2D.drawOval(0, ((int) (this.h1 / 2)), HANDLE_SIZE, HANDLE_SIZE);

		// center right
		image2D.drawOval((int) this.w1, ((int) (this.h1 / 2)), HANDLE_SIZE,
				HANDLE_SIZE);

		// bottom left
		image2D.drawOval(0, (int) this.h1, HANDLE_SIZE, HANDLE_SIZE);

		// bottom center
		image2D.drawOval(((int) (this.w1 / 2)), (int) this.h1, HANDLE_SIZE,
				HANDLE_SIZE);

		// bottom right
		image2D
				.drawOval((int) this.w1, (int) this.h1, HANDLE_SIZE,
						HANDLE_SIZE);

	}

	public boolean isSelected() {
		return selected;
	}

	// selection and de-selection of a region
	public void setSelected(boolean isSelected) {
		this.selected = isSelected;

		// select nodes in this region
		Iterator itx = this.getNodeViews().iterator();
		while (itx.hasNext()) {
			NodeView nv = (NodeView) itx.next();
			nv.setSelected(selected);
		}
		this.repaint();
	}

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

	public CyNetworkView getMyView() {
		if (myView == null) {
			myView = Cytoscape.getCurrentNetworkView();
		}
		return myView;
	}

	public void setMyView(CyNetworkView view) {
		this.myView = view;
	}

	public CyGroup getMyGroup() {
		return myGroup;
	}

	private void setMyGroup(CyGroup group) {
		myGroup = group;
	}
}
