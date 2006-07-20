/*
 * Created on Jul 5, 2005
 *
 */
package cytoscape.editor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.SwingConstants;
import javax.swing.undo.UndoableEdit;

import cern.colt.list.IntArrayList;
import cytoscape.CyNetwork;
import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;
import cytoscape.editor.actions.DeleteAction;
import cytoscape.editor.actions.NewNetworkAction;
import cytoscape.editor.event.NetworkEditEventAdapter;
import cytoscape.editor.impl.CytoscapeEditorManagerSupport;
import cytoscape.editor.impl.ShapePalette;
import cytoscape.view.CyNetworkView;
import cytoscape.view.CytoscapeDesktop;
import cytoscape.visual.CalculatorCatalog;
import cytoscape.visual.VisualMappingManager;
import cytoscape.visual.VisualStyle;
import ding.view.DGraphView;
import ding.view.InnerCanvas;

/**
 * The <b>CytoscapeEditorManager </b> is the central class in the editor
 * framework API. It maintains the state of the editing environment, maintains
 * global state information, and implements static methods for editor
 * registration, editor invocation (via CytoscapeEditorFactory), node/edge
 * addition, undo/redo.
 * 
 * @author Allan Kuchinsky, Agilent Technologies
 * @version 1.0
 * @see CytoscapeEditorFactory, CytoscapeEditorManagerSupport
 * 
 */
public abstract class CytoscapeEditorManager {

	/**
	 * holding area for deleted nodes used when undo-ing deletes.
	 */
	// AJK: 09/05/05: nodeClipBoard, edgeClipBoard, and networkClipBoard seem to
	// be set but never referenced
	// so I will not bother making one per NetworkView
	private static IntArrayList nodeClipBoard;

	/**
	 * holding area for deleted edges used when undo-ing deletes
	 */
	private static IntArrayList edgeClipBoard;

	/**
	 * default border width for a node. Used for highlighted the node upon
	 * mouseEnter, via thickening the node border.
	 */
	static float defaultBorderWidth = Float.NaN;

	/**
	 * subsidiary class which implements methods that require non-static
	 * references, e.g. a Swing PropertyChangeListener
	 */
	public static CytoscapeEditorManagerSupport manager;

	/**
	 * pointer to currently active editor
	 */
	protected static CytoscapeEditor currentEditor = null;

	/**
	 * flag that tells whether the full multi-editor framework is enabled for
	 * Cytoscape 2.2, only a simple editor will be deployed, and that simple
	 * editor will be invoked when Cytoscape is initialized future versions of
	 * Cytoscape will contain the full editor framework, with multiple available
	 * editors In Cytoscape 2.2, this flag can be set TRUE via a command line
	 * argument to Cytoscape (which is not advertised) when this flag is set,
	 * the full editor framewok will be loaded This functionality is for testing
	 * and prototyping purposes only please send an email to
	 * mailto:sysbio@labs.agilent.com if you are interested in experimenting
	 * with this multi-editorframework
	 */
	protected static boolean runningEditorFramework = false;

	/**
	 * flag that tells whether an editor setup is in process used to consume
	 * stateChanged() events when visual style it set, so that we don't try to
	 * set editor twice
	 */
	private static boolean settingUpEditor = false;

	/**
	 * map that associates a network view with its editor
	 */
	protected static HashMap editorViewMap = new HashMap();

	/**
	 * 
	 */
	protected static boolean editingEnabled = false;

	/**
	 * 
	 */
	protected static CytoscapeEditor defaultEditor;

	/**
	 * palette from which shapes are dropped onto the canvas to form Nodes and Edges
	 */
	protected static ShapePalette currentShapePalette;

	/**
	 * associates a view with its NetworkEditEventAdapter
	 */
	protected static HashMap viewNetworkEditEventAdapterMap = new HashMap();

	/**
	 * associate a CyNetworkView with a ShapePalette
	 */
	protected static HashMap viewShapePaletteMap = new HashMap();

	/**
	 * associates a network with all nodes hidden from it
	 */
	protected static HashMap networkHiddenNodesMap = new HashMap();

	/**
	 * associates a network with all edges hidden from it
	 */
	protected static HashMap networkHiddenEdgesMap = new HashMap();

