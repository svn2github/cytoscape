/*
 * Created on Jul 31, 2005
 *
 */
package cytoscape.editor.event;

import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.event.PInputEventFilter;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolox.util.PFixedWidthStroke;
import edu.umd.cs.piccolox.util.PNodeLocator;
import giny.model.Node;
import giny.view.NodeView;

import java.awt.Color;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.geom.Point2D;

import javax.swing.JPanel;

import phoebe.PGraphView;
import cytoscape.CyEdge;
import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.editor.CytoscapeEditor;
import cytoscape.editor.CytoscapeEditorManager;
import cytoscape.editor.editors.BasicCytoscapeEditor;

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
 */
public class BasicNetworkEditEventHandler extends NetworkEditEventAdapter
		implements ActionListener, cytoscape.data.attr.MultiHashMapListener {

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
	protected PCanvas canvas;

	/**
	 * the current network view
	 */
	protected PGraphView view;

	PNodeLocator locator;

	/**
	 * counter variable used in setting unique names for nodes
	 */
	protected static int counter = 0;

	// AJK: 05/19/05 BEGIN
	//   edit modes
	public final int ADD_MODE = 1;

	public final int CONNECT_MODE = 2;

	public final int LABEL_MODE = 3;

	public final int SELECT_MODE = 0;

	public int mode = SELECT_MODE;


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

	/**
	 * CytoscapeAttribute: NODE_TYPE
	 */
	public static final String NODE_TYPE = "NODE_TYPE";

	/**
	 * CytoscapeAttribute: EDGE_TYPE
	 *  
	 */
	public static final String EDGE_TYPE = "EDGE_TYPE";

	/**
	 * flag that indicates whether we are currently in the process of handling a
	 * dropped edge TODO: handling dropped edges should probably be moved to the
	 * PaletteNetworkEditEventHandler
	 */
	public boolean handlingEdgeDrop = false;

	public BasicNetworkEditEventHandler() {
		setMode(SELECT_MODE);
		locator = new PNodeLocator(new PNode());
		setEventFilter(new PInputEventFilter(InputEvent.BUTTON1_MASK));

		_nodeBeingLabeled = null;
		_nodeViewBeingLabeled = null;
		_nodeLabelerPanel = new JPanel();
	}

	public BasicNetworkEditEventHandler(CytoscapeEditor caller) {
		this();
		_caller = caller;
	}

	/**
	 *  
	 */
	public PCanvas getCanvas() {
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
	 * @param e inputEvent for mouse pressed
	 * @see BasicCytoscapeEditor
	 */
	public void mousePressed(PInputEvent e) {
		// TODO: break this into smaller routines
		nextPoint = e.getPosition();
//		System.out.println("Mouse pressed at: " + e.getPosition());
		if ((e.isControlDown()) || (getMode() == ADD_MODE)
				|| (getMode() == LABEL_MODE) || (getMode() == CONNECT_MODE))
		{
			// user wants to add a node or an edge
			boolean onNode = false;
			if (e.getPickedNode() instanceof NodeView) {
				onNode = true;
				locator.setNode(e.getPickedNode());
				locator.locatePoint(nextPoint);
				nextPoint = e.getPickedNode().localToGlobal(nextPoint);
			}
			if (onNode && !edgeStarted
					&& (e.isControlDown() || (getMode() == CONNECT_MODE))) {
				// begin edge creation
				beginEdge(e);
			} else if (onNode && edgeStarted && (e.getPickedNode() != node)) {
				// Finish Edge Creation
				finishEdge(e);

			} else if (!onNode && edgeStarted)  // turn off rubberbanding if clicked on empty area of canvas
			{
				edgeStarted = false;
				this.getCanvas().getLayer().removeChild(edge);				
				setMode(SELECT_MODE);
				this.setHandlingEdgeDrop(false);
			}
			
			  else if (!onNode && !edgeStarted && (getMode() != CONNECT_MODE)) {
				// Create a Node on Click
				createNode (e);
			}				
		}
		// doubleclick sets up node labeling
		// TODO: perhaps hook doubleclick into attribute editor/browser
		else if (e.getClickCount() >= 2) // open node for labeling
		{
			// AJK: 09/16/05 disable node labeler, rely on attribute editor/browser
//			labelNode(e, nextPoint);
		}
		else // clicking anywhere on screen will turn off node Labeling
		{
//			 AJK: 09/16/05 disable node labeler, rely on attribute editor/browser
//			clearNodeLabeler();
//			System.out.println ("Calling mouse pressed on class " + super.toString());
			super.mousePressed(e);
		}
	}
	

	/**
	 * begin drawing an edge from the node containing input point
	 * @param e input event for mouse press
	 */
	public void beginEdge(PInputEvent e)
	{
		edgeStarted = true;
		node = (NodeView) e.getPickedNode();
		edge = new PPath();
		getCanvas().getLayer().addChild(edge);

		edge.setStroke(new PFixedWidthStroke(3));
		edge.setPaint(Color.black);
		startPoint = nextPoint;
		updateEdge();		
	}
	
	
	/**
	 * finish edge on node containing input point
	 * @param e input event for mouse press
	 */
	public CyEdge finishEdge (PInputEvent e)
	{
		edgeStarted = false;
		updateEdge();

		// From the Pick Path
		NodeView target = (NodeView) e.getPickedNode();
		// From Earlier
		NodeView source = node;

		Node source_node = source.getNode();
		Node target_node = target.getNode();

		CyEdge myEdge = CytoscapeEditorManager.addEdge(source_node,
				target_node, cytoscape.data.Semantics.INTERACTION,
				"default", true, "DefaultEdge");

		//				Cytoscape.getCurrentNetwork().restoreEdge(myEdge);

		getCanvas().getLayer().removeChild(edge);
		edge = null;
		node = null;
		if (isHandlingEdgeDrop()) {
			setMode(SELECT_MODE);
			this.setHandlingEdgeDrop(false);
		}
		return myEdge;
	}
	
	/**
	 * create a new node at the point where mouse was pressed
	 * @param e event for mouse press
	 */
	public CyNode createNode (PInputEvent e)
	{
		CyNode cn = null;
		if ((getMode() == ADD_MODE) || (e.isControlDown())) {
			// add a node
			cn = CytoscapeEditorManager.addNode("node" + counter, true,
					"DefaultNode");

		} else if (getMode() == LABEL_MODE) {

			// add a freestanding label
			// functionality not available in Cytoscape 2.2
			cn = CytoscapeEditorManager.addNode("node" + counter, true,
					"Label");
		}

		counter++;
		double zoom = Cytoscape.getCurrentNetworkView().getZoom();
		//				Cytoscape.getCurrentNetwork().restoreNode(cn);
		NodeView nv = Cytoscape.getCurrentNetworkView().getNodeView(cn);

		// do node labeling
//		_nodeBeingLabeled = cn;
//		_nodeViewBeingLabeled = nv;
		nv.setOffset(nextPoint.getX(), nextPoint.getY());
//		canvas.add(_nodeLabelerPanel);
//		_nodeLabelerPanel.setVisible(true);
//		_nodeLabeler.setText(cn.getIdentifier());

//		 AJK: 09/16/05 disable node labeler, rely on attribute editor/browser
//		initializeNodeLabeler(cn, nv);	
		return cn;
	}

	/**
	 * updates rendering of edge if an edge is under construction
	 */
	public void mouseMoved(PInputEvent e) {
		super.mouseMoved(e);
		if (edgeStarted) {
			//we need to update the latest section of the edge
			nextPoint = e.getPosition();
			updateEdge();
		}
		if (e.getPickedNode() instanceof NodeView) {

			final PNode node = e.getPickedNode();
		}
	}

	/**
	 * if hovering over a node, then highlight the node by temporarily
	 * thickening its border
	 */
	public void mouseEntered(PInputEvent e) {
		if (e.getPickedNode() instanceof NodeView) {
			NodeView nv = (NodeView) e.getPickedNode();

			Float borderWidth = new Float(CytoscapeEditorManager
					.getDefaultBorderWidth());
			if (borderWidth.equals(new Float(Float.NaN))) {
				// global variable not yet set; only set it once
				CytoscapeEditorManager.setDefaultBorderWidth(nv
						.getBorderWidth());
			}
			nv.setBorderWidth(3 * CytoscapeEditorManager
					.getDefaultBorderWidth());
			this.getCanvas().repaint();
		}
	}

	/**
	 * if a node was being highlighted due to mouseEnter() event, then reset its
	 * width back to default width
	 */
	public void mouseExited(PInputEvent e) {
		if (e.getPickedNode() instanceof NodeView) {
			NodeView nv = (NodeView) e.getPickedNode();
			nv.setBorderWidth(CytoscapeEditorManager.getDefaultBorderWidth());
			this.getCanvas().repaint();
		}
	}

	public void mouseDragged(PInputEvent e) {
		if (!edgeStarted) {

			super.mouseDragged(e);
		}
		if (edgeStarted) {
			//we need to update the latest section of the edge
			nextPoint = e.getPosition();
			updateEdge();
		}
		if (e.getPickedNode() instanceof NodeView) {
		}
	}
	


	/**
	 * updates the rubberbanded edge line as the mouse is moved
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

		edge.setPathToPolyline(new Point2D[] { startPoint, nextPoint });
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
//		System.out.println("drop mode set to: " + mode);
	}


	/**
	 * 
	 * MultiHashMapListener methods
	 */
	public void attributeValueAssigned(java.lang.String objectKey,
			java.lang.String attributeName, java.lang.Object[] keyIntoValue,
			java.lang.Object oldAttributeValue,
			java.lang.Object newAttributeValue) {
//		System.out.println("attributeValueAssigned: " + newAttributeValue);
		Cytoscape.getCurrentNetworkView().redrawGraph(true, true);

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

	/**
	 * @return Returns the locator.
	 */
	public PNodeLocator getLocator() {
		return locator;
	}

	/**
	 * @param locator
	 *            The locator to set.
	 *  
	 */
	public void setLocator(PNodeLocator locator) {
		this.locator = locator;
	}

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
	public PGraphView getView() {
		return view;
	}

	/**
	 * @param view
	 *            The view to set.
	 *  
	 */
	public void setView(PGraphView view) {
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
	public void start(PGraphView view) {
		this.view = view;
		this.canvas = view.getCanvas();
		canvas.addInputEventListener(this);
	}

	/**
	 * stops the event handler by removing the input event listener from the
	 * canvas this is called when the user switches between editors
	 *  
	 */
	public void stop() {
		if (canvas != null) {
			canvas.removeInputEventListener(this);
			this.view = null;
			this.canvas = null;
		}
	}
}