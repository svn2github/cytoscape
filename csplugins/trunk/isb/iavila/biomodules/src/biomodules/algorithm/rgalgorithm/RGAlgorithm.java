/**  Copyright (c) 2003 Institute for Systems Biology
 **  This program is free software; you can redistribute it and/or modify
 **  it under the terms of the GNU General Public License as published by
 **  the Free Software Foundation; either version 2 of the License, or
 **  any later version.
 **
 **  This program is distributed in the hope that it will be useful,
 **  but WITHOUT ANY WARRANTY; without even the implied warranty of
 **  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  The software and
 **  documentation provided hereunder is on an "as is" basis, and the
 **  Institute for Systems Biology has no obligations to provide maintenance, 
 **  support, updates, enhancements or modifications.  In no event shall the
 **  Institute for Systems Biology be liable to any party for direct, 
 **  indirect, special,incidental or consequential damages, including 
 **  lost profits, arising out of the use of this software and its 
 **  documentation, even if the Institute for Systems Biology 
 **  has been advised of the possibility of such damage. See the
 **  GNU General Public License for more details.
 **   
 **  You should have received a copy of the GNU General Public License
 **  along with this program; if not, write to the Free Software
 **  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 **/

/**
 * An implementation of <code>BiomodulesAlgorithm</code> that is based on the
 * algorithm developed by Rives and Galitski. This algorithm consists of these steps:
 * 1. Calculate the APSP of the network
 * 2. Calculate the Manhattan-distances of the APSP table
 * 3. Hierarchically cluster the Manhattan distances
 * 4. "Cut" the hierarchical tree and form biomodules
 *
 * @author Iliana Avila-Campillo iavila@systemsbiology.org
 * @version %I%, %G%
 * @since 2.0
 */

// TODO:
// 1. Use progress monitors for lenghty tasks

package biomodules.algorithm.rgalgorithm;

import biomodules.algorithm.*;
import java.util.*;
import cytoscape.*;
import giny.util.*;
import giny.view.*;
import common.algorithms.hierarchicalClustering.*;

public class RGAlgorithm implements BiomodulesAlgorithm{

  /**
   * The String that is used as a key to store or retrieve a <code>RGAlgorithmData</code>
   * client object contained in a <code>CyNetwork</code>
   */
  public static final String CLIENT_DATA_KEY = "biomodules.rgalgorithm.data";

  /**
   * Constructor.
   */
  public RGAlgorithm (){}//constructor
  
  /**
   * Runs the algorithm for finding biomodules on the given network.
   *
   * @param network the <code>CyNetwork</code> to calculate Biomodules
   * @return a 2D array of <code>CyNodes</code>, where each row represents
   * a biomodule, and each cell in that row contains a node in the biomodule
   */
  public CyNode [][] run (CyNetwork network){
    return calculateBiomodules(network);
  }//run
  
