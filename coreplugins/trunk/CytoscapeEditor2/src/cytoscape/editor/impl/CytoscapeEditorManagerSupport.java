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

import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.undo.AbstractUndoableEdit;

import cytoscape.CyEdge;
import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;
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
		ChangeListener, GraphPerspectiveChangeListener
		, CytoPanelListener
{

	/**
	 * register interest in NETWORK_VIEW_FOCUSED and NETWORK_VIEW_CREATED events
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

		Cytoscape.getVisualMappingManager().addChangeListener(this);
		Cytoscape.getDesktop().getCytoPanel(SwingConstants.WEST)
				.addCytoPanelListener(this);
	}

	/**
	 * respond to a ChangeEvent, typically this is caused by switching visual styles
	 */
	public void stateChanged(ChangeEvent e) {
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

	/**
	 * respond to selection of a CytoPanels component, in particular respond to 
	 * selection of a tab on the WEST CytoPanel.
	 */
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

	/**
	 * sets up editor and visual style and builds the ShapePalette
	 * @param style
	 */
	public void updateEditorPalette(VisualStyle style) {

		// AJK: 06/16/06 only update palette after CYTOSCAPE_INITIALIZED
		if (!CytoscapeEditorManager.isEditingEnabled()) {
			return;
		}

		String editorType = style.getName();
		CytoscapeEditor editorForStyle = null;

		try {
			editorForStyle = CytoscapeEditorFactory.INSTANCE
					.getEditor(editorType);

		} catch (InvalidEditorException ex) {
			editorType = CytoscapeEditorManager.DEFAULT_EDITOR_TYPE;
			try {
				editorForStyle = CytoscapeEditorFactory.INSTANCE
						.getEditor(editorType);

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
//			System.out.println("Got editor for view: " + editorForView);

			if ((editorForView != null)
					&& (!CytoscapeEditorManager.isSettingUpEditor())) {

				System.out.println("Disabling controls for editor: "
						+ editorForView);
				editorForView.disableControls(null);
			}
//			System.out.println("Initializing controls for " + editorForStyle);
			editorForStyle.initializeControls(null);
			CytoscapeEditorManager.setEditorForView(view, editorForStyle);
			CytoscapeEditorManager.setupNewNetworkView(view);
			CytoscapeEditorManager.setCurrentEditor(editorForStyle);
			CytoscapeEditorManager.setEventHandlerForView(view);
		}

	}


	/**
	 * respond to a PropertyChangeEvent.  This is typically the Creation or Modification
	 * of a Network or NetworkView.  
	 * If networkView focus changes, then bring up the appropriate editor for the
	 * enw network view.
	 */
	public void propertyChange(PropertyChangeEvent e) {

		//System.out.println("Got property change: " + e.getPropertyName());
		if (e.getPropertyName().equals(Cytoscape.NETWORK_CREATED)) {
			String netId = e.getNewValue().toString();
			CyNetwork net = Cytoscape.getNetwork(netId);
			net.addGraphPerspectiveChangeListener(this);
		}
		if (e.getPropertyName().equals(CytoscapeDesktop.NETWORK_VIEW_CREATED)) {
		} else if (e.getPropertyName().equals(Cytoscape.ATTRIBUTES_CHANGED)) {

		}
		// redraw graph if the network is modified, e.g. by an undoable edit
		else if (e.getPropertyName().equals(Cytoscape.NETWORK_MODIFIED)) {
			if (e.getOldValue() != null) 
			{
				// AJK: 06/19/06 hack that uses OldValue field to indicate that this 
				//    event was fired from CytoscapeEditor, thus avoids any 
				//    unnecessary redraws due to multiple event firings from an 
				//    -- any non-null value will do
				if ((e.getOldValue().equals(CytoscapeEditorManager.CYTOSCAPE_EDITOR)) ||
						(e.getOldValue().equals("cytoscape.util.UndoManager")))	
				{  
					Cytoscape.getCurrentNetworkView().redrawGraph(true, true);
				}
			}
		}

		// AJK: 06/15/06: enable editing once Cytoscape has been initialized
		else if (e.getPropertyName().equals(Cytoscape.CYTOSCAPE_INITIALIZED)) {
			CytoscapeEditorManager.setEditingEnabled(true);
		}

		else if (e.getPropertyName().equals(
				CytoscapeDesktop.NETWORK_VIEW_FOCUSED)) {

			CyNetworkView view = Cytoscape.getCurrentNetworkView();
			CytoscapeEditor cyEditor = CytoscapeEditorManager
					.getEditorForView(view);
			
			cytoscape.util.UndoManager undo = CytoscapeDesktop.undo;

			// AJK: 06/07/06 clear UndoManager when the view changes
			undo.discardAllEdits();

			if (cyEditor == null) {
				try {
					cyEditor = CytoscapeEditorFactory.INSTANCE
							.getEditor(CytoscapeEditorManager.DEFAULT_EDITOR_TYPE);
				} catch (InvalidEditorException ex) {
				}
			} 

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

		if (node != null) {
			setupUndoableNodeAdditionEdit(net, node);
		} else if (edge != null) {
			setupUndoableEdgeAdditionEdit(net, edge);
		}
	}

	private void setupUndoableNodeAdditionEdit(CyNetwork net, CyNode node) {
		final int[] nodes = new int[1];
		int nodeIdx = node.getRootGraphIndex();
		nodes[0] = nodeIdx;
		final CyNetworkView networkView = Cytoscape.getCurrentNetworkView();
		final CyNetwork cyNet = net;
		final Node n = node;

		NodeView nview = networkView.getNodeView(node);
		final Point2D offset = nview.getOffset();
//		System.out.println("added node: " + node + " at coordinates "
//				+ nview.getOffset());
		
		// AJK: 06/21/06 gevalt, what a hack!  store coordinate position on node attributes so
		//    that a subsequent redo will restore node to its coordinate position
		CyAttributes nodeAttribs = Cytoscape.getNodeAttributes();
		nodeAttribs.setAttribute(node.getIdentifier(), "X_pos", new Double(offset.getX()));
		nodeAttribs.setAttribute(node.getIdentifier(), "Y_pos", new Double(offset.getY()));
		
		// setup the clipboard and undo manager to be able to undo the deletion
		CytoscapeEditorManager.getNodeClipBoard().elements(nodes);

		CytoscapeEditorManager.addEdit(new AbstractUndoableEdit() {

			final String network_id = cyNet.getIdentifier();

			public String getPresentationName() {
				return "Add";
			}

			public String getRedoPresentationName() {
				return "Redo: Added Node";
			}

			public String getUndoPresentationName() {
				return "Undo: Added Node";
			}

			public void undo() {
				super.undo();
				CyNetwork network = Cytoscape.getNetwork(network_id);
				if (network != null) {
					network.hideNodes(nodes);
					CytoscapeEditorManager.getNodeClipBoard().elements(nodes);
				}
			}

			public void redo() {
				super.redo();
				CyNetwork network = Cytoscape.getNetwork(network_id);
				if (network != null) {
					network.restoreNodes(nodes);
					// signal end to Undo Manager; this enables redo
					// restore positions of nodes
					NodeView nv = networkView.getNodeView(n);
					CyAttributes nodeAttribs = Cytoscape.getNodeAttributes();
					Double xPos = nodeAttribs.getDoubleAttribute(n.getIdentifier(), "X_pos");
					Double yPos = nodeAttribs.getDoubleAttribute(n.getIdentifier(), "Y_pos");
					nv.setOffset(xPos.doubleValue(), yPos.doubleValue());
				}
			}
		});
	}

	private void setupUndoableEdgeAdditionEdit(CyNetwork net, CyEdge edge) {
		final int[] edges = new int[1];
		final CyNetwork cyNet = net;

		if (edge != null) {
			int edgeIdx = edge.getRootGraphIndex();
			edges[0] = edgeIdx;
		}

		// setup the clipboard and undo manager to be able to undo the deletion
		CytoscapeEditorManager.getEdgeClipBoard().elements(edges);

		System.out.println("Adding an ADD edit to " + cyNet);
		CytoscapeEditorManager.addEdit(new AbstractUndoableEdit() {

			final String network_id = cyNet.getIdentifier();

			public String getPresentationName() {
				return "Add";
			}

			public String getRedoPresentationName() {
				return "Redo: Added Edge";
			}

			public String getUndoPresentationName() {
				return "Undo: Added Edge";
			}

			public void undo() {
				super.undo();
				CyNetwork network = Cytoscape.getNetwork(network_id);
				if (network != null) {
					network.hideEdges(edges);
					CytoscapeEditorManager.getEdgeClipBoard().elements(edges); // sets
					// elements
				}

			}

			public void redo() {
				super.redo();
				CyNetwork network = Cytoscape.getNetwork(network_id);
				if (network != null) {
					network.restoreEdges(edges);
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
			Cytoscape.firePropertyChange(Cytoscape.NETWORK_MODIFIED, 
					CytoscapeEditorManager.CYTOSCAPE_EDITOR, // for distinguishing from batch firing of event
					Cytoscape.getCurrentNetwork());
		}
	}
}
