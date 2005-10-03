/*
 * Created on Jul 5, 2005
 *
 */
package cytoscape.editor;

import giny.model.Node;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.swing.JMenuItem;
import javax.swing.undo.UndoManager;
import javax.swing.undo.UndoableEdit;

import phoebe.PGraphView;
import phoebe.PhoebeCanvas;
import cern.colt.list.IntArrayList;
import cytoscape.CyEdge;
import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.CytoscapeInit;
import cytoscape.CytoscapeModifiedNetworkManager;
import cytoscape.data.CytoscapeData;
import cytoscape.editor.actions.NewNetworkAction;
import cytoscape.editor.actions.RedoAction;
import cytoscape.editor.actions.SetEditorAction;
import cytoscape.editor.actions.UndoAction;
import cytoscape.editor.event.NetworkEditEventAdapter;
import cytoscape.editor.impl.CytoscapeEditorManagerSupport;
import cytoscape.giny.PhoebeNetworkView;
import cytoscape.view.CyNetworkView;

/**
 * The <b>CytoscapeEditorManager</b> is the central class in the editor framework
 * API.  It maintains the state of the editing environment, maintains global state information,
 * and implements static methods for editor registration, editor invocation (via
 * CytoscapeEditorFactory), node/edge addition, undo/redo.  
 * 
 * @author Allan Kuchinsky, Agilent Technologies
 * @version 1.0
 * @see CytoscapeEditorFactory, CytoscapeEditorManagerSupport
 * 
 */
public abstract class CytoscapeEditorManager {

	/**
	 * holding area for deleted nodes
	 * used when undo-ing deletes.  
	 */
	// AJK: 09/05/05: nodeClipBoard, edgeClipBoard, and networkClipBoard seem to be set but never referenced 
	//                so I will not bother making one per NetworkView
	private static IntArrayList nodeClipBoard;

	/**
	 * holding area for deleted edges
	 * used when undo-ing deletes
	 */
	private static IntArrayList edgeClipBoard;


	/**
	 * holding area for deleted networks
	 * used when undo-ing deletes
	 */
	private static String networkClipBoard;

	/**
	 * basic controller for undo/redo functionality for deletion
	 * in this version of CytoscapeEditor, deletion of nodes/edges can 
	 * be undone.  Undo is multi-level.  There is also a multi-level redo
	 */
	private static UndoManager undo;
	
	/**
	 * extension to support one UndoManager per NetworkView
	 */
	private static UndoManager currentUndoManager;

	
	/**
	 * action that implements undo of node/edge deletion
	 */
	private static UndoAction undoAction;

	/**
	 * action that implements redo of node/edge deletion
	 */
	private static RedoAction redoAction;

	/**
	 * drawing area for the editor.  Accepts drop events, mouse events.
	 */
	private PhoebeCanvas canvas;


	/**
	 * default border width for a node.  Used for highlighted the node upon mouseEnter, via thickening
	 * the node border.
	 */
	static float defaultBorderWidth = Float.NaN;

	/**
	 * subsidiary class which implements methods that require non-static references, e.g. a 
	 * Swing PropertyChangeListener
	 */
	protected static CytoscapeEditorManagerSupport manager;

	/**
	 * pointer to currently active editor
	 */
	protected static CytoscapeEditor currentEditor = null;

	/**
	 * flag that tells whether the full multi-editor framework is enabled
	 * for Cytoscape 2.2, only a simple editor will be deployed, and that simple editor will be
	 * invoked when Cytoscape is initialized
	 * future versions of Cytoscape will contain the full editor framework, with multiple available editors
	 * In Cytoscape 2.2, this flag can be set TRUE via a command line argument to Cytoscape (which is not advertised)
	 * when this flag is set, the full editor framewok will be loaded
	 * This functionality is for testing and prototyping purposes only
	 * please send an email to mailto:sysbio@labs.agilent.com if you are interested in experimenting with this multi-editorframework
	 */
	protected static boolean runningEditorFramework = false;

	/**
	 * map that associates a network view with its editor
	 */
	protected static HashMap editorViewMap = new HashMap();
	
	
	/*
	 * map that associates a network view with its UndoManager
	 */
	protected static HashMap undoManagerViewMap = new HashMap();

	/**
	 * map that associates a network with its editor
	 */
	protected static HashMap editorNetworkMap = new HashMap();

	/**
	 * associates an editor type with its NetworkEditEventAdapter
	 */
	protected static HashMap editorTypeEventAdapterMap = new HashMap();

	/**
	 * associates a view with its NetworkEditEventAdapter
	 */
	protected static HashMap viewNetworkEditEventAdapterMap = new HashMap();
	
	
	/**
	 * associates an editor with the controlling NodeAttribute that drives rendering of icons on palette
	 */
	protected static HashMap editorControllingNodeAttributeMap = new HashMap();
	
	/**
	 * associates an editor with the controlling EdgeAttribute that drives rendering of icons on palette
	 */
	protected static HashMap editorControllingEdgeAttributeMap = new HashMap();	
	
