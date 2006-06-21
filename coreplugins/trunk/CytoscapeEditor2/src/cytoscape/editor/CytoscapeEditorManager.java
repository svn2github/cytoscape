/*
 * Created on Jul 5, 2005
 *
 */
package cytoscape.editor;

import giny.model.GraphPerspective;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.SwingConstants;
import javax.swing.undo.UndoManager;
import javax.swing.undo.UndoableEdit;

import phoebe.PhoebeCanvas;
import cern.colt.list.IntArrayList;
import cytoscape.CyNetwork;
import cytoscape.Cytoscape;
import cytoscape.CytoscapeModifiedNetworkManager;
import cytoscape.data.CyAttributes;
import cytoscape.editor.actions.DeleteAction;
import cytoscape.editor.actions.NewNetworkAction;
import cytoscape.editor.actions.RedoAction;
import cytoscape.editor.actions.UndoAction;
import cytoscape.editor.event.NetworkEditEventAdapter;
import cytoscape.editor.impl.CytoscapeEditorManagerSupport;
import cytoscape.editor.impl.ShapePalette;
import cytoscape.view.CyNetworkView;
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
	 * holding area for deleted networks used when undo-ing deletes
	 */
	private static String networkClipBoard;

	/**
	 * basic controller for undo/redo functionality for deletion in this version
	 * of CytoscapeEditor, deletion of nodes/edges can be undone. Undo is
	 * multi-level. There is also a multi-level redo
	 */
	// AJK: 10/21/05 comment out, make UndoManager per view
	private static UndoManager undo;

	/**
	 * extension to support one Manager per NetworkView
	 */
	private static UndoManager currentUndoManager;

	/**
	 * action that implements undo of node/edge deletion
	 */
	// AJK: 10/21/05 make this per view
	// private static UndoAction undoAction;
	/**
	 * action that implements redo of node/edge deletion
	 */
	// AJK: 10/21/05 make this per view
	// private static RedoAction redoAction;
	/**
	 * drawing area for the editor. Accepts drop events, mouse events.
	 */
	private PhoebeCanvas canvas;

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
	 * 
	 */
	protected static ShapePalette currentShapePalette;

	/*
	 * map that associates a network view with its UndoManager
	 */
	protected static HashMap undoManagerViewMap = new HashMap();

	/*
	 * map that associates a network view with its UndoAction
	 */
	protected static HashMap undoActionViewMap = new HashMap();

	/*
	 * map that associates a network view with its RedoAction
	 */
	protected static HashMap redoActionViewMap = new HashMap();

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
	 * associates an editor with the controlling NodeAttribute that drives
	 * rendering of icons on palette
	 */
	protected static HashMap editorControllingNodeAttributeMap = new HashMap();

	/**
	 * associates an editor with the controlling EdgeAttribute that drives
	 * rendering of icons on palette
	 */
	protected static HashMap editorControllingEdgeAttributeMap = new HashMap();

	/**
	 * counter variable used in generating names for new networks invoked by the
	 * New->Network menu item.
	 */
	private static int networkNameCounter = 0;

	/**
	 * associates an editor type with a visual style name
	 */
	protected static HashMap editorTypeVisualStyleNameMap = new HashMap();

	/**
	 * associate a CyNetworkView with a ShapePalette
	 */
	protected static HashMap viewShapePaletteMap = new HashMap();

	/**
	 * associates a visual style with an editor
	 */
	protected static HashMap visualStyleEditorMap = new HashMap();

	/**
	 * associates a visual style with an editor type
	 */
	protected static HashMap visualStyleNameEditorTypeMap = new HashMap();

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

	public static final String BIOPAX_NODE_TYPE = "BIOPAX_NODE_TYPE";

	/**
	 * CytoscapeAttribute: EDGE_TYPE
	 * 
	 */
	public static final String EDGE_TYPE = "EDGE_TYPE";

	public static final String ANY_VISUAL_STYLE = "ANY_VISUAL_STYLE";

	public static final String DEFAULT_EDITOR_TYPE = "DefaultCytoscapeEditor";
	
	// AJK: 06/19/06 CytoscapeEditor class descriptor -- for checking against NETWORK_MODIFIED events
	public static final String CYTOSCAPE_EDITOR = "cytoscape.editor";

	/**
	 * main data structures for all node and edge attributes
	 */
	public static cytoscape.data.CyAttributes nodeAttribs = Cytoscape
			.getNodeAttributes();

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

		CytoscapeModifiedNetworkManager modifiedManager = new CytoscapeModifiedNetworkManager();

		NewNetworkAction newNetwork = new NewNetworkAction("Empty Network",
				CytoscapeEditorFactory.INSTANCE);
		Cytoscape.getDesktop().getCyMenus().getMenuBar().getMenu("File.New");
		Cytoscape.getDesktop().getCyMenus().addAction(newNetwork);

		// initially disable New Network creation until an editor is set
		
		Cytoscape.getDesktop().getCyMenus().getMenuBar().getMenu("File.New")
