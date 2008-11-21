// $Id: MergeNetworkEdit.java,v 1.1 2007/06/22 16:02:34 grossb Exp $
//------------------------------------------------------------------------------
/** Copyright (c) 2007 Memorial Sloan-Kettering Cancer Center.
 **
 ** Code written by: Ethan Cerami, Benjamin Gross
 ** Authors: Ethan Cerami, Gary Bader, Chris Sander, Benjamin Gross
 **
 ** This library is free software; you can redistribute it and/or modify it
 ** under the terms of the GNU Lesser General Public License as published
 ** by the Free Software Foundation; either version 2.1 of the License, or
 ** any later version.
 **
 ** This library is distributed in the hope that it will be useful, but
 ** WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
 ** MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
 ** documentation provided hereunder is on an "as is" basis, and
 ** Memorial Sloan-Kettering Cancer Center
 ** has no obligations to provide maintenance, support,
 ** updates, enhancements or modifications.  In no event shall
 ** Memorial Sloan-Kettering Cancer Center
 ** be liable to any party for direct, indirect, special,
 ** incidental or consequential damages, including lost profits, arising
 ** out of the use of this software and its documentation, even if
 ** Memorial Sloan-Kettering Cancer Center
 ** has been advised of the possibility of such damage.  See
 ** the GNU Lesser General Public License for more details.
 **
 ** You should have received a copy of the GNU Lesser General Public License
 ** along with this library; if not, write to the Free Software Foundation,
 ** Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 **/
package org.cytoscape.coreplugin.cpath2.cytoscape;

// imports

import cytoscape.Cytoscape;
import cytoscape.util.undo.CyAbstractEdit;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.view.GraphView;
import org.cytoscape.view.NodeView;

import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * An undoable edit used by MergeNetworkTask
 * to provide undo/redo support.  Code based on cytoscape.editor.AddNodeEdit
 */
public class MergeNetworkEdit extends CyAbstractEdit {

    /**
     * ref to GraphPerspective that we are modifying
     */
    private CyNetwork cyNetwork;

    /**
     * ref to map: node is key, value is node position
     */
    private Map<CyNode, Point2D.Double> cyNodes;

    /**
     * ref to edge set
     */
    private Set<CyEdge> cyEdges;

    /**
     * Constructor.
     *
     * @param cyNetwork GraphPerspective
     * @param cyNodes   Set<Node>
     * @param cyEdges   Set<Edge>
     */
    public MergeNetworkEdit(CyNetwork cyNetwork, Set<CyNode> cyNodes, Set<CyEdge> cyEdges) {
        super("Merge Network");

        // check args
        if (cyNetwork == null || cyNodes == null || cyEdges == null)
            throw new IllegalArgumentException("network, nodes, or edges is null");

        // init args
        this.cyNetwork = cyNetwork;
        this.cyEdges = cyEdges;

        this.cyNodes = new HashMap<CyNode, Point2D.Double>();
        GraphView view = Cytoscape.getNetworkView(cyNetwork.getIdentifier());
        if (view != null || view != Cytoscape.getNullNetworkView()) {
            for (CyNode cyNode : cyNodes) {
                NodeView nv = view.getNodeView(cyNode);
                Point2D.Double point = new Point2D.Double(nv.getXPosition(), nv.getYPosition());
                this.cyNodes.put(cyNode, point);
            }
        }
    }

    /**
     * Method to undo this network merge
     */
    public void undo() {
        super.undo();

        // iterate through nodes and hide each one
        for (CyNode cyNode : cyNodes.keySet()) {
            cyNetwork.hideNode(cyNode);
        }

        // iteracte through edges and hide each one
        for (CyEdge cyEdge : cyEdges) {
            cyNetwork.hideEdge(cyEdge);
        }

        // fire Cytoscape.NETWORK_MODIFIED
        Cytoscape.firePropertyChange(Cytoscape.NETWORK_MODIFIED, null, cyNetwork);
    }

    /**
     * Method to redo this network merge
     */
    public void redo() {
        super.redo();

        // get ref to view
        GraphView view = Cytoscape.getNetworkView(cyNetwork.getIdentifier());

        if (view != null || view != Cytoscape.getNullNetworkView()) {

            // iterate through nodes and restore each one (also set proper position)
            for (CyNode cyNode : cyNodes.keySet()) {
                cyNetwork.restoreNode(cyNode);
                Point2D.Double point = cyNodes.get(cyNode);
                NodeView nv = view.getNodeView(cyNode);
                nv.setXPosition(point.getX());
                nv.setYPosition(point.getY());
            }

            // interate through edges and restore each one...
            for (CyEdge cyEdge : cyEdges) {
                cyNetwork.restoreEdge(cyEdge);
            }

            // do we perform layout here ?
        }

        // fire Cytoscape.NETWORK_MODIFIED
        Cytoscape.firePropertyChange(Cytoscape.NETWORK_MODIFIED, null, cyNetwork);
	}
}