	/**
	 * counter variable used in generating names for new networks invoked by the New->Network menu item.
	 */
	private static int networkNameCounter = 0;
	
	
	/**
	 * associates an editor type with a visual style name
	 */
	protected static HashMap editorTypeVisualStyleNameMap = new HashMap();
	
	/**
	 * CytoscapeAttribute: NODE_TYPE
	 */
	public static final String NODE_TYPE = "NODE_TYPE";
	
	public static final String BIOPAX_NODE_TYPE = "BIOPAX_NODE_TYPE";
	
	
	/**
	 * CytoscapeAttribute: EDGE_TYPE
	 *
	 */
	public static final String EDGE_TYPE = "EDGE_TYPE";
	
	public static final String ANY_VISUAL_STYLE = "ANY_VISUAL_STYLE";
	
	/**
	 * initial setup of controls, menu items, undo/redo actions, and keyboard accelerators
	 *
	 */
	public static void initialize() {
		manager = new CytoscapeEditorManagerSupport();
		
		CytoscapeModifiedNetworkManager modifiedManager = new CytoscapeModifiedNetworkManager();

		NewNetworkAction newNetwork = new NewNetworkAction("Network",
				CytoscapeEditorFactory.INSTANCE);
		Cytoscape.getDesktop().getCyMenus().getMenuBar().getMenu("File.New");
		Cytoscape.getDesktop().getCyMenus().addAction(newNetwork);


		undo = new UndoManager();
		undoAction = new UndoAction(undo);
		redoAction = new RedoAction(undo);
		undoAction.setRedoAction(redoAction);
		redoAction.setUndoAction(undoAction);
		// AJK: 09/06/05 accommodate one undo manager per network view.  Set the global one here.
		undoManagerViewMap.put(Cytoscape.getCurrentNetworkView(), undo);
		setCurrentUndoManager(undo);

		JMenuItem undoItem = new JMenuItem(undoAction);
		JMenuItem redoItem = new JMenuItem(redoAction);

		undoItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(
				java.awt.event.KeyEvent.VK_Z, ActionEvent.CTRL_MASK));
		redoItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(
				java.awt.event.KeyEvent.VK_Y, ActionEvent.CTRL_MASK));

		Cytoscape.getDesktop().getCyMenus().getMenuBar().getMenu("Edit").add(
				undoItem);
		Cytoscape.getDesktop().getCyMenus().getMenuBar().getMenu("Edit").add(
				redoItem);
		

	}

	/**
	 * sets up menus for invoking the editor
	 * 
	 * @param editorName text for editor name, to appear in menus
	 *            
	 */
	public static void register(String editorName) {
		register(editorName, "BasicNetworkEventHandler");
	}


	/**
	 * makes the system aware of the editor. adds the editor's type to the list of editor types 
     * maintained by the CytoscapeEditorFactory. 
	 * sets up menu sub-items for invoking the editor
	 * @param editorName specifies the 'type' of the editor
	 * @param networkEditAdapterName every editor has a NetworkEditEventHandler that handles user input in
	 * in a way that is specialized for that editor.  This is the heart of the editors behaviour.
	 */
	public static void register(String editorName, String networkEditAdapterName) {

//		System.out.println("register editor: " + editorName);
//		System.out.println("register editor: " + networkEditAdapterName);


		// AJK: 08/11/05 BEGIN
		//     for version 2.2, only add ability to set editors if we are running
		// the framework
		// AJK: 09/26/05 we are now implementing the framework in 2.2
//		if (isRunningEditorFramework()) {
			SetEditorAction editNetwork = new SetEditorAction(editorName,
					CytoscapeEditorFactory.INSTANCE);
			// create With menu if not already there
			Cytoscape.getDesktop().getCyMenus().getMenuBar().getMenu(
					"File.SetEditor");
			Cytoscape.getDesktop().getCyMenus().getMenuBar().getMenu(
			"File.SetEditor").setEnabled(true);
			Cytoscape.getDesktop().getCyMenus().addAction(editNetwork);
//		}
		// AJK: 08/11/05 END

		CytoscapeEditorManager.setNetworkEditEventAdapterType(editorName,
				networkEditAdapterName);
	}
	
	public static void register (String editorName, String networkEditAdapterName, 
			String controllingNodeAttribute, String controllingEdgeAttribute)
	{
		register(editorName, networkEditAdapterName);
		CytoscapeEditorManager.setControllingNodeAttribute(editorName, controllingNodeAttribute);
		CytoscapeEditorManager.setControllingEdgeAttribute(editorName, controllingEdgeAttribute);
	}
	
	

	/**
	 * checks to see if there are already right (context) menu items defined for nodes and edges
	 * in the current view.  If false, then the calling method can safely add items to right (context) 
	 * menu, i.e. there is no duplication of menu items.
	 * @param view the Network View being checked
	 * @return true if there are already context menu items defined for nodes and edges in the current
	 * view, false otherwise.
	 */
	public static boolean hasContextMethods(CyNetworkView view) {
		Object[] methods = view.getContextMethods("class phoebe.PNodeView",
				false);
//		System.out.println("Checking for context methods: Methods[] = "
//				+ methods);
		if (methods != null) {
			for (int i = 0; i < methods.length; i++) {
				Object[] methodObj = (Object[]) methods[i];
				String methodClassName = methodObj[0].toString();

//				System.out.println("methodClassName = " + methodClassName);
				if ((methodClassName != null)
						&& (methodClassName
								.equals("cytoscape.editor.actions.NodeAction"))) {
					return true;
				}

			}
		}
		return false;
	}

	/**
	 * Handles the logistics of setting up a New Network view.
	 * <ul>
	 * <li> checks to see if context methods are already loadedfor the view.  If not, then they aer created.
	 * <li> sets dimensions, background, layers, camera, coloring for canvas
	 * <li> sets up a NetworkEditEventHandler for the view
	 * </ul>
	 * @param newView the NetworkView being created
	 */
	public static void setupNewNetworkView(CyNetworkView newView) {

		if (!hasContextMethods(newView)) {
			newView.addContextMethod("class phoebe.PNodeView",
					"cytoscape.editor.actions.NodeAction",
					"getContextMenuItem", new Object[] { newView },
					CytoscapeInit.getClassLoader());	

			newView.addContextMethod("class phoebe.PEdgeView",
					"cytoscape.editor.actions.EdgeAction",
					"getContextMenuItem", new Object[] { newView },
					CytoscapeInit.getClassLoader());
		}

		// AJK: 09/09/05: BEGIN
		//    comment this out; just use newView's bounds to set bounds for canvas
		/*
		int canvasWidth = Cytoscape.getDesktop().getWidth()
				- Cytoscape.getDesktop().getNetworkPanel().getWidth();

		int canvasHeight = Cytoscape.getDesktop().getHeight()
				- Cytoscape.getDesktop().getCyMenus().getToolBar().getHeight();
		*/
		
		CytoscapeEditor cyEditor = CytoscapeEditorManager.getCurrentEditor();

		PhoebeCanvas canvas = ((PhoebeCanvas) ((PhoebeNetworkView) newView)
				.getCanvas());
		NetworkEditEventAdapter event = CytoscapeEditorManager
				.getViewNetworkEditEventAdapter(newView);
		if (event == null) {
			event = CytoscapeEditorFactory.INSTANCE
					.getNetworkEditEventAdapter(cyEditor);
			CytoscapeEditorManager.setViewNetworkEditEventAdapter(newView,
					event);

			event.start((PGraphView) newView);
			canvas.addPhoebeCanvasDropListener(event);
			
			// AJK: 09/09/05: BEGIN
			//    setup listeners for changes to attributes
			CyNetwork net = newView.getNetwork();
			CytoscapeData nodeAttribs = Cytoscape.getNodeNetworkData();
//			CyData nodeAttribs = net.getNodeData();
			nodeAttribs.addDataListener(event);

			CytoscapeData edgeAttribs = Cytoscape.getEdgeNetworkData();
//			CyData edgeAttribs = net.getEdgeData();
			edgeAttribs.addDataListener(event);	
	
			
			// AJK: 09/09/05: END
		}
		
		// AJK: 09/06/05 BEGIN
		//   setup and undo manager for this network view
		Object undoObj = getUndoManagerForView(newView);
		if (undoObj instanceof UndoManager)
		{
			setCurrentUndoManager ((UndoManager) undoObj);
		}
		else
		{
			UndoManager newUndo = new UndoManager();
			setUndoManagerForView (newView, newUndo);
			setCurrentUndoManager (newUndo);
		}
		// AJK: 09/06/05 END

//		canvas.getLayer().setBounds(0, 0, canvasWidth, canvasHeight);
//		canvas.getLayer().setBounds(0, 0, newView.getComponent().getWidth() / 2,
//				newView.getComponent().getHeight() / 2);
		
//	
		canvas.getLayer().setBounds(newView.getComponent().getBounds());



//		Color myColor = new Color(225, 225, 250);
		Color myColor = new Color(225, 250, 200);

		canvas.setBackground(myColor);
		canvas.getCamera().setPaint(myColor);

		canvas.setEnabled(true);

	}
	
	



	/**
	 * this method is called when a new editor is set for Cytoscape
	 * it loops through the existing network views
	 * and for each view stops the existing network edit event handler and creates a new edit
	 * event handler that corresponds to the new editor type
	 */
	public static void resetEventHandlerForExistingViews() {
		Map viewMap = Cytoscape.getNetworkViewMap();
		Set keySet = viewMap.keySet();
		Iterator it = keySet.iterator();

		while (it.hasNext()) {
			Object key = it.next();
			Object entryObj = viewMap.get(key);
//			System.out.println("Got entry obj = " + entryObj);
			if (entryObj instanceof PhoebeNetworkView) {

				PhoebeNetworkView entryView = (PhoebeNetworkView) entryObj;
				PhoebeCanvas canvas = ((PhoebeCanvas) entryView.getCanvas());
				NetworkEditEventAdapter oldEvent = CytoscapeEditorManager
						.getViewNetworkEditEventAdapter(entryView);

				if (oldEvent != null) // remove event from this canvas
				{

					canvas.removeInputEventListener(oldEvent);
					canvas.removePhoebeCanvasDropListener(oldEvent);
				}

				CytoscapeEditor cyEditor = CytoscapeEditorManager
						.getCurrentEditor();
				NetworkEditEventAdapter newEvent = CytoscapeEditorFactory.INSTANCE
						.getNetworkEditEventAdapter(cyEditor);
				CytoscapeEditorManager.setViewNetworkEditEventAdapter(
						entryView, newEvent);

				newEvent.start((PGraphView) entryView);
				canvas.addPhoebeCanvasDropListener(newEvent);
			}
		}
	}


	/**
	 * associates a NetworkEditEventHandler class with a CytoscapeEditor class.
	 * then, when the CytoscapeEditor is invoked, the corresponding NetworkEditEventHandler
	 * will be built
	 * 
	 * @param editorType the type of the CytoscapeEditor that this NetworkEditEventHandler is associated with
	 * @param event the type of the NetworkEditEventHandler to be built when the CytoscapeEditor is instantiated
	 */
	public static void setNetworkEditEventAdapterType(String editorType,
			String event) {
		editorTypeEventAdapterMap.put(editorType, event);
	}

	/**
	 * retrieves the type of the adapter that handles drags/drops, other mouse events on the
	 * canvas, for the specified type of CytoscapeEditor
	 * 
	 * @param editorType the type of the CytoscapeEditor that this NetworkEditEventHandler is associated with
	 * @return the NetworkEventAdapter
	 */
	public static String getNetworkEditEventAdapterType(String editorType) {
		Object obj = editorTypeEventAdapterMap.get(editorType);
		if (obj != null) {
			{
				return obj.toString();
			}
		}
		return null;
	}

	/**
	 * set the adapter that handles drags/drops, other mouse events on the
	 * canvas.  Associate it with the input view.
	 * 
	 * @param view the Network View
	 * @param event the NetworkEditEventHandler associated with the view.
	 */
	public static void setViewNetworkEditEventAdapter(CyNetworkView view,
			NetworkEditEventAdapter event) {
		viewNetworkEditEventAdapterMap.put(view, event);
	}

	/**
	 * retrieves the adapter that handles drags/drops, other mouse events on the
	 * canvas
	 * 
	 * @return the NetworkEventAdapter
	 */
	public static NetworkEditEventAdapter getViewNetworkEditEventAdapter(
			CyNetworkView view) {
		Object obj = viewNetworkEditEventAdapterMap.get(view);
		if (obj != null) {
			if (obj instanceof NetworkEditEventAdapter) {
				return (NetworkEditEventAdapter) obj;
			}
		}
		return null;
	}

	/**
	 * @return Returns the editorEventAdapterMap, which is used to associate a type of CytoscapeEditor with
	 * a type of NetworkEditEventHandler
	 */
	public static HashMap getEditorTypeEventAdapterMap() {
		return editorTypeEventAdapterMap;
	}

	/**
	 * @return Returns the editorNetworkMap, which associates a Network with its editor
	 */
	public static HashMap getEditorNetworkMap() {
		return editorNetworkMap;
	}

	/**
	 * @return Returns the editorViewMap, which associates a NetworkView with its Cytoscape editor
	 */
	public static HashMap getEditorViewMap() {
		return editorViewMap;
	}

	/**
	 * wrapper for adding a node in Cytoscape.  This is intended to be called by the CytoscapeEditor in lieu
	 * of making direct modifications to the Cytoscape model.  Thus, it provides an insulating level of 
	 * abstraction between the CytoscapeEditor and the Cytoscape implementation, allowing for portability and
	 * extensibility of the editor.
	 * @param nodeName the name of the node to be created.  This will be used as a unique identifier for the node.
	 * @param create if true, then create a node if one does not already exist.  Otherwise, only return a node if
	 * it already exists.
	 * @param attribute a defining property for the node, that can be used in conjunction with the Visual Mapper
	 * to assign visual characteristics to different types of nodes.  Also can be used, by the canvas when handling
	 * a dropped item, to distinguish between nodes and edges, so should be set to something like "NodeType".
	 * @param value the value of the attribute for this node.  This can be used in conjunction with the Visual Mapper
	 * to assign visual characteristics to different types of nodes, for example to assign a violet diamond shape
	 * to a 'smallMolecule' node type.
	 * @return the CyNode that has been either reused or created.
	 */
	public static CyNode addNode(String nodeName, boolean create,
			String attribute, String value) {
		CyNode cn = Cytoscape.getCyNode(nodeName, create);
		CyNetwork net = Cytoscape.getCurrentNetwork();
		if (attribute != null) {
			net.setNodeAttributeValue(cn, attribute, value);
			net.setNodeAttributeValue(cn, NODE_TYPE, value);
			// hack for BioPAX visual style
			net.setNodeAttributeValue(cn, BIOPAX_NODE_TYPE, value);
			net.restoreNode(cn);
		}

		// hack for BioPAX
		// TODO: move this hack for BIOPAX into the PaletteNetworkEditEventHandler code
		/*
		if (attribute.equals("BIOPAX_NODE_TYPE")) {
			net.setNodeAttributeValue(cn, "BIOPAX_NAME", nodeName);
		}
		*/
		
		manager.setupUndoableAdditionEdit(net, cn, null);
		Cytoscape.firePropertyChange(Cytoscape.NETWORK_MODIFIED, null, net);

		return cn;
	}

	/**
	 * wrapper for adding a node in Cytoscape.  This is intended to be called by the CytoscapeEditor in lieu
	 * of making direct modifications to the Cytoscape model.  Thus, it provides an insulating level of 
	 * abstraction between the CytoscapeEditor and the Cytoscape implementation, allowing for portability and
	 * extensibility of the editor.
	 * @param nodeName the name of the node to be created.  This will be used as a unique identifier for the node.
	 * @param create if true, then create a node if one does not already exist.  Otherwise, only return a node if
	 * it already exists.
	 * @param nodeType the value of the 'NodeType' attribute for this node.  This can be used in conjunction 
	 * with the Visual Mapper
	 * to assign visual characteristics to different types of nodes.  Also can be used, by the canvas when 
	 * handling, a dropped item, to distinguish between nodes and edges.
	 * @return the CyNode that has been either reused or created.
	 */
	public static CyNode addNode(String nodeName, boolean create,
			String nodeType) {
		CyNode cn = Cytoscape.getCyNode(nodeName, create);
		CyNetwork net = Cytoscape.getCurrentNetwork();
		net.restoreNode(cn);
		if (nodeType != null) {
			net.setNodeAttributeValue(cn, NODE_TYPE, nodeType);
			// hack for BioPAX visual style
			net.setNodeAttributeValue(cn, BIOPAX_NODE_TYPE, nodeType);
			//			System.out.println ("NodeType for CyNode " + cn + " set to " + 
//					net.getNodeAttributeValue(cn, NODE_TYPE));
		}
		manager.setupUndoableAdditionEdit(net, cn, null);
		Cytoscape.firePropertyChange(Cytoscape.NETWORK_MODIFIED, null, net);
		return cn;
		
	}

	/**
	 * wrapper for adding a node in Cytoscape.  This is intended to be called by the CytoscapeEditor in lieu
	 * of making direct modifications to the Cytoscape model.  Thus, it provides an insulating level of 
	 * abstraction between the CytoscapeEditor and the Cytoscape implementation, allowing for portability and
	 * extensibility of the editor.
	 * @param nodeName the name of the node to be created.  This will be used as a unique identifier for the node.
	 * @param create if true, then create a node if one does not already exist.  Otherwise, only return a node if
	 * it already exists.
	 * @return the CyNode that has been either reused or created.
	 */
	public static CyNode addNode(String nodeName, boolean create) {
		return addNode(nodeName, create, null);
	}

	/**
	 * wrapper for adding a node in Cytoscape.  This is intended to be called by the CytoscapeEditor in lieu
	 * of making direct modifications to the Cytoscape model.  Thus, it provides an insulating level of 
	 * abstraction between the CytoscapeEditor and the Cytoscape implementation, allowing for portability and
	 * extensibility of the editor.  This form of addNode() will create a node in all cases, whether it 
	 * previously exists or not.
	 * @param nodeName the name of the node to be created.  This will be used as a unique identifier for the node.
	 * @return the CyNode that has been either reused or created.
	 */
	public static CyNode addNode(String nodeName) {
		return addNode(nodeName, true, null);
	}

	/**
	 * wrapper for adding a node in Cytoscape.  This is intended to be called by the CytoscapeEditor in lieu
	 * of making direct modifications to the Cytoscape model.  Thus, it provides an insulating level of 
	 * abstraction between the CytoscapeEditor and the Cytoscape implementation, allowing for portability and
	 * extensibility of the editor.  This form of addNode() will create a node in all cases, whether it 
	 * previously exists or not.
	 * @param nodeName the name of the node to be created.  This will be used as a unique identifier for the node.
	 * @param nodeType the value of the 'NodeType' attribute for this node.  This can be used in conjunction 
	 * with the Visual Mapper
	 * to assign visual characteristics to different types of nodes.  Also can be used, by the canvas when 
	 * handling, a dropped item, to distinguish between nodes and edges.
	 * @return the CyNode that has been either reused or created.
	 */
	public static CyNode addNode(String nodeName, String nodeType) {
		return addNode(nodeName, true, nodeType);
	}

	/**
	 * 
	 * wrapper for adding an edge in Cytoscape.  This is intended to be called by the CytoscapeEditor in lieu
	 * of making direct modifications to the Cytoscape model.  Thus, it provides an insulating level of 
	 * abstraction between the CytoscapeEditor and the Cytoscape implementation, allowing for portability and
	 * extensibility of the editor.	 
     * @param node_1 Node at one end of the edge
	 * @param node_2 Node at the other end of the edge
	 * @param attribute the attribute of the edge to be searched, a common one is Semantics.INTERACTION
	 * @param attribute_value a value for the attribute, like "pp" or "default"
	 * @param create if true, then create an edge if one does not already exist.  Otherwise, return the edge if
	 * it already exists.
	 * @param edgeType a value for the "EdgeType" attribute assigned to the edge.  This can be used in conjunction 
	 * with the Visual Mapper.
	 * @return the CyEdge that has either been reused or created
	 * 
	 */
	public static CyEdge addEdge(Node node_1, Node node_2, String attribute,
			Object attribute_value, boolean create, String edgeType) {
		CyEdge edge = Cytoscape.getCyEdge(node_1, node_2, attribute,
				attribute_value, create);
		CyNetwork net = Cytoscape.getCurrentNetwork();
		net.restoreEdge(edge);
		if (edgeType != null) {
			net.setEdgeAttributeValue(edge, EDGE_TYPE, edgeType);
		}
		manager.setupUndoableAdditionEdit(net, null, edge);
		Cytoscape.firePropertyChange(Cytoscape.NETWORK_MODIFIED, null, net);
		return edge;
	}

	/**
	 * 
	 * wrapper for adding an edge in Cytoscape.  This is intended to be called by the CytoscapeEditor in lieu
	 * of making direct modifications to the Cytoscape model.  Thus, it provides an insulating level of 
	 * abstraction between the CytoscapeEditor and the Cytoscape implementation, allowing for portability and
	 * extensibility of the editor.	 This version always creates an edge, whether or not one already exists.
     * @param node_1 Node at one end of the edge
	 * @param node_2 Node at the other end of the edge
	 * @param attribute the attribute of the edge to be searched, a common one is Semantics.INTERACTION
	 * @param attribute_value a value for the attribute, like "pp" or "default"
	 * @return the CyEdge that has been created
	 * 
	 */
	public static CyEdge addEdge(Node node_1, Node node_2, String attribute,
			Object attribute_value) {
		return addEdge(node_1, node_2, attribute, attribute_value, true, null);
	}

	/**
	 * 
	 * wrapper for adding an edge in Cytoscape.  This is intended to be called by the CytoscapeEditor in lieu
	 * of making direct modifications to the Cytoscape model.  Thus, it provides an insulating level of 
	 * abstraction between the CytoscapeEditor and the Cytoscape implementation, allowing for portability and
	 * extensibility of the editor.	 This version always creates an edge, whether or not one already exists.
     * @param node_1 Node at one end of the edge
	 * @param node_2 Node at the other end of the edge
	 * @param attribute the attribute of the edge to be searched, a common one is Semantics.INTERACTION
	 * @param attribute_value a value for the attribute, like "pp" or "default"
	 * @param edgeType a value for the "EdgeType" attribute assigned to the edge.  This can be used in conjunction 
	 * with the Visual Mapper.
	 * @return the CyEdge that has been created
	 * 
	 */
	public static CyEdge addEdge(Node node_1, Node node_2, String attribute,
			Object attribute_value, String edgeType) {
		return addEdge(node_1, node_2, attribute, attribute_value, true,
				edgeType);
	}

	/**
	 * 
	 * wrapper for adding an edge in Cytoscape.  This is intended to be called by the CytoscapeEditor in lieu
	 * of making direct modifications to the Cytoscape model.  Thus, it provides an insulating level of 
	 * abstraction between the CytoscapeEditor and the Cytoscape implementation, allowing for portability and
	 * extensibility of the editor.	 
     * @param node_1 Node at one end of the edge
	 * @param node_2 Node at the other end of the edge
	 * @param attribute the attribute of the edge to be searched, a common one is Semantics.INTERACTION
	 * @param attribute_value a value for the attribute, like "pp" or "default"
	 * @param create if true, then create an edge if one does not already exist.  Otherwise, return the edge if
	 * it already exists.
	 * @return the CyEdge that has either been reused or created
	 * 
	 */
	public static CyEdge addEdge(Node node_1, Node node_2, String attribute,
			Object attribute_value, boolean create) {
		return addEdge(node_1, node_2, attribute, attribute_value, create, null);
	}

	/**
	 * Deletes (hides) a node from the current network
	 * @param node the node to be deleted
	 */
	public static void deleteNode(Node node) {
		CyNetwork net = Cytoscape.getCurrentNetwork();
		net.hideNode(node);
		// TODO: if number of networks containing nodes falls to zero, then
		// delete it
		//    delete it from the root graph
		//    how to find out how many networks contain node, is there an easy way
		//    to do this or do I have to iterate?
		//    also, how does this affect undo/redo?
	}

	/**
	 * Deletes (hides) an edge from the current network
	 * @param edge the edge to be deleted
	 */
	public static void deleteEdge(CyEdge edge) {
		CyNetwork net = Cytoscape.getCurrentNetwork();
		net.hideEdge(edge);
		CytoscapeModifiedNetworkManager.setModified(net, CytoscapeModifiedNetworkManager.MODIFIED);

		// TODO: if number of networks containing edges falls to zero,
		//    delete it from the root graph
		//    how to find out how many networks contain edge, is there an easy way
		//    to do this or do I have to iterate?
		//    also, how does this affect undo/redo?
	}

	/**
	 * get the editor that is assigned to this network
	 * @param net a Cytoscape network
	 * @return the editor assigned to this network
	 */
	public static CytoscapeEditor getEditorForNetwork(CyNetwork net) {
		Object obj = editorNetworkMap.get(net);
		if (obj != null) {
			if (obj instanceof CytoscapeEditor) {
				return (CytoscapeEditor) obj;
			}
		}
		return null;
	}
	
	
    /**
     * set the editor for a Cytoscape network
     * @param net the Cytoscape network
     * @param editor the editor to be assigned to the Cytoscape network
     */
	public static void setEditorForNetwork(CyNetwork net, CytoscapeEditor editor) {
		editorViewMap.put(net, editor);
	}

	/**
	 * get the editor that is assigned to this CyNetworkView
	 * @param view a CyNetworkView
	 * @return the editor assigned to this CyNetworkView
	 */
	public static CytoscapeEditor getEditorForView(CyNetworkView view) {
		Object obj = editorViewMap.get(view);
		if (obj != null) {
			if (obj instanceof CytoscapeEditor) {
				return (CytoscapeEditor) obj;
			}
		}
		return null;
	}
	
	
	
	/**
	 * 
	 * @param editorType
	 * @return CytoscapeVisualStyle that is associated with this Editor type
	 */
	public static String getVisualStyleForEditorType(String editorType)
	{
		Object obj =  editorTypeVisualStyleNameMap.get(editorType);
		if (obj != null)
		{
			return obj.toString();
		}
		else
		{
			return null;
		}
	}
	
	/**
	 * sets the visual style that is to be associated with an editor type.
	 * this enables visual style to be automatically loaded when an editor is set
	 * @param editorType
	 * @param vizStyle
	 */
	public static void setVisualStyleNameForEditorType (String editorType, String vizStyle)
	{
		editorTypeVisualStyleNameMap.put(editorType, vizStyle);
	}
	

	/**
	 * gets the controlling NodeAttribute that drives rendering of node icon in editor palette
	 * @param editor
	 * @return name of controlling NodeAttribute
	 */
	public static String getControllingNodeAttribute (CytoscapeEditor editor)
	{
		String editorName = editor.getEditorName();
		Object obj =  editorControllingNodeAttributeMap.get(editorName);
		if (obj != null)
		{
			return obj.toString();
		}
		return null;
	}
	
	
	
	/**
	 * gets the controlling EdgeAttribute that drives rendering of node icon in editor palette
	 * @param editor
	 * @return name of controlling EdgeAttribute
	 */
	public static String getControllingEdgeAttribute (CytoscapeEditor editor)
	{
		System.out.println ("get controlling edge attribute for editor: " + editor);
		String editorName = editor.getEditorName();
		Object obj =  editorControllingEdgeAttributeMap.get(editorName);
		System.out.println ("returned: " + obj);
		if (obj != null)
		{
			return obj.toString();
		}
		return null;
	}	
	
	

	/**
	 * sets the controlling NodeAttribute that drives rendering of node icon in editor palette
	 * @param editorName
	 * @param attribute
	 */
	public static void setControllingNodeAttribute (String editorName, String attribute)
	{
		editorControllingNodeAttributeMap.put(editorName, attribute);
	}
	
	
	/**
	 * sets the controlling EdgeAttribute that drives rendering of node icon in editor palette
	 * @param editorName
	 * @param attribute
	 */
	public static void setControllingEdgeAttribute (String editorName, String attribute)
	{
		System.out.println("Setting controlling edge attribute: " + attribute + " for editor: " + editorName);
		editorControllingEdgeAttributeMap.put(editorName, attribute);
	}
	
	
		
	
	
	 
	/**
     * set the editor for a CyNetworkView
     * @param view the CyNetworkView
     * @param editor the editor to be assigned to the CyNetworkView
     */
	public static void setEditorForView(CyNetworkView view,
			CytoscapeEditor editor) {
		editorViewMap.put(view, editor);
	}


	/**
	 * get the UndoManager that is assigned to this CyNetworkView
	 * @param view a CyNetworkView
	 * @return the editor assigned to this CyNetworkView
	 */
	public static UndoManager getUndoManagerForView(CyNetworkView view) {
		Object obj = undoManagerViewMap.get(view);
//		System.out.println ("Get undoManager for view: " + view + " = " + obj);
		if (obj != null) {
			if (obj instanceof UndoManager) {
				return (UndoManager) obj;
			}
		}
		return null;
	}
	 
	/**
     * set the UndoManager for a CyNetworkView
     * @param view the CyNetworkView
     * @param editor the editor to be assigned to the CyNetworkView
     */
	public static void setUndoManagerForView(CyNetworkView view,
			UndoManager undo) {
		undoManagerViewMap.put(view, undo);
//		System.out.println("Setting undo manager for view: " + view + " = " + undo);
	}

	
	
	/**
	 * adds an undoable edit to the UndoManager.  Currently the Cytoscape Editor framework
	 * supports undo/redo for deletion operations.  This method is typically invoked 
	 * from within the code for performing deletion.
	 * @param edit the edit method to be added to the UndoManager.  
	 */
	public static void addEdit(UndoableEdit edit) {
		// AJK: 09/05/05 BEGIN 
		// accommodate one UndoManager per each Network view
//		undo.addEdit(edit);
		UndoManager undoMgr = getCurrentUndoManager();
		undoMgr.addEdit(edit);
		// AJK: 09/05/05 END
		
		undoAction.update();
		redoAction.update();
	}

	/**
	 * TODO: this may be superfluous and could be tossed
	 * @return
	 */
	public static String getNetworkClipBoard() {
		return networkClipBoard;
	}

	/**
	 * TODO: this may be superfluous and could be tossed
	 * @param id
	 */
	public static void setNetworkClipBoard(String id) {
		networkClipBoard = id;
	}


	/**
	 * clipboard for storing deleted nodes.  Used by undo/redo.
	 * @return clipboard for storing deleted nodes.
	 */
	public static IntArrayList getNodeClipBoard() {
		if (nodeClipBoard == null)
			nodeClipBoard = new IntArrayList();
		return nodeClipBoard;
	}

	/**
	 * clipboard for storing deleted edges.  Used by undo/redo.
	 * @return clipboard for storing deleted edges.
	 */
	public static IntArrayList getEdgeClipBoard() {
		if (edgeClipBoard == null)
			edgeClipBoard = new IntArrayList();
		return edgeClipBoard;
	}




	/**
	 * counter used for generating unique network names
	 * @return the networkNameCounter.
	 */
	public static int getNetworkNameCounter() {
		return networkNameCounter;
	}



	/**
	 * increments the counter used for generating unique network names
	 *
	 */
	public static void incrementNetworkNameCounter() {
		CytoscapeEditorManager.networkNameCounter += 1;
	}




	/**
	 * Returns the editor being currently used in Cytoscape.
	 * @return the editor
	 */
	public static CytoscapeEditor getCurrentEditor() {
		return currentEditor;
	}

	/**
	 * @param currentEditor the editor being currently used
	 *            
	 */
	public static void setCurrentEditor(CytoscapeEditor currentEditor) {
		CytoscapeEditorManager.currentEditor = currentEditor;
	}

	/**
	 * returns the default node border width.  This is used in highlighting a node, by thickening its border,
	 * upon mouseEntry.
	 * @return the default border width
	 */
	public static float getDefaultBorderWidth() {
		return defaultBorderWidth;
	}

	/**
	 * sets the default node border width.  This is used in highlighting a node, by thickening its border,
	 * upon mouseEntry.
	 * @param The defaultBorderWidth to set.
	 */
	public static void setDefaultBorderWidth(float defaultBorderWidth) {
		CytoscapeEditorManager.defaultBorderWidth = defaultBorderWidth;
	}

	/**
	 * flag that indicates whether or not the editor framework is running
	 * @return true if the editor framework is running, false otherwise
	 */
	public static boolean isRunningEditorFramework() {
		return runningEditorFramework;
	}

	/**
	 * set the flag that indicates whether or not the editor framework is running.  Currently this is done via
	 * command line argument to Cytoscape
	 * @param true if the editor framework is running, false otherwise
	 */
	public static void setRunningEditorFramework(boolean runningEditorFramework) {
		CytoscapeEditorManager.runningEditorFramework = runningEditorFramework;
	}
	/**
	 * @return Returns the currentUndoManager.
	 */
	public static UndoManager getCurrentUndoManager() {
//		return currentUndoManager;
		return getUndoManagerForView(Cytoscape.getCurrentNetworkView());
	}
	/**
	 * @param currentUndoManager The currentUndoManager to set.
	 */
	public static void setCurrentUndoManager(UndoManager currentUndoManager) {
		CytoscapeEditorManager.currentUndoManager = currentUndoManager;
	}
}