/* -*-Java-*-
********************************************************************************
*
* File:         CytoscapeEditorManagerSupport.java
* RCS:          $Header: $
* Description:
* Author:       Allan Kuchinsky
* Created:      Sat Jul 30 17:00:27 2005
* Modified:     Wed May 09 13:59:52 2007 (Michael L. Creech) creech@w235krbza760
* Language:     Java
* Package:
* Status:       Experimental (Do Not Distribute)
*
* (c) Copyright 2006, Agilent Technologies, all rights reserved.
*
********************************************************************************
*
* Revisions:
*
* Wed May 09 13:59:45 2007 (Michael L. Creech) creech@w235krbza760
*  Removed several unneeded imports.
* Wed Dec 27 09:04:18 2006 (Michael L. Creech) creech@w235krbza760
*  Added getDeleteAction() and parameter to constructor.
* Sun Aug 06 11:14:28 2006 (Michael L. Creech) creech@w235krbza760
*  Fixed updateEditorPalette() to not assume visualStyleName is the
*  same as Editor name.
* Sat Aug 05 17:01:38 2006 (Michael L. Creech) creech@w235krbza760
*  Added some comments.
********************************************************************************
*/
package cytoscape.editor.impl;

import giny.model.GraphPerspectiveChangeEvent;
import giny.model.GraphPerspectiveChangeListener;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import cytoscape.CyNetwork;
import cytoscape.Cytoscape;
import cytoscape.editor.CytoscapeEditor;
import cytoscape.editor.CytoscapeEditorFactory;
import cytoscape.editor.CytoscapeEditorManager;
import cytoscape.editor.InvalidEditorException;
import cytoscape.editor.actions.DeleteAction;
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
public class CytoscapeEditorManagerSupport implements PropertyChangeListener, ChangeListener,
                                                      GraphPerspectiveChangeListener,
                                                      CytoPanelListener {
	/**
	 * register interest in NETWORK_VIEW_FOCUSED and NETWORK_VIEW_CREATED events
	 *
	 */

	// MLC 12/27/06:
	private DeleteAction _deleteAction;
	

	// MLC 12/27/06:
	/**
	 * Creates a new CytoscapeEditorManagerSupport object.
	 *
	 * @param dAction  DOCUMENT ME!
	 */
	public CytoscapeEditorManagerSupport(DeleteAction dAction) {
		super();
		// MLC 12/27/06:
		_deleteAction = dAction;
		Cytoscape.getDesktop().getSwingPropertyChangeSupport()
		         .addPropertyChangeListener(CytoscapeDesktop.NETWORK_VIEW_FOCUSED, this);
		Cytoscape.getDesktop().getSwingPropertyChangeSupport()
		         .addPropertyChangeListener(CytoscapeDesktop.NETWORK_VIEW_CREATED, this);
		Cytoscape.getSwingPropertyChangeSupport().addPropertyChangeListener(this);

		Cytoscape.getVisualMappingManager().addChangeListener(this);
		Cytoscape.getDesktop().getCytoPanel(SwingConstants.WEST).addCytoPanelListener(this);
	}

	// MLC 12/27/06 BEGIN:
	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public DeleteAction getDeleteAction() {
		return _deleteAction;
	}

	// MLC 12/27/06 END.

	/**
	 * respond to a ChangeEvent, typically this is caused by switching
	 * visual styles
	 */

	// implements ChangeListener interface:
	public void stateChanged(ChangeEvent e) {
		if (!CytoscapeEditorManager.isEditingEnabled()) {
			return;
		}

		// AJK: 06/10/06 BEGIN
		// don't do any work building editor palette if editor tab is not
		// selected in CytoPanel
		int idx = Cytoscape.getDesktop().getCytoPanel(SwingConstants.WEST).indexOfComponent("Editor");

		if (idx != Cytoscape.getDesktop().getCytoPanel(SwingConstants.WEST).getSelectedIndex()) {
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

	// implements CytoPanelListener interface:
	public void onComponentSelected(int componentIndex) {
		int idx = Cytoscape.getDesktop().getCytoPanel(SwingConstants.WEST).indexOfComponent("Editor");

		if (componentIndex == idx) {
			updateEditorPalette(Cytoscape.getVisualMappingManager().getVisualStyle());

			// If no networks exist, create an empty network.
			CytoscapeEditorManager.log("Number networks = " + Cytoscape.getNetworkSet().size());
			if ( Cytoscape.getNetworkSet().size() == 0 ) {
				CyNetwork newNet = Cytoscape.createNetwork(CytoscapeEditorManager.createUniqueNetworkName());
				CyNetworkView newView = Cytoscape.createNetworkView(newNet);
				CytoscapeEditorManager.log("created a new network view: " + newView);
				CytoscapeEditorManager.setEditorForView(newView, CytoscapeEditorManager.getCurrentEditor());
				CytoscapeEditorManager.setupNewNetworkView(newView);
				CytoscapeEditorManager.setEventHandlerForView(newView);			
				}
		}
	}

	/**
	 * Notifies the listener on a change in the CytoPanel state.
	 *
	 * @param newState
	 *            The new CytoPanel state - see CytoPanelState class.
	 */
	public void onStateChange(CytoPanelState newState) {
	}

	/**
	 * Notifies the listener when a component is added to the CytoPanel.
	 *
	 * @param count
	 *            The number of components on the CytoPanel after the add.
	 */

	// implements CytoPanelListener interface:
	public void onComponentAdded(int count) {
	}

	/**
	 * Notifies the listener when a component is removed from the CytoPanel.
	 *
	 * @param count
	 *            The number of components on the CytoPanel after the remove.
	 */

	// implements CytoPanelListener interface:
	public void onComponentRemoved(int count) {
	}

	/**
	 * sets up editor and visual style and builds the ShapePalette
	 * @param style
	 */
	public void updateEditorPalette(VisualStyle style) {
		// AJK: 06/16/06 only update palette after CYTOSCAPE_INITIALIZED
		CytoscapeEditorManager.log("setting up editor for visual style: " + style);

		if (!CytoscapeEditorManager.isEditingEnabled()) {
			return;
		}

		// MLC 08/06/06 BEGIN:
		// ASSUMES visual style name is the same as the editor!
		// String editorType = style.getName();
		String editorType = CytoscapeEditorManager.getEditorNameForVisualStyleName(style.getName());
		CytoscapeEditorManager.log("got editor name for visual style: " + editorType);

		// MLC 08/06/06 END.
		CytoscapeEditor editorForStyle = null;

		try {
			editorForStyle = CytoscapeEditorFactory.INSTANCE.getEditor(editorType);
			CytoscapeEditorManager.log("got editor for style: " + style.getName() + " = "
			                           + editorForStyle);
		} catch (InvalidEditorException ex) {
			CytoscapeEditorManager.log("Could not find editor for editor type: " + editorType);
			editorType = CytoscapeEditorManager.DEFAULT_EDITOR_TYPE;

			try {
				editorForStyle = CytoscapeEditorFactory.INSTANCE.getEditor(editorType);
			} catch (InvalidEditorException exe) {
				CytoscapeEditorManager.log("Error building editor for editor type = " + editorType
				                           + ", error = " + ex);
				exe.printStackTrace();
			}
		}

		if (editorForStyle != null) {
			CyNetworkView view = Cytoscape.getCurrentNetworkView();
			CytoscapeEditor editorForView = CytoscapeEditorManager.getEditorForView(Cytoscape
			                                                                                                                                                                                                                                .getCurrentNetworkView());

			CytoscapeEditorManager.log("Got editor for view: " + editorForView);

			if ((editorForView != null) && (!CytoscapeEditorManager.isSettingUpEditor())) {
				CytoscapeEditorManager.log("Disabling controls for editor: " + editorForView);
				editorForView.disableControls(null);
			}

			CytoscapeEditorManager.log("Initializing controls for " + editorForStyle);
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

	// implements PropertyChangeListener interface:
	public void propertyChange(PropertyChangeEvent e) {
		//CytoscapeEditorManager.log("Got property change: " + e.getPropertyName());
		if (e.getPropertyName().equals(Cytoscape.NETWORK_CREATED)) {
			String netId = e.getNewValue().toString();
			CyNetwork net = Cytoscape.getNetwork(netId);
			net.addGraphPerspectiveChangeListener(this);
		}

		// redraw graph if the network is modified, e.g. by an undoable edit
		// else if (e.getPropertyName().equals(Cytoscape.NETWORK_MODIFIED)) {
		if (e.getPropertyName().equals(Cytoscape.NETWORK_MODIFIED)) {
			if (e.getOldValue() != null) {
				// AJK: 06/19/06 hack that uses OldValue field to indicate that this 
				//    event was fired from CytoscapeEditor, thus avoids any 
				//    unnecessary redraws due to multiple event firings from an 
				//    -- any non-null value will do
				if ((e.getOldValue().equals(CytoscapeEditorManager.CYTOSCAPE_EDITOR))
				    || (e.getOldValue().equals("cytoscape.util.UndoManager"))) {
					Cytoscape.getCurrentNetworkView().redrawGraph(true, true);
				}
			}
		}
		// AJK: 06/15/06: enable editing once Cytoscape has been initialized
		else if (e.getPropertyName().equals(Cytoscape.CYTOSCAPE_INITIALIZED)) {
			CytoscapeEditorManager.setEditingEnabled(true);
			
		} 
		else if (e.getPropertyName().equals(CytoscapeDesktop.NETWORK_VIEW_CREATED))
		{
			CytoscapeEditorManager.setNetworkViewBeingCreated(true);
		}
		
		else if (e.getPropertyName().equals(CytoscapeDesktop.NETWORK_VIEW_FOCUSED)) {
			CytoscapeEditorManager.log("NETWORK_VIEW_FOCUSED: " + e.getNewValue());
			CytoscapeEditorManager.log("From old network view: " + e.getOldValue());
			CyNetworkView view = Cytoscape.getCurrentNetworkView();

			// AJK: 12/09/06 BEGIN
			//   try to get editor for visual style
			// MLC 12/27/06:
			// VisualStyle vs = view.getVisualStyle();           
			CytoscapeEditor cyEditor = CytoscapeEditorManager.getEditorForView(view);

			if (cyEditor == null) {
				try {
					CytoscapeEditorManager.log("looking for default editor");
					cyEditor = CytoscapeEditorFactory.INSTANCE.getEditor(CytoscapeEditorManager.DEFAULT_EDITOR_TYPE);
					CytoscapeEditorManager.log("got default editor: " + cyEditor);
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
					CytoscapeEditorManager.log("Building network view for: " + view
					                           + " using editor " + cyEditor);
					CytoscapeEditorManager.setupNewNetworkView(view);
					// AJK: 08/28/07 
					CytoscapeEditorManager.setNetworkViewBeingCreated(false);
				}
			}
			// AJK: 08/28/07 BEGIN
			//    check for case where we have a network view being created and there already
			//    is a network
			else if (CytoscapeEditorManager.isNetworkViewBeingCreated())
			{
				CytoscapeEditorManager.setupNewNetworkView(view);
				// AJK: 08/28/07 
				CytoscapeEditorManager.setNetworkViewBeingCreated(false);			
			}
			// AJK: 08/28/07 END
			
		}
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

		// CytoscapeEditorManager.log ("GraphPerspectiveChanged for network: " + net);
		boolean nodeChanges = false; // only create the set if we need it

		// CytoscapeEditorManager.log ("GraphPerspectiveChanged for network: " + net);
		boolean edgeChanges = false; // only create the set if we need it

		// at least one node was hidden
		if (event.isNodesHiddenType()) {
			int[] hiddenNodes = event.getHiddenNodeIndices();

			for (int i = 0; i < hiddenNodes.length; i++) {
				CytoscapeEditorManager.addHiddenNodeForNetwork(net, hiddenNodes[i]);
			}

			if (hiddenNodes != null) {
				nodeChanges = true;
			}
		}

		// at least one edge is hidden
		if (event.isEdgesHiddenType()) {
			int[] hiddenEdges = event.getHiddenEdgeIndices();

			if (hiddenEdges != null) {
				edgeChanges = true;
			}

			for (int i = 0; i < hiddenEdges.length; i++) {
				CytoscapeEditorManager.addHiddenEdgeForNetwork(net, hiddenEdges[i]);
			}
		}

		if (nodeChanges || edgeChanges) {
			// AJK: 12/13/06 to fix NPE bug 
			//            Cytoscape.firePropertyChange(Cytoscape.NETWORK_MODIFIED,
			// for distinguishing from batch firing of event
			//                                         CytoscapeEditorManager.CYTOSCAPE_EDITOR, 
			//                                         Cytoscape.getCurrentNetwork());
		}
	}
}