  /**
   * Calculates Biomodules.
   * <p>
   * The algorithm consists of these steps:
   * 1. Calculate APSP of the network
   * 2. Calculate Manhattan Distances between every pair of rows in the APSP
   * 3. Hierarchically cluster the network's nodes. The manhattan-distances
   * table is used as the correlation measure to decide which nodes to cluster at each
   * iteration of the hierarchical clustering algorithm.
   * 4. Since the hierarchical tree is not divided into discrete clusters, 'cut' the
   * tree into subtrees by intelligently choosing a 'cutting' point. Each subtree (if it
   * meets the 'biomodule' condition, see <code>meetsBiomoduleCondition()</code>) will
   * become a biomodule.
   * </p>
   *
   * @param network the <code>CyNetwork</code> for which biomodules
   * will be calculated
   * @return a 2D array of <code>CyNodes</code>, where each row represents
   * a biomodule, and each cell in that row contains a node in the biomodule
   */
  public static CyNode[][] calculateBiomodules (CyNetwork network){
  	
  	// TODO: Remove
  	System.out.println("In RGAlgorithm.calculateBiomodules: " + Thread.currentThread());
  	
    ArrayList nodeList = new ArrayList();
  
    // Calculate the All-Pairs-Shortest-Path matrix:
    int [][] apsp = calculateAPSP(network,nodeList);
    
    // Calculate the Manhattan-Distances of the APSP:
    double [][] distances = calculateManhattanDists(apsp);
    
    // Cluster the network starting with the Manhattain-distances:
    HierarchicalClustering clustering = cluster(network,nodeList,distances);
    
    // Set data after it has been calculated so that if there were any errors, the old
    // data is not lost:
    RGAlgorithmData data = getClientData(network);
    data.setOrderedNodes(nodeList);
    if(data.getSaveIntermediaryData()){
    	data.setAPSP(apsp);
    	data.setManhattanDistances(distances);
    }else{
    	data.clearIntermediaryData();
    }
    data.setHierarchicalClustering(clustering);
    int cutJoin = clustering.getJoinNumberWithMaxNumClusters();
    data.setCutJoinNumber(cutJoin);
    
    // Create the biomodules now that the needed data is complete in RGAlgorithmData
    CyNode [][] biomodules = createBiomodules(data);

    
    return biomodules;
  }//calculateBiomodules
    
  /**
   * Gets the client object stored in the given network using the <code>CLIENT_DATA_KEY</code>
   * and returns it. If there is no client object associated with the network, it creates one,
   * sets it, and returns it.
   *
   * @return the <code>RGAlgorithmData</code> object associated with the given network
   */
  public static RGAlgorithmData getClientData (CyNetwork network){
    RGAlgorithmData clientData = (RGAlgorithmData)network.getClientData(CLIENT_DATA_KEY);
    if(clientData == null){
      clientData = new RGAlgorithmData(network);
      network.putClientData(CLIENT_DATA_KEY,clientData);
    }
    return clientData;
  }//getClientData
  
  /**
   * Calculates the All-Pairs-Shortest-Path table for the given network.
   *
   * @param network the <code>CyNetwork</code> for which the APSP will
   * be calculated
   * @param node_list an empty <code>ArrayList</code> in which the <code>CyNodes</code>
   * will be stored in the same order used for the APSP
   * @return a 2D int array of distances, or null if there was an error
   */
  protected static int [][] calculateAPSP (CyNetwork network, ArrayList node_list){
    long startTime = System.currentTimeMillis();
    System.err.println("Calculating APSP...");
    if(network == null){
      return null;
    }
   
    //TODO: NodeDistances is broken!
 
    // my way:
    node_list.clear();
    Iterator it = network.nodesIterator();
    while(it.hasNext()){
      CyNode node = (CyNode)it.next();
      node_list.add(node);
    }//while it
    
    HashMap nodeIndexToMatrixIndexMap = new HashMap();
    CyNode [] nodes = (CyNode[])node_list.toArray(new CyNode[0]);
    for(int i = 0; i < nodes.length; i++){
      nodeIndexToMatrixIndexMap.put(new Integer(nodes[i].getRootGraphIndex()),
                                    new Integer(i));
      System.out.println(nodes[i].getRootGraphIndex() + " -> " + i);
    }//for i

   
    // Gary's way:
    
    //initialize the index map
    //HashMap matrixIndexToNodeIndexMap = new HashMap();
    //HashMap nodeIndexToMatrixIndexMap = new HashMap();
    //Iterator nodes = 
    //Cytoscape.getNetworkView(network.getIdentifier()).getNodeViewsIterator();
    //int count=0;
    //while (nodes.hasNext()) {
    //NodeView nodeView = (NodeView) nodes.next();
    //nodeIndexToMatrixIndexMap.put(new Integer(nodeView.getRootGraphIndex()), 
    //                              new Integer(count));
    //matrixIndexToNodeIndexMap.put(new Integer(count), 
    //                              new Integer(nodeView.getRootGraphIndex()));
    //count++;
    //}

    //create a list of nodes that has the same indices as the nodeIndexToMatrixIndexMap
    //node_list.clear();
    //Collection matrixIndices = matrixIndexToNodeIndexMap.values();
    //int i = 0;
    //for(Iterator iterator = matrixIndices.iterator(); iterator.hasNext();) {
    //Integer nodeIndex = (Integer) iterator.next();
    //node_list.add(i, network.getNode(nodeIndex.intValue()));
    //i++;
    //}
    
    NodeDistances ind = new NodeDistances(node_list, 
                                          network, 
                                          nodeIndexToMatrixIndexMap);
    int[][] distances = (int[][])ind.calculate();
    
     
    //TODO: Monitor NodeDistances. Right now I can't, because NodeDistances is
    // a MonitorableTask, but CytoscapeProgressMonitor takes a MonitoredTask.
    
    //CytoscapeProgressMonitor pm = 
    //new CytoscapeProgressMonitor(apspCalculator,Cytoscape.getDesktop());
    //pm.startMonitor(true);
    //int [][] distances = apspCalculator.getDistances();
    
    if(distances == null){
      System.out.println("Calculated apsp distances are null.");
    }
    
    long secs = (System.currentTimeMillis() - startTime)/1000;
    System.err.println("...done calculating APSP, time = " + secs + " secs");
    return distances;
  }//calculateAPSP

