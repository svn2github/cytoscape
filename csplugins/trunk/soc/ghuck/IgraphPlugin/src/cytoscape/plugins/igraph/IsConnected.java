package cytoscape.plugins.igraph;

import java.awt.event.ActionEvent;
import javax.swing.JOptionPane;
import java.util.*;

import cytoscape.Cytoscape;
import cytoscape.*;
import cytoscape.data.*;
import cytoscape.plugin.CytoscapePlugin;
import cytoscape.util.CytoscapeAction;
import cytoscape.task.Task;
import cytoscape.task.TaskMonitor;


import giny.model.*;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Platform;

public class IsConnected extends CytoscapeAction {


    private IgraphTaskThread t; //This thread is actually where the action is
    private Thread currentThread;


    public IsConnected(IgraphPlugin myPlugin) {
	super("IsConnected");
	setPreferredMenu("Plugins.Igraph");
    }

    public void actionPerformed(ActionEvent e) {
	JOptionPane.showMessageDialog(Cytoscape.getDesktop(), "Is Connected?: " + isConnected());
    }

    public boolean isConnected() {

	loadGraph_optimized();
        return IgraphInterface.isConnected(); //Only call this function if the graph has been loaded


// 	boolean res = false;
// 	try {
// 	    ArrayList<Integer> nodes = loadGraph_optimized();
// 	    JOptionPane.showMessageDialog(Cytoscape.getDesktop(), "Graph loaded...Number of nodes: " + nodes.size());	   
// 	    res = IgraphInterface.isConnected();
// 	}
// 	catch (Exception e) {
// 	    e.printStackTrace();
// 	    JOptionPane.showMessageDialog(Cytoscape.getDesktop(), "Igraph Plugin error:\n" + e.getMessage());	       
// 	    res =  false;
// 	}
// 	finally{
// 	    return res;
// 	}


//         t = new IgraphTaskThread();
//         t.setAlgorithm(1);

//         //This marks the current thread
//         currentThread = Thread.currentThread();

//         t.start();
//         try{
//             t.join(); //Join the background thread running the algorithm with the current thread showing the progressbar
//         }
//         catch(InterruptedException e){
//             //System.out.println("Algorithm terminated");
//             //The failed C algorithm may cause issues here
// 	    e.printStackTrace();
// 	    JOptionPane.showMessageDialog(Cytoscape.getDesktop(), "Igraph Plugin error:\n" + e.getMessage()); 
//         }

// 	return t.getResult();
    }

    //This will load the graph into c
    public static ArrayList<Integer> loadGraph_optimized(){

        // Nodes are ordered according to node array.
        CyNetwork network = Cytoscape.getCurrentNetwork();
	
        ArrayList<Integer> selectedNodeIndices = new ArrayList<Integer>();

        int[] edgeIndicesArray = network.getEdgeIndicesArray();
        int[] nodeArray = network.getNodeIndicesArray();

        // Create a reverse mapping
        HashMap<Integer, Integer> nodeIdMapping = new HashMap<Integer, Integer>(nodeArray.length);
        for(int i=0; i<nodeArray.length; i++){
            nodeIdMapping.put(nodeArray[i], i);
        }

        // Get the selectedNodes
        // Return the selected node array list
        Iterator<Node> nodeIt = network.getSelectedNodes().iterator();
        while(nodeIt.hasNext()){
            
            Node node = nodeIt.next();
            ////System.out.println("This is gd:" + nodeIdMapping.get(node.getRootGraphIndex()));
            selectedNodeIndices.add(nodeIdMapping.get(node.getRootGraphIndex()));
        }

        //If this works then it will be a lot faster
        int[] edgeArray = new int[edgeIndicesArray.length * 2];

        //Get all edges, then simplify in igraph
        for(int i = 0; i < edgeIndicesArray.length; i++){

            edgeArray[i*2] = nodeIdMapping.get(network.getEdgeSourceIndex(edgeIndicesArray[i]));
            edgeArray[i*2 + 1] = nodeIdMapping.get(network.getEdgeTargetIndex(edgeIndicesArray[i]));
        }

        IgraphInterface.createGraph(edgeArray, edgeArray.length);
	IgraphInterface.simplify();

        return selectedNodeIndices;
    }
    
}