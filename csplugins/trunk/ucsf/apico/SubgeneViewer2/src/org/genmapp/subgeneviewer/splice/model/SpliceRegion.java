package org.genmapp.subgeneviewer.splice.model;

import giny.model.Node;
import giny.view.NodeView;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
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
import cytoscape.groups.CyGroupManager;
import cytoscape.util.undo.CyUndo;
import cytoscape.view.CyNetworkView;
import cytoscape.visual.GlobalAppearanceCalculator;
import cytoscape.visual.VisualStyle;
import ding.view.DGraphView;
import ding.view.DingCanvas;
import ding.view.ViewportChangeListener;

@SuppressWarnings("serial")
public class SpliceRegion extends JComponent implements ViewportChangeListener {

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

	private int viewID = 0;

	/**
	 * For undo/redo
	 */
	private Point2D[] _undoOffsets;

	private Point2D[] _redoOffsets;

	private NodeView[] _selectedNodeViews;

	private SpliceRegion _thisRegion;

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

	private String _region_id; // pure region id (e.g., E1.1)

	private String _region_name; // unique combo of feature_label + region_id

	private Color color;

	private int red;

	private int green;

	private int blue;

	private Color _fillColor = Color.white;

	private String _type;

	private int _units; // width in units of feature nodes

	private boolean _containsStartSite;

	private boolean _isConstitutive;

	private String _annotation;

	private NodeView featureNodeView;

	/**
	 * This is used to generate all the properties of the layout region object.
	 * 
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 */
	// public SpliceRegion2(double x, double y, double width, double height,
	// String attName, ArrayList<Object> regionAttValues) {
	// super();
	//
	// /**
	// * Set attributeName and regionAttributeValues from dialog
	// */
	// this.setAttributeName(attName);
	// // this.setRegionAttributeValue(regionAttValues);
	//		
	// /**
	// * Setbounds must come before populate nodeviews.
	// *
	// * Note that coordinates are in terms of screen coordinates, not node
	// * coordinates. Use double coordinates, to avoid roundoff errors
	// * setBounds((int) x, (int) y, (int) width, (int) height);
	// */
	// setBounds(x, y, width, height);
	// nodeViews = populateNodeViews();
	//
	// // determine color of layout region
	// colorIndex = (++colorIndex % colors.length == 0) ? 0 : colorIndex;
	// this.paint = colors[colorIndex];
	// this.setColorIndex(colorIndex);
	//
	// myView = Cytoscape.getCurrentNetworkView();
	//		
	// // add ViewportChangeListener for accommodating pan/zoom
	// ((DGraphView) myView)
	// .addViewportChangeListener(this);
	//		
	// // add region to hashmap
	// // LayoutRegionManager.addRegion(myView, this);
	// }
	/**
	 * Constructor for xGMML read-in using group node attributes
	 * 
	 */
	public SpliceRegion(String name, CyNetworkView view, String id,
			String type, int units, boolean constitutive, boolean start,
			String annotation) {
		// double x, double y, double width, double height, ArrayList name,
		// List<NodeView> nv, int color, CyNetworkView view, CyGroup group
		super();
		_region_name = name;
		_region_id = id;
		_type = type;
		_units = units;
		_isConstitutive = constitutive;
		_containsStartSite = start;
		_annotation = annotation;

		CyNode cn = Cytoscape.getCyNode(name);
		NodeView nv = view.getNodeView(cn);
		this.featureNodeView = nv;

		// this.setRegionAttributeValue(name);
		// setBounds((int) featureNodeView.getXPosition(), (int) featureNodeView
		// .getYPosition(), (int) featureNodeView.getWidth(),
		// (int) featureNodeView.getHeight(), true);
		// System.out.println("coords= x: " + (int)
		// featureNodeView.getXPosition()
		// + " y: "+ (int) featureNodeView.getYPosition()
		// + " w: "+ (int) featureNodeView.getWidth()
		// + " h: "+ (int) featureNodeView.getHeight());
		double featureNodeWidth = featureNodeView.getWidth();
		double featureNodeHeight = featureNodeView.getHeight();

		double x = featureNodeView.getXPosition() - (featureNodeWidth / 2);
		double y = featureNodeView.getYPosition() - (featureNodeHeight * 2);
		double w = featureNodeWidth * 4 * _units;
		double h = featureNodeHeight * 2;

		setBounds(x, y, w, h, true);
		// setBounds(featureNodeView.getXPosition(),featureNodeView.getYPosition(),
		// featureNodeView.getWidth(), featureNodeView.getHeight(), true);

		if (this._isConstitutive) {
			this._fillColor = Color.blue;
		}

		// nodeViews = nv;
		// this.paint = Color.blue;
		// this.setColorIndex(color);
		myView = view;
		// this.setMyGroup(group);
		((DGraphView) myView).addViewportChangeListener(this);
		// LayoutRegionManager.addRegionFromFile(myView, this);

		DGraphView dview = (DGraphView) Cytoscape.getCurrentNetworkView();
		// DGraphView dview = (DGraphView) view;
		DingCanvas aLayer = dview
				.getCanvas(DGraphView.Canvas.FOREGROUND_CANVAS);
		aLayer.add(this);
		// System.out.println("canvas: "+aLayer);
		dview.setZoom(dview.getZoom() * 0.99999999999999999d);

	}

