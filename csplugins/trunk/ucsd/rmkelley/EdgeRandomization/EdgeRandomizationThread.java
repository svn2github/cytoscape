package ucsd.rmkelley.EdgeRandomization;
import java.io.*;
import java.util.*;
import edu.umd.cs.piccolo.activities.*;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.JOptionPane;
import giny.view.NodeView;
import giny.model.*;
import cytoscape.plugin.CytoscapePlugin;
import cytoscape.Cytoscape;
import cytoscape.CyNetwork;
import cytoscape.view.CyNetworkView;
import phoebe.PNodeView;
import phoebe.PGraphView;
import cytoscape.data.Semantics;
import javax.swing.*;
import javax.swing.table.*;
import javax.swing.event.*; 
import java.awt.BorderLayout;
import java.awt.event.*;
import cytoscape.layout.*;


public class EdgeRandomizationThread extends Thread{
  int iteration_limit;
  Random rand = new Random();
  //the file to which the results are output
  File scoreFile;

  /**
   * The network which will be randomizaed
   */
  CyNetwork currentNetwork;
  
  /**
   * The options for computation
   */
  EdgeRandomizationOptions options;

  public EdgeRandomizationThread(EdgeRandomizationOptions options){
    this.options = options;
  }
  
  public void run(){
    currentNetwork = options.currentNetwork;
    int [][] counts = createCountMatrix(currentNetwork.getNodeCount());
     //figure out what the different edge types are in the graph
    
    //Assign each edge to its respective type
    HashMap type2EdgeList = new HashMap();
    for(Iterator edgeIt = currentNetwork.edgesIterator();edgeIt.hasNext();){
      Edge edge = (Edge)edgeIt.next();
      if(edge.getSource() == edge.getTarget()){
	System.err.println("Ignoring self edge");
	continue;
      }
      String type = (String)currentNetwork.getEdgeAttributeValue(edge,Semantics.INTERACTION);
      if(!type2EdgeList.containsKey(type)){
	type2EdgeList.put(type,new Vector());
      }
      ((List)type2EdgeList.get(type)).add(edge);
    }
    
   
    List directedTypes = options.directedTypes;
    iteration_limit = options.iterations;
    boolean [][] adjacencyMatrix = new boolean[currentNetwork.getNodeCount()][currentNetwork.getNodeCount()];
    
    for(Iterator typeIt = type2EdgeList.keySet().iterator();typeIt.hasNext();){
      String type = (String)typeIt.next();
      boolean directed = directedTypes.contains(type);
      List edgeList = (List)type2EdgeList.get(type);
      int [][] edges = new int[edgeList.size()][2];
      int idx = 0;
      for(Iterator edgeIt = edgeList.iterator();edgeIt.hasNext();idx++){
	Edge edge = (Edge)edgeIt.next();
	edges[idx][0] = currentNetwork.getIndex(edge.getSource())-1;
	edges[idx][1] = currentNetwork.getIndex(edge.getTarget())-1;
	if(!directed && edges[idx][0] < edges[idx][1]){
	  int temp = edges[idx][0];
	  edges[idx][0] = edges[idx][1];
	  edges[idx][1] = temp;
	}
      }
      updateCountMatrix(currentNetwork, type, edges, directed, counts, adjacencyMatrix);
    }
    
    String filename = currentNetwork.getTitle()+".rand";
    scoreFile = new File(filename);
    try{
      ProgressMonitor myMonitor =  new ProgressMonitor(Cytoscape.getDesktop(),null, "Writing file to disk",0,currentNetwork.getNodeCount());
      myMonitor.setMillisToPopup(50);
      int updateInterval = (int)Math.ceil(currentNetwork.getNodeCount()/100.0);
      PrintStream stream = new PrintStream(new FileOutputStream(scoreFile));
      stream.println(iteration_limit);
      for(int idx=0;idx<currentNetwork.getNodeCount();idx++){
	if(idx % updateInterval == 0){
	  if(myMonitor.isCanceled()){
	    throw new RuntimeException("Score file generation cancelled");
	  }
	  myMonitor.setProgress(idx);
	}
	stream.print(currentNetwork.getNodeAttributeValue(currentNetwork.getNode(idx+1),Semantics.CANONICAL_NAME));
	for(int idy=0;idy<counts[idx].length;idy++){
	  stream.print("\t"+counts[idx][idy]);
	}
	stream.println();
      }
      myMonitor.close();
      stream.close();
    }catch(Exception e){
      e.printStackTrace();
      System.exit(-1);
    }
    JOptionPane.showMessageDialog(Cytoscape.getDesktop(),"Result stored in file: "+filename,"Randomization complete",JOptionPane.INFORMATION_MESSAGE);
  }



