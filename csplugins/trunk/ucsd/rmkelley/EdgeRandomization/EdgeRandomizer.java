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
import cern.colt.map.OpenIntIntHashMap;




/**
 * The purpose of this class is to provide facilities for 
 * randomizing edges on a graph. This includes things
 * like generating a topology score matrix (currently only
 * undirected) and creating an entirely new random
 * network with the same score distribution. If you create this object
 * and then change the seed network, bad things will probably happen.
 * You've been warned.
 */
public class EdgeRandomizer{
  Random rand;
  //CyNetwork seedNetwork;
  List directedTypes;
  //HashMap type2EdgeList;
  HashMap type2SourceArray;
  HashMap type2TargetArray;
  boolean [][] adjacencyMatrix;
  int [] nodeIndices;
  OpenIntIntHashMap perspective2root;
  /**
   * When randomizing, the expected number of times each
   * edge will be swapped
   */
  protected static final int EXPTECTED_EDGE_SWAPS = 2;
  public EdgeRandomizer(CyNetwork seedNetwork, List directedTypes){
    //this.seedNetwork = seedNetwork;
    this.directedTypes = new Vector(directedTypes);
    this.rand = new Random();
    perspective2root = new OpenIntIntHashMap(seedNetwork.getNodeCount());
    nodeIndices = seedNetwork.getNodeIndicesArray();
    for(int idx = 0;idx<nodeIndices.length;idx++){
      perspective2root.put(idx,nodeIndices[idx]);
    }
    HashMap type2EdgeList = createType2EdgeListMap(seedNetwork);
    type2SourceArray = new HashMap();
    type2TargetArray = new HashMap();
    adjacencyMatrix = new boolean[seedNetwork.getNodeCount()][seedNetwork.getNodeCount()];
    for(Iterator typeIt = type2EdgeList.keySet().iterator();typeIt.hasNext();){
      Object type = typeIt.next();
      int [] sources = generateSources((List)type2EdgeList.get(type),seedNetwork);
      int [] targets = generateTargets((List)type2EdgeList.get(type),seedNetwork);
      type2SourceArray.put(type,sources);
      type2TargetArray.put(type,targets);
      boolean directed = directedTypes.contains(type);
      for(int idx = 0;idx < sources.length; idx++){
	adjacencyMatrix[sources[idx]][targets[idx]] = true;
	if(!directed){
	  adjacencyMatrix[targets[idx]][sources[idx]] = true;
	}
      }
    }
  }

  /**
   * Create a matrix detailing how many types a paritcular node
   * pairing was observed in "iteration_limit" degree-preserving
   * random graphs
   */
  public int [][] createUndirectedCountMatrix(int iteration_limit){
    /*
     * First generate the matrix that will hold the counts, this is only
     * triangle because we are only interested in keeping track
     * of undirected counts
     */
    int [][] counts = initializeUndirectedCountMatrix(nodeIndices.length);
       
    /**
     * Separately randomize each type of edge, updating the count
     * matrix
     */
    for(Iterator typeIt = type2SourceArray.keySet().iterator();typeIt.hasNext();){
      String type = (String)typeIt.next();
      updateUndirectedCountsForType(type,(int[])type2SourceArray.get(type),(int[])type2TargetArray.get(type),directedTypes.contains(type), iteration_limit, counts);
    }

    return counts;
  }

  public CyNetwork randomizeNetwork(){
    
    CyNetwork result = null;
    /*
     * For each edge type, create the random edges
     */
    for(Iterator typeIt = type2SourceArray.keySet().iterator();typeIt.hasNext();){
      String type = (String)typeIt.next();
      //List edgeList = (List)type2EdgeList.get(type);
      //int [] sources = generateSources(edgeList);
      //int [] targets = generateTargets(edgeList);
      int [] sources = (int[])type2SourceArray.get(type);
      int [] targets = (int[])type2TargetArray.get(type);
      boolean directed = directedTypes.contains(type);
      randomizeEdges(sources,targets,directed,adjacencyMatrix);
      /*
       * Map the indices back into root graph indices so we can generate
       * hte nodes
       */
      int [] root_sources = new int [sources.length];
      int [] root_targets = new int [targets.length];
      for(int idx = 0;idx < sources.length; idx++){
	root_sources[idx] = perspective2root.get(sources[idx]);
	root_targets[idx] = perspective2root.get(targets[idx]);
      }
      /*
       * Generate the new edges
       */
      int [] new_edges = Cytoscape.getRootGraph().createEdges(root_sources,root_targets,directed);
      if(result == null){
	result = Cytoscape.createNetwork(nodeIndices,new_edges,"Random network");
      }
      else{
	/*
	 * Add the edges into the network
	 */
	result.restoreEdges(new_edges);
      }
    }
    return result;
  }
  
  protected HashMap createType2EdgeListMap(CyNetwork seedNetwork){
    /*
     * Generate a list of edges for each type
     * of edge
     */
    HashMap result = new HashMap();
    for(Iterator edgeIt = seedNetwork.edgesIterator();edgeIt.hasNext();){
      Edge edge = (Edge)edgeIt.next();
      if(edge.getSource() == edge.getTarget()){
	System.err.println("Ignoring self edge");
	continue;
      }
      String type = (String)seedNetwork.getEdgeAttributeValue(edge,Semantics.INTERACTION);
      if(!result.containsKey(type)){
	result.put(type,new Vector());
      }
      ((List)result.get(type)).add(edge);
    }
    return result;
  }