  /**
   * Calculates the Manhattan distances between all rows of the given
   * 2D array.
   *
   * @param matrix the 2D array of integers for which Manhattan distances will
   * be calculated
   * @return a 2D double array containing the distances, or null if there was an error
   */
  protected static double[][] calculateManhattanDists (int [][] matrix){
    long startTime = System.currentTimeMillis();
    System.err.println("Calculating manhattan-distances...");
    if(matrix == null){
      return null;
    }
    VectorCorrelationCalculator corrCalculator = 
      new VectorCorrelationCalculator(matrix,
                                      VectorCorrelationCalculator.MANHATTAN,
                                      false);
    double [][] manDistances = corrCalculator.calculate();
    long secs = (System.currentTimeMillis() - startTime)/1000;
    System.err.println("...done calculating manhattan-distances, time = " + secs + " secs");
    return manDistances;
  }//calculateManhattanDists

  /**
   * Hierarchicaly clusters nodes in the given network using the given
   * 2D double array as the starting distances between nodes.
   *
   * @param network the <code>CyNetwork</code> with the nodes that will
   * be clustered
   * @param node_list an array of <code>CyNode</code> objects that are ordered so that
   * indices of rows in <code>correlations</code> correspond to indices in this list
   * @param correlations the correlations that will be used to start the
   * hierarchical clustering
   * @return the <code>HierarchicalClustering</code> object that was used
   * for clustering and that contains the final hierarchical tree.
   */
  protected static HierarchicalClustering cluster (CyNetwork network,
                                                   ArrayList node_list,
                                                   double [][] correlations){
    long startTime = System.currentTimeMillis();
    System.err.println("Clustering...");
    
    CyNode [] nodes = (CyNode[])node_list.toArray(new CyNode[node_list.size()]);
    
    HierarchicalClustering hClustering = 
      new HierarchicalClustering(
                                 nodes,
                                 correlations,
                                 VectorCorrelationCalculator.DISTANCE_METRIC,
                                 HierarchicalClustering.ROWS,
                                 EisenClustering.AVERAGE_LINK
                                 );
    // Don't reorder leaves after clustering, since we are not interested on this
    // ordering
    hClustering.setLeafReorderingEnabled(false);
    RGAlgorithmData data = getClientData(network);
    // The conditions don't change, so just create them once
    EisenClustering.JoiningCondition jCond = data.getJoiningCondition();
    EisenClustering.ClusterCondition cCond = data.getClusterCondition();
    if(jCond == null){
      jCond = createJoiningCondition(network);
      data.setJoiningCondition(jCond);
    }
    if(cCond == null){
      cCond = createClusterCondition(network);
      data.setClusterCondition(cCond);
    }
    hClustering.setRowJoiningCondition(jCond);
    hClustering.setRowClusterCondition(cCond);
    // Cluster:
    //hClustering.clusterAndMonitor(Cytoscape.getDesktop());
    hClustering.cluster();
    long secs = (System.currentTimeMillis() - startTime)/1000;
    System.err.println("...done clustering, time = " + secs + " secs");
    return hClustering;
  }//cluster

