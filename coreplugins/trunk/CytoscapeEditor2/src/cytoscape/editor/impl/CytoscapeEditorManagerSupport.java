/* -*-Java-*-
********************************************************************************
*
* File:         CytoscapeEditorManagerSupport.java
* RCS:          $Header: $
* Description:
* Author:       Allan Kuchinsky
* Created:      Sat Jul 30 17:00:27 2005
* Modified:     Wed Dec 27 09:43:05 2006 (Michael L. Creech) creech@w235krbza760
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

import cytoscape.CyEdge;
import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.Cytoscape;

import cytoscape.data.CyAttributes;

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
                                                      ChangeListener,
                                                      GraphPerspectiveChangeListener,
                                                      CytoPanelListener {
    /**
     * register interest in NETWORK_VIEW_FOCUSED and NETWORK_VIEW_CREATED events
     *
     */

    // MLC 12/27/06:
    private DeleteAction _deleteAction;

    // MLC 12/27/06:
    //    public CytoscapeEditorManagerSupport() {
    // MLC 12/27/06:
    public CytoscapeEditorManagerSupport(DeleteAction dAction) {
        super();
	// MLC 12/27/06:
	_deleteAction = dAction;
        Cytoscape.getDesktop().getSwingPropertyChangeSupport().addPropertyChangeListener(
            CytoscapeDesktop.NETWORK_VIEW_FOCUSED,
            this);
        Cytoscape.getDesktop().getSwingPropertyChangeSupport().addPropertyChangeListener(
            CytoscapeDesktop.NETWORK_VIEW_CREATED,
            this);
        Cytoscape.getSwingPropertyChangeSupport().addPropertyChangeListener(this);

        Cytoscape.getVisualMappingManager().addChangeListener(this);
        Cytoscape.getDesktop().getCytoPanel(SwingConstants.WEST)
                 .addCytoPanelListener(this);
    }

    // MLC 12/27/06 BEGIN:
    // Needed by some CytoscapeEditor subclasses (e.g., HyperEdgeEditor).
    public DeleteAction getDeleteAction () {
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
        int idx = Cytoscape.getDesktop().getCytoPanel(SwingConstants.WEST).indexOfComponent(
            "Editor");

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

    // implements CytoPanelListener interface:
    public void onComponentSelected(int componentIndex) {
        int idx = Cytoscape.getDesktop().getCytoPanel(SwingConstants.WEST).indexOfComponent(
        "Editor");
//        if (componentIndex == Cytoscape.getDesktop()
//                                       .getCytoPanel(SwingConstants.WEST)
//                                       .getSelectedIndex()) {
        if (componentIndex == idx) {
            updateEditorPalette(
                Cytoscape.getVisualMappingManager().getVisualStyle());
        }
    }

    /**
     * Notifies the listener on a change in the CytoPanel state.
     *
     * @param newState
     *            The new CytoPanel state - see CytoPanelState class.
     */

    // implements CytoPanelListener interface:
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
        String editorType = CytoscapeEditorManager.getEditorNameForVisualStyleName(
            style.getName());
       	CytoscapeEditorManager.log("got editor name for visual style: " + editorType);
        

        // MLC 08/06/06 END.
        CytoscapeEditor editorForStyle = null;

        try {
            editorForStyle = CytoscapeEditorFactory.INSTANCE.getEditor(editorType);
            CytoscapeEditorManager.log("got editor for style: " + style.getName() 
            		+ " = " + editorForStyle);

        } catch (InvalidEditorException ex) {
        	CytoscapeEditorManager.log
        	("Could not find editor for editor type: " + editorType);
            editorType = CytoscapeEditorManager.DEFAULT_EDITOR_TYPE;

            try {
                editorForStyle = CytoscapeEditorFactory.INSTANCE.getEditor(
                    editorType);
            } catch (InvalidEditorException exe) {
                CytoscapeEditorManager.log(
                    "Error building editor for editor type = " + editorType +
                    ", error = " + ex);
                exe.printStackTrace();
            }
        }

        if (editorForStyle != null) {
            CyNetworkView   view          = Cytoscape.getCurrentNetworkView();
            CytoscapeEditor editorForView = CytoscapeEditorManager.getEditorForView(
                Cytoscape.getCurrentNetworkView());

            			CytoscapeEditorManager.log("Got editor for view: " + editorForView);
            if ((editorForView != null) &&
                (!CytoscapeEditorManager.isSettingUpEditor())) {
                CytoscapeEditorManager.log(
                    "Disabling controls for editor: " + editorForView);
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
            String    netId = e.getNewValue().toString();
            CyNetwork net = Cytoscape.getNetwork(netId);
            net.addGraphPerspectiveChangeListener(this);
        }
        // MLC 08/06/06 BEGIN:
        // if (e.getPropertyName().equals(CytoscapeDesktop.NETWORK_VIEW_CREATED)) {
        // } else if (e.getPropertyName().equals(Cytoscape.ATTRIBUTES_CHANGED)) {
        // }
        // redraw graph if the network is modified, e.g. by an undoable edit
        // else if (e.getPropertyName().equals(Cytoscape.NETWORK_MODIFIED)) {
        if (e.getPropertyName().equals(Cytoscape.NETWORK_MODIFIED)) {
        // MLC 08/06/06 END.
            if (e.getOldValue() != null) {
                // AJK: 06/19/06 hack that uses OldValue field to indicate that this 
                //    event was fired from CytoscapeEditor, thus avoids any 
                //    unnecessary redraws due to multiple event firings from an 
                //    -- any non-null value will do
                if ((e.getOldValue()
                      .equals(CytoscapeEditorManager.CYTOSCAPE_EDITOR)) ||
                    (e.getOldValue().equals("cytoscape.util.UndoManager"))) {
                    Cytoscape.getCurrentNetworkView().redrawGraph(true, true);
                }
            }
        }
        // AJK: 06/15/06: enable editing once Cytoscape has been initialized
        else if (e.getPropertyName().equals(Cytoscape.CYTOSCAPE_INITIALIZED)) {
            CytoscapeEditorManager.setEditingEnabled(true);
        }
        else if (e.getPropertyName()
                  .equals(CytoscapeDesktop.NETWORK_VIEW_FOCUSED)) {
            CyNetworkView   view     = Cytoscape.getCurrentNetworkView();
            
            // AJK: 12/09/06 BEGIN
            //   try to get editor for visual style
            // MLC 12/27/06:
            // VisualStyle vs = view.getVisualStyle();           
            CytoscapeEditor cyEditor = CytoscapeEditorManager.getEditorForView(
                view);

            cytoscape.util.UndoManager undo = CytoscapeDesktop.undo;

            // AJK: 06/07/06 clear UndoManager when the view changes
            undo.discardAllEdits();

            if (cyEditor == null) {
                try {
                	CytoscapeEditorManager.log("looking for default editor");
                    cyEditor = CytoscapeEditorFactory.INSTANCE.getEditor(
                        CytoscapeEditorManager.DEFAULT_EDITOR_TYPE);
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
                	CytoscapeEditorManager.log("Building network view for: " 
                			+ view + " using editor " + cyEditor);
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
        final int[] nodes       = new int[1];
        int         nodeIdx = node.getRootGraphIndex();
        nodes[0] = nodeIdx;

        final CyNetworkView networkView = Cytoscape.getCurrentNetworkView();
        final CyNetwork     cyNet = net;
        final Node          n     = node;

        NodeView      nview  = networkView.getNodeView(node);
        final Point2D offset = nview.getOffset();

        //		CytoscapeEditorManager.log("added node: " + node + " at coordinates "
        //				+ nview.getOffset());

        // AJK: 06/21/06 gevalt, what a hack!  store coordinate position on node attributes so
        //    that a subsequent redo will restore node to its coordinate position
        CyAttributes nodeAttribs = Cytoscape.getNodeAttributes();
        nodeAttribs.setAttribute(node.getIdentifier(),
                                 "X_pos",
                                 new Double(offset.getX()));
        nodeAttribs.setAttribute(node.getIdentifier(),
                                 "Y_pos",
                                 new Double(offset.getY()));

        // setup the clipboard and undo manager to be able to undo the deletion
        CytoscapeEditorManager.getNodeClipBoard().elements(nodes);

        CytoscapeEditorManager.addEdit(
            new AbstractUndoableEdit() {
                    // MLC 08/06/06:
                    private static final long serialVersionUID = -3554511199191774681L;
                    final String              network_id = cyNet.getIdentifier();

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
                            CytoscapeEditorManager.getNodeClipBoard()
                                                  .elements(nodes);
                        }
                    }

                    public void redo() {
                        super.redo();

                        CyNetwork network = Cytoscape.getNetwork(network_id);

                        if (network != null) {
                            network.restoreNodes(nodes);

                            // signal end to Undo Manager; this enables redo
                            // restore positions of nodes
                            NodeView     nv          = networkView.getNodeView(n);
                            CyAttributes nodeAttribs = Cytoscape.getNodeAttributes();
                            Double       xPos        = nodeAttribs.getDoubleAttribute(
                                n.getIdentifier(),
                                "X_pos");
                            Double       yPos        = nodeAttribs.getDoubleAttribute(
                                n.getIdentifier(),
                                "Y_pos");
                            nv.setOffset(xPos.doubleValue(), yPos.doubleValue());
                        }
                    }
                });
    }

    private void setupUndoableEdgeAdditionEdit(CyNetwork net, CyEdge edge) {
        final int[]     edges = new int[1];
        final CyNetwork cyNet = net;

        if (edge != null) {
            int edgeIdx = edge.getRootGraphIndex();
            edges[0] = edgeIdx;
        }

        // setup the clipboard and undo manager to be able to undo the deletion
        CytoscapeEditorManager.getEdgeClipBoard().elements(edges);

        CytoscapeEditorManager.log("Adding an ADD edit to " + cyNet);
        CytoscapeEditorManager.addEdit(
            new AbstractUndoableEdit() {
                    // MLC 08/06/06:
                    private static final long serialVersionUID = 1707778386107798302L;
                    final String              network_id = cyNet.getIdentifier();

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
                            CytoscapeEditorManager.getEdgeClipBoard()
                                                  .elements(edges); // sets
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

        // CytoscapeEditorManager.log ("GraphPerspectiveChanged for network: " + net);
        boolean nodeChanges = false; // only create the
                                     // set if we need it

        // CytoscapeEditorManager.log ("GraphPerspectiveChanged for network: " + net);
        boolean edgeChanges = false; // only create the
                                     // set if we need it

        if (event.isNodesHiddenType()) { // at least one node was hidden

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
        	// AJK: 12/13/06 to fix NPE bug 
//            Cytoscape.firePropertyChange(Cytoscape.NETWORK_MODIFIED,
//                                         CytoscapeEditorManager.CYTOSCAPE_EDITOR, // for distinguishing from batch firing of event
//                                         Cytoscape.getCurrentNetwork());
        }
    }
}