  protected int [][] initializeUndirectedCountMatrix(int nodeCount){
    int [][] result = new int[nodeCount][];
    for(int idx = 0;idx<nodeCount;idx++){
      result[idx] = new int[idx];
      for(int idy=0;idy<result[idx].length;idy++){
	result[idx][idy] = 1;
      }
    }
    return result;
  }

  protected void updateUndirectedCountsForType(String type, int [] sources, int [] targets, boolean directed, int iteration_limit, int [][] counts){
    /*
     * The adjacency matrix keeps track of adjacent nodes for this edge type
     */
    //boolean [][] adjacencyMatrix = new boolean[nodeIndicies.getNodeCount()][seedNetwork.getNodeCount()];
    /*
     * Make the adjacency matrix consistent with the current sources
     * and targets
     */
    //for(int idx = 0;idx < sources.length; idx++){
    //  adjacencyMatrix[sources[idx]][targets[idx]] = true;
    //  if(!directed){
    //	adjacencyMatrix[targets[idx]][sources[idx]] = true;
    //  }
    //}
    /*
     * Set up the progress monitor
     */
    ProgressMonitor myMonitor =  new ProgressMonitor(Cytoscape.getDesktop(),null, "Randomizing Type: "+type,0,100);
    myMonitor.setMillisToPopup(50);
    int updateInterval = (int)Math.ceil(iteration_limit/100.0);
    int progress = 0;
    /*
     * Randomize the edges, and then update the count matrix
     */
    for(int idx = 0; idx< iteration_limit; idx++){
      if(idx%updateInterval == 0){
	if(myMonitor.isCanceled()){
	  throw new RuntimeException("Score file generation cancelled");
	}
	myMonitor.setProgress(progress++);
      }

      randomizeEdges(sources,targets,directed,adjacencyMatrix);
      for(int idy = 0;idy < sources.length; idy++){
	int source = sources[idy];
	int target = targets[idy];
	if(source > target){
	  counts[source][target] += 1;
	}
	else{
	  counts[target][source] += 1;
	}
      }
    }
    myMonitor.close();
  }

  protected void randomizeEdges(int [] sources, int [] targets, boolean directed, boolean [][] adjacencyMatrix){
    int randomized_edges = 0;
    int randomized_edge_limit = sources.length*EXPTECTED_EDGE_SWAPS;
    while(randomized_edges < randomized_edge_limit){
      /*
       * Choose a random pair of edges
       */
      int edge_one = rand.nextInt(sources.length);
      int edge_two = rand.nextInt(targets.length);

      int old_source_1 = sources[edge_one];
      int old_target_1 = targets[edge_one];

      /**
       * If the edge is undirected, we can 
       * swap the source and target of one
       * of the edges to do a different type
       * of randomization, since two ways
       * of swapping the edges are possible
       */
      int old_source_2,old_target_2;
      if(directed || rand.nextBoolean()){
	old_source_2 = sources[edge_two];
	old_target_2 = targets[edge_two];
      }
      else{
	old_source_2 = targets[edge_two];
	old_target_2 = sources[edge_two];
      }
      
      /*
       * Here we determine what the new edges would be
       */
      int new_target_1,new_target_2;
      new_target_1 = old_target_2;
      new_target_2 = old_target_1;
    
      /*
       * Then we check if these are valid new edges
       */
      /*
       * The first check is to see if the edges are already present
       * in the network
       */
      if(adjacencyMatrix[old_source_1][new_target_1] || adjacencyMatrix[old_source_2][new_target_2]){
	continue;
      }
      /*
       * The second check is to see if one of the edges will form a 
       * self-loop
       */
      if(old_source_1 == new_target_1 || old_source_2 == new_target_2){
	continue;
      }
      /*
       * Everything looks ok, actually perform the swap
       */
      /*
       * First update the adjacecny matrix
       */
      adjacencyMatrix[old_source_1][old_target_1] = false;
      adjacencyMatrix[old_source_2][old_target_2] = false;
      adjacencyMatrix[old_source_1][new_target_1] = true;
      adjacencyMatrix[old_source_2][new_target_2] = true;
      
      /*
       * If the edge type is undirected, we hae to do
       * additional updates
       */
      if(!directed){
	adjacencyMatrix[old_target_1][old_source_1] = false;
	adjacencyMatrix[old_target_2][old_source_2] = false;
	adjacencyMatrix[new_target_1][old_source_1] = true;
	adjacencyMatrix[new_target_2][old_source_2] = true;
	sources[edge_one] = old_source_1;
	sources[edge_two] = old_source_2;
      }
      
      targets[edge_one] = new_target_1;
      targets[edge_two] = new_target_2;
      
      randomized_edges++;
    }
  }

  protected int [] generateSources(List edgeList, CyNetwork seedNetwork){
    int [] sources = new int[edgeList.size()];
    int idx = 0;
    for(Iterator edgeIt = edgeList.iterator();edgeIt.hasNext();idx++){
      Edge edge = (Edge)edgeIt.next();
      sources[idx] = seedNetwork.getIndex(edge.getSource())-1;
    }
    return sources;
  }
  
  protected int [] generateTargets(List edgeList, CyNetwork seedNetwork){
    int [] targets = new int[edgeList.size()];
    int idx = 0;
    for(Iterator edgeIt = edgeList.iterator();edgeIt.hasNext();idx++){
      Edge edge = (Edge)edgeIt.next();
      targets[idx] = seedNetwork.getIndex(edge.getTarget())-1;
    }
    return targets;
  }
}