  /**
   * Creates and returns a joining condition that specifies that only adjacent
   * nodes in the network can be joined while hierarchically clustering it.
   *
   * @param network the <code>CyNetwork</code> for which this condition is being created
   * @return a <code>EisenClustering.JoiningCondition</code>
   */
  protected static EisenClustering.JoiningCondition createJoiningCondition (CyNetwork network){
    final CyNetwork finalNet = network;
    return new HierarchicalClustering.JoiningCondition(){
        
        public boolean join ( HierarchicalClustering.EisenClusterNode node1, 
                              HierarchicalClustering.EisenClusterNode node2){
          
          if(node1.getChildCount() == 2){
            Object obj1 = node2.joinConditionMap.get(node1.getChildAt(0));
            Object obj2 = node2.joinConditionMap.get(node1.getChildAt(1)); 
            
            if(obj1 != null &&
               obj2 != null){
              // Node2 has entries to node1's children
              //System.out.println("Node2 has entries to node1's children");
              boolean b1 = ((Boolean)obj1).booleanValue();
              boolean b2 = ((Boolean)obj2).booleanValue();
              return b1 || b2;
            }
          }
          
          if(node2.getChildCount() == 2){
            Object obj1 = node1.joinConditionMap.get(node2.getChildAt(0));
            Object obj2 = node1.joinConditionMap.get(node2.getChildAt(1));
            if(obj1 != null &&
               obj2 != null ){
              // Node1 has entries to node2's children
              //System.out.println("Node1 has entries to node2's children");
              boolean b1 = ((Boolean)obj1).booleanValue();
              boolean b2 = ((Boolean)obj2).booleanValue(); 
              return b1 || b2;
            }
          }
          
          // true if a member in node1 is adjacent to a member in node2 in the graph
          Enumeration node1_leaves = node1.depthFirstEnumeration();
          Enumeration node2_leaves;
          HierarchicalClustering.EisenClusterNode node1_leaf;
          HierarchicalClustering.EisenClusterNode node2_leaf;
          CyNode graph_node1;
          CyNode graph_node2;
          while( node1_leaves.hasMoreElements() ) {
            node1_leaf = 
              ( HierarchicalClustering.EisenClusterNode )node1_leaves.nextElement();
            if( !node1_leaf.isLeaf() ||
                !(node1_leaf.getUserObject() instanceof CyNode) ){
              continue;
            }
            graph_node1 = (CyNode)node1_leaf.getUserObject();
            node2_leaves = node2.depthFirstEnumeration();
            while( node2_leaves.hasMoreElements() ) {
              node2_leaf = 
                ( HierarchicalClustering.EisenClusterNode )node2_leaves.nextElement();
              if( !node2_leaf.isLeaf() ||
                  !(node2_leaf.getUserObject() instanceof CyNode) ){
                continue;
              }
              graph_node2 = (CyNode)node2_leaf.getUserObject();
              
              // TODO: REMOVE
              //System.err.println( "checking to see if there's an edge between " 
              //+ graph_node1 + " and " 
              //+ graph_node2 + ".." );
              
              List nodeList = new ArrayList();
              nodeList.add(graph_node1);
              nodeList.add(graph_node2);
              List connectingEdges = finalNet.getConnectingEdges(nodeList); 
              if( connectingEdges != null && connectingEdges.size() > 0) {
                // TODO: REMOVE
                //System.err.println( ".yes!" );
                return true;
              }
              // TODO: REMOVE
              //System.err.println( ".nope." );
            }
          }
          return false;
        } // join
        
	    };
    
  }//createJoiningCondition
  
