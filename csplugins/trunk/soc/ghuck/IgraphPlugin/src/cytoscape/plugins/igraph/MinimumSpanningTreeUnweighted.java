/**************************************************************************************
Copyright (C) Gerardo Huck, 2011


This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.

**************************************************************************************/

package cytoscape.plugins.igraph;

import java.awt.event.ActionEvent;
import javax.swing.JOptionPane;
import java.util.*;

import cytoscape.Cytoscape;
import cytoscape.*;
import cytoscape.data.*;
import cytoscape.plugin.CytoscapePlugin;
import cytoscape.util.CytoscapeAction;
import cytoscape.logger.CyLogger;
import giny.model.*;

import giny.view.NodeView;
import cytoscape.view.CyNodeView;
import cytoscape.view.CyNetworkView;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Platform;


public class MinimumSpanningTreeUnweighted extends CytoscapeAction {
    
    Boolean selectedOnly;
    
    public MinimumSpanningTreeUnweighted(IgraphPlugin myPlugin, String name, boolean selectedOnly) {
	super(name);
	setPreferredMenu("Plugins.Igraph.Minimum Spanning Tree (Unweighted)");
	this.selectedOnly = new Boolean(selectedOnly);
    }
	
    public void actionPerformed(ActionEvent e) {

	CyLogger logger = CyLogger.getLogger(MinimumSpanningTreeUnweighted.class);

	try {
	    CyNetwork network = Cytoscape.getCurrentNetwork();
	    int numNodes;
	    if (selectedOnly)
		numNodes = network.getSelectedNodes().size();
	    else
		numNodes = network.getNodeCount();

	    /*          Load graph into Igraph library          */
	    HashMap<Integer,Integer> mapping = IgraphAPI.loadGraph(selectedOnly, false);

	    /*          Prepare variables to hold results from native call          */
	    int[] mst = new int[2 * numNodes - 2];	
	    int numEdges;
	    
	    /*          Compute MST          */
	    numEdges = IgraphInterface.minimum_spanning_tree_unweighted(mst);

	    logger.info("Nodes in MST: " + numNodes);
	    logger.info("Edges in MST: " + numEdges);
	    String nodesString = new String();
	    for (int i = 0; i < 2 * numEdges; i++)
		nodesString = nodesString + mst[i] + ", ";
	    logger.info("Nodes: "+ nodesString);

	    /*          Create new network & networkView          */
	    CyNetworkView oldView = Cytoscape.getCurrentNetworkView();

	    // Prepare nodes
	    int[] nodes = new int[numNodes];	
	    int j = 0;
	    Iterator<Node> nodeIt;
	    if(selectedOnly) {
		nodeIt = network.getSelectedNodes().iterator();
	    } else {
		nodeIt = network.nodesIterator();
	    }
		
	    while(nodeIt.hasNext()){            
		Node node = nodeIt.next();
		nodes[j] = node.getRootGraphIndex();
		j++;
	    }

	    // Prepare edges
	    int[] edges = new int[numEdges];
	    int i = 0;


	    // create node reverse mapping (igraphId -> rootGraphIndex)
	    HashMap<Integer,Integer> reverseMapping = new HashMap<Integer,Integer>(mapping.size());

	    Iterator<Map.Entry<Integer,Integer>> mapIter = mapping.entrySet().iterator();
	    while (mapIter.hasNext()) {
		Map.Entry entry = (Map.Entry) mapIter.next();
		reverseMapping.put((Integer) entry.getValue(), (Integer) entry.getKey());
	    }


	    for (int k = 0; k < 2 * numEdges; k += 2) {

		// get rootGraphId's for both nodes of this edge
		int n1 = reverseMapping.get(mst[k]);
		int n2 = reverseMapping.get(mst[k + 1]);

		// Get edges starting or ending in n1
		int[] adjacentEdgesArray = network.getAdjacentEdgeIndicesArray(n1,
									       true,  // undirected edges
									       true, // incoming edges
									       true); // outgoing edges
		
		for (int l = 0; l < adjacentEdgesArray.length; l++) {
		    Edge edge = network.getEdge(adjacentEdgesArray[l]);
		    int edgeSource = edge.getSource().getRootGraphIndex();
		    int edgeTarget = edge.getTarget().getRootGraphIndex();

		    if (edgeSource == n2 || edgeTarget == n2) {
			// This edge is in the MST!
			edges[i] = edge.getRootGraphIndex();
			i++;
			break;
		    }		    
		}
	    }

	    if (i != numEdges) {
		JOptionPane.showMessageDialog(Cytoscape.getDesktop(), "ERROR! Edges found: " + i + " , should be: " + numEdges);		
		return;
	    }
		

// 	    Iterator<Edge> edgeIt = network.edgesIterator();
// 	    while(edgeIt.hasNext()){            
// 		Edge edge = (CyEdge) edgeIt.next();		
// 		Node source = edge.getSource();
// 		Node target = edge.getTarget();
		
// 		int n1 = mapping.get(source.getRootGraphIndex());
// 		int n2 = mapping.get(target.getRootGraphIndex());

// 		logger.info("N1 = " + n1 + " N2 = " + n2);

// 		for(int k = 0; k < 2 * numEdges; k += 2) {
// 		    if (mst[k] == n1 && mst[k + 1] == n2){
// 			edges[i] = edge.getRootGraphIndex();
// 			logger.info("Edge: " + edges[i] + ", i = " + i);
// 			i++;
// 			break;
// 		    }		
// 		}		
// 	    }
	    
// 	    JOptionPane.showMessageDialog(Cytoscape.getDesktop(), "i = " + i + " , numEdges = " + numEdges);

			
	    String newNetworkName = "Minimum Spanning Tree (" + network.getTitle() + ")";

	    CyNetwork newNetwork = Cytoscape.createNetwork(nodes,
							   edges,
							   newNetworkName,
							   network,
							   true);


	    /*          Set node positions          */
	    CyNetworkView newView = Cytoscape.getCurrentNetworkView();	    

	    Iterator<Node> newNodesIt = newNetwork.nodesIterator();

	    while(newNodesIt.hasNext()){            
		Node node = newNodesIt.next();
		NodeView newNodeView = newView.getNodeView(node);
		NodeView oldNodeView = oldView.getNodeView(node);

		//oldNodeView.;

		// Set X and Y positions
		newNodeView.setXPosition(oldNodeView.getXPosition());
		newNodeView.setYPosition(oldNodeView.getYPosition());

		// Set label position
		newNodeView.setLabelPosition(oldNodeView.getLabelPosition());
	    }

	    // Redraw the whole network
	    newView.updateView();
	    newView.redrawGraph(true, true);	    
	    newView.fitContent();
	    

	} catch (Exception ex) {
	    ex.printStackTrace();
	    String message = "Error:\n" + ex.getMessage(); 
	    JOptionPane.showMessageDialog( Cytoscape.getDesktop(), message);
	}


    }

}