package cytoscape.editor.editors;

import giny.model.Node;
import giny.view.EdgeView;
import giny.view.NodeView;

import java.awt.Cursor;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.MenuElement;

import cytoscape.CyEdge;
import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.CytoscapeModifiedNetworkManager;
import cytoscape.data.CyAttributes;
import cytoscape.data.FlagEvent;
import cytoscape.data.FlagEventListener;
import cytoscape.data.Semantics;
import cytoscape.editor.CytoscapeEditor;
import cytoscape.editor.CytoscapeEditorManager;
import cytoscape.editor.actions.DeleteAction;
import cytoscape.editor.event.BasicNetworkEditEventHandler;
import cytoscape.giny.PhoebeNetworkView;
import cytoscape.util.CytoscapeAction;
import cytoscape.util.CytoscapeToolBar;
import cytoscape.view.CyMenus;
import cytoscape.view.CyNetworkView;
import ding.view.EdgeContextMenuListener;
import ding.view.NodeContextMenuListener;

/**
 * The <b>BasicCytoscapeEditor</b> provides base level graph editing functionality for Cytoscape version 2.2
 * provides a “node” button on the Cytoscape toolbar.  Click on the “node” button and cursor takes on a 
 * rectangular shape, the system goes into "ADD_MODE", and subsequent clicking of mouse on canvas creates 
 * nodes with default labels.  
 * The default label appears in an editable text field and can be edited.
 * <p>
 * Provides a "connect" button for the toolbar that puts the user in “CONNECT_MODE” mode, 
 * wherein the cursor changes to some form of “connector” icon.  The user clicks when over the desired source 
 * node, moves the mouse to the desired target node, and clicks the mouse when over the desired target node.
 * <p>
 * Provides an "Edit => Connect Selected Nodes" menu item that, when chosen, 
 * creates a clique amongst the selected nodes.  
 * <p> 
 * Going back out of “ADD_MODE" or "CONNECT_MODE is accomplished by pressing the “select” button on the toolbar
 * <p>
 * Provides accelerators for modeless addition of nodes and edges.  
 * Control-clicking at a position on the canvas creates a node with default label in that position.  
 * The default label appears in an editable text field, so the user can edit its name immediately by just 
 * beginning to type.  Hit ENTER or click (or control-click) anywhere outside the field, and the edited field 
 * is assigned as the label for the node.
 * Control-clicking on a node on the canvas starts an edge with source at that node.  Move the cursor and a 
 * rubber-banded line follows the cursor.  As the cursor passes over another node, that node is
 * highlighted and the rubber-banded line will snap to a connection point on that second node.  
 * Control-click the mouse again and the connection is established.  
 * <p>
 * Provides functionality for deleting selected nodes and edges and an undo/redo framework for deletion of nodes 
 * and edges
 * 
 * @author Allan Kuchinsky, Agilent Technologies
 * @version 1.0
 * @see BasicNetworkEditEventHandler
 *  
 */