//				.setEnabled(false);
		// AJK: 06/05/06: for Cytoscape 2.3, enable this menu item
		        .setEnabled(true);

		// AJK: 06/07/06 BEGIN
		//     move DeleteSelected to the editor from CyMenus
		DeleteAction delete  = new DeleteAction();
		Cytoscape.getDesktop().getCyMenus().getMenuBar().getMenu("Edit");
		Cytoscape.getDesktop().getCyMenus().addAction(delete);
		
		// AJK: 06/10/06 BEGIN
		ShapePalette shapePalette = new ShapePalette();
		Cytoscape.getDesktop().getCytoPanel( SwingConstants.WEST ).add( "Editor", 
				shapePalette);
		

		// initially disable New Network creation until an editor is set
		
		// AJK: 06/07/06 END
			
		
		// AJK: 11/15/05 BEGIN
		// hide restoreAction while we are experimenting with Undo/Redo
        // AJK: 06/05/06: for Cytoscape 2.3, take restore off the menu
//		RestoreAction restoreAction = new RestoreAction();
//		Cytoscape.getDesktop().getCyMenus().addAction(restoreAction);
		// AJK: 11/15/05 END

		// AJK: 09/06/05 BEGIN
		// accommodate one undo manager per network view. No global one, no
		// accelerators
		// on menu items (no menu items, just an iconic button)
		// undo = new UndoManager();
		// undoAction = new UndoAction(undo);
		// redoAction = new RedoAction(undo);
		// undoAction.setRedoAction(redoAction);
		// redoAction.setUndoAction(undoAction);
		//
		//
		// undoManagerViewMap.put(Cytoscape.getCurrentNetworkView(), undo);
		// setCurrentUndoManager(undo);
		//
		// JMenuItem undoItem = new JMenuItem(undoAction);
		// JMenuItem redoItem = new JMenuItem(redoAction);
		//
		// undoItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(
		// java.awt.event.KeyEvent.VK_Z, ActionEvent.CTRL_MASK));
		// redoItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(
		// java.awt.event.KeyEvent.VK_Y, ActionEvent.CTRL_MASK));

		// Cytoscape.getDesktop().getCyMenus().getMenuBar().getMenu("Edit").add(
		// undoItem);
		// Cytoscape.getDesktop().getCyMenus().getMenuBar().getMenu("Edit").add(
		// redoItem);

		// AJK: 09/06/05 END

	}
	
	// AJK: 06/08/06 BEGIN
	//    initialize the editor as a side-effect of registering it

	public static void initializeEditor (String editorName, String networkEditAdapterName)
	{
		try {

			CytoscapeEditorManager.setSettingUpEditor(true);
			// setup a new editor
			CytoscapeEditor cyEditor = CytoscapeEditorFactory.INSTANCE.getEditor(editorName);

			// setup visual style for this editor

			String visualStyleName = CytoscapeEditorManager
					.getVisualStyleForEditorType(editorName);
			System.out.println("getting visual style for: " + visualStyleName);
			if ((visualStyleName != null)
					&& (!(visualStyleName
							.equals(CytoscapeEditorManager.ANY_VISUAL_STYLE)))) {
				VisualMappingManager manager = Cytoscape.getDesktop()
						.getVizMapManager();
				CalculatorCatalog catalog = manager.getCalculatorCatalog();
				VisualStyle existingStyle = catalog
						.getVisualStyle(visualStyleName);
				System.out.println("Got visual style: " + existingStyle);
				System.out.println("getting visual style for editor: "
						+ editorName);
				if (existingStyle != null) {
					manager.setVisualStyle(existingStyle);
					// AJK: 10/15/05 set editor for visual style
					CytoscapeEditorManager.setEditorForVisualStyle(
							existingStyle, cyEditor);
				}
			}

			// AJK: 09/19/05 END

			CytoscapeEditorManager.setCurrentEditor(cyEditor);

			// AJK: 06/15/06 don't initialize controls until after CYTOSCAPE
			//         is initialized, just build the visual style for the editor
//			cyEditor.initializeControls(null);
			cyEditor.buildVisualStyle();

			CytoscapeEditorManager.setSettingUpEditor(false);

			// enable the "File.New" submenu
			Cytoscape.getDesktop().getCyMenus().getMenuBar()
					.getMenu("File.New").setEnabled(true);

		} catch (InvalidEditorException ex) {
			// TODO: put some error handling here
			CytoscapeEditorManager.setSettingUpEditor(false);
			ex.printStackTrace();
		}		
	}
	
	// AJK: 06/08/06 END

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
	 * of editor types maintained by the CytoscapeEditorFactory. sets up menu
	 * sub-items for invoking the editor
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

	public static void register(String editorName,
			String networkEditAdapterName, String visualStyleName) {
		register(editorName, networkEditAdapterName, 
				CytoscapeEditorManager.NODE_TYPE,
				CytoscapeEditorManager.EDGE_TYPE, 
				visualStyleName);
//		System.out.println("Setting editor type: " + editorName
//				+ " for visual style: " + visualStyleName);
//		CytoscapeEditorManager.setEditorTypeForVisualStyleName(visualStyleName,
//				editorName);
//		// AJK: 06/10/06 initialize editor
//		CytoscapeEditorManager.initializeEditor(editorName, networkEditAdapterName);
	}

	public static void register(String editorName,
			String networkEditAdapterName, String controllingNodeAttribute,
			String controllingEdgeAttribute) {
		register(editorName, networkEditAdapterName, controllingNodeAttribute, 
				controllingEdgeAttribute, CytoscapeEditorManager.ANY_VISUAL_STYLE);
//		CytoscapeEditorManager.setControllingNodeAttribute(editorName,
//				controllingNodeAttribute);
//		CytoscapeEditorManager.setControllingEdgeAttribute(editorName,
//				controllingEdgeAttribute);
//		// AJK: 06/10/06 initialize editor
//		CytoscapeEditorManager.initializeEditor(editorName, networkEditAdapterName);
	}

	public static void register(String editorName,
			String networkEditAdapterName, String controllingNodeAttribute,
			String controllingEdgeAttribute, String visualStyleName) {
		CytoscapeEditorManager.setNetworkEditEventAdapterType(editorName,
				networkEditAdapterName);
		CytoscapeEditorManager.setControllingNodeAttribute(editorName,
				controllingNodeAttribute);
		CytoscapeEditorManager.setControllingEdgeAttribute(editorName,
				controllingEdgeAttribute);
		CytoscapeEditorManager.setEditorTypeForVisualStyleName(visualStyleName,
				editorName);
		CytoscapeEditorManager.setVisualStyleNameForEditorType(editorName, 
				visualStyleName);
		// AJK: 06/10/06 initialize editor
		CytoscapeEditorManager.initializeEditor(editorName, networkEditAdapterName);
	}

	/**
	 * checks to see if there are already right (context) menu items defined for
	 * nodes and edges in the current view. If false, then the calling method
	 * can safely add items to right (context) menu, i.e. there is no
	 * duplication of menu items.
	 * 
	 * @param view
	 *            the Network View being checked
	 * @return true if there are already context menu items defined for nodes
	 *         and edges in the current view, false otherwise.
	 */
	public static boolean hasContextMethods(CyNetworkView view) {
		// AJK: 04/26/06 update for ding renderer
		Object[] methods = view.getContextMethods("class ding.view.DNodeView",
				false);
		// System.out.println("Checking for context methods: Methods[] = "
		// + methods);
		if (methods != null) {
			for (int i = 0; i < methods.length; i++) {
				Object[] methodObj = (Object[]) methods[i];
				String methodClassName = methodObj[0].toString();

				// System.out.println("methodClassName = " + methodClassName);
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
	 * <li>checks to see if context methods are already loadedfor the view. If
	 * not, then they aer created.
	 * <li>sets dimensions, background, layers, camera, coloring for canvas
	 * <li>sets up a NetworkEditEventHandler for the view
	 * </ul>
	 * 
	 * @param newView
	 *            the NetworkView being created
	 */
	public static void setupNewNetworkView(CyNetworkView newView) {

		// AJK: 11/25/05 comment this out. Don't put delete items on
		// AJK: 04/27/06 uncomment to test for new ding renderer
//		if (!hasContextMethods(newView)) {
//			newView.addContextMethod("class ding.view.DNodeView",
//					"cytoscape.editor.actions.NodeAction",
//					"getContextMenuItem", new Object[] { newView },
//					CytoscapeInit.getClassLoader());
//
//			// newView.addContextMethod("class ding.view.DEdgeView",
//			// "cytoscape.editor.actions.EdgeAction",
//			// "getContextMenuItem", new Object[] { newView },
//			// CytoscapeInit.getClassLoader());
//		}

		// AJK: 09/09/05: BEGIN
		// comment this out; just use newView's bounds to set bounds for canvas
		/*
		 * int canvasWidth = Cytoscape.getDesktop().getWidth() -
		 * Cytoscape.getDesktop().getNetworkPanel().getWidth();
		 * 
		 * int canvasHeight = Cytoscape.getDesktop().getHeight() -
		 * Cytoscape.getDesktop().getCyMenus().getToolBar().getHeight();
		 */

		CytoscapeEditor cyEditor = CytoscapeEditorManager.getCurrentEditor();

		// AJK: 04/02/06 BEGIN
		// PhoebeCanvas canvas = ((PhoebeCanvas) ((PhoebeNetworkView) newView)
		// .getCanvas());

		GraphPerspective wiw = newView.getGraphPerspective();
		ding.view.DGraphView wiwx = (DGraphView) newView;
		ding.view.InnerCanvas canvas = wiwx.getCanvas();

		// AJK: 04/02/06 END

		NetworkEditEventAdapter event = CytoscapeEditorManager
				.getViewNetworkEditEventAdapter(newView);
		if (event == null) {
			event = CytoscapeEditorFactory.INSTANCE
					.getNetworkEditEventAdapter(cyEditor);
			CytoscapeEditorManager.setViewNetworkEditEventAdapter(newView,
					event);

			// AJK: 04/02/06 comment out eventstart?
			// event.start((PGraphView) newView);
			canvas.addPhoebeCanvasDropListener(event);

			// AJK: 09/09/05: BEGIN
			// setup listeners for changes to attributes
			CyNetwork net = newView.getNetwork();
			// nodeAttribs = Cytoscape.getNodeAttributes();
			// CyData nodeAttribs = net.getNodeData();
			nodeAttribs.getMultiHashMap().addDataListener(event);

			// edgeAttribs = Cytoscape.getEdgeAttributes();
			edgeAttribs.getMultiHashMap().addDataListener(event);

			// AJK: 09/09/05: END
		}

		// canvas.getLayer().setBounds(0, 0, canvasWidth, canvasHeight);
		// canvas.getLayer().setBounds(0, 0, newView.getComponent().getWidth() /
		// 2,
		// newView.getComponent().getHeight() / 2);

		//	

		// AJK: 04/02/06 BEGIN
		// canvas.getLayer().setBounds(newView.getComponent().getBounds());
		canvas.getBounds();
		// AJK: 04/02/06 END

		// Color myColor = new Color(225, 225, 250);
		Color myColor = new Color(225, 250, 200);

		// canvas.setBackground(myColor);
		// canvas.getCamera().setPaint(myColor);

		canvas.setEnabled(true);

		// // set the visualStyle being used as the network's default visual
		// style
		// VisualStyle style = newView.getVisualStyle();
		// if (style != null)
		// {
		// String styleName = style.getName();
		// setVisualStyleforNetwork (newView.getNetwork().getIdentifier(),
		// styleName);
		//			
		// }

	}

	/**
	 * this method is called when a new editor is set for Cytoscape it loops
	 * through the existing network views and for each view stops the existing
	 * network edit event handler and creates a new edit event handler that
	 * corresponds to the new editor type AJK: 10/19/05 obsoleted, due to design
	 * decision to tie event handler to a specific view AJK: 10/19/05 see
	 * setEventHandlerForView() below
	 */
	public static void resetEventHandlerForExistingViews() {
		Map viewMap = Cytoscape.getNetworkViewMap();
		Set keySet = viewMap.keySet();
		Iterator it = keySet.iterator();

		while (it.hasNext()) {
			Object key = it.next();
			Object entryObj = viewMap.get(key);
			// System.out.println("Got entry obj = " + entryObj);
			// if (entryObj instanceof PhoebeNetworkView) {

			// PhoebeNetworkView entryView = (PhoebeNetworkView) entryObj;
			// PhoebeCanvas canvas = ((PhoebeCanvas) entryView.getCanvas());
			DGraphView entryView = (DGraphView) entryObj;
			InnerCanvas canvas = ((InnerCanvas) entryView.getCanvas());
			NetworkEditEventAdapter oldEvent = CytoscapeEditorManager
					.getViewNetworkEditEventAdapter((CyNetworkView) entryView);

			if (oldEvent != null) // remove event from this canvas
			{

				// canvas.removeInputEventListener(oldEvent);
				canvas.removeMouseListener(oldEvent);
				canvas.removeMouseMotionListener(oldEvent);
				canvas.removePhoebeCanvasDropListener(oldEvent);
			}

			CytoscapeEditor cyEditor = CytoscapeEditorManager
					.getCurrentEditor();
			NetworkEditEventAdapter newEvent = CytoscapeEditorFactory.INSTANCE
					.getNetworkEditEventAdapter(cyEditor);
			CytoscapeEditorManager.setViewNetworkEditEventAdapter(
					(CyNetworkView) entryView, newEvent);

			newEvent.start((DGraphView) entryView);
			canvas.addPhoebeCanvasDropListener(newEvent);
			canvas.addMouseListener(newEvent);
			canvas.addMouseMotionListener(newEvent);
			System.out.println("Mouse and MotionListeners added to " + canvas);
			System.out.println("Canvas has total number of Listeners = "
					+ canvas.getMouseListeners().length);
		}
		// }
	}

	public static void setEventHandlerForView(CyNetworkView view) {

		// if (view instanceof PhoebeNetworkView) {

		// PhoebeNetworkView thisView = (PhoebeNetworkView) view;
		// PhoebeCanvas canvas = ((PhoebeCanvas) thisView.getCanvas());
		DGraphView thisView = (DGraphView) view;
		InnerCanvas canvas = ((InnerCanvas) thisView.getCanvas());
		NetworkEditEventAdapter oldEvent = CytoscapeEditorManager
				.getViewNetworkEditEventAdapter((CyNetworkView) thisView);

		if (oldEvent != null) // remove event from this canvas
		{

			// canvas.removeInputEventListener(oldEvent);
			canvas.removeMouseListener(oldEvent);
			canvas.removeMouseMotionListener(oldEvent);
			canvas.removePhoebeCanvasDropListener(oldEvent);
		}

		CytoscapeEditor cyEditor = CytoscapeEditorManager.getCurrentEditor();
		NetworkEditEventAdapter newEvent = CytoscapeEditorFactory.INSTANCE
				.getNetworkEditEventAdapter(cyEditor);
		CytoscapeEditorManager.setViewNetworkEditEventAdapter(
				(CyNetworkView) thisView, newEvent);

		// AJK: 11/20/05 set View for event handler, to help trap events that
		// are outside
		// current view
		newEvent.setView((DGraphView) view);

		newEvent.start((DGraphView) thisView);
		canvas.addPhoebeCanvasDropListener(newEvent);

		// adding mouseListeners here is redundant, since it is done in
		// event.start() method
		// canvas.addMouseListener(newEvent);
		// canvas.addMouseMotionListener(newEvent);
		System.out.println("Mouse and MotionListeners added to " + canvas);
		System.out.println("Canvas has total number of Mouse Listeners = "
				+ canvas.getMouseListeners().length);
		// }
	}

	/**
	 * associates a NetworkEditEventHandler class with a CytoscapeEditor class.
	 * then, when the CytoscapeEditor is invoked, the corresponding
	 * NetworkEditEventHandler will be built
	 * 
	 * @param editorType
	 *            the type of the CytoscapeEditor that this
	 *            NetworkEditEventHandler is associated with
	 * @param event
	 *            the type of the NetworkEditEventHandler to be built when the
	 *            CytoscapeEditor is instantiated
	 */
	public static void setNetworkEditEventAdapterType(String editorType,
			String event) {
		editorTypeEventAdapterMap.put(editorType, event);
	}

	/**
	 * retrieves the type of the adapter that handles drags/drops, other mouse
	 * events on the canvas, for the specified type of CytoscapeEditor
	 * 
	 * @param editorType
	 *            the type of the CytoscapeEditor that this
	 *            NetworkEditEventHandler is associated with
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
	 * @return Returns the editorEventAdapterMap, which is used to associate a
	 *         type of CytoscapeEditor with a type of NetworkEditEventHandler
	 */
	public static HashMap getEditorTypeEventAdapterMap() {
		return editorTypeEventAdapterMap;
	}

	/**
	 * @return Returns the editorNetworkMap, which associates a Network with its
	 *         editor
	 */
	public static HashMap getEditorNetworkMap() {
		return editorNetworkMap;
	}

	/**
	 * @return Returns the editorViewMap, which associates a NetworkView with
	 *         its Cytoscape editor
	 */
	public static HashMap getEditorViewMap() {
		return editorViewMap;
	}

	/**
	 * get the editor that is assigned to this network
	 * 
	 * @param net
	 *            a Cytoscape network
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
	 * returns the type of editor that is associated with a network
	 * 
	 * @param net
	 * @return
	 */
	public static String getEditorTypeForNetwork(CyNetwork net) {
		CytoscapeEditor ed = getEditorForNetwork(net);
		if (ed == null) {
			return null;
		} else {
			return ed.getEditorName();
		}
	}

	/**
	 * set the editor for a Cytoscape network
	 * 
	 * @param net
	 *            the Cytoscape network
	 * @param editor
	 *            the editor to be assigned to the Cytoscape network
	 */
	public static void setEditorForNetwork(CyNetwork net, CytoscapeEditor editor) {
		// AJK: 02/07/06 BUG, should be editorNetworkMap.
		// editorViewMap.put(net, editor);
		editorNetworkMap.put(net, editor);

	}

	/**
	 * 
	 * @param net
	 * @param editorType
	 */
	public static void setEditorTypeForNetwork(CyNetwork net, String editorType) {
		// TODO: need to setup a new editor for the input network
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
	 * get the ShapePalette that is associated with a CyNetworkView needed when
	 * a view changes
	 * 
	 * @param view
	 * @return
	 */
	public static ShapePalette getShapePaletteForView(CyNetworkView view) {
		return (ShapePalette) viewShapePaletteMap.get(view);
	}

	public static void setShapePaletteForView(CyNetworkView view,
			ShapePalette shape) {
		viewShapePaletteMap.put(view, shape);
	}

	/**
	 * 
	 * @param editorType
	 * @return CytoscapeVisualStyle that is associated with this Editor type
	 */
	public static String getVisualStyleForEditorType(String editorType) {
		Object obj = editorTypeVisualStyleNameMap.get(editorType);
		if (obj != null) {
			return obj.toString();
		} else {
			return null;
		}
	}

	/**
	 * sets the visual style that is to be associated with an editor type. this
	 * enables visual style to be automatically loaded when an editor is set
	 * 
	 * @param editorType
	 * @param vizStyle
	 */
	public static void setVisualStyleNameForEditorType(String editorType,
			String vizStyle) {
		editorTypeVisualStyleNameMap.put(editorType, vizStyle);
	}

	/**
	 * get the editor that is associated with the visual style this enables
	 * editor to be switched when a visual style changes
	 * 
	 * @param style
	 * @return
	 */
	public static CytoscapeEditor getEditorForVisualStyle(VisualStyle style) {
		return ((CytoscapeEditor) visualStyleEditorMap.get(style));
	}

	/**
	 * 
	 * set the editor that is associated with the visual style this enables
	 * editor to be switched when a visual style changes *
	 * 
	 * @param style
	 * @param editor
	 */
	public static void setEditorForVisualStyle(VisualStyle style,
			CytoscapeEditor editor) {
		visualStyleEditorMap.put(style, editor);
	}

	/**
	 * get the editor type that is associated with the visual style this enables
	 * editor to be switched when a visual style changes, in the situation where
	 * editor and visual style have not yet been instantiated for that visual
	 * style
	 * 
	 * @param styleName
	 * @return
	 */
	public static String getEditorTypeForVisualStyleName(String styleName) {
		Object editorType = visualStyleNameEditorTypeMap.get(styleName);
		if (editorType != null) {
			return editorType.toString();
		} else {
			return null;
		}
	}

	/**
	 * 
	 * get the editor type that is associated with the visual style this enables
	 * editor to be switched when a visual style changes, in the situation where
	 * an editor and visual style have not yet been instantiated for that visual
	 * style
	 * 
	 * @param styleName
	 * @param editorType
	 */
	public static void setEditorTypeForVisualStyleName(String styleName,
			String editorType) {
		visualStyleNameEditorTypeMap.put(styleName, editorType);
	}

	// /**
	// *
	// * @param network
	// * @return CytoscapeVisualStyle that is associated with this CyNetwork
	// */
	// public static String getVisualStyleForNetwork (String networkName) {
	// Object obj = networkVisualStyleMap.get(networkName);
	// if (obj != null) {
	// return obj.toString();
	// } else {
	// return null;
	// }
	// }
	//
	// /**
	// * sets the visual style that is to be associated with a network. this
	// *
	// * @param networkName
	// * @param vizStyle
	// */
	// public static void setVisualStyleforNetwork(String networkName,
	// String vizStyle) {
	// networkVisualStyleMap.put(networkName, vizStyle);
	// }
	//	
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
		// System.out.println("HiddenNodes for Network: " + net + " = " +
		// hiddenNodesList);
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
		// System.out.println("HiddenEdges for Network: " + net + " = " +
		// hiddenEdgesList);
	}
	

	/**
	 * 
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
	 * gets the controlling NodeAttribute that drives rendering of node icon in
	 * editor palette
	 * 
	 * @param editor
	 * @return name of controlling NodeAttribute
	 */
	public static String getControllingNodeAttribute(CytoscapeEditor editor) {
		String editorName = editor.getEditorName();
		Object obj = editorControllingNodeAttributeMap.get(editorName);
		if (obj != null) {
			return obj.toString();
		}
		return null;
	}

	/**
	 * gets the controlling EdgeAttribute that drives rendering of node icon in
	 * editor palette
	 * 
	 * @param editor
	 * @return name of controlling EdgeAttribute
	 */
	public static String getControllingEdgeAttribute(CytoscapeEditor editor) {
		System.out.println("get controlling edge attribute for editor: "
				+ editor);
		String editorName = editor.getEditorName();
		Object obj = editorControllingEdgeAttributeMap.get(editorName);
		System.out.println("returned: " + obj);
		if (obj != null) {
			return obj.toString();
		}
		return null;
	}

	/**
	 * sets the controlling NodeAttribute that drives rendering of node icon in
	 * editor palette
	 * 
	 * @param editorName
	 * @param attribute
	 */
	public static void setControllingNodeAttribute(String editorName,
			String attribute) {
		editorControllingNodeAttributeMap.put(editorName, attribute);
	}

	/**
	 * sets the controlling EdgeAttribute that drives rendering of node icon in
	 * editor palette
	 * 
	 * @param editorName
	 * @param attribute
	 */
	public static void setControllingEdgeAttribute(String editorName,
			String attribute) {
		System.out.println("Setting controlling edge attribute: " + attribute
				+ " for editor: " + editorName);
		editorControllingEdgeAttributeMap.put(editorName, attribute);
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
	 * get the UndoManager that is assigned to this CyNetworkView
	 * 
	 * @param view
	 *            a CyNetworkView
	 * @return the editor assigned to this CyNetworkView
	 */
	public static UndoManager getUndoManagerForView(CyNetworkView view) {
		Object obj = undoManagerViewMap.get(view);
		// System.out.println ("Get undoManager for view: " + view + " = " +
		// obj);
		if (obj != null) {
			if (obj instanceof UndoManager) {
				return (UndoManager) obj;
			}
		}
		return null;
	}

	/**
	 * set the UndoManager for a CyNetworkView
	 * 
	 * @param view
	 *            the CyNetworkView
	 * @param editor
	 *            the editor to be assigned to the CyNetworkView
	 */
	public static void setUndoManagerForView(CyNetworkView view,
			UndoManager undo) {
		undoManagerViewMap.put(view, undo);
		// System.out.println("Setting undo manager for view: " + view + " = " +
		// undo);
	}

	/**
	 * set the UndoAction for a view
	 * 
	 * @param view
	 * @param undo
	 */
	public static void setUndoActionForView(CyNetworkView view, UndoAction undo) {
		undoActionViewMap.put(view, undo);
		// System.out.println("Setting undo action for view: " + view + " = " +
		// undo);
	}

	/**
	 * get the UndoAction for a view
	 * 
	 * @param view
	 * @return
	 */
	public static UndoAction getUndoActionForView(CyNetworkView view) {
		Object obj = undoActionViewMap.get(view);
		// System.out.println ("Get action for view: " + view + " = " +
		// obj);
		if (obj != null) {
			if (obj instanceof UndoAction) {
				return (UndoAction) obj;
			}
		}
		return null;
	}

	/**
	 * get the RedoAction for a view
	 * 
	 * @param view
	 * @return
	 */
	public static RedoAction getRedoActionForView(CyNetworkView view) {
		Object obj = redoActionViewMap.get(view);
		// System.out.println ("Get redoAction for view: " + view + " = " +
		// obj);
		if (obj != null) {
			if (obj instanceof RedoAction) {
				return (RedoAction) obj;
			}
		}
		return null;
	}

	public static void setRedoActionForView(CyNetworkView view, RedoAction redo) {
		redoActionViewMap.put(view, redo);
		// System.out.println("Setting redo action for view: " + view + " = " +
		// redo);
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
		// AJK: 11/17/05 comment this out for 2.2 release. Deal with undo later
		// cytoscape.util.UndoManager undo = Cytoscape.getDesktop().undo;
		// undo.addEdit (edit, Cytoscape.getCurrentNetworkView());
		// AJK: 02/22/06 
	       System.out.println ("adding undoableEdit to undoManager: " + 
	        		Cytoscape.getDesktop().undo);
	 
		Cytoscape.getDesktop().undo.addEdit(edit);
	}

	/**
	 * TODO: this may be superfluous and could be tossed
	 * 
	 * @return
	 */
	public static String getNetworkClipBoard() {
		return networkClipBoard;
	}

	/**
	 * TODO: this may be superfluous and could be tossed
	 * 
	 * @param id
	 */
	public static void setNetworkClipBoard(String id) {
		networkClipBoard = id;
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
	 * counter used for generating unique network names
	 * 
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
	 * @return Returns the currentUndoManager.
	 */
	public static UndoManager getCurrentUndoManager() {
		// return currentUndoManager;
		return getUndoManagerForView(Cytoscape.getCurrentNetworkView());
	}

	/**
	 * @param currentUndoManager
	 *            The currentUndoManager to set.
	 */
	public static void setCurrentUndoManager(UndoManager currentUndoManager) {
		CytoscapeEditorManager.currentUndoManager = currentUndoManager;
		CyNetworkView view = Cytoscape.getCurrentNetworkView();
		if (view != null) {
			CytoscapeEditorManager.setUndoManagerForView(view,
					currentUndoManager);
		}
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
	 * @return Returns the defaultEditor.
	 */
	public static CytoscapeEditor getDefaultEditor() {
		return defaultEditor;
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