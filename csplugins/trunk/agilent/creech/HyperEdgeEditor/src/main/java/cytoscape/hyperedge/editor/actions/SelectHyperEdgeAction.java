/*
 Copyright (c) 2011, The Cytoscape Consortium (www.cytoscape.org)

 This library is free software; you can redistribute it and/or modify it
 under the terms of the GNU Lesser General Public License as published
 by the Free Software Foundation; either version 2.1 of the License, or
 any later version.

 This library is distributed in the hope that it will be useful, but
 WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
 MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
 documentation provided hereunder is on an "as is" basis, and the
 Institute for Systems Biology and the Whitehead Institute
 have no obligations to provide maintenance, support,
 updates, enhancements or modifications.  In no event shall the
 Institute for Systems Biology and the Whitehead Institute
 be liable to any party for direct, indirect, special,
 incidental or consequential damages, including lost profits, arising
 out of the use of this software and its documentation, even if the
 Institute for Systems Biology and the Whitehead Institute
 have been advised of the possibility of such damage.  See
 the GNU Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public License
 along with this library; if not, write to the Free Software Foundation,
 Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
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
