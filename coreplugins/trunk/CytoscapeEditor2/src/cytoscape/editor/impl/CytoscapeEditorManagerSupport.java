/*
 * Created on Jul 30, 2005
 *
 */
package cytoscape.editor.impl;

import giny.model.GraphPerspectiveChangeEvent;
import giny.model.GraphPerspectiveChangeListener;
import giny.model.Node;
import giny.view.NodeView;

import java.awt.geom.Point2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;

import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.undo.AbstractUndoableEdit;

import cytoscape.CyEdge;
import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.data.FlagEvent;
import cytoscape.data.FlagEventListener;
import cytoscape.editor.CytoscapeEditor;
import cytoscape.editor.CytoscapeEditorFactory;
import cytoscape.editor.CytoscapeEditorManager;
import cytoscape.editor.InvalidEditorException;
import cytoscape.view.CyNetworkView;
import cytoscape.view.CytoscapeDesktop;
import cytoscape.view.cytopanels.CytoPanelListener;
import cytoscape.view.cytopanels.CytoPanelState;
import cytoscape.visual.VisualMappingManager;
import cytoscape.visual.VisualStyle;

/**
 * provides non-static methods needed by the CytoscapeEditorManager, in
 * particular those methods associated with the PropertyChangeListener class
 * 
 * @author Allan Kuchinsky, Agilent Technologies
 * @version 1.0
 * @see CytoscapeEditorManager
 * 
 */
public class CytoscapeEditorManagerSupport implements PropertyChangeListener,
		ChangeListener, GraphPerspectiveChangeListener, FlagEventListener
		// AJK: 06/10/06
		, CytoPanelListener
// ,MouseMotionListener

