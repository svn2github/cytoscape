/** Copyright (c) 2003 Institute for Systems Biology, University of
 ** California at San Diego, and Memorial Sloan-Kettering Cancer Center.
 **
 ** Code written by: Ethan Cerami
 ** Authors: Ethan Cerami, Gary Bader, Chris Sander
 **
 ** This library is free software; you can redistribute it and/or modify it
 ** under the terms of the GNU Lesser General Public License as published
 ** by the Free Software Foundation; either version 2.1 of the License, or
 ** any later version.
 **
 ** This library is distributed in the hope that it will be useful, but
 ** WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
 ** MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
 ** documentation provided hereunder is on an "as is" basis, and the
 ** Institute for Systems Biology, the University of California at San Diego
 ** and/or Memorial Sloan-Kettering Cancer Center
 ** have no obligations to provide maintenance, support,
 ** updates, enhancements or modifications.  In no event shall the
 ** Institute for Systems Biology, the University of California at San Diego
 ** and/or Memorial Sloan-Kettering Cancer Center
 ** be liable to any party for direct, indirect, special,
 ** incidental or consequential damages, including lost profits, arising
 ** out of the use of this software and its documentation, even if the
 ** Institute for Systems Biology, the University of California at San
 ** Diego and/or Memorial Sloan-Kettering Cancer Center
 ** have been advised of the possibility of such damage.  See
 ** the GNU Lesser General Public License for more details.
 **
 ** You should have received a copy of the GNU Lesser General Public License
 ** along with this library; if not, write to the Free Software Foundation,
 ** Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 **/

import giny.model.GraphPerspective;
import giny.model.RootGraph;
import luna.LunaRootGraph;
import y.base.Edge;
import y.base.EdgeCursor;
import y.base.Node;
import y.base.NodeCursor;
import y.view.Graph2D;

import java.util.HashMap;

/**
 * Converts YFiles to GINY.
 *
 * @author Ethan Cerami.
 */
public class ConvertYFilesToGiny {

    /**
     * Convert Specified YFiles Graph2D Object to a GINY GraphPerspective.
     * @param yGraph YFiles Graph2D Object.
     * @return GINY Graph Perspective Object.
     */
    public static GraphPerspective convertToGiny(Graph2D yGraph) {
        RootGraph gRootGraph = new LunaRootGraph();
        HashMap nodeMap = new HashMap();

        //  Transfer All Nodes
        NodeCursor nodeCursor = yGraph.nodes();
        while (nodeCursor.ok()) {
            Node yNode = nodeCursor.node();
            int ginyId = gRootGraph.createNode();
            giny.model.Node gNode = gRootGraph.getNode(ginyId);
            gNode.setIdentifier(yNode.toString());
            nodeMap.put(yNode.toString(), gNode);
            nodeCursor.next();
        }

        //  Transfer All Edges
        EdgeCursor edgeCursor = yGraph.edges();
        while (edgeCursor.ok()) {
            Edge yEdge = edgeCursor.edge();
            Node ySource = yEdge.source();
            Node yTarget = yEdge.target();
            giny.model.Node gSource = (giny.model.Node)
                    nodeMap.get(ySource.toString());
            giny.model.Node gTarget = (giny.model.Node)
                    nodeMap.get(yTarget.toString());
            gRootGraph.createEdge(gSource, gTarget);
            edgeCursor.next();
        }
        GraphPerspective gGraph = gRootGraph.createGraphPerspective
                (gRootGraph.getNodeIndicesArray(),
                        gRootGraph.getEdgeIndicesArray());
        return gGraph;
    }
}
