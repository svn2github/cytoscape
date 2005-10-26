/*
 * Created on Jul 30, 2005
 *
 */
package cytoscape.editor.impl;

import giny.model.GraphPerspective;
import giny.model.GraphPerspectiveChangeEvent;
import giny.model.GraphPerspectiveChangeListener;
import giny.model.Node;
import giny.model.RootGraph;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashSet;
import java.util.Set;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.UndoManager;

import cytoscape.CyEdge;
import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.editor.CytoscapeEditor;
import cytoscape.editor.CytoscapeEditorFactory;
import cytoscape.editor.CytoscapeEditorManager;
import cytoscape.editor.InvalidEditorException;
import cytoscape.editor.actions.RedoAction;
import cytoscape.editor.actions.UndoAction;
import cytoscape.giny.Edge;
import cytoscape.view.CyNetworkView;
import cytoscape.view.CytoscapeDesktop;
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
		ChangeListener, GraphPerspectiveChangeListener {

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

	}

	// AJK: 10/15/05 BEGIN
	//    handle change of visual style
	//    if new visual style has an editor associated with it, then switch to it.
	public void stateChanged(ChangeEvent e) {

		boolean newEditor = false;

		System.out.println("Got stateChange event: " + e);

		if (!CytoscapeEditorManager.isEditingEnabled()) {
			return;
		}
		CytoscapeEditor oldEditor = CytoscapeEditorManager.getCurrentEditor();

		VisualMappingManager VMM = (VisualMappingManager) e.getSource();
		if (VMM != null) {
			VisualStyle style = VMM.getVisualStyle();
			CytoscapeEditor editor = CytoscapeEditorManager
					.getEditorForVisualStyle(style);
			if (editor == null) {
				// setup an editor for the visual style
				String editorType = CytoscapeEditorManager
						.getEditorTypeForVisualStyleName(style.getName());

				// AJK: 10/21/05 BEGIN
				//    modify: if no editor exists, then just use default editor
				/*
				 * // if no editor exists for this visual type, then just
				 * disable // the old editor and return // except by shortcut,
				 * this network will not be editable unless // another editor is
				 * set if (editorType == null) { System.out.println("have visual
				 * style without editorType: " + style.getName()); if (oldEditor !=
				 * null) { oldEditor.disableControls(null); } int idx =
				 * Cytoscape.getDesktop().getCytoPanel(
				 * SwingConstants.WEST).indexOfComponent("Editor");
				 * System.out.println("index of current palette = " + idx); if
				 * (idx >= 0) { Cytoscape.getDesktop()
				 * .getCytoPanel(SwingConstants.WEST).remove(idx); System.out
				 * .println("removing palette at Cytopanel index: " + idx); }
				 * CytoscapeEditorManager.setCurrentEditor(null); return;
				 */
				// AJK: 10/21/05 END
				if (editorType == null) {
					editorType = CytoscapeEditorManager.DEFAULT_EDITOR_TYPE;
				}

				try {
					editor = CytoscapeEditorFactory.INSTANCE
							.getEditor(editorType);
					CytoscapeEditorManager.setEditorForVisualStyle(style,
							editor);
					System.out.println("built new editor: " + editor
							+ ", for visual Style = " + style);
					newEditor = true;
				} catch (InvalidEditorException ex) {
					System.out
							.println("Error building editor for editor type = "
									+ editorType + ", error = " + ex);
					ex.printStackTrace();
				}
			}
			if (editor != null) {
				CyNetworkView view = Cytoscape.getCurrentNetworkView();
				CytoscapeEditorManager.setEditorForView(view, editor);
				CytoscapeEditorManager.setupNewNetworkView(view);
				if (oldEditor == editor) {
					// AJK: 10/21/05 always switch palette when visual style
					// changes
					//					return;
				}
				if ((oldEditor != null)
						&& (!CytoscapeEditorManager.isSettingUpEditor())) {

					oldEditor.disableControls(null);
				}
				// AJK: 10/21/05 always build a new shape palette when changing
				// visual styles
				//				if (newEditor) {
				editor.initializeControls(null);
				//				} else {
				//					editor.enableControls(null);
				//				}
				CytoscapeEditorManager.setCurrentEditor(editor);
				CytoscapeEditorManager.setEventHandlerForView(view);
			}
		}
	}

	// AJK: 10/15/05 END

	public void propertyChange(PropertyChangeEvent e) {

		if (e.getPropertyName().equals(Cytoscape.NETWORK_CREATED))
		{
			String netId = e.getNewValue().toString();
			CyNetwork net = Cytoscape.getNetwork(netId);
//			CyNetwork net = Cytoscape.getCurrentNetwork();
			net.addGraphPerspectiveChangeListener(this);
			System.out.println ("Added graph perspective change listener to: " + net);
		}
		if (e.getPropertyName().equals(CytoscapeDesktop.NETWORK_VIEW_CREATED)) {
		} else if (e.getPropertyName().equals(Cytoscape.ATTRIBUTES_CHANGED)) {
			// implement ATTRIBUTES_CHANGED handler
			//			System.out.println("Property changed: " + e.getPropertyName());
			//			System.out.println("Old value = " + e.getOldValue());
			//			System.out.println("New value = " + e.getNewValue());

		} else if (e.getPropertyName().equals(
				CytoscapeDesktop.NETWORK_VIEW_FOCUSED)) {

			CyNetworkView view = Cytoscape.getCurrentNetworkView();
			CytoscapeEditor cyEditor = CytoscapeEditorManager
					.getEditorForView(view);

			// AJK: 09/06/05 BEGIN
			//   setup an undo manager for this network view
			Object undoObj = CytoscapeEditorManager.getUndoManagerForView(view);
			if (undoObj instanceof UndoManager) {
				CytoscapeEditorManager
						.setCurrentUndoManager((UndoManager) undoObj);
			} else {
				UndoManager newUndo = new UndoManager();
				CytoscapeEditorManager.setUndoManagerForView(view, newUndo);
				//				System.out.println ("SetUndoManagerForView: " +
				// Cytoscape.getCurrentNetworkView());
				CytoscapeEditorManager.setCurrentUndoManager(newUndo);
				UndoAction undoAction = new UndoAction(newUndo);
				CytoscapeEditorManager.setUndoActionForView(view, undoAction);
				RedoAction redoAction = new RedoAction(newUndo);
				undoAction.setRedoAction(redoAction);
				redoAction.setUndoAction(undoAction);
				CytoscapeEditorManager.setRedoActionForView(view, redoAction);
				System.out.println("Set redo manager for view: " + redoAction);
			}
			// AJK: 09/06/05 END

			// set the buttons on the shapePalette to undo, redo actions
			ShapePalette palette = CytoscapeEditorManager
					.getShapePaletteForView(view);
			if (palette != null) {
				// AJK: 10/24/05: comment this out, undo functionality is moving
				// to Cytoscape
				//    			palette.getUndoButton().setAction(CytoscapeEditorManager.getUndoActionForView(view));
				//    			palette.getRedoButton().setAction(CytoscapeEditorManager.getRedoActionForView(view));
			}

			if (cyEditor == null) {
				cyEditor = CytoscapeEditorManager.getCurrentEditor();
			}

			if (cyEditor == null) {
				// this would be because no editor has been set yet. Just return
				return;
			}

			// at this point there is an editor but it is not assigned to this
			// view
			// this is probably the case if we are loading a network, rather
			// than creating a new one
			// in this case, we need to setup the network view, which sets all
			// the event handler, etc.
			CytoscapeEditorManager.setupNewNetworkView(view);
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

		if (node != null) {
			int nodeIdx = node.getRootGraphIndex();
			nodes[0] = nodeIdx;
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

		CytoscapeEditorManager.addEdit(new AbstractUndoableEdit() {

			final String network_id = cyNet.getIdentifier();

			public String getPresentationName() {
				// AJK: 10/21/05 return null as presentation name because we are
				// using iconic buttons
				return "Add";
				//				return null;
			}

			public String getRedoPresentationName() {
				if (isNode)
					// AJK: 10/21/05 return null as presentation name because we
					// are using iconic buttons
					return "Redo: Added Node";
				//					return null;
				else
					// AJK: 10/21/05 return null as presentation name because we
					// are using iconic buttons
					return "Redo: Added Edge";
				//				return null;
			}

			public String getUndoPresentationName() {

				if (isNode)
					// AJK: 10/21/05 return null as presentation name because we
					// are using iconic buttons
					return "Undo: Added Node";
				//					return null;
				else
					// AJK: 10/21/05 return null as presentation name because we
					// are using iconic buttons
					return "Undo: Added Edge";
				//				return null;

			}

			public void undo() {
				// removes the removed nodes and edges from the network
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
		//careful: this event can represent both hidden nodes and hidden edges
		//if a hide node operation implicitly hid its incident edges
		CyNetwork net = Cytoscape.getCurrentNetwork();
//		System.out.println ("GraphPerspectiveChanged for network: " + net);
		boolean nodeChanges = false, edgeChanges = false; //only create the set if we need it
		if (event.isNodesHiddenType()) {//at least one node was hidden
			int[] hiddenNodes = event.getHiddenNodeIndices();
			for (int i = 0; i < hiddenNodes.length; i++)
			{
				CytoscapeEditorManager.addHiddenNodeForNetwork(net, hiddenNodes[i]);
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
			for (int i = 0; i < hiddenEdges.length; i++)
			{
				CytoscapeEditorManager.addHiddenEdgeForNetwork(net, hiddenEdges[i]);
			}
		}

		if (nodeChanges || edgeChanges) {
			Cytoscape.firePropertyChange(Cytoscape.NETWORK_MODIFIED, null,
					Cytoscape.getCurrentNetwork());
		}
        

    }
}