	// /**
	// * Empty Constructor
	// *
	// */
	// public SpliceRegion2() {
	// super();
	//
	// nodeViews = new ArrayList<NodeView>();
	//
	// }

	// /**
	// * @return Color
	// */
	// public Color getColor() {
	// return colors[LayoutRegion.colorIndex];
	// }
	//
	// /**
	// * @return colorIndex
	// */
	// public int getColorIndex() {
	// return LayoutRegion.colorIndex;
	// }
	//
	// /**
	// * @param colorIndex
	// */
	// public void setColorIndex(int colorIndex) {
	// LayoutRegion.colorIndex = colorIndex;
	// }

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

	// select all nodeViews with specified attribute value for attribute
	// public List<NodeView> populateNodeViews() {
	// Comparator<Object> comparator = new Comparator<Object>() {
	// public int compare(Object o1, Object o2) {
	// return o1.toString().compareToIgnoreCase(o2.toString());
	// }
	// };
	// SortedSet<Object> selectedNodes = new TreeSet<Object>(comparator);
	// CyAttributes attribs = Cytoscape.getNodeAttributes();
	// Iterator it = Cytoscape.getCurrentNetwork().nodesIterator();
	// while (it.hasNext()) {
	// Cytoscape.getCurrentNetwork().unselectAllNodes();
	// Node node = (Node) it.next();
	// String val = null;
	// String terms[] = new String[1];
	// // add support for parsing List type attributes
	// if (attribs.getType(attributeName) == CyAttributes.TYPE_SIMPLE_LIST) {
	// List valList = attribs.getListAttribute(node.getIdentifier(),
	// attributeName);
	// // iterate through all elements in the list
	// if (valList != null && valList.size() > 0) {
	// terms = new String[valList.size()];
	// for (int i = 0; i < valList.size(); i++) {
	// Object o = valList.get(i);
	// terms[i] = o.toString();
	// }
	// }
	// val = join(terms);
	// } else {
	// val = attribs.getStringAttribute(node.getIdentifier(),
	// attributeName);
	// }
	//
	// // loop through elements in array below and match
	//
	// if ((!(val == null) && (!val.equals("null")) && (val.length() > 0))) {
	// for (Object o : regionAttributeValues) {
	// if (val.indexOf(o.toString()) >= 0) {
	// selectedNodes.add(node);
	// }
	// }
	// } else if (regionAttributeValues.get(0).equals("unassigned")) {
	// selectedNodes.add(node);
	// }
	// }
	// Cytoscape.getCurrentNetwork().setSelectedNodeState(selectedNodes, true);
	// System.out.println("Selected " + selectedNodes.size()
	// + " nodes for layout in "
	// + this.regionAttributeValues.toString());
	//
	// // only run layout if some nodes are selected
	// if (selectedNodes.size() > 0) {
	//
	// // for undo/redo
	// List<NodeView> selectedNodeViews = new ArrayList<NodeView>();
	// _selectedNodeViews = new NodeView[selectedNodes.size()];
	// _undoOffsets = new Point2D[selectedNodes.size()];
	// _redoOffsets = new Point2D[selectedNodes.size()];
	// _thisRegion = this;
	// int j = 0;
	// for (Object o : selectedNodes) {
	// Node n = (Node) o;
	// selectedNodeViews.add(Cytoscape.getCurrentNetworkView()
	// .getNodeView(n));
	// _selectedNodeViews[j] = Cytoscape.getCurrentNetworkView()
	// .getNodeView(n);
	// _undoOffsets[j] = _selectedNodeViews[j].getOffset();
	// j++;
	// }
	//
	// HierarchicalLayoutListener hierarchicalListener = new
	// HierarchicalLayoutListener();
	// System.out.println("Running hierarchical layout algorithm");
	// hierarchicalListener.actionPerformed(null);
	//
	// NodeViewsTransformer.transform(Cytoscape.getCurrentNetworkView()
	// .getSelectedNodes(), new Rectangle2D.Double(x1
	// + (HANDLE_SIZE / 2), y1 + (HANDLE_SIZE / 2), w1, h1));
	//
	// // add automatic edge minimization following region routing
	// UnCrossAction.unCross(selectedNodeViews, false);
	//
	// // undo/redo facility
	// for (int k = 0; k < _selectedNodeViews.length; k++) {
	// _redoOffsets[k] = _selectedNodeViews[k].getOffset();
	// }
	//
	// CyUndo.getUndoableEditSupport().postEdit(
	// new AbstractUndoableEdit() {
	//
	// public String getPresentationName() {
	// return "Interactive Layout";
	// }
	//
	// public String getRedoPresentationName() {
	//
	// return "Redo: Layout Region";
	// }
	//
	// public String getUndoPresentationName() {
	// return "Undo: Layout Region";
	// }
	//
	// public void redo() {
	// for (int m = 0; m < _selectedNodeViews.length; m++) {
	// _selectedNodeViews[m].setOffset(_redoOffsets[m]
	// .getX(), _redoOffsets[m].getY());
	// }
	// // Add region to list of regions for this view
	// LayoutRegionManager.addRegionForView(Cytoscape
	// .getCurrentNetworkView(), _thisRegion);
	//
	// // Grab ArbitraryGraphicsCanvas (a prefab canvas)
	// // and add the layout region
	// DGraphView view = (DGraphView) Cytoscape
	// .getCurrentNetworkView();
	// DingCanvas backgroundLayer = view
	// .getCanvas(DGraphView.Canvas.BACKGROUND_CANVAS);
	// backgroundLayer.add(_thisRegion);
	//
	// }
	//
	// public void undo() {
	// for (int m = 0; m < _selectedNodeViews.length; m++) {
	// _selectedNodeViews[m].setOffset(_undoOffsets[m]
	// .getX(), _undoOffsets[m].getY());
	// }
	// // Add region to list of regions for this view
	// LayoutRegionManager.removeRegionFromView(Cytoscape
	// .getCurrentNetworkView(), _thisRegion);
	//
	// // Grab ArbitraryGraphicsCanvas (a prefab canvas)
	// // and add the layout region
	// DGraphView view = (DGraphView) Cytoscape
	// .getCurrentNetworkView();
	// DingCanvas backgroundLayer = view
	// .getCanvas(DGraphView.Canvas.BACKGROUND_CANVAS);
	// backgroundLayer.remove(_thisRegion);
	// }
	// });
	//
	// Cytoscape.getCurrentNetworkView().redrawGraph(true, true);
	//
	// // Associate selected node views with region
	// List<NodeView> selectedNodeViewsList = new ArrayList<NodeView>();
	// for (int i = 0; i < _selectedNodeViews.length; i++) {
	// selectedNodeViewsList.add(_selectedNodeViews[i]);
	// }
	// return selectedNodeViewsList;
	// } else {
	// return null;
	// }
	// }

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
		if ((width > 0) && (height > 0)) {
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
			Color drawColor = Color.black;

			// image to draw
			Graphics2D image2D = image.createGraphics();

			image2D.setPaint(_fillColor);

			if (_type.equalsIgnoreCase("e")) { // exon
				image2D.fillRect(0, 0, image.getWidth(null), image
						.getHeight(null));
				image2D.setColor(drawColor);
				image2D.draw3DRect(0, 0, image.getWidth(null) - 1, image
						.getHeight(null) - 1, true);
			} else if (_type.equalsIgnoreCase("i")) { // intron
				image2D.setPaint(drawColor);
//				image2D.fillRect(0, image.getHeight()/2 - 1, image.getWidth(null), image.getHeight()/2 +1);
				image2D.setStroke(new BasicStroke(1.5f));
				image2D.drawLine(0, image.getHeight()/2, image.getWidth(null), image.getHeight()/2);
			} else { // untranslated
				image2D.setPaint(drawColor);
				//image2D.drawRect(0, 0, image.getWidth(null), 1);
				image2D.setStroke(new BasicStroke(1.0f, BasicStroke.CAP_SQUARE,
						BasicStroke.JOIN_MITER, 10, new float[] { 4, 4 }, 0));
				image2D.drawLine(0, image.getHeight()/2, image.getWidth(null), image.getHeight()/2);
			}

			((Graphics2D) g).drawImage(image, null, 0, 0);

		}
	}

	// /**
	// * add handles on the corners and edges of the region -- affordances for
	// * stretching
	// *
	// */
	// private void drawHandles(Graphics2D image2D) {
	//
	// // first fill in handles
	// image2D.setColor(Color.white);
	//
	// // top left
	// image2D.fillOval(0, 0, HANDLE_SIZE, HANDLE_SIZE);
	//
	// // top center
	// image2D.fillOval(((int) (this.w1 / 2)), 0, HANDLE_SIZE, HANDLE_SIZE);
	// // top right
	// image2D.fillOval((int) this.w1, 0, HANDLE_SIZE, HANDLE_SIZE);
	//
	// // center left
	// image2D.fillOval(0, ((int) (this.h1 / 2)), HANDLE_SIZE, HANDLE_SIZE);
	//
	// // center right
	// image2D.fillOval((int) this.w1, ((int) (this.h1 / 2)), HANDLE_SIZE,
	// HANDLE_SIZE);
	//
	// // bottom left
	// image2D.fillOval(0, (int) this.h1, HANDLE_SIZE, HANDLE_SIZE);
	//
	// // bottom center
	// image2D.fillOval(((int) (this.w1 / 2)), (int) this.h1, HANDLE_SIZE,
	// HANDLE_SIZE);
	//
	// // bottom right
	// image2D
	// .fillOval((int) this.w1, (int) this.h1, HANDLE_SIZE,
	// HANDLE_SIZE);
	//
	// // now draw outline of handle
	// image2D.setColor(Color.black);
	// // top left
	// image2D.drawOval(0, 0, HANDLE_SIZE, HANDLE_SIZE);
	//
	// // top center
	// image2D.drawOval(((int) (this.w1 / 2)), 0, HANDLE_SIZE, HANDLE_SIZE);
	// // top right
	// image2D.drawOval((int) this.w1, 0, HANDLE_SIZE, HANDLE_SIZE);
	//
	// // center left
	// image2D.drawOval(0, ((int) (this.h1 / 2)), HANDLE_SIZE, HANDLE_SIZE);
	//
	// // center right
	// image2D.drawOval((int) this.w1, ((int) (this.h1 / 2)), HANDLE_SIZE,
	// HANDLE_SIZE);
	//
	// // bottom left
	// image2D.drawOval(0, (int) this.h1, HANDLE_SIZE, HANDLE_SIZE);
	//
	// // bottom center
	// image2D.drawOval(((int) (this.w1 / 2)), (int) this.h1, HANDLE_SIZE,
	// HANDLE_SIZE);
	//
	// // bottom right
	// image2D
	// .drawOval((int) this.w1, (int) this.h1, HANDLE_SIZE,
	// HANDLE_SIZE);
	//
	// }

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

	// public CyNetworkView getMyView() {
	// if (myView == null) {
	// myView = Cytoscape.getCurrentNetworkView();
	// }
	// return myView;
	// }

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
	public String getName() {
		return _region_name;
	}

	public String getId() {
		return _region_id;
	}

	public void setId(String id) {
		_region_id = id;
	}

	// public final void paint(Graphics g) {
	// Graphics2D g2d = (Graphics2D)g.create();
	// g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
	// RenderingHints.VALUE_ANTIALIAS_ON);
	// g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
	// RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
	// System.out.println("painting!");
	// doPaint(g2d);
	// }
	//	
	// protected void doPaint(Graphics2D g2d){
	// Rectangle b = getBounds();
	// g2d.setFont(new Font("Arial" , Font.PLAIN, 12));
	// g2d.setColor(new Color(100,50,200));
	//		
	// g2d.drawString("Hello?", 0, b.height / 2);
	//		
	// g2d.dispose();
	// }

	// public SpliceEvent addSpliceEvent(String toBlock, String toRegion) {
	//		
	// SpliceEvent spliceEvent = new SpliceEvent(this);
	// spliceEvent.setId(toBlock, toRegion);
	// listOfSpliceEvents.add(spliceEvent);
	// spliceEvent.setRegion(this);
	// return spliceEvent;
	// }

	public Color getColor() {
		if (color == null) {
			this.color = new Color(255, 255, 255);
		}
		return color;
	}

	public void setColor(int r, int g, int b) {
		this.red = r;
		this.green = g;
		this.blue = b;
		this.color = new Color(red, green, blue);
	}

	public boolean isContainsStartSite() {
		return _containsStartSite;
	}

	public void setContainsStartSite(boolean containsStartSite) {
		this._containsStartSite = containsStartSite;
	}

	public boolean isConstitutive() {
		return _isConstitutive;
	}

	public void setConstitutive(boolean isConstitutive) {
		this._isConstitutive = isConstitutive;
	}

	public String getType() {
		return _type;
	}

	public void setType(String type) {
		this._type = type;
	}

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