	/**
	 * associates a network with the visual style used to edit it
	 */
	// protected static HashMap networkVisualStyleMap = new HashMap();

	/**
	 * associates a visual style with its set of EDGE_TYPEs
	 */
	protected static HashMap visualStyleEdgeTypesMap = new HashMap();
	
	/**
	 * CytoscapeAttribute: NODE_TYPE
	 */
	public static final String NODE_TYPE = "NODE_TYPE";

	/**
	 * attribute used for BioPAX-like editor
	 */
	public static final String BIOPAX_NODE_TYPE = "BIOPAX_NODE_TYPE";

	/**
	 * CytoscapeAttribute: EDGE_TYPE
	 * 
	 */
	public static final String EDGE_TYPE = "EDGE_TYPE";

	public static final String ANY_VISUAL_STYLE = "ANY_VISUAL_STYLE";

	public static final String DEFAULT_EDITOR_TYPE = "DefaultCytoscapeEditor";
	
	/**
	 * AJK: 06/19/06 CytoscapeEditor class descriptor -- for checking against NETWORK_MODIFIED events
	 */ 	
	public static final String CYTOSCAPE_EDITOR = "cytoscape.editor";

	/**
	 * main data structure for all node attributes
	 */
	public static cytoscape.data.CyAttributes nodeAttribs = Cytoscape
			.getNodeAttributes();

	/**
	 * main data structure for all edge attributes
	 */
	public static cytoscape.data.CyAttributes edgeAttribs = Cytoscape
			.getEdgeAttributes();

	// public static final String DEFAULT_VISUAL_STYLE =
	// CytoscapeInit.getDefaultVisualStyle();

	/**
	 * initial setup of controls, menu items, undo/redo actions, and keyboard
	 * accelerators
	 * 
	 */
	public static void initialize() {
		manager = new CytoscapeEditorManagerSupport();

		NewNetworkAction newNetwork = new NewNetworkAction("Empty Network",
				CytoscapeEditorFactory.INSTANCE);
		Cytoscape.getDesktop().getCyMenus().getMenuBar().getMenu("File.New");
		Cytoscape.getDesktop().getCyMenus().addAction(newNetwork);

		Cytoscape.getDesktop().getCyMenus().getMenuBar().getMenu("File.New")
		        .setEnabled(true);

		DeleteAction delete  = new DeleteAction();
		Cytoscape.getDesktop().getCyMenus().getMenuBar().getMenu("Edit");
		Cytoscape.getDesktop().getCyMenus().addAction(delete);
		
		ShapePalette shapePalette = new ShapePalette();
		Cytoscape.getDesktop().getCytoPanel( SwingConstants.WEST ).add( "Editor", 
				shapePalette);
	}
	
	
	/**
	 * initialize the editor as a side-effect of registering it.
	 * builds editor and its associated event handler
	 * builds visual style associated with editor
	 * @param editorName
	 * @param networkEditAdapterName
	 * @return the editor that is built
	 */   
	public static CytoscapeEditor initializeEditor (String editorName, String networkEditAdapterName)
	{
		try {

			CytoscapeEditorManager.setSettingUpEditor(true);
			// setup a new editor
			CytoscapeEditor cyEditor = CytoscapeEditorFactory.INSTANCE.getEditor(editorName);

			NetworkEditEventAdapter event = 
				initializeEditEventAdapter(cyEditor, networkEditAdapterName);
			cyEditor.setNetworkEditEventAdapter(event);
			
			// setup visual style for this editor

			
			String visualStyleName = editorName;
//			System.out.println("getting visual style for: " + visualStyleName);
			if ((visualStyleName != null)
					&& (!(visualStyleName
							.equals(CytoscapeEditorManager.ANY_VISUAL_STYLE)))) {
				VisualMappingManager manager = 
					Cytoscape.getVisualMappingManager();
				CalculatorCatalog catalog = manager.getCalculatorCatalog();
				VisualStyle existingStyle = catalog
						.getVisualStyle(visualStyleName);
//				System.out.println("Got visual style: " + existingStyle);
//				System.out.println("getting visual style for editor: "
//						+ editorName);
				if (existingStyle != null) {
					manager.setVisualStyle(existingStyle);
				}
			}

			// AJK: 09/19/05 END
			
			 
			
			CytoscapeEditorManager.setCurrentEditor(cyEditor);

			cyEditor.buildVisualStyle();

			CytoscapeEditorManager.setSettingUpEditor(false);

			// enable the "File.New" submenu
			Cytoscape.getDesktop().getCyMenus().getMenuBar()
					.getMenu("File.New").setEnabled(true);
			return cyEditor;

		} catch (InvalidEditorException ex) {
			CytoscapeEditorManager.setSettingUpEditor(false);
			ex.printStackTrace();
			return null;
		}	
	}
	
