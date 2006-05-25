/*
 * Created on Jul 31, 2005
 *
 */
package cytoscape.editor.event;

import ding.view.DGraphView;
import ding.view.InnerCanvas;
import edu.umd.cs.piccolo.nodes.PPath;
import giny.model.Node;
import giny.view.NodeView;

import java.awt.Color;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;

import javax.swing.JPanel;

import cytoscape.CyEdge;
import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.editor.CytoscapeEditor;
import cytoscape.editor.editors.BasicCytoscapeEditor;
import cytoscape.view.CyNetworkView;


/**
 * 
 * The <b>BasicNetworkEditEventHandler </b> class provides specialized methods
 * defining the behavior of the basic Cytoscape editor provided in Cytoscape
 * 2.2. The behavior is defined in terms of how the event handler responds to
 * mouse events, drag/drop events, and button press events.
 * 
 * @author Allan Kuchinsky
 * @version 1.0
 * @see BasicCytoscapeEditor
 * 
 * revised: 04/15/2006 to integrate with Cytoscape 2.3 renderer Phase 1: switch
 * underlying node identification and edge drawing code Phase 2: remove
 * dependencies upon Piccolo
 * 
 */
public class BasicNetworkEditEventHandler extends NetworkEditEventAdapter
		implements ActionListener, cytoscape.data.attr.MultiHashMapListener {
	// implements ActionListener {

	/**
	 * the node that will be dropped
	 */
	protected NodeView node;

	/**
	 * the edge that will be dropped
	 */
	protected PPath edge;

	/**
	 * flag that indicates whether there is an edge under construction
	 */
	protected boolean edgeStarted;

	/**
	 * the mouse press location for the drop point
	 */
	protected Point2D startPoint;

	/**
	 * point used in tracking mouse movement
	 */
	protected Point2D nextPoint;

	/**
	 * the canvas that this event handler is listening to
	 */
	// AJK: 04/15/06 go from PCanvas to DING InnerCanvas
	// protected PCanvas canvas;
	protected InnerCanvas canvas;

	/**
	 * the current network view
	 */
	// AJK: 04/15/06 go from PGraphView to DGraphView
	// protected PGraphView view;
	protected DGraphView view;

	// PNodeLocator locator;

	/**
	 * counter variable used in setting unique names for nodes
	 */
	protected static int counter = 0;

	// AJK: 05/19/05 BEGIN
	// edit modes
	public final int ADD_MODE = 1;

	public final int CONNECT_MODE = 2;

	public final int LABEL_MODE = 3;

	public final int SELECT_MODE = 0;

	public int mode = SELECT_MODE;

	/**
	 * CytoscapeAttribute: NODE_TYPE
	 */
	public static final String NODE_TYPE = "NODE_TYPE";

	/**
	 * CytoscapeAttribute: EDGE_TYPE
	 * 
	 */
	public static final String EDGE_TYPE = "EDGE_TYPE";

	public static final String DEFAULT_NODE = "DefaultNode";

	public static final String DEFAULT_EDGE = "DefaultEdge";

	/**
	 * attribute used to set NODE_TYPE
	 */
	protected String nodeAttributeName = NODE_TYPE;

	/**
	 * value for attribute used in setting NODE_TYPE
	 */
	protected String nodeAttributeValue = DEFAULT_NODE;

	/**
	 * attribute used to set EDGE_TYPE
	 */
	protected String edgeAttributeName = EDGE_TYPE;

	/**
	 * value for attribute used in setting EDGE_TYPE
	 */
	protected String edgeAttributeValue = DEFAULT_EDGE;

	/**
	 * the node being labeled via the _nodeLabeler
	 */
	CyNode _nodeBeingLabeled;

	/**
	 * the NodeView that corresponds to the node being labeled
	 */ 
	NodeView _nodeViewBeingLabeled;

	/**
	 * component that contains the nodeLabeler
	 */
	JPanel _nodeLabelerPanel;

	CytoscapeEditor _caller;
	
	/*
	 * for drawing rubberbanded lines
	 */
	double saveX1 = Double.MIN_VALUE;
	double saveY1 = Double.MIN_VALUE;
	double saveX2 = Double.MIN_VALUE;
	double saveY2 = Double.MIN_VALUE;

	/**
	 * flag that indicates whether we are currently in the process of handling a
	 * dropped edge TODO: handling dropped edges should probably be moved to the
	 * PaletteNetworkEditEventHandler
	 */
	public boolean handlingEdgeDrop = false;

	public BasicNetworkEditEventHandler() {
		// setMode(SELECT_MODE);
		// locator = new PNodeLocator(new PNode());
		// setEventFilter(new PInputEventFilter(InputEvent.BUTTON1_MASK));

		_nodeBeingLabeled = null;
		_nodeViewBeingLabeled = null;
		_nodeLabelerPanel = new JPanel();
	}

	/**
	 * 
	 * @param caller
	 */
	public BasicNetworkEditEventHandler(CytoscapeEditor caller) {
		this();
		_caller = caller;
	}

	/**
	 * 
	 * @param caller
	 * @param view
	 */
	public BasicNetworkEditEventHandler(CytoscapeEditor caller,
			CyNetworkView view) {
		this();
		_caller = caller;
		// this.setView((PGraphView) view);
		this.setView((DGraphView) view);
	}

	/**
	 * 
	 */
	// public PCanvas getCanvas() {
	public InnerCanvas getCanvas() {
		return canvas;
	}

	/**
	 * The <b>mousePressed() </b> method is at the heart of the basic Cytoscape
	 * editor. If the editor is in "ADD_MODE" due to a mouse press on the 'Add
	 * node' button, subsequent mousePressed() events on the canvas create nodes
	 * with default labels. The default label appears in an editable text field
	 * and can be edited.
	 * <p>
	 * If the editor is in "CONNECT_MODE", then user clicks when over the
	 * desired source node, moves the mouse to the desired target node, and
	 * clicks the mouse when over the desired target node.
	 * <p>
	 * There are also accelerators for modeless addition of nodes and edges.
	 * Control-clicking at a position on the canvas creates a node with default
	 * label in that position. The default label appears in an editable text
	 * field, so the user can edit its name immediately by just beginning to
	 * type. Hit ENTER or click (or control-click) anywhere outside the field,
	 * and the edited field is assigned as the label for the node.
	 * Control-clicking on a node on the canvas starts an edge with source at
	 * that node. Move the cursor and a rubber-banded line follows the cursor.
	 * As the cursor passes over another node, that node is highlighted and the
	 * rubber-banded line will snap to a connection point on that second node.
	 * Control-click the mouse again and the connection is established.
	 * 
	 * @param e
	 *            inputEvent for mouse pressed
	 * @see BasicCytoscapeEditor
	 */

	// AJK: 04/15/06 rewritten for Cytoscape 2.3 renderer
	// public void mousePressed(PInputEvent e) {
	public void mousePressed(MouseEvent e) {
		// TODO: break this into smaller routines
		// nextPoint = e.getPosition();
		nextPoint = e.getPoint();
		NodeView nv = null;


		System.out
				.println("Mouse pressed at viewport coordinate: " + nextPoint);

		boolean onNode = false;
		// AJK: 04/15/06 for Cytoscape 2.3 renderer
		// if (e.getPickedNode() instanceof NodeView) {
		nv = view.getPickedNodeView(nextPoint);
		System.out.println("Picked NodeView = " + nv);
		System.out.println("Edge started = " + edgeStarted);
		System.out.println("node = " + node);
		if (nv != null) {
			onNode = true;
			// locator.setNode(e.getPickedNode());
			// locator.locatePoint(nextPoint);

			// nextPoint = e.getPickedNode().localToGlobal(nextPoint);
		}

		if (onNode && !edgeStarted && (e.isControlDown())) {
			// begin edge creation
			beginEdge(nextPoint, nv);
		} else if (onNode && edgeStarted && (nv != node)) {
			System.out.println ("calling finishEdge for NodeView " + nv);
			// Finish Edge Creation
			finishEdge(nextPoint, nv);
			edgeStarted = false;
			onNode = false;

		} else if (!onNode && edgeStarted) // turn off rubberbanding if clicked
		// on empty area of canvas
		{
			edgeStarted = false;
			saveX1 = Double.MIN_VALUE;
			saveX2 = Double.MIN_VALUE;
			saveY1 = Double.MIN_VALUE;
			saveY2 = Double.MIN_VALUE;
			this.setHandlingEdgeDrop(false);
		}

		else if (!onNode && !edgeStarted && (e.isControlDown())) {
			createNode(nextPoint);
		} else // clicking anywhere on screen will turn off node Labeling
		{
//			super.mousePressed(e);
		}
	}
	
	public void keyPressed (KeyEvent e)
	{
		int keyVal = e.getKeyCode();
		System.out.println("Key code for typed key = " + keyVal);
		System.out.println("VK_ESCAPE = " + KeyEvent.VK_ESCAPE);
		
		if (keyVal == KeyEvent.VK_ESCAPE)
		{
			if (edgeStarted) // turn off rubberbanding if clicked
			// on empty area of canvas
			{
				edgeStarted = false;
				saveX1 = Double.MIN_VALUE;
				saveX2 = Double.MIN_VALUE;
				saveY1 = Double.MIN_VALUE;
				saveY2 = Double.MIN_VALUE;
				this.setHandlingEdgeDrop(false);
			}			
		}
	}

//	public NodeView getPickedNodeView(Point2D pt) {
//		NodeView nv = null;
//		double[] locn = new double[2];
//		locn[0] = pt.getX();
//		locn[1] = pt.getY();
//		int chosenNode = 0;
//		this.getView().xformComponentToNodeCoords(locn);
//
//		final IntStack nodeStack = new IntStack();
//		this
//				.getView()
//				.getNodesIntersectingRectangle(
//						(float) locn[0],
//						(float) locn[1],
//						(float) locn[0],
//						(float) locn[1],
//						(canvas.getLastRenderDetail() & GraphRenderer.LOD_HIGH_DETAIL) == 0,
//						nodeStack);
//
//		chosenNode = (nodeStack.size() > 0) ? nodeStack.peek() : 0;
//		if (chosenNode != 0) {
//			nv = this.getView().getNodeView(chosenNode);
//		}
//
//		return nv;
//	}

	/**
	 * begin drawing an edge from the input point
	 * 
	 * 
	 * @param location   works in Canvas coordinates
	 * 
	 */
	public void beginEdge(Point2D location, NodeView nv) {
		edgeStarted = true;
		// node = (NodeView) e.getPickedNode();
		node = nv;
		// edge = new PPath();
		// getCanvas().getLayer().addChild(edge);

		// edge.setStroke(new PFixedWidthStroke(3));
		// edge.setPaint(Color.black);
		startPoint = location;
		updateEdge();

		// setMode(CONNECT_MODE);
		setEdgeStarted(true);
		// setEdge(edge);
		setStartPoint(startPoint);

	}

	/**
	 * finish edge on node containing input pointf
	 * 
	 * @param location works in Canvas coordinates
	 */
	public CyEdge finishEdge(Point2D location, NodeView target) {
		// System.out.println("finishEdge in BasicNetworkEventHandler");
		edgeStarted = false;
		updateEdge();
		
		saveX1 = Double.MIN_VALUE;
		saveX2 = Double.MIN_VALUE;
		saveY1 = Double.MIN_VALUE;
		saveY2 = Double.MIN_VALUE;

		// From the Pick Path
		// NodeView target = (NodeView) e.getPickedNode();
		// From Earlier
		NodeView source = node;

		Node source_node = source.getNode();
		Node target_node = target.getNode();

		CyEdge myEdge = _caller.addEdge(source_node, target_node,
				cytoscape.data.Semantics.INTERACTION,
				// "default", true, this.DEFAULT_EDGE);
				"default", true, (this.getEdgeAttributeValue() != null) ? this
						.getEdgeAttributeValue() : this.DEFAULT_EDGE);

		// Cytoscape.getCurrentNetwork().restoreEdge(myEdge);

		// getCanvas().getLayer().removeChild(edge);
		edge = null;
		node = null;
		// setMode(SELECT_MODE);
		if (isHandlingEdgeDrop()) {

			this.setHandlingEdgeDrop(false);
		}
		// AJK: 11/19/05 invert selection of target, which will have had its
		// selection inverted upon mouse entry
		target.setSelected(!target.isSelected());
		this.getCanvas().repaint();
		// redraw graph so that the correct arrow is shown (but only if network
		// is small enough to see the edge...
		if (Cytoscape.getCurrentNetwork().getNodeCount() <= 500) {
			Cytoscape.getCurrentNetworkView().redrawGraph(true, true);

		}

		return myEdge;
	}

	/**
	 * create a new node at the point where mouse was pressed 
	 * 
	 * @param location    point of mouse press (in Canvas coordinates)
	 */
	public CyNode createNode(Point2D location) {
		CyNode cn = null;
		// if ((getMode() == ADD_MODE) || (e.isControlDown())) {
		// add a node
		cn = _caller.addNode("node" + counter, this.getNodeAttributeName(),
				this.getNodeAttributeValue());

		// } else if (getMode() == LABEL_MODE) {

		// add a freestanding label
		// functionality not available in Cytoscape 2.2
		// cn = CytoscapeEditorManager.addNode("node" + counter, "Label");
		// }

		counter++;
		double zoom = Cytoscape.getCurrentNetworkView().getZoom();
		// Cytoscape.getCurrentNetwork().restoreNode(cn);
		NodeView nv = Cytoscape.getCurrentNetworkView().getNodeView(cn);
//		nv.setOffset(location.getX(), location.getY());
		double [] nextLocn = new double[2]; 
		nextLocn[0] = nextPoint.getX();
		nextLocn[1] = nextPoint.getY();
		view.xformComponentToNodeCoords(nextLocn);
		nv.setOffset (nextLocn[0], nextLocn[1]);
		nv.setToolTip(cn.getIdentifier());
		// do node labeling
		// _nodeBeingLabeled = cn;
		// _nodeViewBeingLabeled = nv;
		// nv.setOffset(nextPoint.getX(), nextPoint.getY());

		// canvas.add(_nodeLabelerPanel);
		// _nodeLabelerPanel.setVisible(true);
		// _nodeLabeler.setText(cn.getIdentifier());

		// AJK: 09/16/05 disable node labeler, rely on attribute editor/browser
		// initializeNodeLabeler(cn, nv);
		
		// AJK: 04/26/06 BEGIN
		//     set tooltipText  (a test)
//		((DNodeView) nv).setToolTip(cn.getIdentifier());
		nv.setToolTip(cn.getIdentifier());
//		System.out.println("Setting tooltip text on nodeView: " + nv);
//		System.out.println("   to: " + ((DNodeView) nv).getToolTip());
		// AJK: 04/26/06 END
		
		return cn;
	}

	/**
	 * updates rendering of edge if an edge is under construction
	 */
	// public void mouseMoved(PInputEvent e) {
	public void mouseMoved(MouseEvent e) {
//		super.mouseMoved(e);
		nextPoint = e.getPoint();
//		System.out.println ("mouse moved to " + e.getPoint() + ", EdgeStarted = " + edgeStarted);
		if (edgeStarted) {
			// we need to update the latest section of the edge
			updateEdge();
		}
//		NodeView nv = getPickedNodeView (nextPoint);
//		if (nv != null)
//		{
//			node = nv;
//	
//		}
//		if (e.getPickedNode() instanceof NodeView) {
//
//			final PNode node = e.getPickedNode();
//		}
	}

	/**
	 * if hovering over a node, then highlight the node by temporarily
	 * inverting its selection
	 */
//	public void mouseEntered(PInputEvent e) {
//		if (e.getPickedNode() instanceof NodeView) {
//			NodeView nv = (NodeView) e.getPickedNode();
	public void mouseEntered (MouseEvent e)
	{
		Point2D location = e.getPoint();
	    NodeView nv = view.getPickedNodeView (location);
	    if (nv != null)
	    {
			if (edgeStarted) {
				nv.setSelected(!nv.isSelected());
			}

			// Float borderWidth = new Float(CytoscapeEditorManager
			// .getDefaultBorderWidth());
			// if (borderWidth.equals(new Float(Float.NaN))) {
			// // global variable not yet set; only set it once
			// CytoscapeEditorManager.setDefaultBorderWidth(nv
			// .getBorderWidth());
			// }
			// nv.setBorderWidth(3 * CytoscapeEditorManager
			// .getDefaultBorderWidth());
			this.getCanvas().repaint();
		}
	}

	/**
	 * revert temporary node highlighting that was done upon MouseEnter
	 */
	public void mouseExited(MouseEvent e) {
		Point2D location = e.getPoint();
	    NodeView nv = view.getPickedNodeView (location);
	    if (nv != null) {
//		if (e.getPickedNode() instanceof NodeView) {
//			NodeView nv = (NodeView) e.getPickedNode();
			// nv.setBorderWidth(CytoscapeEditorManager.getDefaultBorderWidth());
			if (edgeStarted) {
				nv.setSelected(!nv.isSelected());
			}
			this.getCanvas().repaint();
		}
	}

//	public void mouseDragged(PInputEvent e) {
    public void mouseDragged (MouseEvent e) {
		nextPoint = e.getPoint();

		boolean onNode = false;
		Point2D location = e.getPoint();
	    NodeView nv = view.getPickedNodeView (location);
		
	    if (nv != null) {
//		if (e.getPickedNode() instanceof NodeView) {
			onNode = true;
//			locator.setNode(e.getPickedNode());
//			locator.locatePoint(nextPoint);
//			nextPoint = e.getPickedNode().localToGlobal(nextPoint);
		}

		if (onNode && !edgeStarted && (e.isControlDown())) {
			// begin edge creation
			beginEdge(nextPoint, nv);
		}
		if (!edgeStarted) {

			// super.mouseDragged(e);
		}
		if (edgeStarted) {
			// we need to update the latest section of the edge
//			nextPoint = e.getPosition();
			updateEdge();
		}
//		if (e.getPickedNode() instanceof NodeView) {
			// System.out.println("mouse dragged on: " + e.getPickedNode());
//		}
	}

	/**
	 * updates the rubberbanded edge line as the mouse is moved, works in Canvas coordinates
	 */
	public void updateEdge() {
		double x1 = startPoint.getX();
		double y1 = startPoint.getY();
		double x2 = nextPoint.getX();
		double y2 = nextPoint.getY();
		double lineLen = Math.sqrt(((x2 - x1) * (x2 - x1))
				+ ((y2 - y1) * (y2 - y1)));
		double offset = 5;

		if (lineLen == 0)
			lineLen = 1;

		y2 = y2 + (((y1 - y2) / lineLen) * offset);
		x2 = x2 + (((x1 - x2) / lineLen) * offset);

		nextPoint.setLocation(x2, y2);

//		edge.setPathToPolyline(new Point2D[] { startPoint, nextPoint });
		Color saveColor = canvas.getGraphics().getColor();
		
		// draw a line of width 3
//		canvas.getGraphics().fillRect(((int) x1) - 1,
//				((int) y1) - 1, ((int) x2) + 1, ((int) y2) + 1);
//		Cytoscape.getCurrentNetworkView().redrawGraph(true, true);
		if (saveX1 != Double.MIN_VALUE)
		{
			canvas.getGraphics().setColor(canvas.getBackground());
			canvas.getGraphics().drawLine(((int) saveX1) - 1,
					((int) saveY1) - 1, ((int) saveX2) + 1, ((int) saveY2) + 1);
		}
		
		
		canvas.update(canvas.getGraphics());
		canvas.getGraphics().setColor(Color.BLACK);
		canvas.getGraphics().drawLine(((int) x1) - 1,
				((int) y1) - 1, ((int) x2) + 1, ((int) y2) + 1);
		canvas.getGraphics().setColor(saveColor);
		
		
		saveX1 = x1;
		saveX2 = x2;
		saveY1 = y1;
		saveY2 = y2;
		
		
	}

	// AJK: 05/22/05 END
	/**
	 * @return Returns the mode.
	 */
	public int getMode() {
		return mode;
	}

	/**
	 * @param mode
	 *            The mode to set.
	 */
	public void setMode(int mode) {
		this.mode = mode;
		// System.out.println("drop mode set to: " + mode);
	}

	/**
	 * 
	 * MultiHashMapListener methods
	 */
	public void attributeValueAssigned(java.lang.String objectKey,
			java.lang.String attributeName, java.lang.Object[] keyIntoValue,
			java.lang.Object oldAttributeValue,
			java.lang.Object newAttributeValue) {
		// System.out.println("attributeValueAssigned: " + newAttributeValue);
		CyNetworkView view = Cytoscape.getCurrentNetworkView();
		if (view != null) {
			// view.redrawGraph(true, true);
		}
	}

	public void attributeValueRemoved(java.lang.String objectKey,
			java.lang.String attributeName, java.lang.Object[] keyIntoValue,
			java.lang.Object attributeValue) {

	}

	public void allAttributeValuesRemoved(java.lang.String objectKey,
			java.lang.String attributeName) {

	}

	/**
	 * @return Returns the edge.
	 */
	public PPath getEdge() {
		return edge;
	}

	/**
	 * @param edge
	 *            the edge to set.
	 */
	public void setEdge(PPath edge) {
		this.edge = edge;
	}

	/**
	 * @return flag indicating whether an edge is under construction
	 */
	public boolean isEdgeStarted() {
		return edgeStarted;
	}

	/**
	 * set the flag that indicates whether an edge is under construction
	 * 
	 * @param edgeStarted
	 * 
	 */
	public void setEdgeStarted(boolean edgeStarted) {
		this.edgeStarted = edgeStarted;
	}

	// AJK: 04/15/06 BEGIN
	//
	// /**
	// * @return Returns the locator.
	// */
	// public PNodeLocator getLocator() {
	// return locator;
	// }
	//
	// /**
	// * @param locator
	// * The locator to set.
	// *
	// */
	// public void setLocator(PNodeLocator locator) {
	// this.locator = locator;
	// }
	// AJK: 04/15/06 END

	/**
	 * @return Returns the nextPoint.
	 */
	public Point2D getNextPoint() {
		return nextPoint;
	}

	/**
	 * @param nextPoint
	 *            The nextPoint to set
	 * 
	 */
	public void setNextPoint(Point2D nextPoint) {
		this.nextPoint = nextPoint;
	}

	/**
	 * @return Returns the node.
	 */
	public NodeView getNode() {
		return node;
	}

	/**
	 * @param node
	 *            The node to set.
	 * 
	 */
	public void setNode(NodeView node) {
		this.node = node;
	}

	/**
	 * @return Returns the startPoint.
	 */
	public Point2D getStartPoint() {
		return startPoint;
	}

	/**
	 * @param startPoint
	 *            The startPoint to set.
	 * 
	 */
	public void setStartPoint(Point2D startPoint) {
		this.startPoint = startPoint;
	}

	/**
	 * @return Returns the view.
	 */
	// AJK: 04/15/06 for Cytoscape 2.3 renderer
	// public PGraphView getView() {
	public DGraphView getView() {
		return view;
	}

	/**
	 * @param view
	 *            The view to set.
	 * 
	 */
	// AJK: 04/15/06 for Cytoscape 2.3 renderer
	// public void setView(PGraphView view) {
	public void setView(DGraphView view) {
		this.view = view;
	}

	/**
	 * @return Returns the flag that indicates whether we are handling the drop
	 *         of an edge onto the canvas TODO: move edge drop handling into
	 *         PaletteNetworkEditEventHandler
	 */
	public boolean isHandlingEdgeDrop() {
		return handlingEdgeDrop;
	}

	/**
	 * @param handlingEdgeDrop
	 *            sets the flag that indicates whether we are handling the drop
	 *            of an edge onto the canvas
	 * 
	 * 
	 */
	public void setHandlingEdgeDrop(boolean handlingEdgeDrop) {
		this.handlingEdgeDrop = handlingEdgeDrop;
	}

	/**
	 * starts up the event handler on the input network view adds an input event
	 * listener to the view's canvas
	 * 
	 * @param view
	 *            a Cytoscape network view
	 */
	// AJK: 04/15/06 for Cytoscape 2.3 renderer
	// public void start(PGraphView view) {
	public void start(DGraphView view) {
		this.view = view;
		this.canvas = view.getCanvas();
		// canvas.addInputEventListener(this);
		System.out.println("Started event listener: " + this);
		canvas.addMouseListener(this);
		canvas.addMouseMotionListener(this);
		canvas.addKeyListener(this);
	}

	/**
	 * stops the event handler by removing the input event listener from the
	 * canvas this is called when the user switches between editors
	 * 
	 */
	public void stop() {
		if (canvas != null) {
			// AJK: 04/15/06 for Cytoscape 2.3 renderer
			// canvas.removeInputEventListener(this);
			System.out.println("stopped event listener: " + this);
			canvas.removeMouseListener(this);
			canvas.removeMouseMotionListener(this);
			Cytoscape.getDesktop().removeKeyListener(this);
			this.view = null;
			this.canvas = null;
		}
	}

	/**
	 * @return Returns the edgeAttributeValue.
	 */
	public String getEdgeAttributeValue() {
		return edgeAttributeValue;
	}

	/**
	 * @param edgeAttributeValue
	 *            The edgeAttributeValue to set.
	 */
	public void setEdgeAttributeValue(String edgeAttributeValue) {
		this.edgeAttributeValue = edgeAttributeValue;
	}

	/**
	 * @return Returns the nodeAttributeName.
	 */
	public String getNodeAttributeName() {
		return nodeAttributeName;
	}

	/**
	 * @param nodeAttributeName
	 *            The nodeAttributeName to set.
	 */
	public void setNodeAttributeName(String nodeAttributeName) {
		this.nodeAttributeName = nodeAttributeName;
	}

	/**
	 * @return Returns the edgeAttributeName.
	 */
	public String getEdgeAttributeName() {
		return edgeAttributeName;
	}

	/**
	 * @param edgeAttributeName
	 *            The edgeAttributeName to set.
	 */
	public void setEdgeAttributeName(String edgeAttributeName) {
		this.edgeAttributeName = edgeAttributeName;
	}

	/**
	 * @return Returns the nodeAttributeValue.
	 */
	public String getNodeAttributeValue() {
		return nodeAttributeValue;
	}

	/**
	 * @param nodeAttributeValue
	 *            The nodeAttributeValue to set.
	 */
	public void setNodeAttributeValue(String nodeAttributeValue) {
		this.nodeAttributeValue = nodeAttributeValue;
	}

	/**
	 * @return Returns the _caller.
	 */
	public CytoscapeEditor get_caller() {
		return _caller;
	}

	/**
	 * @param _caller
	 *            The _caller to set.
	 */
	public void set_caller(CytoscapeEditor _caller) {
		this._caller = _caller;
	}
}