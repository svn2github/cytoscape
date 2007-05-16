package cytoscape.bubbleRouter;

import giny.model.Node;
import giny.view.NodeView;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
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
import javax.swing.JOptionPane;
import javax.swing.undo.AbstractUndoableEdit;

import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;
import cytoscape.util.undo.CyUndo;
import cytoscape.visual.GlobalAppearanceCalculator;
import cytoscape.visual.NodeAppearance;
import cytoscape.visual.VisualStyle;
import ding.view.DGraphView;
import ding.view.DingCanvas;
import ding.view.InnerCanvas;
import ding.view.ViewportChangeListener;

public class LayoutRegion extends JComponent
// AJK: 01/4/07 ViewportListener for accommodating pan/zoom
		implements ViewportChangeListener {
	// {
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

	// AJK: 11/15/06 BEGIN
	// make non-static so that we can have different attribute values and names
	// across regions
	//	
	// /**
	// * name of the selected attribute field
	// */
	// private static String attributeName = null;
	//
	// /**
	// * unique list of values for attributeName
	// */
	// private static Object[] attributeValues = null;
	//
	// /**
	// * particular value associated with a layout region
	// */
	// public static Object regionAttributeValue = null;

	/**
	 * name of the selected attribute field
	 */
	private String attributeName = null;

	/**
	 * unique list of values for attributeName
	 */
	private Object[] attributeValues = null;

	/**
	 * particular value associated with a layout region
	 */
	public ArrayList<Object> regionAttributeValues = new ArrayList<Object>();

	// AJK: 11/15/06 END

	/**
	 * list of nodes associated with a layout region based on
	 * regionAttributeValue
	 */
	private List nodeViews;

	// AJK: 11/15/06 BEGIN
	/**
	 * for undo/redo
	 */
	private Point2D[] _undoOffsets;

	private Point2D[] _redoOffsets;

	private NodeView[] _selectedNodeViews;

	private LayoutRegion _thisRegion;

	// AJK: 11/15/06 END

	// AJK: 12/25/06 for stretching
	private Cursor savedCursor;

	private static final int HANDLE_SIZE = 8;

	private boolean selected = false;

	// AJK: 01/04/06 for accommodating pan and zoom of InnerCanvas
	private double currentZoom = Double.NaN;

	private double currentCenterX = Double.NaN, currentCenterY = Double.NaN;

	private boolean viewportSet = false;

	// private double originalZoom = Double.NaN;
	// private double originalOffsetX = Double.NaN, originalOffsetY =
	// Double.NaN;
	private int viewportWidth;

	private int viewportHeight;

	/**
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 */
	public LayoutRegion(double x, double y, double width, double height) {
		super();

		// AJK: 04/18/07 BEGIN
		//   enforce minimum size on region of 110% default node width and height
		VisualStyle vizStyle = Cytoscape.getCurrentNetworkView().getVisualStyle();
		NodeAppearance na = vizStyle.getNodeAppearanceCalculator().getDefaultAppearance();
		if ((width < 1.1 * na.getWidth()) || (height < 1.1 * na.getHeight()))
		{
			JOptionPane.showMessageDialog(Cytoscape.getDesktop(),
					"This region is too small to fit anything.  Please draw a larger region.");
			return;
		}
		// AJK: 04/18/07 END
	
		// init member vars
		selectRegionAttributeValue();

		//if no selection is made, or if 'cancel' is clicked
		if (this.getRegionAttributeValue() == null || this.getRegionAttributeValue().toString().contentEquals("[]")) {
			return;
		}
		
	

		// AJK: 12/25/06 for stretching
		savedCursor = this.getCursor();

		// setbounds must come before populate nodeviews

		// note that coordinates are in terms of screen coordinates, not node
		// coordinates
		// AJK: 01/09/07 use double coordinates, to avoid roundoff errors
		// setBounds((int) x, (int) y, (int) width, (int) height);
		setBounds(x, y, width, height);
		nodeViews = populateNodeViews();

		// determine color of layout region
		colorIndex = (++colorIndex % colors.length == 0) ? 0 : colorIndex;
		this.paint = colors[colorIndex];
		this.setColorIndex(colorIndex);

		// AJK: 01/04/07 add ViewportChangeListener for accommodating pan/zoom
		// currentZoom = ((DGraphView) Cytoscape.getCurrentNetworkView())
		// .getZoom();
		// currentX = ((DGraphView)
		// Cytoscape.getCurrentNetworkView()).getCenter()
		// .getX();
		// currentY = ((DGraphView)
		// Cytoscape.getCurrentNetworkView()).getCenter()
		// .getY();
		((DGraphView) Cytoscape.getCurrentNetworkView())
				.addViewportChangeListener(this);

	}
	
	/**
	 * Constructor for xGMML read-in using group node attributes
	 * 
	 */
	public LayoutRegion(double x, double y, double width, double height, ArrayList name, List nv, int color) {
		super();
		this.setRegionAttributeValue(name);
		savedCursor = this.getCursor();
		setBounds(x, y, width, height);
		nodeViews = nv;
		this.paint = colors[color];
		((DGraphView) Cytoscape.getCurrentNetworkView())
		.addViewportChangeListener(this);		
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
	 * @return colorIndex
	 */
	public int getColorIndex() {
		return colorIndex;
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

	// AJK: 11/15/06 BEGIN
	// make non-static so that we can have different attribute names/values
	// across different regions
	// public static void selectRegionAttributeValue() {
	public void selectRegionAttributeValue() {

		// Use Ethan's QuickFind dialog for attribute selection
		// TODO: modify dialog to provide value selection as well
		// and perhaps an all-value "brick" layout too
		// if (attributeName == null) {
		// new QuickFindConfigDialog();
		new BRQuickFindConfigDialog(this);

		// System.out.println("Got attribute name: " + attributeName);
		// AJK: 11/15/06 END

		// }

		// Object s = JOptionPane.showInputDialog(Cytoscape.getDesktop(),
		// "Assign a value to this region", "Bubble Router",
		// JOptionPane.PLAIN_MESSAGE, null, attributeValues,
		// attributeValues[0]);
		// return s;
	}

	/**
	 * Used by QuickFindConfigDialog
	 * 
	 * @param newAttributeKey
	 */
	// AJK: 11/15/06 make non-static
	// public static void setAttributeName(String newAttributeKey) {
	public void setAttributeName(String newAttributeKey) {
		attributeName = newAttributeKey;
		// System.out.println("Attribute name selected: " + attributeName);
	}

	/**
	 * Could be used to display attribute name associated with region and/or to
	 * initialize selection dialog when changing attribute name
	 */
	public String getAttributeName() {
		return attributeName;
	}

	/**
	 * Used by QuickFindConfigDialog
	 * 
	 * @param objects
	 */
	// AJK: 11/15/06 make non-static
	// public static void setAttributeValues(Object[] objects) {
	public void setAttributeValues(Object[] objects) {
		attributeValues = objects;
	}

	/**
	 * Used by BubbleRouterPlugin to verify that a value has been selected,
	 * i.e., that the user did NOT cancel the selection dialog
	 * 
	 * @return Returns the regionAttributeValue.
	 */
	public Object getRegionAttributeValue() {
		return regionAttributeValues;
	}

	/**
	 * Could be used to allow user control over changing a regions attribute
	 * value association
	 * 
	 * @param regionAttributeValue
	 */
	// AJK: 11/15/06 make non-static
	// public static void setRegionAttributeValue(Object object) {
	public void setRegionAttributeValue(ArrayList selected) {
		for (Object o: selected){
			regionAttributeValues.add(o);
		}
//		Iterator itx = selected.iterator();
//		while (itx.hasNext()){
//			regionAttributeValues.add(itx.next());
//		}
		// System.out.println("Attribute value selected: " +
		// regionAttributeValue);

	}

	// AJK: 01/04/07 BEGIN
	// respond to changes in viewport to zoom or scroll
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

		// new image coordinates
		// double[] currentNodeCoordinates = new double[4];
		// currentNodeCoordinates[0] = this.x1;
		// currentNodeCoordinates[1] = this.y1;
		// currentNodeCoordinates[2] = this.x1 + this.w1;
		// currentNodeCoordinates[3] = this.y1 + this.h1;
		// InnerCanvas canvas = ((DGraphView)
		// Cytoscape.getCurrentNetworkView()).getCanvas();
		// AffineTransform transform = canvas.getAffineTransform();
		// transform.transform(currentNodeCoordinates, 0,
		// currentNodeCoordinates, 0, 2);
		//		
		// this.x1 = currentNodeCoordinates[0];
		// this.y1 = currentNodeCoordinates[1];
		// this.w1 = currentNodeCoordinates[2] - currentNodeCoordinates[0];
		// this.h1 = currentNodeCoordinates[3] - currentNodeCoordinates[1];

//		System.out.println(" ");
//		System.out.println("Viewport changed, w = " + w + ", h = " + h
//				+ ", newXCenter = " + newXCenter + ", newYCenter = "
//				+ newYCenter + ", newScaleFactor = " + newScaleFactor);

		InnerCanvas canvas = ((DGraphView) Cytoscape.getCurrentNetworkView())
				.getCanvas();
		// System.out.println("Inner Canvas: width = " +
		// canvas.getWidth() + ", height = " + canvas.getHeight() +
		// ", centerX = " + (canvas.getX() + ((int) (canvas.getWidth() * 0.5)))
		// +
		// ", centerY = " + (canvas.getY() + ((int) (canvas.getHeight() * 0.5)))
		// +
		// ". zoom = " + Cytoscape.getCurrentNetworkView().getZoom());
		//		
		// first time initialization of zoom and centerpoint, if needed
		if (!viewportSet) {
			viewportSet = true;
			currentZoom = newScaleFactor;
			currentCenterX = newXCenter;
			currentCenterY = newYCenter;
			viewportWidth = w;
			viewportHeight = h;
		}
//		System.out.println("currentZoom = " + currentZoom
//				+ ", currentCenterX = " + currentCenterX
//				+ ", currentCenterY = " + currentCenterY);

		double deltaZoom = newScaleFactor / currentZoom;
		double deltaX = this.x1 - (0.5 * w);
		double deltaY = this.y1 - (0.5 * h);

		if ((deltaZoom > 0.999999) && (deltaZoom < 1.000001) && (viewportWidth == w)
				&& (viewportHeight == h))
		// we are just panning
		{
			this.x1 += (currentCenterX - newXCenter) * newScaleFactor;
			this.y1 += (currentCenterY - newYCenter) * newScaleFactor;
		} else if ((viewportWidth != w) || (viewportHeight != h)) { // we are
																	// resizing viewport
			this.x1 += (0.5 * (w - viewportWidth));
			this.y1 += (0.5 * (h - viewportHeight));

		} else // we are zooming
		{
			this.w1 *= deltaZoom;
			this.h1 *= deltaZoom;

			deltaX *= deltaZoom; 
			deltaY *= deltaZoom;

			this.x1 = (0.5 * w) + deltaX;
			this.y1 = (0.5 * h) + deltaY;
//			 this.x1 = ((this.x1 - currentCenterX) + (currentCenterX * deltaZoom))
//					/ deltaZoom;
//			this.y1 = ((this.y1 - currentCenterY) + (currentCenterY * deltaZoom))
//					/ deltaZoom;
			// if we are both zooming and panning, then do the pan
//			this.x1 += (currentCenterX - newXCenter) * deltaZoom;
//			this.y1 += (currentCenterY - newYCenter) * deltaZoom;
//			this.x1 = newXCenter + (deltaZoom * (this.x1 - currentCenterX));
//			this.y1 = newXCenter + (deltaZoom * (this.y1 - currentCenterY));
//			this.x1 += currentCenterX - newXCenter;
//			this.y1 += currentCenterY - newYCenter;
			
			// do whatever translation is necessary
			this.x1 += (currentCenterX - newXCenter) * newScaleFactor;
			this.y1 += (currentCenterY - newYCenter) * newScaleFactor;
		}

//		System.out.println("new scale factor: " + newScaleFactor
//				+ " deltaZoom: " + deltaZoom);

		// this.x1 = newXCenter + (deltaZoom * deltaX);
		// this.y1 = newYCenter + (deltaZoom * deltaY);
		// this.w1 *= deltaZoom;
		// this.h1 *= deltaZoom;
		currentZoom = newScaleFactor;
		currentCenterX = newXCenter;
		currentCenterY = newYCenter;
		viewportWidth = w;
		viewportHeight = h;

//		System.out.println("newX1 = " + this.x1 + ", newY1 = " + this.y1
//				+ ", newWidth = " + this.w1 + ", newHeight = " + this.h1);
//		System.out.println(" ");

		// AJK: 01/09/07 use double coordinates to avoid roundoff error
		// this.setBounds((int) this.x1, (int) this.y1, (int) this.w1,
		// (int) this.h1);
		this.setBounds(this.x1, this.y1, this.w1, this.h1);

		// repaint();
	}


	/*
		public void viewportChanged(int w, int h, double newXCenter,
				double newYCenter, double newScaleFactor) {


			InnerCanvas canvas = ((DGraphView) Cytoscape.getCurrentNetworkView())
					.getCanvas();
			if (!viewportSet) {
				viewportSet = true;
				currentZoom = newScaleFactor;
				currentCenterX = newXCenter;
				currentCenterY = newYCenter;
				viewportWidth = w;
				viewportHeight = h;
			}
			System.out.println("currentZoom = " + currentZoom
					+ ", currentCenterX = " + currentCenterX
					+ ", currentCenterY = " + currentCenterY);

			double deltaZoom = newScaleFactor / currentZoom;
			double deltaX = newXCenter - currentCenterX;
			double deltaY = newYCenter - currentCenterY;
			double deltaViewWidth = w - viewportWidth;
			double deltaViewHeight = h - viewportHeight;
			
			
			

			
			
			
			
			currentZoom = newScaleFactor;
			currentCenterX = newXCenter;
			currentCenterY = newYCenter;
			viewportWidth = w;
			viewportHeight = h;

			System.out.println("newX1 = " + this.x1 + ", newY1 = " + this.y1
					+ ", newWidth = " + this.w1 + ", newHeight = " + this.h1);
			System.out.println(" ");

			// AJK: 01/09/07 use double coordinates to avoid roundoff error
			// this.setBounds((int) this.x1, (int) this.y1, (int) this.w1,
			// (int) this.h1);
			this.setBounds(this.x1, this.y1, this.w1, this.h1);

			// repaint();
		}

*/
	// AJK: 01/04/07 END

	// select all nodeViews with specified attribute value for attribute
	public List populateNodeViews() {
	    Comparator<Object> comparator = new Comparator<Object>() {
	         public int compare (Object o1, Object o2) {
	        	 return o1.toString().compareToIgnoreCase(o2.toString());
	            }
	        };
	    SortedSet<Object> selectedNodes = new TreeSet<Object>(comparator);
		CyAttributes attribs = Cytoscape.getNodeAttributes();
		Iterator it = Cytoscape.getCurrentNetwork().nodesIterator();
		while (it.hasNext()) {
			Cytoscape.getCurrentNetwork().unselectAllNodes();
			Node node = (Node) it.next();
			String val = null;
			String terms[] = new String[1];
			//AP: 2/26/07 add support for parsing List type attributes
			if (attribs.getType(attributeName) == CyAttributes.TYPE_SIMPLE_LIST){
				List valList = attribs.getListAttribute(node.getIdentifier(), attributeName);
                //  Iterate through all elements in the list
                if (valList != null && valList.size() > 0) {
                    terms = new String [valList.size()];
                    for (int i = 0; i < valList.size(); i++) {
                        Object o = valList.get(i);
                        terms[i] = o.toString();
                    }
                }          
				val = join(terms);
			}
			else {
				val = attribs.getStringAttribute(node.getIdentifier(),
					attributeName);
			}
//			System.out.println("VAL= " + val + "; REGION= " + regionAttributeValue);
			// loop through elements in array below and match

			if ((!(val == null) && (!val.equals("null")) && (val.length() > 0))) {
//				System.out.println("this.regionAttributeValue = "
//						+ this.regionAttributeValue);
				// if
				// (val.equalsIgnoreCase(this.regionAttributeValue.toString()))
				// {
				for (Object o: regionAttributeValues){
					if (val.indexOf(o.toString()) >= 0) {
						selectedNodes.add(node);
					}
				}				
//				Iterator itx = regionAttributeValues.iterator();
//				while (itx.hasNext()){
//				if (val.indexOf(itx.next().toString()) >= 0) {
//					selectedNodes.add(node);
//				}
//				}
			} else if (regionAttributeValues.get(0).equals("unassigned")) {
				selectedNodes.add(node);
			}
		}
		Cytoscape.getCurrentNetwork().setSelectedNodeState(selectedNodes, true);
		System.out.println("\n\rSelected " + selectedNodes.size()
				+ " nodes for layout in " + this.regionAttributeValues.toString());

		// If some nodes were select, then it's safe to run the hierarchical
		// layout
		if (selectedNodes.size() > 0) {

			// AJK: 11/15/06 BEGIN
			// for undo/redo
			_selectedNodeViews = new NodeView[selectedNodes.size()];
			_undoOffsets = new Point2D[selectedNodes.size()];
			_redoOffsets = new Point2D[selectedNodes.size()];
			_thisRegion = this;
			int j = 0;
			for (Object o: selectedNodes){
					Node n = (Node) o;
					_selectedNodeViews[j] = Cytoscape.getCurrentNetworkView()
							.getNodeView(n);
					_undoOffsets[j] = _selectedNodeViews[j].getOffset();
					j++;
				}
//			Iterator itx = selectedNodes.iterator();
//			int j = 0;
//			while (itx.hasNext()) {
//				Node n = (Node) itx.next();
//				_selectedNodeViews[j] = Cytoscape.getCurrentNetworkView()
//						.getNodeView(n);
//				_undoOffsets[j] = _selectedNodeViews[j].getOffset();
//				j++;
//			}
			// AJK: 11/15/96 END

			HierarchicalLayoutListener hierarchicalListener = new HierarchicalLayoutListener();
			System.out.println("Running hierarchical layout algorithm");
			hierarchicalListener.actionPerformed(null);

			NodeViewsTransformer.transform(Cytoscape.getCurrentNetworkView()
			// AJK: 1/1/06 accommodate space for handles
					// .getSelectedNodes(), new Rectangle2D.Double(x1, y1,
					.getSelectedNodes(), new Rectangle2D.Double(x1
					+ (HANDLE_SIZE / 2), y1 + (HANDLE_SIZE / 2), w1, h1));

			// AP: 2/25/07 add automatic edge minimization following region
			// routing
			// AJK: 03/17/07 for performance reasons, only call unCross if we are below a certain 
			//     threshold for network size
			if (Cytoscape.getCurrentNetwork().getNodeCount() < UnCrossAction.UNCROSS_THRESHOLD)
			{
				UnCrossAction.unCross(Cytoscape.getCurrentNetworkView()
						.getSelectedNodes());

			}
	
			// AJK: 11/15/06 BEGIN
			// undo/redo facility
			for (int k = 0; k < _selectedNodeViews.length; k++) {
				_redoOffsets[k] = _selectedNodeViews[k].getOffset();
			}

			CyUndo.getUndoableEditSupport().postEdit(new AbstractUndoableEdit() {
//			CytoscapeDesktop.undo.addEdit(new AbstractUndoableEdit() {

				public String getPresentationName() {
					return "Interactive Layout";
				}

				public String getRedoPresentationName() {

					return "Redo: Layout Region";
				}

				public String getUndoPresentationName() {
					return "Undo: Layout Region";
				}

				public void redo() {
					for (int m = 0; m < _selectedNodeViews.length; m++) {
						_selectedNodeViews[m].setOffset(_redoOffsets[m].getX(),
								_redoOffsets[m].getY());
					}
					// Add region to list of regions for this view
					LayoutRegionManager.addRegionForView(Cytoscape
							.getCurrentNetworkView(), _thisRegion);

					// Grab ArbitraryGraphicsCanvas (a prefab canvas) and
					// add
					// the
					// layout region
					DGraphView view = (DGraphView) Cytoscape
							.getCurrentNetworkView();
					DingCanvas backgroundLayer = view
							.getCanvas(DGraphView.Canvas.BACKGROUND_CANVAS);
					backgroundLayer.add(_thisRegion);

				}

				public void undo() {
					for (int m = 0; m < _selectedNodeViews.length; m++) {
						_selectedNodeViews[m].setOffset(_undoOffsets[m].getX(),
								_undoOffsets[m].getY());
					}
					// Add region to list of regions for this view
					LayoutRegionManager.removeRegionFromView(Cytoscape
							.getCurrentNetworkView(), _thisRegion);

					// Grab ArbitraryGraphicsCanvas (a prefab canvas) and add
					// the
					// layout region
					DGraphView view = (DGraphView) Cytoscape
							.getCurrentNetworkView();
					DingCanvas backgroundLayer = view
							.getCanvas(DGraphView.Canvas.BACKGROUND_CANVAS);
					backgroundLayer.remove(_thisRegion);
				}
			});
			// AJK: 11/15/06 END

			Cytoscape.getCurrentNetworkView().redrawGraph(true, true);

			// Associate selected nodes with region

			// AJK: 12/24/06 BEGIN
			// bug fix: need to return selectedNodeViews, not selectedNodes
			// return selectedNodes;
			List selectedNodeViewsList = new ArrayList();
			for (int i = 0; i < _selectedNodeViews.length; i++) {
				selectedNodeViewsList.add(_selectedNodeViews[i]);
			}
			return selectedNodeViewsList;
			// AJK: 12/24/06 END
		} else {
			return null;
		}
	}

//	public void setBounds(int x, int y, int width, int height) {
//		// AJK: 1/1/06 BEGIN
//		// make room for handles
//		// super.setBounds(x, y, width, height);
//		super.setBounds(x - (HANDLE_SIZE / 2), y - (HANDLE_SIZE / 2), width
//				+ HANDLE_SIZE, height + HANDLE_SIZE);
//
//		// set member vars
//		this.x1 = x;
//		this.y1 = y;
//		this.w1 = width;
//		this.h1 = height;
//
//		// our bounds have changed, create a new image with new size
//		if ((width > 0) && (height > 0)) {
//			// AJK: 1/1/06 make room for handles
//			// image = new BufferedImage(width, height,
//			image = new BufferedImage(width + HANDLE_SIZE,
//					height + HANDLE_SIZE, BufferedImage.TYPE_INT_ARGB);
//		}
//	}
//
	// AJK: 01/09/07 BEGIN
	// make a version of setBounds that takes double coordinates, to fix
	// roundoff problems
	public void setBounds(double x, double y, double width, double height) {
		// AJK: 1/1/06 BEGIN
		// make room for handles
		// super.setBounds(x, y, width, height);
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
			// AJK: 1/1/06 make room for handles
			// image = new BufferedImage(width, height,
			image = new BufferedImage(((int) width + HANDLE_SIZE),
					((int) height + HANDLE_SIZE), BufferedImage.TYPE_INT_ARGB);
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

			// AJK: 1/1/06 BEGIN
			// leave space for handles

			// // first clear background in case region has been deselected
			// image2D.setPaint(Cytoscape.getCurrentNetworkView()
			// .getVisualStyle().getGlobalAppearanceCalculator()
			// .calculateBackgroundColor(Cytoscape.getCurrentNetwork()));
			// image2D.fillRect(0, 0, image.getWidth(), image.getHeight());

			VisualStyle vs = Cytoscape.getVisualMappingManager()
					.getVisualStyle();
			GlobalAppearanceCalculator gCalc = vs
					.getGlobalAppearanceCalculator();
			Color backgroundColor = gCalc.getDefaultBackgroundColor();
			image2D.setPaint(backgroundColor);
			image2D.fillRect(0, 0, image.getWidth(), image.getHeight());

			image2D.setPaint(fillColor);

			// image2D.fillRect(0, 0, image.getWidth(null),
			// image.getHeight(null));
			image2D.fillRect(HANDLE_SIZE / 2, HANDLE_SIZE / 2, image
					.getWidth(null)
					- HANDLE_SIZE, image.getHeight(null) - HANDLE_SIZE);

			image2D.setColor(new Color(0, 0, 0, 255));
			if (regionAttributeValues != null) {
				image2D.drawString(regionAttributeValues.toString(), 20, 20);
			}
			// AJK: 01/01/06 END

			image2D.setColor(drawColor);
			// adds thickness to border

			// AJK: 1/1/06 BEGIN
			// leave space for handles
			// image2D.drawRect(1, 1, image.getWidth(null) - 3, image
			// .getHeight(null) - 3);
			// // give border dimensionality
			// image2D.draw3DRect(0, 0, image.getWidth(null) - 1, image
			// .getHeight(null) - 1, true);
			image2D.drawRect(1 + (HANDLE_SIZE / 2), 1 + (HANDLE_SIZE / 2),
					image.getWidth(null) - 3 - HANDLE_SIZE, image
							.getHeight(null)
							- 3 - HANDLE_SIZE);
			// give border dimensionality
			image2D.draw3DRect(HANDLE_SIZE / 2, HANDLE_SIZE / 2, image
					.getWidth(null)
					- 1 - HANDLE_SIZE, image.getHeight(null) - 1 - HANDLE_SIZE,
					true);
			// AJK: 1/1/06 END

			// AJK: 12/29/06 BEGIN
			// draw handles for stretching if selected
			if (this.isSelected()) {
				drawHandles(image2D);
			}

			image2D.setComposite(origComposite);
			((Graphics2D) g).drawImage(image, null, 0, 0);

		}
	}

	// AJK: 12/29/06 BEGIN
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
						HANDLE_SIZE); // bottom right

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
						HANDLE_SIZE); // bottom right

	}

	// AJK: 12/29/06 END

	// AJK: 12/25/06 BEGIN
	// for stretching
	public Cursor getSavedCursor() {
		return savedCursor;
	}

	// AJK: 12/25/06 END

	public boolean isSelected() {
		return selected;
	}

	// AJK: 02/20/07 BEGIN
	// selection and de-selection of a region

	public void setSelected(boolean isSelected) {
		this.selected = isSelected;
		// select nodes in this region
		// TODO: should *all* other nodes be unselected?
		Iterator itx = this.getNodeViews().iterator();
		while (itx.hasNext()) {
			NodeView nv = (NodeView) itx.next();
			nv.setSelected(selected);
		}
		this.repaint();
	}

	// AJK: 02/20/07 END
	
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

}
