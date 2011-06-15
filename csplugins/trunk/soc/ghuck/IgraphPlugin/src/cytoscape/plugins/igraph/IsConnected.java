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

import giny.model.*;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Platform;

public class IsConnected extends CytoscapeAction {

    Boolean selectedOnly;

    public IsConnected(IgraphPlugin myPlugin, String name, boolean selectedOnly) {
	super(name);
	setPreferredMenu("Plugins.Igraph.IsConnected");
	this.selectedOnly = new Boolean(selectedOnly);
    }

    public void actionPerformed(ActionEvent e) {
	JOptionPane.showMessageDialog(Cytoscape.getDesktop(), "Is Connected?: " + isConnected(this.selectedOnly));
    }

    public boolean isConnected(boolean selectedOnly) {
	loadGraph_optimized(selectedOnly);
        return IgraphInterface.isConnected(); 
    }

    //This will load the graph into c
    public static HashMap<Integer,Integer> loadGraph_optimized(boolean selectedOnly){

        // Nodes are ordered according to node array.
        CyNetwork network = Cytoscape.getCurrentNetwork();
	
	//        ArrayList<Integer> selectedNodeIndices = new ArrayList<Integer>();

	//        int[] edgeIndicesArray = network.getEdgeIndicesArray();
        // int[] nodeArray = network.getNodeIndicesArray();
	//	Set<Node> selectedNodes = network.geSelectedNodes();

        // Create a reverse mapping
	int nodeCount;
	if(selectedOnly) {
	    nodeCount = network.getSelectedNodes().size();
	} else {
	    nodeCount = network.getNodeCount();
	}
        HashMap<Integer, Integer> nodeIdMapping = new HashMap<Integer, Integer>(nodeCount);
	int j = 0;
	Iterator<Node> nodeIt;
	if(selectedOnly) {
	   nodeIt = network.getSelectedNodes().iterator();
	} else {
	    nodeIt = network.nodesIterator();
	}
	
        while(nodeIt.hasNext()){            
            Node node = nodeIt.next();
            nodeIdMapping.put(node.getRootGraphIndex(), j);
	    j++;
        }
	

//         for(int i=0; i<nodeArray.length; i++){
//             nodeIdMapping.put(nodeArray[i],i);
//         }

        // Get the selectedNodes
        // Return the selected node array list
//         Iterator<Node> nodeIt = network.getSelectedNodes().iterator();
//         while(nodeIt.hasNext()){
            
//             Node node = nodeIt.next();
//             ////System.out.println("This is gd:" + nodeIdMapping.get(node.getRootGraphIndex()));
//             selectedNodeIndices.add(nodeIdMapping.get(node.getRootGraphIndex()));
//         }

        //If this works then it will be a lot faster


        int[] edgeArray = new int[network.getEdgeCount() * 2];
	int i = 0;

	Iterator<Edge> it = network.edgesIterator();

	while (it.hasNext()) {
	    Edge e = (CyEdge) it.next();

	    Node source = e.getSource();
	    Node target = e.getTarget();

	    if(!selectedOnly || (network.isSelected(source) && network.isSelected(target)) ){
		edgeArray[i]     = nodeIdMapping.get(source.getRootGraphIndex());
		edgeArray[i + 1] = nodeIdMapping.get(target.getRootGraphIndex());
		i += 2;
	    }
	}


//         for(int i = 0; i < edgeIndicesArray.length; i++){

// 	    if(!selectedOnly || 
// 	       (network.isSelected() && network.isSelected()) ){
//             edgeArray[i * 2]     = nodeIdMapping.get(network.getEdgeTargetIndex(edgeIndicesArray[i]));
//             edgeArray[i * 2 + 1] = nodeIdMapping.get(network.getEdgeSourceIndex(edgeIndicesArray[i]));
// 	    }
// 	}

        IgraphInterface.createGraph(edgeArray, i);
	//	IgraphInterface.simplify();

        return nodeIdMapping;
    }
    
}