{

	/**
	 * register interest in NETWORK_VIEW_FOCUSED and NETWORK_VIEW_CREATED events
	 * AJK: 10/15/05 register interestin visual style change event
	 * 
	 */
	public CytoscapeEditorManagerSupport() {
		super();

		Cytoscape.getDesktop().getSwingPropertyChangeSupport()
				.addPropertyChangeListener(
						CytoscapeDesktop.NETWORK_VIEW_FOCUSED, this);
		Cytoscape.getDesktop().getSwingPropertyChangeSupport()
				.addPropertyChangeListener(
						CytoscapeDesktop.NETWORK_VIEW_CREATED, this);
		Cytoscape.getSwingPropertyChangeSupport().addPropertyChangeListener(
				this);

		// AJK: 10/15/05 register interestin visual style change event
		Cytoscape.getDesktop().getVizMapManager().addChangeListener(this);

		// AJK: 06/10/06 listen for change of tab selection on CytoPanel.WEST
		Cytoscape.getDesktop().getCytoPanel(SwingConstants.WEST)
				.addCytoPanelListener(this);

		// AJK: 11/25/05 register interest in mouse motion events
		// so that we can trap out dragging of nodes when an edge
		// is being drawn
		// Cytoscape.getCurrentNetworkView().getComponent().addMouseMotionListener(this);

	}

	// AJK: 05/29/06 BEGIN
	// rewrite stateChanged() so that editor is brought up by visual style

	// AJK: 10/15/05 BEGIN
	// handle change of visual style
	// if new visual style has an editor associated with it, then switch to it.
	// public void stateChanged(ChangeEvent e) {
	//
	// boolean newEditor = false;
	//
	// System.out.println("Got stateChange event: " + e);
	//
	// if (!CytoscapeEditorManager.isEditingEnabled()) {
	// return;
	// }
	// // CytoscapeEditor oldEditor = CytoscapeEditorManager.getCurrentEditor();
	//
	// VisualMappingManager VMM = (VisualMappingManager) e.getSource();
	// if (VMM != null) {
	// VisualStyle style = VMM.getVisualStyle();
	//
	// CytoscapeEditor editorForStyle = CytoscapeEditorManager
	// .getEditorForVisualStyle(style);
	// if (editorForStyle == null) {
	// // setup an editor for the visual style
	// String editorType = CytoscapeEditorManager
	// .getEditorTypeForVisualStyleName(style.getName());
	//
	// // AJK: 10/21/05 BEGIN
	// // modify: if no editor exists, then just use default editor
	// /*
	// * // if no editor exists for this visual type, then just
	// * disable // the old editor and return // except by shortcut,
	// * this network will not be editable unless // another editor is
	// * set if (editorType == null) { System.out.println("have visual
	// * style without editorType: " + style.getName()); if (oldEditor !=
	// * null) { oldEditor.disableControls(null); } int idx =
	// * Cytoscape.getDesktop().getCytoPanel(
	// * SwingConstants.WEST).indexOfComponent("Editor");
	// * System.out.println("index of current palette = " + idx); if
	// * (idx >= 0) { Cytoscape.getDesktop()
	// * .getCytoPanel(SwingConstants.WEST).remove(idx); System.out
	// * .println("removing palette at Cytopanel index: " + idx); }
	// * CytoscapeEditorManager.setCurrentEditor(null); return;
	// */
	// // AJK: 10/21/05 END
	// if (editorType == null) {
	// editorType = CytoscapeEditorManager.DEFAULT_EDITOR_TYPE;
	// }
	//
	// try {
	// editorForStyle = CytoscapeEditorFactory.INSTANCE
	// .getEditor(editorType);
	// CytoscapeEditorManager.setEditorForVisualStyle(style,
	// editorForStyle);
	// System.out.println("built new editor: " + editorForStyle
	// + ", for visual Style = " + style);
	// newEditor = true;
	// } catch (InvalidEditorException ex) {
	// System.out
	// .println("Error building editor for editor type = "
	// + editorType + ", error = " + ex);
	// ex.printStackTrace();
	// }
	// }
	// if (editorForStyle != null) {
	// CyNetworkView view = Cytoscape.getCurrentNetworkView();
	// CytoscapeEditor editorForView = CytoscapeEditorManager.getEditorForView(
	// Cytoscape.getCurrentNetworkView());
	// System.out.println ("Got editor for view: " + editorForView);
	//				
	//
	// if ((editorForView == editorForStyle) &&
	// (editorForView == CytoscapeEditorManager.getCurrentEditor())){
	// // AJK: 10/21/05 don't switch palette if visual style doesn't change
	// // and editor is unchanged
	//					
	// // AJK: 05/05/06 don't return, always initialize controls, because
	// // we may have same editor (DEFAULT_EDITOR_TYPE) but
	// // different visual style
	// // return;
	// }
	//				
	//								
	// if ((editorForView != null)
	// && (!CytoscapeEditorManager.isSettingUpEditor())) {
	//
	// System.out.println("Disabling controls for editor: " + editorForView);
	// editorForView.disableControls(null);
	// }
	// // AJK: 10/21/05 always build a new shape palette when changing
	// // visual styles
	// // if (newEditor) {
	// editorForStyle.initializeControls(null);
	// CytoscapeEditorManager.setEditorForView(view, editorForStyle);
	// CytoscapeEditorManager.setupNewNetworkView(view); // } else {
	// // editor.enableControls(null);
	// // }
	// CytoscapeEditorManager.setCurrentEditor(editorForStyle);
	// CytoscapeEditorManager.setEventHandlerForView(view);
	// }
	// }
	// }

	public void stateChanged(ChangeEvent e) {
		System.out.println("Got state change event from source: "
				+ e.getSource());

		if (!CytoscapeEditorManager.isEditingEnabled()) {
			return;
		}

		// AJK: 06/10/06 BEGIN
		// don't do any work building editor palette if editor tab is not
		// selected in CytoPanel
		int idx = Cytoscape.getDesktop().getCytoPanel(SwingConstants.WEST)
				.indexOfComponent("Editor");
		if (idx != Cytoscape.getDesktop().getCytoPanel(SwingConstants.WEST)
				.getSelectedIndex()) {
			return;
		}

		VisualMappingManager VMM = (VisualMappingManager) e.getSource();
		if (VMM != null) {
			updateEditorPalette(VMM.getVisualStyle());
		}
	}

	public void onComponentSelected(int componentIndex) {
		if (componentIndex == Cytoscape.getDesktop().getCytoPanel(
				SwingConstants.WEST).getSelectedIndex()) {
			updateEditorPalette(Cytoscape.getVisualMappingManager()
					.getVisualStyle());
		}
	}

	/**
	 * Notifies the listener on a change in the CytoPanel state.
	 * 
	 * @param newState
	 *            The new CytoPanel state - see CytoPanelState class.
	 */
	public void onStateChange(CytoPanelState newState) {
	};

	/**
	 * Notifies the listener when a component is added to the CytoPanel.
	 * 
	 * @param count
	 *            The number of components on the CytoPanel after the add.
	 */
	public void onComponentAdded(int count) {
	};

	/**
	 * Notifies the listener when a component is removed from the CytoPanel.
	 * 
	 * @param count
	 *            The number of components on the CytoPanel after the remove.
	 */
	public void onComponentRemoved(int count) {
	};

	public void updateEditorPalette(VisualStyle style) {
		
		// AJK: 06/16/06 only update palette after CYTOSCAPE_INITIALIZED
		if (!CytoscapeEditorManager.isEditingEnabled())
		{
			return;
		}

		String editorType = style.getName();
		CytoscapeEditor editorForStyle = null;

		try {
			editorForStyle = CytoscapeEditorFactory.INSTANCE
					.getEditor(editorType);
			CytoscapeEditorManager.setEditorForVisualStyle(style,
					editorForStyle);
			System.out.println("Editor for Style: " + style + " = "
					+ editorForStyle);
		} catch (InvalidEditorException ex) {
			editorType = CytoscapeEditorManager.DEFAULT_EDITOR_TYPE;
			try {
				editorForStyle = CytoscapeEditorFactory.INSTANCE
						.getEditor(editorType);
				CytoscapeEditorManager.setEditorForVisualStyle(style,
						editorForStyle);
			} catch (InvalidEditorException exe) {
				System.out.println("Error building editor for editor type = "
						+ editorType + ", error = " + ex);
				exe.printStackTrace();
			}
		}

		if (editorForStyle != null) {
			CyNetworkView view = Cytoscape.getCurrentNetworkView();
			CytoscapeEditor editorForView = CytoscapeEditorManager
					.getEditorForView(Cytoscape.getCurrentNetworkView());
			System.out.println("Got editor for view: " + editorForView);

			if ((editorForView != null)
					&& (!CytoscapeEditorManager.isSettingUpEditor())) {

				System.out.println("Disabling controls for editor: "
						+ editorForView);
				editorForView.disableControls(null);
			}
			System.out.println("Initializing controls for " + editorForStyle);
			editorForStyle.initializeControls(null);
			CytoscapeEditorManager.setEditorForView(view, editorForStyle);
			CytoscapeEditorManager.setupNewNetworkView(view);
			CytoscapeEditorManager.setCurrentEditor(editorForStyle);
			CytoscapeEditorManager.setEventHandlerForView(view);
		}

	}

	// AJK: 05/29/06 END

	public void onFlagEvent(FlagEvent e) {
		System.out.println("Got flagged object: " + e.getTarget());
	}

	// // AJK: 11/25/05 BEGIN
	// // set up mouse motion listeners to trap mouse drags when drawing edge
	// // so that node is not moved along with edge
	// public void mouseMoved (MouseEvent e)
	// {
	//		
	// }
	//	
	// public void mouseDragged (MouseEvent e)
	// {
	//
	// BasicNetworkEditEventHandler event =
	// (BasicNetworkEditEventHandler)
	// CytoscapeEditorManager.getViewNetworkEditEventAdapter(
	// Cytoscape.getCurrentNetworkView());
	// if (event.isEdgeStarted())
	// {
	// e.consume();
	// }
	// }
	//	
	// AJK: 11/25/05 END

	// AJK: 10/15/05 END

	public void propertyChange(PropertyChangeEvent e) {

		System.out.println("Got property change: " + e.getPropertyName());
		if (e.getPropertyName().equals(Cytoscape.NETWORK_CREATED)) {
			String netId = e.getNewValue().toString();
			CyNetwork net = Cytoscape.getNetwork(netId);
			// CyNetwork net = Cytoscape.getCurrentNetwork();
			net.addGraphPerspectiveChangeListener(this);
			net.addFlagEventListener(this);

			// System.out.println ("Added graph perspective change listener to:
			// " + net);
		}
		if (e.getPropertyName().equals(CytoscapeDesktop.NETWORK_VIEW_CREATED)) {

		} else if (e.getPropertyName().equals(Cytoscape.ATTRIBUTES_CHANGED)) {
			// implement ATTRIBUTES_CHANGED handler
			// System.out.println("Property changed: " + e.getPropertyName());
			// System.out.println("Old value = " + e.getOldValue());
			// System.out.println("New value = " + e.getNewValue());

		}
		// redraw graph if the network is modified, e.g. by an undoable edit
		else if (e.getPropertyName().equals(Cytoscape.NETWORK_MODIFIED)) {
			Cytoscape.getCurrentNetworkView().redrawGraph(true, true);
		}
		
		// AJK: 06/15/06: enable editing once Cytoscape has been initialized
		else if (e.getPropertyName().equals(Cytoscape.CYTOSCAPE_INITIALIZED))
		{
			CytoscapeEditorManager.setEditingEnabled(true);
		}

		else if (e.getPropertyName().equals(
				CytoscapeDesktop.NETWORK_VIEW_FOCUSED)) {

			CyNetworkView view = Cytoscape.getCurrentNetworkView();
			CytoscapeEditor cyEditor = CytoscapeEditorManager
					.getEditorForView(view);

			// AJK: 09/06/05 BEGIN
			// setup an undo manager for this network view

			// Object undoObj =
			// CytoscapeEditorManager.getUndoManagerForView(view);
			// if (undoObj instanceof UndoManager) {
			// CytoscapeEditorManager
			// .setCurrentUndoManager((UndoManager) undoObj);
			// } else {
			// UndoManager newUndo = new UndoManager();
			// CytoscapeEditorManager.setUndoManagerForView(view, newUndo);
			// // System.out.println ("SetUndoManagerForView: " +
			// // Cytoscape.getCurrentNetworkView());
			// CytoscapeEditorManager.setCurrentUndoManager(newUndo);
			// UndoAction undoAction = new UndoAction(newUndo);
			// CytoscapeEditorManager.setUndoActionForView(view, undoAction);
			// RedoAction redoAction = new RedoAction(newUndo);
			// undoAction.setRedoAction(redoAction);
			// redoAction.setUndoAction(undoAction);
			// CytoscapeEditorManager.setRedoActionForView(view, redoAction);
			// System.out.println("Set redo manager for view: " + redoAction);
			// }
			// AJK: 09/06/05 END

			// AJK: 11/15/05 BEGIN
			// update IndexOfNextAdd in Cytoscape's UndoManager to point to the
			// indexOfNextAdd
			// for this view
			cytoscape.util.UndoManager undo = CytoscapeDesktop.undo;
			// AJK: 06/07/06 clear UndoManager when the view changes

			undo.discardAllEdits();

			// set the buttons on the shapePalette to undo, redo actions
			ShapePalette palette = CytoscapeEditorManager
					.getShapePaletteForView(view);

			if (palette != null) {
				// AJK: 10/24/05: comment this out, undo functionality is moving
				// to Cytoscape
				// palette.getUndoButton().setAction(CytoscapeEditorManager.getUndoActionForView(view));
				// palette.getRedoButton().setAction(CytoscapeEditorManager.getRedoActionForView(view));
			}

			// AJK: 05/19/06 BEGIN
			// try to set editor from the visual style
			// String visualStyle =
			// CytoscapeEditorManager.getVisualStyleForNetwork
			// (view.getNetwork().getTitle()
			// .substring(0,
			// view.getNetwork().getTitle().lastIndexOf(".")));
			// System.out.println ("Getting visual style for network: " +
			// view.getNetwork().getTitle()
			// .substring(0,
			// view.getNetwork().getTitle().lastIndexOf(".")));
			// System.out.println("Got network's visual style: " + visualStyle);
			// if (visualStyle != null)
			// {
			// String editorType =
			// CytoscapeEditorManager.getEditorTypeForVisualStyleName(visualStyle);
			// System.out.println("Got visual style's editor type: " +
			// editorType);
			// if (editorType == null) // just try getting editor type with same
			// name as visual style
			// {
			// editorType = visualStyle;
			// }
			// if (editorType != null)
			// {
			// try
			// {
			// cyEditor = CytoscapeEditorFactory.INSTANCE.getEditor(editorType);
			// System.out.println("Got editor for editorType: " + editorType
			// + " = " + cyEditor);
			// }
			// catch (InvalidEditorException ex) {}
			// if (cyEditor == null)
			// {
			// try
			// {
			// cyEditor = CytoscapeEditorFactory.INSTANCE.getEditor
			// (CytoscapeEditorManager.DEFAULT_EDITOR_TYPE);
			// }
			// catch (InvalidEditorException ex) {}
			// }
			// }
			// }
			// // AJK: 05/19/06 END
			// AJK: 05/22/06 BEGIN
			// try to create an editor for the network view
			// if (cyEditor == null) {
			// VisualStyle style = view.getVisualStyle();
			// SetEditorAction setEditor = new SetEditorAction(
			// style.getName(), CytoscapeEditorFactory.INSTANCE);
			// setEditor.actionPerformed(null);
			// CytoscapeEditorManager.setupNewNetworkView(view);
			// return;
			// }
			if (cyEditor == null) {
				try {
					cyEditor = CytoscapeEditorFactory.INSTANCE
							.getEditor(CytoscapeEditorManager.DEFAULT_EDITOR_TYPE);
				} catch (InvalidEditorException ex) {
				}
			} // AJK: 05/22/06 END

			if (cyEditor == null) {
				cyEditor = CytoscapeEditorManager.getCurrentEditor();
				if (cyEditor == null) {
					// this would be because no editor has been set yet. Just
					// return
					return;
				} else {
					// at this point there is an editor but it is not assigned
					// to this
					// view
					// this is probably the case if we are loading a network,
					// rather
					// than creating a new one
					// in this case, we need to setup the network view, which
					// sets all
					// the event handler, etc.
					CytoscapeEditorManager.setupNewNetworkView(view);
				}
			}
		}
	}

	/**
	 * sets up the undo/redo operations for an Add of a node or edge
	 * 
	 * @param net
	 *            network node/edge has been added to
	 * @param node
	 *            the added node (null if the add operation was for an edge)
	 * @param edge
	 *            the added edge (null if the add operation was for a node)
	 */
	public void setupUndoableAdditionEdit(CyNetwork net, CyNode node,
			CyEdge edge) {
		final boolean isNode = (node != null) ? true : false;
		final int[] nodes = new int[1];
		final int[] edges = new int[1];
		final CyNetwork cyNet = net;

		// cache the coordinate positions so that they can be restored upon a redo
		final CyNetworkView networkView = Cytoscape.getCurrentNetworkView();
		final HashMap coords = new HashMap();
		final Node[] cyNodes = new Node[1];

		if (node != null) {
			int nodeIdx = node.getRootGraphIndex();
			nodes[0] = nodeIdx;
			
			NodeView nview = networkView.getNodeView(node);
			coords.put(node.getIdentifier(), nview.getOffset());
			System.out.println(
					"added node: " + node + " at coordinates " +
					nview.getOffset());
			cyNodes[0] = node;
		}

		if (edge != null) {
			int edgeIdx = edge.getRootGraphIndex();
			edges[0] = edgeIdx;
		}

		// setup the clipboard and undo manager to be able to undo the deletion
		// operation
		CytoscapeEditorManager.getNodeClipBoard().elements(nodes);
		CytoscapeEditorManager.getEdgeClipBoard().elements(edges);
		CytoscapeEditorManager.setNetworkClipBoard(cyNet.getIdentifier());

		System.out.println("Adding an ADD edit to " + cyNet);
		CytoscapeEditorManager.addEdit(new AbstractUndoableEdit() {

			final String network_id = cyNet.getIdentifier();

			public String getPresentationName() {
				// AJK: 10/21/05 return null as presentation name because we are
				// using iconic buttons
				return "Add";
				// return null;
			}

			public String getRedoPresentationName() {
				if (isNode)
					// AJK: 10/21/05 return null as presentation name because we
					// are using iconic buttons
					return "Redo: Added Node";
				// return null;
				else
					// AJK: 10/21/05 return null as presentation name because we
					// are using iconic buttons
					return "Redo: Added Edge";
				// return null;
			}

			public String getUndoPresentationName() {

				if (isNode)
					// AJK: 10/21/05 return null as presentation name because we
					// are using iconic buttons
					return "Undo: Added Node";
				// return null;
				else
					// AJK: 10/21/05 return null as presentation name because we
					// are using iconic buttons
					return "Undo: Added Edge";
				// return null;

			}

			public void undo() {
				// removes the removed nodes and edges from the network
				System.out.println("Trying to UNDO add on: "
						+ Cytoscape.getNetwork(network_id));
				super.undo();
				CyNetwork network = Cytoscape.getNetwork(network_id);
				if (network != null) {
					network.hideEdges(edges);
					network.hideNodes(nodes);
					CytoscapeEditorManager.getNodeClipBoard().elements(nodes);
					CytoscapeEditorManager.getEdgeClipBoard().elements(edges); // sets
					// elements
				}

			}

			public void redo() {
				super.redo();
				CyNetwork network = Cytoscape.getNetwork(network_id);
				if (network != null) {
					network.restoreNodes(nodes);
					network.restoreEdges(edges);
					// signal end to Undo Manager; this enables redo
					// restore positions of nodes
					for (int i = 0; i < cyNodes.length; i++)
					{
						Node n = cyNodes[i];
						Point2D pt = (Point2D) coords.get(n.getIdentifier());
						System.out.println(
								"restoring Node:" + n 
								+ "to position: " + pt);
				        NodeView nv = 
				        	networkView.getNodeView(n);
				        nv.setOffset(pt.getX(), pt.getY());
					}
				}
			}

		});

	}

	/**
	 * Implementation of the GraphPerspectiveChangeListener interface. Responds
	 * to the removal of nodes and edges by saving them, so that they can be
	 * restored via RestoreDeleted action. Fires a NETWORK_MODIFIED event.
	 */
	public void graphPerspectiveChanged(GraphPerspectiveChangeEvent event) {
		// careful: this event can represent both hidden nodes and hidden edges
		// if a hide node operation implicitly hid its incident edges
		CyNetwork net = Cytoscape.getCurrentNetwork();
		// System.out.println ("GraphPerspectiveChanged for network: " + net);
		boolean nodeChanges = false, edgeChanges = false; // only create the
		// set if we need it
		if (event.isNodesHiddenType()) {// at least one node was hidden
			int[] hiddenNodes = event.getHiddenNodeIndices();
			for (int i = 0; i < hiddenNodes.length; i++) {
				CytoscapeEditorManager.addHiddenNodeForNetwork(net,
						hiddenNodes[i]);
			}
			if (hiddenNodes != null) {
				nodeChanges = true;
			}
		}
		if (event.isEdgesHiddenType()) // at least one edge is hidden
		{
			int[] hiddenEdges = event.getHiddenEdgeIndices();
			if (hiddenEdges != null) {
				edgeChanges = true;
			}
			for (int i = 0; i < hiddenEdges.length; i++) {
				CytoscapeEditorManager.addHiddenEdgeForNetwork(net,
						hiddenEdges[i]);
			}
		}

		if (nodeChanges || edgeChanges) {
			Cytoscape.firePropertyChange(Cytoscape.NETWORK_MODIFIED, null,
					Cytoscape.getCurrentNetwork());
		}

	}
}