	/**
	 * builds the named event handler and associates it with the input editor
	 * @param editor
	 * @param adapterName
	 * @return
	 */
	public static NetworkEditEventAdapter initializeEditEventAdapter( 
			CytoscapeEditor editor, String adapterName)
	{
		NetworkEditEventAdapter event = 
			editor.getNetworkEditEventAdapter();
		if (event != null)
		{
			return event;
		}
		try {
//			Class eventAdapterClass = Class.forName("cytoscape.editor.event."
//					+ adapterName);
			Class eventAdapterClass = Class.forName(adapterName);
			event = (NetworkEditEventAdapter) eventAdapterClass
				.newInstance();
			// AJK: 12/05/05 set caller for event class
            event.set_caller(editor);
		} catch (ClassNotFoundException ex) {
			ex.printStackTrace();
		} catch (InstantiationException ex) {
			ex.printStackTrace();
		} catch (IllegalAccessException ex) {
			ex.printStackTrace();
		}
		return event;
	}
	
	/**
	 * returns the network edit event adapter associated with the editor
	 * @param editor the CytoscapeEditor
	 */
	public static NetworkEditEventAdapter getNetworkEditEventAdapter(
			CytoscapeEditor editor) {
		return editor.getNetworkEditEventAdapter();
	}
	

	/**
	 * sets up menus for invoking the editor
	 * 
	 * @param editorName
	 *            text for editor name, to appear in menus
	 * 
	 */
	public static void register(String editorName) {
		register(editorName, "BasicNetworkEventHandler", 
				CytoscapeEditorManager.NODE_TYPE,
				CytoscapeEditorManager.EDGE_TYPE, 
				CytoscapeEditorManager.ANY_VISUAL_STYLE);
	}

	/**
	 * makes the system aware of the editor. adds the editor's type to the list
	 * of editor types maintained by the CytoscapeEditorFactory. 
	 * 
	 * @param editorName
	 *            specifies the 'type' of the editor
	 * @param networkEditAdapterName
	 *            every editor has a NetworkEditEventHandler that handles user
	 *            input in in a way that is specialized for that editor. This is
	 *            the heart of the editors behaviour.
	 */
	public static void register(String editorName, String networkEditAdapterName) {

		register(editorName, networkEditAdapterName, 
				CytoscapeEditorManager.NODE_TYPE,
				CytoscapeEditorManager.EDGE_TYPE, 
				CytoscapeEditorManager.ANY_VISUAL_STYLE);	}

	/**
	 * 
	 * makes the system aware of the editor. adds the editor's type to the list
	 * of editor types maintained by the CytoscapeEditorFactory. 
	 * 
	 * @param editorName
	 *            specifies the 'type' of the editor
	 * @param networkEditAdapterName
	 *            every editor has a NetworkEditEventHandler that handles user
	 *            input in in a way that is specialized for that editor. This is
	 *            the heart of the editors behaviour.
	 * @param visualStyleName
	 *            specifies the visual style that is to be associated with the editor
	 */
	public static void register(String editorName,
			String networkEditAdapterName, String visualStyleName) {
		register(editorName, networkEditAdapterName, 
				CytoscapeEditorManager.NODE_TYPE,
				CytoscapeEditorManager.EDGE_TYPE, 
				visualStyleName);
	}

