/* -*-Java-*-
********************************************************************************
*
* File:         SelectHyperEdgeAction.java
* RCS:          $Header: /cvs/cvsroot/lstl-lsi/HyperEdgeEditor/src/cytoscape/hyperedge/editor/actions/SelectHyperEdgeAction.java,v 1.1 2007/07/04 01:19:09 creech Exp $
* Description:
* Author:       Michael L. Creech
* Created:      Wed Dec 06 10:30:18 2006
* Modified:     Thu Jun 28 17:35:04 2007 (Michael L. Creech) creech@w235krbza760
* Language:     Java
* Package:
* Status:       Experimental (Do Not Distribute)
*
* (c) Copyright 2006, Agilent Technologies, all rights reserved.
*
********************************************************************************
*/
package cytoscape.hyperedge.editor.actions;

import cytoscape.hyperedge.HyperEdge;
import cytoscape.hyperedge.editor.HyperEdgeEditor;

import cytoscape.CyEdge;
import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.Cytoscape;

import cytoscape.editor.CytoscapeEditorManager;


import cytoscape.util.CytoscapeAction;

import cytoscape.view.CyNetworkView;

import java.awt.event.ActionEvent;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class SelectHyperEdgeAction extends CytoscapeAction {
    /**
         *
         */
    private static final long serialVersionUID = 5456878151703608284L;
    private final static String ACTION_NAME = "Select";
    private HyperEdge           _he;
    private CyNetwork           _net;

    public SelectHyperEdgeAction(HyperEdge he, CyNetwork net) {
        super(ACTION_NAME);
        _he  = he;
        _net = net;
    }

    public void actionPerformed(ActionEvent ae) {
        if (_he != null) {
	    _net.unselectAllNodes();
	    _net.unselectAllEdges();
            CyNode           node;
            List<CyNode>     selectedNodes = new ArrayList<CyNode>();
            List<CyEdge>     selectedEdges = new ArrayList<CyEdge>();
            Iterator<CyNode> nodes         = _he.getNodes(null);

            while (nodes.hasNext()) {
                node = nodes.next();

                // selectedNodes.add(node);
                Iterator<CyEdge> edges = _he.getEdges(node);

                while (edges.hasNext()) {
                    selectedEdges.add(edges.next());
                }
            }

            selectedNodes.add(_he.getConnectorNode());
            _net.setSelectedNodeState(selectedNodes, true);
            _net.setSelectedEdgeState(selectedEdges, true);

            CyNetworkView   cnv             = Cytoscape.getCurrentNetworkView();
            // HyperEdgeEditor hyperEdgeEditor = (HyperEdgeEditor) CytoscapeEditorManager.getEditorForView(cnv);
            HyperEdgeEditor hyperEdgeEditor = (HyperEdgeEditor) CytoscapeEditorManager.getCurrentEditor();
	    //            HEUtils.log("SelectHyperEdgeAction: hyperEdgeEditor = " +
	    //                        hyperEdgeEditor);
	    //            HEUtils.log("SelectHyperEdgeAction: currentEditor = " +
	    //                        CytoscapeEditorManager.getCurrentEditor());
            hyperEdgeEditor.resetHyperEdgeClipboard();
            hyperEdgeEditor.addToHyperEdgeClipboard(_he);
            // assumes _net is the current network:
            cnv.redrawGraph(true, true);
        }
    }
}