  public File getScoreFile(){
    return scoreFile;
  }

  public int [][] createCountMatrix(int nodeCount){
    int [][] result = new int[nodeCount][];
    for(int idx = 0;idx<nodeCount;idx++){
      result[idx] = new int[idx];
      for(int idy=0;idy<result[idx].length;idy++){
	result[idx][idy] = 1;
      }
    }
    return result;
  }


  //this function must be updated to reflect the influence of multiple types of network edges
  //connecting the same two nodes.
  public void updateCountMatrix(CyNetwork fullNetwork,  String type, int [][] edgeList, boolean directed, int [][] counts, boolean [][] adjacencyMatrix){
    for(int idx=0;idx<adjacencyMatrix.length;idx++){
      for(int idy=0;idy<adjacencyMatrix[idx].length;idy++){
	adjacencyMatrix[idx][idy] = false;
      }
    }
        
 
    for(int idx=0;idx < edgeList.length;idx++){
      adjacencyMatrix[edgeList[idx][0]][edgeList[idx][1]] = true;
    }

    int iteration = 0;
    ProgressMonitor myMonitor =  new ProgressMonitor(Cytoscape.getDesktop(),null, "Randomizing Type: "+type,0,100);
    myMonitor.setMillisToPopup(50);
    int updateInterval = (int)Math.ceil(iteration_limit/100.0);
    int progress = 0;
    while(iteration++ < iteration_limit){
      if(iteration%updateInterval == 0){
	if(myMonitor.isCanceled()){
	  throw new RuntimeException("Score file generation cancelled");
	}
	myMonitor.setProgress(progress++);
      }
      int randomized_edges = 0;
      int randomized_edge_limit = edgeList.length*2;
      while(randomized_edges < randomized_edge_limit){
	//chooose pair of random edges
	int edgeOne = rand.nextInt(edgeList.length);
	int edgeTwo = rand.nextInt(edgeList.length);
	
	int old_source_1 = edgeList[edgeOne][0];
	int old_target_1 = edgeList[edgeOne][1];
	int old_source_2 = edgeList[edgeTwo][0];
	int old_target_2 = edgeList[edgeTwo][1];
	
	int new_source_1,new_source_2,new_target_1,new_target_2;
	new_source_1 = old_source_1;
	new_source_2 = old_source_2;
	new_target_1 = old_target_2;
	new_target_2 = old_target_1;
	if(!directed){
	  if(new_source_1 < new_target_1){
	    int temp = new_source_1;
	    new_source_1 = new_target_1;
	    new_target_1 = temp;
	  }
	  if(new_source_2 < new_target_2){
	    int temp = new_source_2;
	    new_source_2 = new_target_2;
	    new_target_2 = temp;
	  }
	}
	//check if the selected edge pair is a valid swap
	//check to see if this will result in a loop
	if(new_source_1 == new_target_1 || new_source_2 == new_target_2){
	  continue;
	}
	//check to see if one of the newly created edges is already in the graph
	if(adjacencyMatrix[new_source_1][new_target_1] || adjacencyMatrix[new_source_2][new_target_2]){
	  continue;
	}
	//everything looks ok, try to actually perform the swap
	adjacencyMatrix[old_source_1][old_target_1] = false;
	adjacencyMatrix[old_source_2][old_target_2] = false;
	adjacencyMatrix[new_source_1][new_target_1] = true;
	adjacencyMatrix[new_source_2][new_target_2] = true;
	edgeList[edgeOne][1] = new_target_1;
	edgeList[edgeTwo][1] = new_target_2;
	if(!directed){
	  edgeList[edgeOne][0] = new_source_1;
	  edgeList[edgeTwo][0] = new_source_2;
	}
	randomized_edges++;
      }
      //update the count array
      for(int idx=0;idx<edgeList.length;idx++){
	int source = edgeList[idx][0];
	int target = edgeList[idx][1];
	//if the edge is present in both directions, only count it once
	if(directed && source < target && adjacencyMatrix[target][source]){
	  continue;
	}
	if(source < target){
	  counts[target][source] += 1;
	}
	else{
	  counts[source][target] += 1;
	}
      }
    }
    myMonitor.close();
  }

}