	/**
	 * makes the system aware of the editor. adds the editor's type to the list
	 * of editor types maintained by the CytoscapeEditorFactory. 
	 * 
	 * @param editorName
	 *            specifies the 'type' of the editor
	 * @param networkEditAdapterName
	 *            every editor has a NetworkEditEventHandler that handles user
	 *            input in in a way that is specialized for that editor. This is
	 *            the heart of the editors behaviour.
	 * @param controllingNodeAttribute
	 *            attribute used by the visual style to map node attribute values into 
	 *            shapes, colors of nodes on palette
	 * @param controllingEdgeAttribute
	 *            attribute used by the visual style to map edge attribute values into 
	 *            target arrows, line types of edges on palette
	 * 
	 */
	public static void register(String editorName,
			String networkEditAdapterName, String controllingNodeAttribute,
			String controllingEdgeAttribute) {
		register(editorName, networkEditAdapterName, controllingNodeAttribute, 
				controllingEdgeAttribute, CytoscapeEditorManager.ANY_VISUAL_STYLE);
	}

	/**
	 * 
	 * makes the system aware of the editor. adds the editor's type to the list
	 * of editor types maintained by the CytoscapeEditorFactory. 
	 * 
	 * @param editorName
	 *            specifies the 'type' of the editor
	 * @param networkEditAdapterName
	 *            every editor has a NetworkEditEventHandler that handles user
	 *            input in in a way that is specialized for that editor. This is
	 *            the heart of the editors behaviour.
	 * @param controllingNodeAttribute
	 *            attribute used by the visual style to map node attribute values into 
	 *            shapes, colors of nodes on palette
	 * @param controllingEdgeAttribute
	 *            attribute used by the visual style to map edge attribute values into 
	 *            target arrows, line types of edges on palette
	 * @param visualStyleName
	 *            specifies the visual style that is to be associated with the editor
	 * 
	 */
	public static void register(String editorName,
			String networkEditAdapterName, String controllingNodeAttribute,
			String controllingEdgeAttribute, String visualStyleName) {
		CytoscapeEditor cyEditor = 
			CytoscapeEditorManager.initializeEditor(editorName,
					networkEditAdapterName);
		if (cyEditor != null)
		{
			cyEditor.setControllingNodeAttribute(controllingNodeAttribute);
			cyEditor.setControllingEdgeAttribute(controllingEdgeAttribute);
		}

	}



	/**
	 * Handles the logistics of setting up a New Network view.
	 * <ul>
	 * <li>checks to see if context methods are already loadedfor the view. If
	 * not, then they are created.
	 * <li>sets dimensions, background, layers, camera, coloring for canvas
	 * <li>sets up a NetworkEditEventHandler for the view
	 * </ul>
	 * 
	 * @param newView
	 *            the NetworkView being created
	 */
	public static void setupNewNetworkView(CyNetworkView newView) {
		CytoscapeEditor cyEditor = CytoscapeEditorManager.getCurrentEditor();
		ding.view.DGraphView wiwx = (DGraphView) newView;
		ding.view.InnerCanvas canvas = wiwx.getCanvas();
		NetworkEditEventAdapter event = CytoscapeEditorManager
				.getViewNetworkEditEventAdapter(newView);
		if (event == null) {
			event = CytoscapeEditorFactory.INSTANCE
					.getNetworkEditEventAdapter(cyEditor);
			CytoscapeEditorManager.setViewNetworkEditEventAdapter(newView,
					event);
			canvas.addPhoebeCanvasDropListener(event);
			nodeAttribs.getMultiHashMap().addDataListener(event);
			edgeAttribs.getMultiHashMap().addDataListener(event);
		}
		canvas.setEnabled(true);
	}