public class BasicCytoscapeEditor implements CytoscapeEditor, FlagEventListener
    , NodeContextMenuListener 
    , EdgeContextMenuListener {

	/**
	 * name and type of the editor
	 */
	protected String editorName;


	/**
	 * pointer to Cytoscape menus
	 */
	CyMenus _cyMenus;

	/**
	 * pointer to Cytoscape toolbar
	 */
	CytoscapeToolBar _toolBar;

	/**
	 * current network view being edited
	 */
	CyNetworkView view;

	boolean DEBUG = false;

	/**
	 * customized cursor associated with "CONNECT MODE"
	 */
	Cursor _edgeCursor;

	/**
	 * customized cursor associated with "ADD MODE"
	 */
	Cursor _nodeCursor;

	/**
	 * customized cursor associated with mode for adding freestanding labels
	 * <b>not</b> used in Cytoscape 2.2
	 */
	Cursor _labelCursor;

	/**
	 * 32x32 image for node cursor
	 */
	Image _nodeCursorImage;

	/**
	 * 32x32 image for edge cursor
	 */
	Image _connectionCursorImage;


	/**
	 * default cursor used by Cytoscape
	 */
	Cursor _originalCursor;

	JButton _addNodeButton, _addEdgeButton, _resetCursorButton,
			_addLabelButton;

	private static final String ICONS_REL_LOC = "images/";

	private static final String _connectedTitle = "Connect Selected Nodes";
	
    /**
     * Cytoscape Attribute:  BioPAX Name.
     */
    public static final String BIOPAX_NAME_ATTRIBUTE
            = "BIOPAX_NAME";



	/**
	 *  
	 */
	public BasicCytoscapeEditor() {
		super();
		
		// AJK: 04/27/06 for context menus
	}
	
	

	/**
	 * wrapper for adding a node in Cytoscape. This is intended to be called by
	 * the CytoscapeEditor in lieu of making direct modifications to the
	 * Cytoscape model. Thus, it provides an insulating level of abstraction
	 * between the CytoscapeEditor and the Cytoscape implementation, allowing
	 * for portability and extensibility of the editor.
	 * 
	 * this method will ensure that the node added is unique.  If it finds that 
	 * there is an existing node for <em>nodeName</em>, it will attempt to 
	 * generate a new, unique, <em>nodeName</em> by extending the <em>nodeName</em> 
	 * argument with a randomly generated extension.
	 * 
	 * @param nodeName
	 *            the name of the node to be created. This will be used as a
	 *            unique identifier for the node.
	 * @param attribute
	 *            a defining property for the node, that can be used in
	 *            conjunction with the Visual Mapper to assign visual
	 *            characteristics to different types of nodes. Also can be used,
	 *            by the canvas when handling a dropped item, to distinguish
	 *            between nodes and edges, so should be set to something like
	 *            "NodeType".
	 * @param value
	 *            the value of the attribute for this node. This can be used in
	 *            conjunction with the Visual Mapper to assign visual
	 *            characteristics to different types of nodes, for example to
	 *            assign a violet diamond shape to a 'smallMolecule' node type.
	 * @return the CyNode that has been either reused or created.
	 */
	public CyNode addNode(String nodeName, String attribute,
			String value) {
		CyNode cn = Cytoscape.getCyNode(nodeName, false); // first see if there is an existing node
		int iteration_limit = 100;
		while ((cn != null) && (iteration_limit > 0))
		{
			java.util.Date d1 = new java.util.Date();
			long t1 = d1.getTime();
			String s1 = Long.toString(t1);
			nodeName += "_" + 
					s1.substring(s1.length() - 3);  // append last 4 digits of time stamp to node name
			cn = Cytoscape.getCyNode(nodeName, false);
			iteration_limit--;
		}
		
		// check for unlikely error condition where we couldn't generate a unique node after a number of tries
		if (iteration_limit <= 0) {

			String expDescript = "Cytoscape Editor cannot generate a unique node for this network.  A serious internal error has occurred.  Please file a bug report at http://www.cytoscape.org.";
			String title = "Cannot generate a unique node";
			JOptionPane.showMessageDialog(Cytoscape.getDesktop(), expDescript,
					title, JOptionPane.PLAIN_MESSAGE);
			return null;

		}
		
		// now create a unique node
		cn = Cytoscape.getCyNode(nodeName, true);
		
		CyNetwork net = Cytoscape.getCurrentNetwork();
		if (attribute != null) {
			CytoscapeEditorManager.nodeAttribs.setAttribute(cn.getIdentifier(), attribute, value);
			if (attribute != CytoscapeEditorManager.NODE_TYPE)
			{
			     CytoscapeEditorManager.nodeAttribs.setAttribute(cn.getIdentifier(), CytoscapeEditorManager.NODE_TYPE, value);
			}
			// hack for BioPAX visual style
			CytoscapeEditorManager.nodeAttribs.setAttribute(cn.getIdentifier(), CytoscapeEditorManager.BIOPAX_NODE_TYPE, value);
//			String canonicalName = nodeAttribs.getStringAttribute(cn.getIdentifier(), 
//                      Semantics.CANONICAL_NAME);
//			System.out.println ("Got canonical name: " + canonicalName);
			CytoscapeEditorManager.nodeAttribs.setAttribute(cn.getIdentifier(),
					BIOPAX_NAME_ATTRIBUTE, 
//					canonicalName);
					cn.getIdentifier());
			net.restoreNode(cn);
		}

		// hack for BioPAX
		// TODO: move this hack for BIOPAX into the
		// PaletteNetworkEditEventHandler code
		/*
		 * if (attribute.equals("BIOPAX_NODE_TYPE")) {
		 * net.setNodeAttributeValue(cn, "BIOPAX_NAME", nodeName); }
		 */

		// AJK: 01/24/06 select the added node
		net.unFlagAllNodes();
		net.setFlagged(cn, true);
		
		CytoscapeEditorManager.manager.setupUndoableAdditionEdit(net, cn, null);
		Cytoscape.firePropertyChange(Cytoscape.NETWORK_MODIFIED, null, net);
		
		// AJK: 05/15/06 BEGIN
		//     set tooltip on the node's view
		NodeView nv = Cytoscape.getCurrentNetworkView().getNodeView(cn);
		
		nv.setToolTip(cn.getIdentifier());
		Cytoscape.getCurrentNetworkView().addNodeContextMenuListener(this);
		// AJK: 05/15/06 END

		
		return cn;
	}
	
	public void onFlagEvent (FlagEvent e)
	{
		Object obj = e.getTarget();

		if (obj instanceof NodeView)
		{
			NodeView nv = (NodeView) obj;
			CyNode myNode = (CyNode) nv.getNode();
			CyAttributes attribs = Cytoscape.getNodeAttributes();
//			String canonicalName = attribs.getStringAttribute(myNode.getIdentifier(), 
//                  Semantics.CANONICAL_NAME);
//			System.out.println("Got target: " + canonicalName);
		}
	}
	
	// AJK: 04/27/08 BEGIN
	//    for context menus
	public void addNodeContextMenuItems (NodeView nodeView, JPopupMenu menu)
	{
		if (nodeView instanceof NodeView)
		{

			final NodeView nv = (NodeView) nodeView;

			// AJK: 06/08/06 BEGIN
			//      don't allow reset of the ID field
			//			if (!menuItemExists(menu, "Rename (reset identifier)"))
//			{
//				menu.add(new AbstractAction("Rename (reset identifier)") {
//					public void actionPerformed(ActionEvent e) {
//						Node node = nv.getNode();
//						String oldId = node.getIdentifier();
//						String newId = JOptionPane.showInputDialog(
//								Cytoscape.getDesktop(), "Please enter a new identifier for this node",
//								oldId);
//						if ((newId != null) && !(newId.equals(oldId)))
//						{
//	     					node.setIdentifier(newId);
//	     					CytoscapeEditorManager.resetAttributes(
//	     							oldId, newId, CytoscapeEditorManager.nodeAttribs);
//	     					// reset cannonical name and common name to newId
//	     					//    later this needs to change to Label?
//	     					// AJK: 05/09/06 BEGIN
//	     					//    use label
////	     					CytoscapeEditorManager.nodeAttribs.setAttribute(
////	     							newId, "canonicalName", newId);
////	    					CytoscapeEditorManager.nodeAttribs.setAttribute(
////	     							newId, "commonName", newId);
//	     					CytoscapeEditorManager.nodeAttribs.setAttribute(
//	     							newId, Semantics.LABEL, newId);
//	     					Cytoscape.firePropertyChange(Cytoscape.NETWORK_MODIFIED, null, 
//	     							Cytoscape.getCurrentNetwork());
//						}
//					}
//				});
//				menu.addSeparator();				
//			}
			
			if (!menuItemExists(menu, "Delete Selected Nodes and Edges"))
			{
				menu.add(new DeleteAction(null, "Delete")); 
			}

		}				
	}
	
	public boolean menuItemExists (JPopupMenu menu, String label)
	{
		boolean itemExists = false;
		
		MenuElement [] elements = menu.getSubElements();
		for (int i = 0; i < elements.length; i++)
		{
			MenuElement elem = elements[i];
			if (elem instanceof JMenuItem)
			{
				JMenuItem item = (JMenuItem) elem;
				if (item.getText().equals(label))
				{
					itemExists = true;
					break;
				}
			}
		}
		
		return itemExists;
	}
	
	public void addEdgeContextMenuItems (EdgeView edgeView, JPopupMenu menu)
	{
		if (edgeView instanceof EdgeView)
		{

			final EdgeView ev = (EdgeView) edgeView;
			
			if (!menuItemExists(menu, "Delete Selected Nodes and Edges"))
			{
				menu.add(new DeleteAction(null, "Delete")); 
			}

		}				
	}	
	
	// AJK: 04/27/08 END
	

	/**
	 * wrapper for adding a node in Cytoscape. This is intended to be called by
	 * the CytoscapeEditor in lieu of making direct modifications to the
	 * Cytoscape model. Thus, it provides an insulating level of abstraction
	 * between the CytoscapeEditor and the Cytoscape implementation, allowing
	 * for portability and extensibility of the editor.
	 * 
	 * @param nodeName
	 *            the name of the node to be created. This will be used as a
	 *            unique identifier for the node.
	 * @param nodeType
	 *            the value of the 'NodeType' attribute for this node. This can
	 *            be used in conjunction with the Visual Mapper to assign visual
	 *            characteristics to different types of nodes. Also can be used,
	 *            by the canvas when handling, a dropped item, to distinguish
	 *            between nodes and edges.
	 * @return the CyNode that has been either reused or created.
	 */
	public CyNode addNode(String nodeName, String nodeType) {
		return addNode (nodeName, CytoscapeEditorManager.NODE_TYPE, nodeType);
	}

	/**
	 * wrapper for adding a node in Cytoscape. This is intended to be called by
	 * the CytoscapeEditor in lieu of making direct modifications to the
	 * Cytoscape model. Thus, it provides an insulating level of abstraction
	 * between the CytoscapeEditor and the Cytoscape implementation, allowing
	 * for portability and extensibility of the editor.
	 * 
	 * @param nodeName
	 *            the name of the node to be created. This will be used as a
	 *            unique identifier for the node.
	 * @param create
	 *            if true, then create a node if one does not already exist.
	 *            Otherwise, only return a node if it already exists.
	 * @return the CyNode that has been either reused or created.
	 */
	public  CyNode addNode(String nodeName, boolean create) {
		return addNode(nodeName, null);
	}

	/**
	 * wrapper for adding a node in Cytoscape. This is intended to be called by
	 * the CytoscapeEditor in lieu of making direct modifications to the
	 * Cytoscape model. Thus, it provides an insulating level of abstraction
	 * between the CytoscapeEditor and the Cytoscape implementation, allowing
	 * for portability and extensibility of the editor. This form of addNode()
	 * will create a node in all cases, whether it previously exists or not.
	 * 
	 * @param nodeName
	 *            the name of the node to be created. This will be used as a
	 *            unique identifier for the node.
	 * @return the CyNode that has been either reused or created.
	 */
	public  CyNode addNode(String nodeName) {
		return addNode(nodeName, null);
	}

	/**
	 * 
	 * wrapper for adding an edge in Cytoscape. This is intended to be called by
	 * the CytoscapeEditor in lieu of making direct modifications to the
	 * Cytoscape model. Thus, it provides an insulating level of abstraction
	 * between the CytoscapeEditor and the Cytoscape implementation, allowing
	 * for portability and extensibility of the editor.
	 * 
	 * @param node_1
	 *            Node at one end of the edge
	 * @param node_2
	 *            Node at the other end of the edge
	 * @param attribute
	 *            the attribute of the edge to be searched, a common one is
	 *            Semantics.INTERACTION
	 * @param attribute_value
	 *            a value for the attribute, like "pp" or "default"
	 * @param create
	 *            if true, then create an edge if one does not already exist.
	 *            Otherwise, return the edge if it already exists.
	 * @param edgeType
	 *            a value for the "EdgeType" attribute assigned to the edge.
	 *            This can be used in conjunction with the Visual Mapper.
	 * @return the CyEdge that has either been reused or created
	 *  
	 */
	public CyEdge addEdge(Node node_1, Node node_2, String attribute,
			Object attribute_value, boolean create, String edgeType) {
		CyEdge edge = Cytoscape.getCyEdge(node_1, node_2, attribute,
				attribute_value, create, true); // edge is directed
		if (edge != null) {
			CyNetwork net = Cytoscape.getCurrentNetwork();
			net.restoreEdge(edge);
			if (edgeType != null) {
				CytoscapeEditorManager.edgeAttribs.setAttribute(edge.getIdentifier(), CytoscapeEditorManager.EDGE_TYPE,
						edgeType);
			}
			CytoscapeEditorManager.manager.setupUndoableAdditionEdit(net, null, edge);
			Cytoscape.firePropertyChange(Cytoscape.NETWORK_MODIFIED, null, net);
			
			// AJK: 05/16/06 
//			((DGraphView) Cytoscape.getCurrentNetworkView()).addEdgeContextMenuListener(this);
			Cytoscape.getCurrentNetworkView().addEdgeContextMenuListener(this);
		}
		return edge;
	}

	/**
	 * 
	 * wrapper for adding an edge in Cytoscape. This is intended to be called by
	 * the CytoscapeEditor in lieu of making direct modifications to the
	 * Cytoscape model. Thus, it provides an insulating level of abstraction
	 * between the CytoscapeEditor and the Cytoscape implementation, allowing
	 * for portability and extensibility of the editor. This version always
	 * creates an edge, whether or not one already exists.
	 * 
	 * @param node_1
	 *            Node at one end of the edge
	 * @param node_2
	 *            Node at the other end of the edge
	 * @param attribute
	 *            the attribute of the edge to be searched, a common one is
	 *            Semantics.INTERACTION
	 * @param attribute_value
	 *            a value for the attribute, like "pp" or "default"
	 * @return the CyEdge that has been created
	 *  
	 */
	public CyEdge addEdge(Node node_1, Node node_2, String attribute,
			Object attribute_value) {
		return addEdge(node_1, node_2, attribute, attribute_value, true, null);
	}

	/**
	 * 
	 * wrapper for adding an edge in Cytoscape. This is intended to be called by
	 * the CytoscapeEditor in lieu of making direct modifications to the
	 * Cytoscape model. Thus, it provides an insulating level of abstraction
	 * between the CytoscapeEditor and the Cytoscape implementation, allowing
	 * for portability and extensibility of the editor. This version always
	 * creates an edge, whether or not one already exists.
	 * 
	 * @param node_1
	 *            Node at one end of the edge
	 * @param node_2
	 *            Node at the other end of the edge
	 * @param attribute
	 *            the attribute of the edge to be searched, a common one is
	 *            Semantics.INTERACTION
	 * @param attribute_value
	 *            a value for the attribute, like "pp" or "default"
	 * @param edgeType
	 *            a value for the "EdgeType" attribute assigned to the edge.
	 *            This can be used in conjunction with the Visual Mapper.
	 * @return the CyEdge that has been created
	 *  
	 */
	public CyEdge addEdge(Node node_1, Node node_2, String attribute,
			Object attribute_value, String edgeType) {
		return addEdge(node_1, node_2, attribute, attribute_value, true,
				edgeType);
	}

	/**
	 * 
	 * wrapper for adding an edge in Cytoscape. This is intended to be called by
	 * the CytoscapeEditor in lieu of making direct modifications to the
	 * Cytoscape model. Thus, it provides an insulating level of abstraction
	 * between the CytoscapeEditor and the Cytoscape implementation, allowing
	 * for portability and extensibility of the editor.
	 * 
	 * @param node_1
	 *            Node at one end of the edge
	 * @param node_2
	 *            Node at the other end of the edge
	 * @param attribute
	 *            the attribute of the edge to be searched, a common one is
	 *            Semantics.INTERACTION
	 * @param attribute_value
	 *            a value for the attribute, like "pp" or "default"
	 * @param create
	 *            if true, then create an edge if one does not already exist.
	 *            Otherwise, return the edge if it already exists.
	 * @return the CyEdge that has either been reused or created
	 *  
	 */
	public  CyEdge addEdge(Node node_1, Node node_2, String attribute,
			Object attribute_value, boolean create) {
		return addEdge(node_1, node_2, attribute, attribute_value, create, null);
	}

	/**
	 * Deletes (hides) a node from the current network
	 * 
	 * @param node
	 *            the node to be deleted
	 */
	public  void deleteNode(Node node) {
		CyNetwork net = Cytoscape.getCurrentNetwork();
		net.hideNode(node);
		CytoscapeModifiedNetworkManager.setModified(net,
				CytoscapeModifiedNetworkManager.MODIFIED);
		// TODO: if number of networks containing nodes falls to zero, then
		// delete it
		//    delete it from the root graph
		//    how to find out how many networks contain node, is there an easy way
		//    to do this or do I have to iterate?
		//    also, how does this affect undo/redo?
	}

	/**
	 * Deletes (hides) an edge from the current network
	 * 
	 * @param edge
	 *            the edge to be deleted
	 */
	public void deleteEdge(CyEdge edge) {
		CyNetwork net = Cytoscape.getCurrentNetwork();
		net.hideEdge(edge);
		CytoscapeModifiedNetworkManager.setModified(net,
				CytoscapeModifiedNetworkManager.MODIFIED);

		// TODO: if number of networks containing edges falls to zero,
		//    delete it from the root graph
		//    how to find out how many networks contain edge, is there an easy way
		//    to do this or do I have to iterate?
		//    also, how does this affect undo/redo?
	}

	/**
	 * build the visualStyle for this editor
	 * this code should be overidden by more specialized editors that 
	 * programmatically create a visual style
	 *
	 */
	public void buildVisualStyle()
	{
	};


	/**
	 * specialized initialization code for editor, called by
	 * CytoscapeEditorManager when a new editor is built, should be overridden
	 * 
	 * @param args
	 *            an arbitrary list of arguments passed to initialization
	 *            routine. Not used in this editor
	 */
	public void initializeControls(List args) {

		// first, check to see if there already is a menu item to "Connect Selected Nodes"
		//    if not, then add an item
		// TODO: look for routines to find a menu item given a string; there should be such a utility
		JMenu editMenu = Cytoscape.getDesktop().getCyMenus().getEditMenu();
		boolean foundConnectSelected = false;
//		System.out.println("checking against edit menu: " + editMenu);
		System.out.println("item count = " + editMenu.getItemCount());
		for (int i = 0; i < editMenu.getItemCount(); i++) {
			JMenuItem jIt = editMenu.getItem(i);
			if (jIt != null) {
				String name = jIt.getText();
//				System.out
//						.println("Checking for get selected against: " + name);
				if (name.equals(_connectedTitle)) {
					foundConnectSelected = true;
					break;
				}
			}
		}
		if (!foundConnectSelected) {
			ConnectSelectedNodesAction connectAction = new ConnectSelectedNodesAction();
			connectAction.setPreferredMenu("Edit");
			Cytoscape.getDesktop().getCyMenus().addAction(connectAction);
		}

		_cyMenus = Cytoscape.getDesktop().getCyMenus();
		
		// AJK: 10/03/05 BEGIN
		//       comment out toolbar icons; disable the 'stamp editor'
		//       remove this code later
		/*
		_toolBar = _cyMenus.getToolBar();

		_toolBar.addSeparator();

		_resetCursorButton = _toolBar.add(new ResetCursorAction());
		_resetCursorButton.setIcon(new ImageIcon(getClass().getResource(
				ICONS_REL_LOC + "UpLeftWhite.gif")));
		_resetCursorButton.setToolTipText("Reset Cursor");
		_resetCursorButton.setDisabledIcon(new ImageIcon(getClass()
				.getResource(ICONS_REL_LOC + "DisabledUpLeftWhite.gif")));

		_addNodeButton = _toolBar.add(new AddNodeAction());
		_addNodeButton.setIcon(new ImageIcon(getClass().getResource(
		//				ICONS_REL_LOC + "rect.gif")));
				//				ICONS_REL_LOC + "ovalNodeCursor.gif")));
				ICONS_REL_LOC + "node16_centered.gif")));
		_addNodeButton.setToolTipText("Add a new Node");
		_addNodeButton.setDisabledIcon(new ImageIcon(getClass().getResource(
				ICONS_REL_LOC + "Disabledrect.gif")));

		//		_addLabelButton = _toolBar.add(new AddLabelAction());
		//		_addLabelButton.setIcon(new ImageIcon(getClass().getResource(
		//				ICONS_REL_LOC + "label.gif")));
		//		_addLabelButton.setToolTipText("Add a new Label");

		_addEdgeButton = _toolBar.add(new AddEdgeAction());
		_addEdgeButton.setIcon(new ImageIcon(getClass().getResource(
		//				ICONS_REL_LOC + "UpRightWhite.gif")));
				ICONS_REL_LOC + "UpRightBlue.gif")));
		_addEdgeButton.setToolTipText("Add a new Edge");
		_addEdgeButton.setDisabledIcon(new ImageIcon(getClass().getResource(
				ICONS_REL_LOC + "DisabledUpRightWhite.gif")));

		Toolkit tk = Toolkit.getDefaultToolkit();
		ImageIcon img;
		//		ImageIcon img = new ImageIcon(getClass().getResource(
		//				ICONS_REL_LOC + "label.gif"));
		//		Image labelPointer = img.getImage();
		//		_labelCursor = tk.createCustomCursor(labelPointer, new Point(1, 1),
		//				"LabelPointer");

		img = new ImageIcon(getClass().getResource(
		//				ICONS_REL_LOC + "rect.gif"));
				ICONS_REL_LOC + "node32.gif"));
		Image nodePointer = img.getImage();
		_nodeCursor = tk.createCustomCursor(nodePointer, new Point(1, 1),
				"NodePointer");

		img = new ImageIcon(getClass().getResource(
				ICONS_REL_LOC + "fit36_blue_cursor.gif"));
		Image edgePointer = img.getImage();
		_edgeCursor = tk.createCustomCursor(edgePointer, new Point(30, 1),
				"EdgePointer");
				*/
		// AJK: 10/03/05 END

		_originalCursor = Cytoscape.getDesktop().getCursor();

	}

	/**
	 * sets controls invisible when editor type is switched
	 * 
	 * @param args
	 *            args an arbitrary list of arguments (not used in this editor)
	 */
	public void disableControls(List args) {
		if (_addNodeButton != null) { // make sure we have buttons before disabling them
			_addNodeButton.setVisible(false);
		}
		if (_addEdgeButton != null)  {
			_addEdgeButton.setVisible(false);
		}
		if (_resetCursorButton != null)  {
			_resetCursorButton.setVisible(false);
		}
	}

	/**
	 * sets controls visible when editor type is switched back to this editor
	 * @param args args an arbitrary list of arguments (not used in this editor)	 * 
	 */
	public void enableControls(List args) {
		System.out.println("enabling controls for " + this);
		_addNodeButton.setVisible(true);
		_addEdgeButton.setVisible(true);
		_resetCursorButton.setVisible(true);
	}

	/**
	 * gets the name (type) of this editor
	 * @return the editorName.
	 */
	public String getEditorName() {
		return editorName;
	}

	/**
	 * sets the name (type) for this editor
	 * @param editorName the editorName to set.
	 */
	public void setEditorName(String editorName) {
		this.editorName = editorName;
	}




	/**
	 * action that is invoked when the "add node" button is pressed
	 * sets the mode of the edit event handler to "ADD_MODE"
	 * @author Allan Kuchinsky, Agilent Technologies
	 * @version 1.0
	 *
	 */
	class AddNodeAction extends CytoscapeAction {
		public AddNodeAction() {
			//			super("Add a new Node");
			super("");
		}

		public void actionPerformed(ActionEvent ae) {
			CyNetworkView view = Cytoscape.getCurrentNetworkView();
			((PhoebeNetworkView) Cytoscape.getCurrentNetworkView()).getCanvas()
					.setCursor(_nodeCursor);
			BasicNetworkEditEventHandler event = (BasicNetworkEditEventHandler) CytoscapeEditorManager
					.getViewNetworkEditEventAdapter(view);

			if (event == null) {
				System.out
						.println("Error: cannot find event handler for view: "
								+ view);
			} else {
//				event.setMode(event.ADD_MODE);
			}
		}
	}

	/**
	 * action that is invoked when the "add label" button is pressed
	 * sets the mode of the edit event handler to "LABEL_MODE"
	 * <p>
	 * not implemented in Cytoscape 2.2
	 * 
	 * @author Allan Kuchinsky, Agilent Technologies
	 * @version 1.0
	 *
	 */
	class AddLabelAction extends CytoscapeAction {
		public AddLabelAction() {
			//			super("Add a new Node");
			super("");
		}

		public void actionPerformed(ActionEvent ae) {
			CyNetworkView view = Cytoscape.getCurrentNetworkView();
			((PhoebeNetworkView) Cytoscape.getCurrentNetworkView()).getCanvas()
					.setCursor(_nodeCursor);
			BasicNetworkEditEventHandler event = (BasicNetworkEditEventHandler) CytoscapeEditorManager
					.getViewNetworkEditEventAdapter(view);

			if (event == null) {
				System.out
						.println("Error: cannot find event handler for view: "
								+ view);
			} else {
//				event.setMode(event.LABEL_MODE);

			}
		}
	}

	/**
	 * action that is invoked when the "connect" button is pressed
	 * sets the mode of the edit event handler to "CONNECT_MODE"
	 * @author Allan Kuchinsky, Agilent Technologies
	 * @version 1.0
	 *
	 */	
	class AddEdgeAction extends CytoscapeAction {
		public AddEdgeAction() {
			//			super("Add a new Edge");
			super("");
		}

		public void actionPerformed(ActionEvent ae) {
			CyNetworkView view = Cytoscape.getCurrentNetworkView();
			((PhoebeNetworkView) Cytoscape.getCurrentNetworkView()).getCanvas()
					.setCursor(_edgeCursor);
			BasicNetworkEditEventHandler event = (BasicNetworkEditEventHandler) CytoscapeEditorManager
					.getViewNetworkEditEventAdapter(view);

			if (event == null) {
				System.out
						.println("Error: cannot find event handler for view: "
								+ view);
			} else {
//				event.setMode(event.CONNECT_MODE);
			}
		}
	}


	/**
	 * action that is invoked when the "reset cursor" button is pressed
	 * sets the mode of the edit event handler to "SELECT_MODE"
	 * <p>
	 * not implemented in Cytoscape 2.2
	 * 
	 * @author Allan Kuchinsky, Agilent Technologies
	 * @version 1.0
	 *
	 */
	class ResetCursorAction extends CytoscapeAction {
		public ResetCursorAction() {
			//			super("Add a new Edge");
			super("");
		}

		public void actionPerformed(ActionEvent ae) {
			CyNetworkView view = Cytoscape.getCurrentNetworkView();
			((PhoebeNetworkView) Cytoscape.getCurrentNetworkView()).getCanvas()
					.setCursor(_originalCursor);
			BasicNetworkEditEventHandler event = (BasicNetworkEditEventHandler) CytoscapeEditorManager
					.getViewNetworkEditEventAdapter(view);

			if (event == null) {
				System.out
						.println("Error: cannot find event handler for view: "
								+ view);
			} else {
//				event.setMode(event.SELECT_MODE);
			}
			
			// clear any partial edges
//			if (event.isEdgeStarted())
//			{
//				event.setEdgeStarted(false);
//				event.getCanvas().getLayer().removeChild(event.getEdge());
//			}
		}
	}

	/**
	 * action performed with the Edit->Connect Selected Nodes menu item is clicked on
	 * creates a clique from the set of selected nodes
	 * @author ajk
	 *
	 **/
	class ConnectSelectedNodesAction extends CytoscapeAction {
		public ConnectSelectedNodesAction() {
			super(_connectedTitle);
			setPreferredMenu("Edit");
			setPreferredIndex(Cytoscape.getDesktop().getCyMenus().getEditMenu().getItemCount()-2);
		}

		public ConnectSelectedNodesAction(boolean label) {
			this(); 
		}
		
		public String getName ()
		{
			return _connectedTitle; 
		}

		public void actionPerformed(ActionEvent e) {

			CyNetworkView view = Cytoscape.getCurrentNetworkView();
			java.util.List nodes = view.getSelectedNodes();

			for (int i = 0; i < nodes.size() - 1; i++) {
				NodeView nv = (NodeView) nodes.get(i);
				CyNode firstCyNode = (CyNode) nv.getNode();
				for (int j = i + 1; j < nodes.size(); j++) {
					NodeView nv2 = (NodeView) nodes.get(j);
					CyNode secondCyNode = (CyNode) nv2.getNode();
					addEdge(firstCyNode, secondCyNode,
							Semantics.INTERACTION, "default", true, "DefaultEdge");
				}
			}
		}

	}

}