  /**
   * Creates and returns a cluster condition that specifies that only subtrees in the
   * hierarchical-tree that contain in their leaves at least 
   * <code>RGAlgorithmData.getMinNumMembers()</code> nodes that represent
   * proteins can be considered as 'clusters' (biomodules in this case). If the nodes
   * do not have molecule type information attached to them, then the condition requires
   * at least <code>RGAlgorithmData.getMinNumMembers()</code> nodes, no matter what their 
   * molecule type is.
   *
   * @param network the <code>CyNetwork</code> for which the condition will be created
   * @return a <code>EisenClusterNode.ClusterCondition</code>
   */
  // TODO: Need an elegant, non-hacky way of knowing if there are proteins in the network
  // which gets us into semantics, the core does not provide a mechanism for this.
  protected static EisenClustering.ClusterCondition createClusterCondition (CyNetwork network){
    
    
    // NEED A WAY OF KNOWING WHAT MOLECULE TYPE THE NODES REPRESENT!!!
    
    //int numProts = 
    //MoleculeTypeNodeAttribute.getNumMTypeNodes(this.biomodulesManager.getGraph(),
    //                                         getNodeAttributes(),
    //                                         "protein");
    
    //if(numProts > 0){
    // if there is at least one node that has a moleculeType attribute equal to protein, 
    // then count proteins for clusters
    //  this.minProtsCCondition = 
    //  new EisenClustering.ClusterCondition(){
    //    public boolean isCluster(EisenClustering.EisenClusterNode ecNode){
    //      return isBiomodule(ecNode,getMinNumProts(),getNodeAttributes());
    //    }//isCluster
    //  };
    //}else{
    
    // FOR NOW, RETURN THIS:
    
    final RGAlgorithmData data = getClientData(network);
    return new EisenClustering.ClusterCondition(){
        
        public boolean isCluster(EisenClustering.EisenClusterNode ecNode){
          return meetsBiomoduleCondition(data,ecNode);
        }// isCluster
      };
    //}
    
  }//createClusterCondition
  
  /**
   * Creates and returns the biomodules for the <code>CyNetwork</code> contained 
   * in the given <code>RGAlgorithmData</code> object, which must have a 
   * <code>HierarchicalClustering</code> that contains a calculated hierarchical-tree.
   *
   * @return a 2D array of <code>CyNodes</code>, where each row represents
   * a biomodule, and each cell in that row contains a node in the biomodule, or null
   * if there was an error (for example, the hierarchical-tree has not been calculated)
   */
  public static CyNode [][] createBiomodules (RGAlgorithmData data){
    
    long startTime = System.currentTimeMillis();
    System.err.println("Creating biomodules...");
    
    HierarchicalClustering hClustering = data.getHierarchicalClustering();
    if(hClustering == null){
      return null;
    }
    // Get the root of the hierarchical-tree
    HierarchicalClustering.HierarchicalClusterNode treeRoot =
      (HierarchicalClustering.HierarchicalClusterNode)hClustering.getRowRoot();
    if(treeRoot == null){
      // This is probably because the clustering has not taken place
      System.err.println("treeRoot is null, createBiomodules() returning null");
      return null;
    }
    // Need to set a join at which to 'cut' the hierarchical-tree
    int joinNumber = data.getCutJoinNumber();
    if(joinNumber < 0){
      // It has not been set
      joinNumber = hClustering.getJoinNumberWithMaxNumClusters();
      data.setCutJoinNumber(joinNumber);
    }
    // Get the roots of the subtrees that result of cuting the hierarchical-tree at
    // joinNumber
    Set roots = new HashSet();
    HierarchicalClustering.clustersCreatedBeforeNode(treeRoot, joinNumber, roots);
    ClusterNode [] subtreeRoots = (ClusterNode[])roots.toArray(new ClusterNode[roots.size()]);
    // Make a biomodule for each subtree
    ArrayList allBiomodules = new ArrayList();
    int numBiomods = 0;
    for(int i = 0; i < subtreeRoots.length; i++){
      if(!meetsBiomoduleCondition(data,(EisenClustering.EisenClusterNode)subtreeRoots[i])){
        continue;
      }
      Iterator leafIterator = subtreeRoots[i].leafIterator();
      ArrayList biomodule = new ArrayList();
      while(leafIterator.hasNext()){
        CyNode node = (CyNode)(((ClusterNode)leafIterator.next()).getUserObject());
        biomodule.add(node);
      }//while leafIterator
      numBiomods++;
      allBiomodules.add((CyNode[])biomodule.toArray(new CyNode[biomodule.size()]));
    }//for i
  
    CyNode[][] biomodules = 
      (CyNode[][])allBiomodules.toArray(new CyNode[allBiomodules.size()][]);
    
    long secs = (System.currentTimeMillis() - startTime)/1000;
    //--- test----
    for(int i = 0; i < biomodules.length; i++){
      System.err.println("-------- Biomodule " + (i+1) + " -------------");
      for(int j = 0; j < biomodules[i].length; j++){
        System.err.print(biomodules[i][j].getIdentifier() + " ");
      }//for j
      System.err.println();
    }//for i
    //-----------
    System.err.println("...done creating biomodules, time = " + secs + " secs");
    return biomodules;
  }//createBiomodules