	/**
	 * sets mapping of event handler to CyNetworkView.  Typically done when a 
	 * NETWORK_VIEW_FOCUSED event is received.
	 * removes various listeners on canvas from previous view and sets up
	 * the listeners for this view on the canvas.
	 * 
	 * @param view
	 */
	public static void setEventHandlerForView(CyNetworkView view) {

		DGraphView thisView = (DGraphView) view;
		InnerCanvas canvas = ((InnerCanvas) thisView.getCanvas());
		NetworkEditEventAdapter oldEvent = CytoscapeEditorManager
				.getViewNetworkEditEventAdapter((CyNetworkView) thisView);

		if (oldEvent != null) // remove event from this canvas
		{
			canvas.removeMouseListener(oldEvent);
			canvas.removeMouseMotionListener(oldEvent);
			canvas.removePhoebeCanvasDropListener(oldEvent);
		}

		CytoscapeEditor cyEditor = CytoscapeEditorManager.getCurrentEditor();
		NetworkEditEventAdapter newEvent = CytoscapeEditorFactory.INSTANCE
				.getNetworkEditEventAdapter(cyEditor);
		CytoscapeEditorManager.setViewNetworkEditEventAdapter(
				(CyNetworkView) thisView, newEvent);
		newEvent.setView((DGraphView) view);
		newEvent.start((DGraphView) thisView);
		canvas.addPhoebeCanvasDropListener(newEvent);
	}


	/**
	 * set the adapter that handles drags/drops, other mouse events on the
	 * canvas. Associate it with the input view.
	 * 
	 * @param view
	 *            the Network View
	 * @param event
	 *            the NetworkEditEventHandler associated with the view.
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
	 * get the editor that is assigned to this CyNetworkView
	 * 
	 * @param view
	 *            a CyNetworkView
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
	 * get the ShapePalette that is associated with a CyNetworkView. needed when
	 * a view changes
	 * 
	 * @param view
	 * @return
	 */
	public static ShapePalette getShapePaletteForView(CyNetworkView view) {
		return (ShapePalette) viewShapePaletteMap.get(view);
	}

	/**
	 * sets the ShapePalette that is associated with a CyNetworkView. needed when
	 * a view changes
	 * @param view
	 * @param shape
	 */
	public static void setShapePaletteForView(CyNetworkView view,
			ShapePalette shape) {
		viewShapePaletteMap.put(view, shape);
	}


	/**
	 * returns nodes hidden from network
	 * 
	 * @param net
	 * @return
	 */
	public static int[] getHiddenNodesForNetwork(CyNetwork net) {
		List hiddenNodesList = (List) networkHiddenNodesMap.get(net);
		if (hiddenNodesList == null) {
			return new int[0];
		}
		int[] nodeIndices = new int[hiddenNodesList.size()];
		for (int i = 0; i < hiddenNodesList.size(); i++) {
			nodeIndices[i] = ((Integer) hiddenNodesList.get(i)).intValue();
		}
		return nodeIndices;
	}

	/**
	 * returns edges hidden from network
	 * 
	 * @param net
	 * @return
	 */
	public static int[] getHiddenEdgesForNetwork(CyNetwork net) {
		List hiddenEdgesList = (List) networkHiddenEdgesMap.get(net);
		if (hiddenEdgesList == null) {
			return new int[0];
		}
		int[] edgeIndices = new int[hiddenEdgesList.size()];
		for (int i = 0; i < hiddenEdgesList.size(); i++) {
			edgeIndices[i] = ((Integer) hiddenEdgesList.get(i)).intValue();
		}
		return edgeIndices;
	}

	/**
	 * adds a node to the list of nodes hidden from network
	 * 
	 * @param net
	 * @param nodeIdx
	 *            index of the node to be added
	 */
	public static void addHiddenNodeForNetwork(CyNetwork net, int nodeIdx) {
		List hiddenNodesList = (List) networkHiddenNodesMap.get(net);
		if (hiddenNodesList == null) {
			hiddenNodesList = new ArrayList();
		}
		hiddenNodesList.add(new Integer(nodeIdx)); // don't worry about
													// duplicates
		networkHiddenNodesMap.put(net, hiddenNodesList);
	}

	/**
	 * adds an edge to the list of edges hidden from network
	 * 
	 * @param net
	 * @param edgeIdx
	 *            index of the edge to be added
	 */
	public static void addHiddenEdgeForNetwork(CyNetwork net, int edgeIdx) {
		List hiddenEdgesList = (List) networkHiddenEdgesMap.get(net);
		if (hiddenEdgesList == null) {
			hiddenEdgesList = new ArrayList();
		}
		hiddenEdgesList.add(new Integer(edgeIdx)); // don't worry about
													// duplicates
		networkHiddenEdgesMap.put(net, hiddenEdgesList);
	}
	

