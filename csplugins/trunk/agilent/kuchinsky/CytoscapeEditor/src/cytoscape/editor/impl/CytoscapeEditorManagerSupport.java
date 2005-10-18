/*
 * Created on Jul 30, 2005
 *
 */
package cytoscape.editor.impl;

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
import cytoscape.editor.CytoscapeEditor;
import cytoscape.editor.CytoscapeEditorFactory;
import cytoscape.editor.CytoscapeEditorManager;
import cytoscape.editor.InvalidEditorException;
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
		ChangeListener {

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
public void stateChanged(ChangeEvent e)
	{
		boolean newEditor = false;
		
		System.out.println ("Got stateChange event: " + e);
		
		if (!CytoscapeEditorManager.isEditingEnabled())
		{
			return;
		}
		CytoscapeEditor oldEditor = CytoscapeEditorManager.getCurrentEditor();
		
		VisualMappingManager VMM = (VisualMappingManager) e.getSource();
		if (VMM != null)
		{
			VisualStyle style = VMM.getVisualStyle();
			CytoscapeEditor editor = CytoscapeEditorManager.getEditorForVisualStyle(style);
			if (editor == null)
			{
				// setup an editor for the visual style
				String editorType = 
					CytoscapeEditorManager.getEditorTypeForVisualStyleName(
							style.getName());
				// if no editor exists for this visual type, then just disable the old editor and return
				// except by shortcut, this network will not be editable unless another editor is set
				if (editorType == null)
				{
					System.out.println("have visual style without editorType: " + style.getName());
					oldEditor.disableControls(null);
			        int idx = Cytoscape.getDesktop().getCytoPanel( SwingConstants.WEST ).indexOfComponent("Editor");
			        System.out.println ("index of current palette = " + idx);
			        if (idx >= 0)
			        {
			        	Cytoscape.getDesktop().getCytoPanel( SwingConstants.WEST ).remove(idx);
			        	System.out.println ("removing palette at Cytopanel index: " + idx);
			        }
					CytoscapeEditorManager.setCurrentEditor(null);
					return;
				}
				
				try
				{
				editor = CytoscapeEditorFactory.INSTANCE.getEditor(editorType);
				CytoscapeEditorManager.setEditorForVisualStyle(style, editor);
				System.out.println ("built new editor: " + editor +   
						", for visual Style = " + style);
				newEditor = true;
				}
				catch (InvalidEditorException ex)
				{
					System.out.println ("Error building editor for editor type = " + 
							editorType + ", error = "+ ex);
					ex.printStackTrace();
				}			
			}
			if (editor != null)
			{
				CyNetworkView view = Cytoscape.getCurrentNetworkView();
				CytoscapeEditorManager.setEditorForView(view, editor);
				CytoscapeEditorManager.setupNewNetworkView(view);
				if (oldEditor == editor)
				{
					return;
				}
				if (oldEditor != null)
				{

					oldEditor.disableControls(null);
				}
				if (newEditor)
				{
					editor.initializeControls(null);
				}
				else
				{
				    editor.enableControls(null);
				}
				CytoscapeEditorManager.setCurrentEditor(editor);
			}
		}
	}
	// AJK: 10/15/05 END

	public void propertyChange(PropertyChangeEvent e) {

		if (e.getPropertyName().equals(CytoscapeDesktop.NETWORK_VIEW_CREATED)) {
		} else if (e.getPropertyName().equals(Cytoscape.ATTRIBUTES_CHANGED)) {
			// implement ATTRIBUTES_CHANGED handler
			System.out.println("Property changed: " + e.getPropertyName());
			System.out.println("Old value = " + e.getOldValue());
			System.out.println("New value = " + e.getNewValue());

		} else if (e.getPropertyName().equals(
				CytoscapeDesktop.NETWORK_VIEW_FOCUSED)) {

			CyNetworkView view = Cytoscape.getCurrentNetworkView();
			CytoscapeEditor cyEditor = CytoscapeEditorManager
					.getEditorForView(view);

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
				return "Add";
			}

			public String getRedoPresentationName() {
				if (isNode)
					return "Redo: Added Node";
				else
					return "Redo: Added Edge";
			}

			public String getUndoPresentationName() {

				if (isNode)
					return "Undo: Added Node";
				else
					return "Undo: Added Edge";

			}

			public void undo() {
				// removes the removed nodes and edges from the network
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
				CyNetwork network = Cytoscape.getNetwork(network_id);
				if (network != null) {
					network.restoreNodes(nodes);
					network.restoreEdges(edges);
				}
			}

		});

	}

}