  /**
   * The condition to be met in order to be considered a Biomodule is to have at least
   * <code>RGAlgorithmData.getMinNumMembers()</code> proteins (if molecule type information
   * is available) or members (if no molecule type information is available).
   *
   * @param data the <code>RGAlgorithmData</code> that holds the network and its attached data
   * @param tree_node a node that in the hierarchical-tree that is contained in 
   * <code>HierarchicalClustering</code>
   * @return true if the given <code>EisenClustering.EisenClusterNode</code> is the root
   * of a hierarchical-clustering subtree whose leaves meet biomodule conditions, false
   * otherwise
   */
  // TODO: Molecule type
  protected static boolean meetsBiomoduleCondition (RGAlgorithmData data,
                                                EisenClustering.EisenClusterNode tree_node){
    
    //NOTE: at this point, data does not have a HierarchicalClustering
    
    int minNumMembers = data.getMinNumMembers();
    if(tree_node.getLeafCount() < minNumMembers){
      return false;
    }
    return true;
    // If a child of this node meets the condition, that means that
    // the node itself meets the condition
    //int numChildren = tree_node.getChildCount();
    //for(int child = 0; child < numChildren; child++){
    //EisenClustering.EisenClusterNode childNode = 
    //  (EisenClustering.EisenClusterNode)tree_node.getChildAt(child);
    //if(childNode.isCluster){
    //  return true;
    //}
    //}//for child

    // None of the children pass the condition.
    //Iterator leafIterator = tree_node.leafIterator();
    //int numProts = 0;
    //while(leafIterator.hasNext()){
    //String leaf = (String)(((ClusterNode)leafIterator.next()).getUserObject());
    //CyNode graph_node = (CyNode)Cytoscape.getCyNode(leaf);
    //if(graph_node == null){
    //  throw new IllegalStateException("The CyNode object that corresponds to [" +
    //                                  leaf + "] is null.");
    //}
      
    // NEED NON-HACKY WAY OF DOING THIS:
    //------------------------------------------------------------
    // moleculeType = (String)nodeAtt.getValue("moleculeType",
    //                                      leaf);
    //if(moleculeType != null && moleculeType.equals("protein")){
    //numProts++;
    //}
    //------------------------------------------------------------
    //if(numProts >= minNumProts){
    //  return true;
    //}
    //}//while leafIterator
    
    // The number of proteins is less than minNumProteins
    //return false;
  }//meetsBiomoduleCondition
                                                
}//class RGAlgorithm