	/**
	 * adds an edge type to the mappings kept between visual style and its EDGE_TYPE 
	 * attribute values
	 * @param vizStyle
	 * @param edgeType
	 */
	public static void addEdgeTypeForVisualStyle(VisualStyle vizStyle, String edgeType)
	{
		List edgeTypes = (List) visualStyleEdgeTypesMap.get(vizStyle);
		if (edgeTypes == null)
		{
			edgeTypes = new ArrayList();
		}
		if (!edgeTypes.contains(edgeType))
		{
			edgeTypes.add(edgeType);
		}
		visualStyleEdgeTypesMap.put(vizStyle, edgeTypes);
	}
	
	/**
	 * 
	 * @param vizStyle
	 * @return a list of the EDGE_TYPEs associated with the visual style
	 */
	public static List getEdgeTypesForVisualStyle (VisualStyle vizStyle)
	{
		return (List) visualStyleEdgeTypesMap.get(vizStyle);
	}



	/**
	 * set the editor for a CyNetworkView
	 * 
	 * @param view
	 *            the CyNetworkView
	 * @param editor
	 *            the editor to be assigned to the CyNetworkView
	 */
	public static void setEditorForView(CyNetworkView view,
			CytoscapeEditor editor) {
		editorViewMap.put(view, editor);
	}



	/**
	 * adds an undoable edit to the UndoManager. Currently the Cytoscape Editor
	 * framework supports undo/redo for deletion operations. This method is
	 * typically invoked from within the code for performing deletion.
	 * 
	 * 
	 * 
	 * @param edit
	 *            the edit method to be added to the UndoManager.
	 */
	public static void addEdit (UndoableEdit edit) {
		CytoscapeDesktop.undo.addEdit(edit);
	}

	/**
	 * clipboard for storing deleted nodes. Used by undo/redo.
	 * 
	 * @return clipboard for storing deleted nodes.
	 */
	public static IntArrayList getNodeClipBoard() {
		if (nodeClipBoard == null)
			nodeClipBoard = new IntArrayList();
		return nodeClipBoard;
	}

	/**
	 * clipboard for storing deleted edges. Used by undo/redo.
	 * 
	 * @return clipboard for storing deleted edges.
	 */
	public static IntArrayList getEdgeClipBoard() {
		if (edgeClipBoard == null)
			edgeClipBoard = new IntArrayList();
		return edgeClipBoard;
	}

	/**
	 * Returns the editor being currently used in Cytoscape.
	 * 
	 * @return the editor
	 */
	public static CytoscapeEditor getCurrentEditor() {
		return currentEditor;
	}

	/**
	 * @param currentEditor
	 *            the editor being currently used
	 * 
	 */
	public static void setCurrentEditor(CytoscapeEditor currentEditor) {
		CytoscapeEditorManager.currentEditor = currentEditor;
	}

	/**
	 * returns the default node border width. This is used in highlighting a
	 * node, by thickening its border, upon mouseEntry.
	 * 
	 * @return the default border width
	 */
	public static float getDefaultBorderWidth() {
		return defaultBorderWidth;
	}

	/**
	 * sets the default node border width. This is used in highlighting a node,
	 * by thickening its border, upon mouseEntry.
	 * 
	 * @param The
	 *            defaultBorderWidth to set.
	 */
	public static void setDefaultBorderWidth(float defaultBorderWidth) {
		CytoscapeEditorManager.defaultBorderWidth = defaultBorderWidth;
	}

	/**
	 * flag that indicates whether or not the editor framework is running
	 * 
	 * @return true if the editor framework is running, false otherwise
	 */
	public static boolean isRunningEditorFramework() {
		return runningEditorFramework;
	}

	/**
	 * set the flag that indicates whether or not the editor framework is
	 * running. Currently this is done via command line argument to Cytoscape
	 * 
	 * @param true
	 *            if the editor framework is running, false otherwise
	 */
	public static void setRunningEditorFramework(boolean runningEditorFramework) {
		CytoscapeEditorManager.runningEditorFramework = runningEditorFramework;
	}

	/**
	 * @return Returns the editingEnabled.
	 */
	public static boolean isEditingEnabled() {
		return editingEnabled;
	}

