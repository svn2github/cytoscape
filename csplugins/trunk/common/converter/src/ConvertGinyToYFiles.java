package common.converter;
/** Copyright (c) 2003 Institute for Systems Biology, University of
 ** California at San Diego, and Memorial Sloan-Kettering Cancer Center.
 **
 ** Code written by: Dan Tenenbaum
 ** Based on Ethan Cerami's ConvertYFilesToGiny.java
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
import y.base.Edge;
import y.base.Node;
import y.view.Graph2D;

import java.util.HashMap;

/**
 * Converts YFiles to GINY.
 * Useful if you want to make use of the y.algo algorithm package.
 *
 * @author Dan Tenenbaum, Ethan Cerami
 * 
 */
public class ConvertGinyToYFiles {

    /**
     * Convert Specified GINY GraphPerspective to a YFiles Graph2D Object.
     * @param gGraph GINY Graph Perspective Object.
     * @return YFiles Graph2D Object.
     */
    public static Graph2D convertToYFiles(GraphPerspective gGraph) {
    	Graph2D retGraph = new Graph2D();
        HashMap nodeMap = new HashMap();

        //  Transfer All Nodes
        int[] nodeIndices = gGraph.getNodeIndicesArray();
        for (int i = 0; i < nodeIndices.length; i++) {
			giny.model.Node gNode = gGraph.getNode(nodeIndices[i]);
			// TODO -- use some real values for node location. 
			// otherwise, if you want to call algorithms that give weight
			// to edge length, they won't work correctly.
			Node yNode = retGraph.createNode(i,i,gNode.toString()); //bogus location parameters
			nodeMap.put(gNode.toString(),yNode);
		}
		
		//  Transfer All Edges
		int[] edgeIndices = gGraph.getEdgeIndicesArray();
		for (int i = 0; i < edgeIndices.length; i++) {
			giny.model.Edge gEdge = gGraph.getEdge(edgeIndices[i]);
			giny.model.Node gSource = gEdge.getSource();
			giny.model.Node gTarget = gEdge.getTarget();
			Node ySource = (Node)nodeMap.get(gSource.toString());
			Node yTarget = (Node)nodeMap.get(gTarget.toString());
			retGraph.createEdge(ySource,yTarget);
		}		

        return retGraph;
    }
}