	/**
	 * @param editingEnabled
	 *            The editingEnabled to set.
	 */
	public static void setEditingEnabled(boolean editingEnabled) {
		CytoscapeEditorManager.editingEnabled = editingEnabled;
	}

	/**
	 * @return Returns the currentShapePalette.
	 */
	public static ShapePalette getCurrentShapePalette() {
		return currentShapePalette;
	}

	/**
	 * @param currentShapePalette
	 *            The currentShapePalette to set.
	 */
	public static void setCurrentShapePalette(ShapePalette currentShapePalette) {
		CytoscapeEditorManager.currentShapePalette = currentShapePalette;
	}

	/**
	 * @param defaultEditor
	 *            The defaultEditor to set.
	 */
	public static void setDefaultEditor(CytoscapeEditor defaultEditor) {
		CytoscapeEditorManager.defaultEditor = defaultEditor;
	}

	/**
	 * @return Returns the settingUpEditor.
	 */
	public static boolean isSettingUpEditor() {
		return settingUpEditor;
	}

	/**
	 * @param settingUpEditor
	 *            The settingUpEditor to set.
	 */
	public static void setSettingUpEditor(boolean settingUpEditor) {
		CytoscapeEditorManager.settingUpEditor = settingUpEditor;
	}

	/**
	 * generate a unique name for the network under construction, utilizing time
	 * stamp
	 * 
	 * @return
	 */
	public static String createUniqueNetworkName() {
		int iteration_limit = 100;
		String netName = "Net:";
		CyNetwork cn;
		while (iteration_limit > 0) {
			netName = "Net";
			java.util.Date d1 = new java.util.Date();
			long t1 = d1.getTime();
			String s1 = Long.toString(t1);
			netName += s1.substring(s1.length() - 3); // append last 4
			// digits of time
			// stamp to
			// name
			cn = Cytoscape.getNetwork(netName);
			if ((cn.getNodeCount() == 0) && (cn.getEdgeCount() == 0)) {
				// we have the null network, so a network with netName was not
				// found.
				return netName;
			}
			iteration_limit--;
		}
		// in the unlikely condition where we couldn't generate a
		// unique name after a number of tries,
		// return a random string and hope for the best
		return new String("Agilent:" + Math.random());
	}

	/**
	 * reset attributes for a CyNode whose identifier has been reset, basically
	 * by copying over attributes from old identifier to new identifier
	 * 
	 * @param oldId
	 *            old node identifier
	 * @param newId
	 *            new node identifier
	 * @param attrs
	 *            attributes
	 * 
	 */
	public static void resetAttributes(String oldId, String newId,
			CyAttributes attrs) {

		final String[] attrNames = attrs.getAttributeNames();
		for (int i = 0; i < attrNames.length; i++) {
			final byte type = attrs.getType(attrNames[i]);
			if (attrs.hasAttribute(oldId, attrNames[i])) {
				if (type == CyAttributes.TYPE_SIMPLE_LIST) {
					List l = attrs.getAttributeList(oldId, attrNames[i]);
					if (l != null && l.size() > 0) {
						attrs.setAttributeList(newId, attrNames[i], l);
					}
				} else if (type == CyAttributes.TYPE_SIMPLE_MAP) {
					Map m = attrs.getAttributeMap(oldId, attrNames[i]);
					if (m != null) {
						attrs.setAttributeMap(newId, attrNames[i], m);
					}
				} else if (type == CyAttributes.TYPE_BOOLEAN) {
					attrs.setAttribute(newId, attrNames[i], attrs
							.getBooleanAttribute(oldId, attrNames[i]));
				} else if (type == CyAttributes.TYPE_INTEGER) {
					attrs.setAttribute(newId, attrNames[i], attrs
							.getIntegerAttribute(oldId, attrNames[i]));
				} else if (type == CyAttributes.TYPE_FLOATING) {
					attrs.setAttribute(newId, attrNames[i], attrs
							.getDoubleAttribute(oldId, attrNames[i]));
				} else if (type == CyAttributes.TYPE_STRING) {
					attrs.setAttribute(newId, attrNames[i], attrs
							.getStringAttribute(oldId, attrNames[i]));
				}
			}
		}
	}
}