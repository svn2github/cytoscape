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

package common.algorithms.hierarchicalClustering;

import giny.util.SwingWorker;
import giny.util.MonitorableTask;
import java.util.*;
import java.io.*;

/**
 * @author Iliana Avila-Campillo iavila@systemsbiology.org
 * @author Paul Edlefsen pedlefsen@systemsbiology.org
 * @version %I%, %G%
 */

public class HierarchicalClustering
  extends EisenClustering {

  /**
   * The maximum number of candidate joins for each node that
   * will be stored in memory (storing all takes up too much memory). 
   * These joins have the highest similarities,
   * or smallest distances.
   */
  protected static int MAX_BEST_CORRELATIONS = 5;
  
  /**
   * A priority queue used to obtain the pair of nodes with the highest
   * similarity value, or the lowest distance value.
   */
  protected transient SortedSet rowCorrelationQueue;
  protected transient SortedSet colCorrelationQueue;

  /**
   * The indeces of the next internal node. Must be unique.
   */
  protected int rowNextInternalNodeIndex;
  protected int colNextInternalNodeIndex;

  /**
   * The number of non-leaf nodes for the two different dimensions.
   */
  protected int numInternalNodesRows = -1;
  protected int numInternalNodesCols = -1;

  // So that NodePairs in the queue are in ascending order with respect
  // to their correlations (smallest value first, largest value last)
  protected Comparator correlationComparator =
    new AscendingNodePairCorrelationComparator();

  // A GUI that displays the Hierarchical Tree
  transient protected HCAnimator hcAnimator;
  // Whether or not to show the hcAnimator while the clustering is happening
  protected boolean showAnimator = false;
  // Whether to instantiate the hcAnimator at all (when in GINY mode set to false)
  // false by default since we are in cvsdir5
  protected boolean createAnimator = false;

  /**
   * Constructor, use when clustering in one dimension only, or in both dimensions
   * with the same objects.
   *
   * @param input_matrix the 2D array that contains the vectors to be clustered
   * @param dimension_objects the objects that will be clustered and that will
   * be assigned to the leaves of the hierachical tree
   * @param correlation_metric the correlation metric to use for clusteting
   * @param cluster_dimensions the dimensions to be clustered (ROWS, COLS, or
   * ROWS_COLS_AS_ROWS)
   * @param use_centroid_method whether or not a centroid method must be used
   * @param link_method one of AVERAGE_LINK, SINGLE_LINK, or COMPLETE_LINK  
   */
  public HierarchicalClustering (
    double[][] input_matrix,
    Object[] dimension_objects,
    int correlation_metric,
    int cluster_dimensions,
    boolean use_centroid_method,
    int link_method
  ) {
    this(
      input_matrix,
      null,
      null,
      ( isRowDimensions( cluster_dimensions ) ?
        dimension_objects :
        null
      ),
      ( isColumnDimensions( cluster_dimensions ) ?
        dimension_objects :
        null
      ),
      correlation_metric,
      cluster_dimensions,
      use_centroid_method,
      link_method
    );
  } // <init>( double[][], Object[], int, int )

  /**
   * Constructor, use if clustering in both dimensions and if weights are needed.
   * 
   * @param input_matrix the 2D array that contains the vectors to be clustered
   * @param row_weights the weights for the rows dimension
   * @param col_weights the weights for the columns dimension
   * @param row_objects the objects that will be clustered and attached to the 
   * hierarchical clustering tree for rows
   * @param col_objects the objects that will be clustered and attached to the 
   * hierarchical clustering tree for cols
   * @param correlation_metric the correlation metric to use for clusteting
   * @param cluster_dimensions the dimensions to be clustered (ROWS, COLS, ROWS_COLS or
   * ROWS_COLS_AS_ROWS)
   * @param use_centroid_method whether or not a centroid method must be used
   * @param link_method one of AVERAGE_LINK, SINGLE_LINK, or COMPLETE_LINK  
   */
  public HierarchicalClustering (
    double[][] input_matrix,
    double[] row_weights,
    double[] col_weights,
    Object[] row_objects,
    Object[] col_objects,
    int correlation_metric,
    int cluster_dimensions,
    boolean use_centroid_method,
    int link_method
  ) {
    super(
      input_matrix,
      row_weights,
      col_weights,
      row_objects,
      col_objects,
      correlation_metric,
      cluster_dimensions,
      use_centroid_method,
      link_method
    );
    taskName = "Building Hierarchical Tree";
  } // <init>( double[][], double[], double[], Object[], Object[], int, int )

  /**
   * Constructor, use if a double [][] matrix of pre-calculated correlations
   * is available, if not available use another constructor (they will be calculated during the 
   * clustering)
   *
   * @param dimension_objects the objects that will be clustered and that will
   * be assigned to the leaves of the hierachical tree
   * @param correlations the pre-calculated correlations
   * @param metric_category either VectorCorrelationCalculator.DISTANCE_METRIC or
   * VectorCorrelationCalculator.SIMILARITY_METRIC
   * @param cluster_dimensions the dimensions to be clustered (ROWS, COLS, or
   * ROWS_COLS_AS_ROWS)
   * @param link_method one of AVERAGE_LINK, SINGLE_LINK, or COMPLETE_LINK
   *
   */
  public HierarchicalClustering (
    Object[] dimension_objects,
    double[][] correlations,
    int metric_category,
    int cluster_dimensions,
    int link_method
  ) {
    this(
      ( isRowDimensions( cluster_dimensions ) ?
        dimension_objects :
        null
      ),
      ( isColumnDimensions( cluster_dimensions ) ?
        dimension_objects :
        null
      ),
      ( isRowDimensions( cluster_dimensions ) ?
        correlations :
        null
      ),
      ( isColumnDimensions( cluster_dimensions ) ?
        correlations :
        null
      ),
      metric_category,
      cluster_dimensions,
      link_method
    );
  } // <init>( Object[], double[][], int )

  /**
   * Constructor, use if clustering in both dimensions and if pre-calculated
   * correlations are available for both rows and columns.
   *
   * @param row_objects the objects that will be clustered and that will be attached
   * to the leaves of the row hierarchical tree
   * @param col_objects the objects that will be clustered and that will be attached
   * to the leaves of the column hierarchical tree
   * @param row_correlations the pre-calculated correlations for the rows
   * @param col_correlations the pre-calculated correlations for the columns
   * @param metric_category either VectorCorrelationCalculator.DISTANCE_METRIC or
   * VectorCorrelationCalculator.SIMILARITY_METRIC
   * @param cluster_dimensions the dimensions to be clustered (ROWS, COLS, or
   * ROWS_COLS_AS_ROWS)
   * @param link_method one of AVERAGE_LINK, SINGLE_LINK, or COMPLETE_LINK
   */
  public HierarchicalClustering (
                                 Object[] row_objects,
                                 Object[] col_objects,
                                 double[][] row_correlations,
                                 double[][] col_correlations,
                                 int metric_category,
                                 int cluster_dimensions,
                                 int link_method
                                 ) {
    super(
          row_objects,
          col_objects,
          row_correlations,
          col_correlations,
          metric_category,
          cluster_dimensions,
          link_method
          );
    taskName = "Building Hierarchical Tree";
  } // <init>( Object[], Object[], double[][], double[][], int, int )
  
  /**
   * Constuctor, use if the clustering targets are in a file.
   *
   * @param input_file_path the full name of the file
   * @param correlation_metric the correlation metric to use for clusteting
   * @param cluster_dimensions the dimensions to cluster (one of ROWS_COLS, ROWS, COLS, 
   * ROWS_COLS_AS_ROWS)
   * @param use_centroid_method whether or not the centroid method should be used
   */
  public HierarchicalClustering (
    String input_file_path,
    int correlation_metric,
    int cluster_dimensions,
    boolean use_centroid_method,
    int link_method
    ) {
    super( 
          input_file_path, 
          correlation_metric, 
          cluster_dimensions,
          use_centroid_method,
          link_method
          );
    taskName = "Building Hierarchical Tree";
  } // <init>( String, int, int )

  /**
   * Perform hierarchical clustering in the given dimension.
   */
  protected void cluster ( int dimension ) {

    // TODO: REMOVE
    //System.err.println( "The inputMatrix is " + ArrayUtilities.toString( inputMatrix ) + "." );
    numInternalNodesRows = 0;
    numInternalNodesCols = 0;

    int count;
    this.hcAnimator = null;
    if( dimension == ROWS ) {
      count = rowCount;
      if(this.createAnimator){
        this.hcAnimator = new HCAnimator (this.rowEisenClusterLeaves);
      }
    } else if( dimension == COLS ) {
      if(this.createAnimator){
        this.hcAnimator = new HCAnimator (this.colEisenClusterLeaves);
      }
      count = colCount;
    } else {
      throw new IllegalArgumentException( "The dimension argument must be either ROWS or COLS.  "+
                                          "The given value, " 
                                          + dimension + ", is not valid." );
    }
    
    if(this.hcAnimator != null && this.showAnimator){
      this.hcAnimator.setVisible(true);
      this.hcAnimator.drawHCTree(true);
    }
   
    // we will measure our progress by the number of joins accomplished, 
    // plus initialization of the queue
    currentProgress = 0;
    targetProgress = count + count/2; // count - 1 is number of possible joins, 
                                      //+1 unit for initialization
    statMessage = "Completed 0%";
    calculateCorrelations( dimension );
    initializeCorrelationQueue( dimension );
    
    SortedSet queue = null;
    if( dimension == ROWS ) {
      queue = rowCorrelationQueue;
      rowNextInternalNodeIndex = 0;
      //TODO: Remove
      //System.out.println("The queue size is " + queue.size());
    } else {
      queue = colCorrelationQueue;
      colNextInternalNodeIndex = 0;
    }

    // TODO: REMOVE
    /*
    if( dimension == ROWS ) {
      System.err.print( "Clustering rows.." );
      System.err.flush();
    } else if( dimension == COLS ) {
      System.err.print( "Clustering columns.." );
      System.err.flush();
    }
    */
    
    // as long as the queue is not empty, there are nodes to join
    Iterator queue_node_iterator;
    NodePair node_pair;
    //TODO: Remove
    //System.out.println("Starting Hierarchical Clustering...");
    while( !queue.isEmpty() ) {
      if(currentMetricCategory == VectorCorrelationCalculator.SIMILARITY_METRIC){
        // Remove a NodePair with the highest similarity
        node_pair = ( NodePair )queue.last();
      }else{
        // A distance metric was used:
        // Remove a NodePair with the lowest distance
        node_pair = (NodePair)queue.first();
      }

      // Join the nodes therein & update the queue and root
      joinNodes( dimension, node_pair );
      
      if(this.canceled){
        reInitialize();
        break;
      }else{
        currentProgress += 1.0;
        double percent = (currentProgress * 100)/targetProgress;
        statMessage = "Completed " + percent  + "%";
      }
      // TODO: REMOVE
      /*
      System.err.print( "." );
      System.err.flush();
      */
    } // End while queue is not empty
    // System.out.println("...Done Hierarchical Clustering (queue is empty).");
   
    // We let the calling method, cluster(), set done to true, in case it wants
    // to do other things.

    // TODO: REMOVE
    /*
    if( dimension == ROWS ) {
      System.err.println( ".done clustering rows." );
    } else {
      System.err.println(".done clustering columns.");
    }
    */
    //if(this.showAnimator){
    if(!this.canceled){
      if(this.hcAnimator != null){
        this.hcAnimator.setVisible(true);
        this.hcAnimator.drawHCTree(true);
      }
    }
    //}
  } // cluster( int )

  /**
   * Create the correlation queue for the given dimension and add to it all
   * NodePairs that can be made by joining pairs of leaves.
   */
  protected boolean initializeCorrelationQueue ( int dimension ) {

    double[][] correlations;
    JoiningCondition joining_condition;
    double correlation_threshold;
    SortedSet queue;
    EisenClusterNode root;
    EisenClusterNode[] leaves;
    if( dimension == ROWS ) {
      joining_condition = rowJoiningCondition;
      correlation_threshold = rowCorrelationThreshold;
      correlations = rowCorrelations;
      queue = rowCorrelationQueue = createCorrelationQueue();
      root = rowRoot;
      leaves = rowEisenClusterLeaves;
    } else if( dimension == COLS ) {
      joining_condition = colJoiningCondition;
      correlation_threshold = colCorrelationThreshold;
      correlations = colCorrelations;
      queue = colCorrelationQueue = createCorrelationQueue();
      root = colRoot;
      leaves = colEisenClusterLeaves;
    } else {
      throw new IllegalArgumentException( "The dimension argument must be either ROWS or COLS.  "
                                          +"The given value, " 
                                          + dimension + ", is not valid." );
    }
   
    // initialize the treeMap using the correlations
    // TODO: REMOVE
    
    //System.err.print( "Initializing " +
    //( ( dimension == ROWS ) ? "row" : "col" ) +
    //"SimilarityQueue.." );
    //System.err.flush();
    

    // First add the leaves to the root.
    for( int i = 0; i < leaves.length; i++ ) {
      root.add( leaves[ i ] );
    }

    HierarchicalClusterNode node1;
    HierarchicalClusterNode node2;
    NodePair node_pair;
    Double correlation_object;
    SortedSet sortedCorrelations = new TreeSet( this.correlationComparator );
    double percent;
    // For all unique pairs of leaves, add a NodePair to the queue (unless the
    // joining condition is not met).
    for( int i = 0; i < correlations.length; i++ ) {
      for( int j = ( i + 1 ); j < correlations.length; j++ ) {
        
        // initially all the tree nodes contain only one object
        node1 = ( HierarchicalClusterNode )leaves[ i ];
        node2 = ( HierarchicalClusterNode )leaves[ j ];

        // TODO: REMOVE
        //System.err.println( "Considering adding pair [ " + node1 + ", " + node2 + " ]." );
        
        if(!centroid){
          // Remember the correlation for average linkage calculations.
          // Only used if not centroid.
          correlation_object = new Double( correlations[ i ][ j ] );
          node1.nodeCorrelationMap.put( node2, correlation_object );
          node2.nodeCorrelationMap.put( node1, correlation_object );
        }
        
        // Skip pair if it is not meant to be joined
        if(!joining_condition.join(node1, node2)){
          node1.joinConditionMap.put(node2, Boolean.FALSE);
          node2.joinConditionMap.put(node1, Boolean.FALSE);
          continue;
        }
        
        node1.joinConditionMap.put(node2, Boolean.TRUE);
        node2.joinConditionMap.put(node1, Boolean.TRUE);
        
        // Skip pairs of nodes that do not pass the correlation test
        if(this.currentMetricCategory == VectorCorrelationCalculator.SIMILARITY_METRIC &&
           correlations[i][j] < correlation_threshold) {
          // TODO: REMOVE
          //System.err.println( "[rejected]" );
          continue;
        }else if(this.currentMetricCategory == VectorCorrelationCalculator.DISTANCE_METRIC &&
                 correlations[i][j] > correlation_threshold){
          continue;
        }

        node_pair = createNodePair( node1, node2, correlations[ i ][ j ] );
        sortedCorrelations.add(node_pair);
      }// End for j
        
      // TODO: REMOVE
      //System.err.println( "[accepted]" );
      
      NodePair candidate;
      for(int k = 0; (k < MAX_BEST_CORRELATIONS && sortedCorrelations.size() != 0); k++){
        if(this.currentMetricCategory == VectorCorrelationCalculator.SIMILARITY_METRIC){
          candidate = (NodePair)sortedCorrelations.last();
        }else{
          candidate = (NodePair)sortedCorrelations.first();
        }
        sortedCorrelations.remove(candidate);
        node1 = ( HierarchicalClusterNode )candidate.nodeOne;
        node2 = ( HierarchicalClusterNode )candidate.nodeTwo;
        queue.add( candidate );
        node1.queueParentsSet.add( candidate );
        node2.queueParentsSet.add( candidate );
      }// For k
      
      sortedCorrelations.clear();
    
      if( (i+1) % 2 == 0){
        currentProgress += 1.0;
        percent = (currentProgress * 100)/targetProgress;
        statMessage = "Completed " + percent  + "%";
      }
    } // End for i
    
    // TODO: REMOVE
    //System.err.println( "..done." );
    return ( queue.size() > 0 );
  } // initializeCorrelationQueue( int )

  /**
   * Join the two nodes in the given NodePair and update the queue and root
   * corresponding to the given dimension.
   */
  protected void joinNodes (                            
                            int dimension,
                            NodePair node_pair
                            ) {

    // TODO: REMOVE
    //System.out.println( (rowNextInternalNodeIndex + 1) + " " + node_pair.correlation);
    
    this.joinDistances.add(new Double(node_pair.correlation));
    
    // If using the centroid method, set the inputIndex as needed
    int inputIndex;
    if(centroid){
      inputIndex = node_pair.nodeOne.inputIndex;
    }else{
      if(dimension == ROWS){
        inputIndex = rowNextInternalNodeIndex;
      }else{
        inputIndex = colNextInternalNodeIndex;
      }
    }
    
    // Join the two nodes into one
    HierarchicalClusterNode new_node =
      createInternalNode(
                         node_pair,
                         ( ( dimension == ROWS ) ?
                           rowNextInternalNodeIndex++ :
                           colNextInternalNodeIndex++ ),
                         inputIndex
                         );

    boolean redraw = false;
    double percent = (currentProgress * 100)/targetProgress;
    if(this.showAnimator && (percent % 10) == 0){
      // Redraw after every 10% is completed
      redraw = true;
    }
    
    if(this.hcAnimator != null){
      this.hcAnimator.joinNodes( new_node.toString(),
                                 node_pair.nodeOne.toString(),
                                 node_pair.nodeTwo.toString(),
                                 redraw);
    }
    
    // Get needed variables depending on the current dimension
    JoiningCondition joining_condition;
    ClusterCondition cluster_condition;
    double correlation_threshold;
    SortedSet queue;
    EisenClusterNode root;
    if( dimension == ROWS ) {
      numInternalNodesRows++;
      joining_condition = rowJoiningCondition;
      cluster_condition = rowClusterCondition;
      correlation_threshold = rowCorrelationThreshold;
      queue = rowCorrelationQueue;
      root = rowRoot;
      if(Double.isNaN(this.maxRowJValue) || 
         (this.maxRowJValue < node_pair.correlation && 
          node_pair.correlation < Double.MAX_VALUE)){
        this.maxRowJValue = node_pair.correlation;
      }
      if(Double.isNaN(this.minRowJValue) ||
         this.minRowJValue > node_pair.correlation){
        this.minRowJValue = node_pair.correlation;
      }
    } else if( dimension == COLS ) {
      numInternalNodesCols++;
      joining_condition = colJoiningCondition;
      cluster_condition = colClusterCondition;
      correlation_threshold = colCorrelationThreshold;
      queue = colCorrelationQueue;
      root = colRoot;
      if(Double.isNaN(this.maxColJValue) ||
         this.maxColJValue < node_pair.correlation){
        this.maxColJValue = node_pair.correlation;
      }
      if(Double.isNaN(this.minColJValue) ||
         this.minColJValue > node_pair.correlation){
        this.minColJValue = node_pair.correlation;
      }
    } else {
      throw new IllegalArgumentException( "The dimension argument must be either ROWS or COLS.  "+
                                          "The given value, " 
                                          + dimension + ", is not valid." );
    }
    
    // Set the isCluster member variable of the new node
    new_node.isCluster = cluster_condition.isCluster(new_node);
    // Calculate how many clusters we have right now for this join.
    HierarchicalClusterNode existing_node;
    int numClusters = 0;
    int root_size = root.getChildCount();
    for( int root_child_i = 0; root_child_i < root_size; root_child_i++ ) {
      existing_node = ( HierarchicalClusterNode )root.getChildAt( root_child_i );
      if(existing_node.isCluster){
        numClusters++;
      }
    }
    if(new_node.isCluster){
      numClusters++;
    }
    this.numClustersAtEachIt.add(new Integer(numClusters));
    
    //TODO: Remove
    //System.out.println(new_node.index + " " + numClusters);
    
    // Update min and max stats for number of clusters
    if(this.minNumClusters > numClusters){
      this.minNumClusters = numClusters;
    }
    if(this.maxNumClusters <= numClusters){
      this.maxNumClusters = numClusters;
      if(dimension == ROWS){
        this.jnMaxClusters = rowNextInternalNodeIndex - 1;
      }else{
        this.jnMaxClusters = colNextInternalNodeIndex - 1;
      }
    }
    // If using the centroid method, merge the two nodes'input vectors
    if(centroid){
      // Do the merge
      new_node.inputVals = mergeInputVectors(node_pair);
      //TODO: Remove
      //Object obj = new_node.getUserObject();
      //String name = new_node.toString();
      //if(obj != null && obj instanceof Node){
      //name = ((Node)obj).toString();
      //}
      //System.out.println("merged vector " + name + " index = " 
      //                 + new_node.index + ", inputIndex = " + new_node.inputIndex);
      //for(int i = 0; i < new_node.inputVals.length; i++){
      //if(inputMask[i]){
      //  System.out.print(new_node.inputVals[i] + " ");
      // }
      //} 
      //System.out.println();
      //}
      
      // Free memory of no longer used input vectors
      node_pair.nodeOne.inputVals = null;
      node_pair.nodeTwo.inputVals = null;
      // Update the input vectors for all other unjoined nodes
      updateInputVectors(new_node,root,node_pair.nodeOne.inputIndex);
    }//if centroid
    
    // TODO: REMOVE
    //System.err.println(" queue before removing: " + queue);

    // Remove all the node pairs in queue that contain nodeOne or nodeTwo.
    
    HashSet nodesToCalculate = new HashSet(); // The nodes for which correlations will be calculated
    nodesToCalculate.add( new_node ); // So that we calculate sims between the new node 
                                      // and all other nodes
    
    Iterator node1_parents_iterator =
      (
       ( HierarchicalClusterNode )node_pair.nodeOne
      ).queueParentsSet.iterator();
    NodePair obsolete_node_pair;
    HierarchicalClusterNode otherNode;
    while( node1_parents_iterator.hasNext() ) {
      obsolete_node_pair = ( NodePair )node1_parents_iterator.next();
      queue.remove( obsolete_node_pair );
      if(node_pair.nodeOne == obsolete_node_pair.nodeOne){
        otherNode = ( HierarchicalClusterNode )obsolete_node_pair.nodeTwo;
      }else{
        otherNode = ( HierarchicalClusterNode )obsolete_node_pair.nodeOne;
      }
      if(otherNode.queueParentsSet != null && 
         otherNode.queueParentsSet.size() == 0){
        // Need to calculate correlations between otherNode and
        // all other un-joined nodes, and keep the top MAX_BEST_CORRELATIONS.
        // This is because at all times we only keep MAX_BEXT_CORRELATIONS
        // pairs per node in the queue. This node has no more pairs in the queue,
        // so we need to calculate more.
        nodesToCalculate.add(otherNode);
        //TODO: Remove
        //System.out.println("--added to nodesToCalculate node with index = " + otherNode.index);
      }
    }//end while for node1
    
    Iterator node2_parents_iterator =
      (
       ( HierarchicalClusterNode )node_pair.nodeTwo
      ).queueParentsSet.iterator();
    while( node2_parents_iterator.hasNext() ) {
      obsolete_node_pair = ( NodePair )node2_parents_iterator.next();
      queue.remove( obsolete_node_pair );
      if(node_pair.nodeTwo == obsolete_node_pair.nodeOne){
        otherNode = ( HierarchicalClusterNode )obsolete_node_pair.nodeTwo;
      }else{
        otherNode = ( HierarchicalClusterNode )obsolete_node_pair.nodeOne;
      }
      if(otherNode.queueParentsSet != null &&
         otherNode.queueParentsSet.size() == 0){
        // Need to calculate correlations between otherNode and
        // all other nodes, and keep the top MAX_BEST_CORRELATIONS.
        // This is because at all times we only keep MAX_BEXT_CORRELATIONS
        // pairs per node in the queue. This node has no more pairs in the queue,
        // so we need to calculate more.
        nodesToCalculate.add(otherNode);
        //TODO: Remove
        //System.out.println("added to nodesToCalculate node with index = " + otherNode.index);
      }
    }

    // Release the memory for the parents sets.
    ( ( HierarchicalClusterNode )node_pair.nodeOne ).queueParentsSet = null;
    ( ( HierarchicalClusterNode )node_pair.nodeTwo ).queueParentsSet = null;

    // TODO: REMOVE
    //System.err.println("queue after removing: " + queue);

    // Add new possible pairs to the queue.
    HierarchicalClusterNode current_node;
    double correlation;
    NodePair new_node_pair;
    SortedSet sortedCorrelations = new TreeSet( this.correlationComparator );
    Iterator it = nodesToCalculate.iterator();
    HashMap alreadyCalculated = new HashMap();
    // NOTE: Remember that iterator will return new_node as well
    while(it.hasNext()){
      current_node = ( HierarchicalClusterNode ) it.next();
      // For all root children, add a new node pair to the queue that pairs that
      // node with our current_node (unless the joining condition is not met).
      for( int root_child_i = 0; root_child_i < root_size; root_child_i++ ) {
        
        existing_node = ( HierarchicalClusterNode )root.getChildAt( root_child_i );
      
        //TODO: Remove
        //Object obj2 = existing_node.getUserObject();
        //String name2 = current_node.toString();
        //if(obj2 != null && obj2 instanceof Node){
        //name2 = ((Node)obj2).toString();
        //}
        //System.out.println("Existing node is " + name2);
        //--
        
        // TODO: REMOVE
        //System.err.println( "Considering adding pair [ " + new_node + ", " + existing_node + " ]." );
        if(centroid){
          // Skip pairs that don't meet joining condition
          if(!joining_condition.join(current_node, existing_node)){
            // TODO: Remove
            //  Object obj1 = current_node.getUserObject();
            //             String name1 = current_node.toString();
            //             if(obj1 != null && obj1 instanceof Node){
            //               name1 = ((Node)obj1).toString();
            //             }
            //             System.out.println(name1 + " and " + name2 + " failed JC. Skip.");
            current_node.joinConditionMap.put(existing_node, Boolean.FALSE);
            existing_node.joinConditionMap.put(current_node, Boolean.FALSE);
            //--
            continue;
          }
          
          current_node.joinConditionMap.put(existing_node, Boolean.TRUE);
          existing_node.joinConditionMap.put(current_node, Boolean.TRUE);
          
          correlation =
            VectorCorrelationCalculator.calculateCorrelation(
                                                     current_node.inputVals,
                                                     existing_node.inputVals,
                                                     null,
                                                     this.correlationMetric,
                                                     false,
                                                     false,
                                                     this.inputMask
                                                     );
          
        }else{
          correlation =
            averageLinkageCorrelation( dimension, current_node, existing_node );
          // Remember correlation for future average linkage calculations, only useful if not centroid
          current_node.nodeCorrelationMap.put( existing_node, new Double( correlation ) );
          existing_node.nodeCorrelationMap.put( current_node, new Double( correlation ) );
          // Skip pairs that don't meet joining condition
          if(!joining_condition.join(current_node, existing_node)){
            current_node.joinConditionMap.put(existing_node, Boolean.FALSE);
            existing_node.joinConditionMap.put(current_node, Boolean.FALSE);
            continue;

          }

          current_node.joinConditionMap.put(existing_node, Boolean.TRUE);
          existing_node.joinConditionMap.put(current_node, Boolean.TRUE);
        }
                
        // Skip pairs that don't meet threshold test
        if( this.currentMetricCategory == VectorCorrelationCalculator.SIMILARITY_METRIC && 
            correlation < correlation_threshold ){
          //TODO: Remove
          //System.out.println("SIM " + current_node.index + " and " + existing_node.index + 
          // " failed th test. Skip.");
          continue;
        }else if (this.currentMetricCategory == VectorCorrelationCalculator.DISTANCE_METRIC &&
                  correlation > correlation_threshold){
          //TODO: Remove
          //System.out.println("DIST " + current_node.index + " and " + existing_node.index + 
          // " failed th test. Skip.");
          continue;
        }
        
        // This pair met conditions, create a candidate node pair
        new_node_pair = createNodePair( current_node, existing_node, correlation );
        boolean succesAdd = sortedCorrelations.add( new_node_pair );
        //TODO: Remove
        //if(succesAdd){
        //System.out.println("Added to sortedCorrelations pair " + new_node_pair);
        //}else{
        //System.out.println("ERROR: Failed to add " + new_node_pair + " to sortedCorrelations.");
        //}
      }//  End for each root child, add a new possible node pairs to the queue
        
      // TODO: REMOVE
      //System.err.println( "[accepted]" );
      
      NodePair candidate;
      //TODO: Remove
      //System.out.println("Before getting into loop, sorted set size == " 
      //+ sortedCorrelations.size());
      for (int i = 0; (i < MAX_BEST_CORRELATIONS && sortedCorrelations.size() != 0); i++){
        if(this.currentMetricCategory == VectorCorrelationCalculator.SIMILARITY_METRIC){
          candidate = ( NodePair )sortedCorrelations.last();
        }else{
          candidate = ( NodePair )sortedCorrelations.first();
        }
        sortedCorrelations.remove(candidate);
        current_node = ( HierarchicalClusterNode )candidate.nodeOne;
        existing_node = ( HierarchicalClusterNode )candidate.nodeTwo;
        boolean successAdd = queue.add( candidate );
        //TODO: Remove
        // if(successAdd){
        //System.out.println("Added to the queue pair " + candidate);
        //}else{
        //System.out.println("Failed to add the pair " + candidate + " to de queue");
        //}
        current_node.queueParentsSet.add( candidate );
        existing_node.queueParentsSet.add( candidate );
      }// For i
      
      sortedCorrelations.clear();
    }// while it.hasNext()
    
    // Release the memory for the node correlation maps.
    ( ( HierarchicalClusterNode )node_pair.nodeOne ).nodeCorrelationMap = null;
    ( ( HierarchicalClusterNode )node_pair.nodeTwo ).nodeCorrelationMap = null;
    // Release the memory for the join condition maps
    node_pair.nodeOne.joinConditionMap = null;
    node_pair.nodeTwo.joinConditionMap = null;
    // Help out that garbage collector.
    node_pair.nodeOne = null;
    node_pair.nodeTwo = null;
    
    // Now, finally, add the new node to the root.
    root.add( new_node );

  } // joinNodes(..)

  /**
   * Merges the input vectors of the nodes in the given NodePair.
   * Uses user defined method for joining: AVERAGE_LINK, SINGLE_LINK,
   * or COMPLETE_LINK. Returns merged vector.
   */
  protected double [] mergeInputVectors (NodePair nodePair){
 
    HierarchicalClusterNode n1 = (HierarchicalClusterNode)nodePair.nodeOne;
    HierarchicalClusterNode n2 = (HierarchicalClusterNode)nodePair.nodeTwo;
    double [] v1 = n1.inputVals;
    double [] v2 = n2.inputVals;
    
    double [] vector = new double[v1.length];
    
    if(clusterDimensions == ROWS_COLS_AS_ROWS){
      
      // Clustering symmetrical dimensions
      // Remember indeces that are no longer valid
      // The index of the new merged vector is n1.inputIndex
      vector[n1.inputIndex] = inputMatrix[n1.inputIndex][n1.inputIndex]; //an object to itself
      inputMask[n2.inputIndex] = false;
      inputMask[n1.inputIndex] = false; // temporary, so that vector[n1.inputIndex] does not get overwritten in next loops
    
      if(linkMethod == AVERAGE_LINK){
        for(int i = 0; i < inputMask.length; i++){
          if(inputMask[i]){
            // So that infinity stays infinity
            if(v1[i] == Double.MAX_VALUE && v2[i] != Double.MAX_VALUE){
              vector[i] = Double.MAX_VALUE;
            }else if(v1[i] != Double.MAX_VALUE && v2[i] == Double.MAX_VALUE){
              vector[i] = Double.MAX_VALUE;
            }else{
              vector[i] = (v1[i] + v2[i])/2;
            }
          }
        }
      }else if(linkMethod == SINGLE_LINK){
        for(int i = 0; i < inputMask.length; i++){
          if(inputMask[i]){
            vector[i] = Math.min(v1[i], v2[i]);
          }
        }
      }else if(linkMethod == COMPLETE_LINK){
        for(int i = 0; i < inputMask.length; i++){
          if(inputMask[i]){
            vector[i] = Math.max(v1[i], v2[i]);
          }
        }
      }
      // Reset to true
      inputMask[n1.inputIndex] = true;
    }else{
      // Not clustering symmetrical dimensions
       if(linkMethod == AVERAGE_LINK){
        for(int i = 0; i < inputMask.length; i++){
          vector[i] = (v1[i] + v2[i])/2;
        }
      }else if(linkMethod == SINGLE_LINK){
        for(int i = 0; i < inputMask.length; i++){
          vector[i] = Math.min(v1[i], v2[i]);
        }
      }else if(linkMethod == COMPLETE_LINK){
        for(int i = 0; i < inputMask.length; i++){
          vector[i] = Math.max(v1[i], v2[i]);
        }
      }
    }
    
    return vector;
    
  }//mergeInputVectors

  /**
   * @param newNode the new node with respect to which the input vectors will be updated
   * @param root the root of the HierarchicalClustering tree that contains the nodes that will
   * be updated
   * @param newVectorIndex the index of the newNode's inputVector, usually the inputIndex of nodeOne
   * in the NodePair that was joined to get newNode. (See method mergeInputVectors).
   */
  protected void updateInputVectors (HierarchicalClusterNode newNode, 
                                     EisenClusterNode root,
                                     int newVectorIndex){
    // Input vectors for all nodes only need to be updated if the clustering is symmetrical
    // in both dimensions (ROWS and COLUMNS).
    if(this.clusterDimensions != ROWS_COLS_AS_ROWS){return;}
    // Iterate over un-joined nodes, and update their input vectors
    int root_size = root.getChildCount();
    HierarchicalClusterNode existing_node;
    for( int root_child_i = 0; root_child_i < root_size; root_child_i++ ) {
      existing_node = ( HierarchicalClusterNode )root.getChildAt( root_child_i );
      existing_node.inputVals[newVectorIndex] = newNode.inputVals[existing_node.inputIndex];
      // TODO: Remove
      // Object obj = existing_node.getUserObject();
//       String name = existing_node.toString();
//       if(obj != null && obj instanceof Node){
//         name = ((Node)obj).toString();
//       }
      //System.out.println("updated vector node "+ name + " index " + existing_node.index 
      //+ " input index " + existing_node.inputIndex);
      //for(int i = 0; i < existing_node.inputVals.length;i++){
      //if(inputMask[i]){
      //  System.out.print(existing_node.inputVals[i]+" ");
      //}
      // }
      //System.out.println();
      //--
    }//for root_child
  }//updateInputVectors
      
  /**
   * non-centroid averageLinkageCorrelation.
   * @return the average-linkage correlation between the given nodes in the
   * given dimension.
   */
  protected double averageLinkageCorrelation (
                                             int dimension,
                                             HierarchicalClusterNode new_node,
                                             HierarchicalClusterNode existing_node
                                             ) {
    // We can use existing knowledge to calculate this efficiently.  We've got
    // the new node's childrens' correlations to the existing node calculated
    // already.  The mean we're after, m = ( n / d ), can be calculated using
    // the old numerators and denomenators.
    // Remembering that the denomenator is the total number of pairwise
    // correlations, so that
    // d = ( new_node.leafCount * existing_node.leafCount ),
    // the final formula will be
    // m = ( ( child1_numerator + child2_numerator ) / d ).
    HierarchicalClusterNode new_node_child_1 =
      ( HierarchicalClusterNode )new_node.getChildAt( 0 );
    double new_node_child_1_numerator =
      ( ( ( Double )new_node_child_1.nodeCorrelationMap.get( existing_node )
        ).doubleValue() *
        ( new_node_child_1.leafCount *
          existing_node.leafCount
        )
      );
    HierarchicalClusterNode new_node_child_2 =
      ( HierarchicalClusterNode )new_node.getChildAt( 1 );
    double new_node_child_2_numerator =
      ( ( ( Double )new_node_child_2.nodeCorrelationMap.get( existing_node )
        ).doubleValue() *
        ( new_node_child_2.leafCount *
          existing_node.leafCount
        )
      );
    return 
      ( ( new_node_child_1_numerator + new_node_child_2_numerator ) /
        ( new_node.leafCount * existing_node.leafCount )
      );
  } // averageLinkageCorrelation(..)

  /**
   * Factory method to instantiate the SortedSet for storing NodePairs,
   * sorted on their correlation values.
   */
  protected SortedSet createCorrelationQueue () {
    return new TreeSet( correlationComparator );
  } // createCorrelationQueue()

  /**
   * Factory method for instantiating the (Object)nodePeer for an internal node
   * with the given index.  The default implementation returns the String
   * &quot;NODE&quot; + ( index + 1 ) + &quot;X&quot;, to match the results of
   * the cluster and XCluster programs.
   */
  protected Object createInternalNodePeer ( int index ) {
    //return ( "NODE" + ( index + 1 ) + "X" );
    return ("NODE" + (index) + "X");
  } // createInternalNodePeer( int )

  /**
   * Factory method to instantiate an internal node with the given children and
   * index.
   */
  protected HierarchicalClusterNode createInternalNode (
                                                        NodePair node_pair,
                                                        int index,
                                                        int inputIndex
                                                        ) {
    HierarchicalClusterNode new_node = ( HierarchicalClusterNode )
      createClusterNode(
        createInternalNodePeer( index ),
        index,
        inputIndex,
        node_pair.correlation
      );
    // NOTE: This implicitly does: node_pair.nodeOne.getParent().remove( node_pair.nodeOne ), 
    // and also for nodeTwo.
    new_node.add( node_pair.nodeOne );
    new_node.add( node_pair.nodeTwo );
    new_node.leafCount =
      ( ( ( HierarchicalClusterNode )node_pair.nodeOne ).leafCount +
        ( ( HierarchicalClusterNode )node_pair.nodeTwo ).leafCount );
    return new_node;
  } // createInternalNode(..)
  
  protected EisenClusterNode createClusterNode (
                                                Object node_peer,
                                                int index,
                                                int inputIndex,
                                                double correlation
                                                ) {
    if( node_peer == null ) {
      node_peer = String.valueOf( index );
    }
    return new HierarchicalClusterNode( node_peer, index, inputIndex, correlation );
  }
  
  // overrides EisenClustering
  protected EisenClusterNode createClusterNode (
                                                Object node_peer,
                                                int index,
                                                int inputIndex
                                                ) {
    if( node_peer == null ) {
      node_peer = String.valueOf( index );
    }
    return new HierarchicalClusterNode( node_peer, index, inputIndex, Double.NaN );
  }
  
  // overrides EisenClustering
  protected EisenClusterNode createClusterLeaf (
                                                Object node_peer,
                                                int index,
                                                int inputIndex
                                                ) {
    if( node_peer == null ) {
      node_peer = String.valueOf( index );
    }
    return new HierarchicalClusterNode( node_peer, index, inputIndex );
  }

  /**
   * Given the index of a <code>HierarchicalClusterNode</code>, 
   * it returns a set of <code>HierarchicalClusterNode</code>s
   * that were created before that node, including the node itself.
   * In order to get the set of nodes, it perfomes a DFS starting at the given root.
   *
   * @param root the <code>HierarchicalClusterNode</code> that is the root of the Hierarchical tree
   * @param node_index the index of the node
   * @param cluster_set the <code>Set</code> where the nodes that were created before the node with
   * index node_index will be stored (inclusing the node with index node_index)
   */
  public static void clustersCreatedBeforeNode ( HierarchicalClusterNode root,
                                                 int node_index,
                                                 Set cluster_set){
    if(cluster_set == null){
      cluster_set = new HashSet();
    }
    if(root == null){return;}
    if(root.index != -1 && root.index <= node_index){
      //TODO: Remove
      //System.out.println("clustersCreatedBeforeNode, adding node with index = " + root.index);
      //System.out.println(" uid = " + root.toString());
      cluster_set.add(root);
      return; // terminate search on this subtree
    }
    int numChildren = root.getChildCount();// root may have > 2 children (joining condition)
    for(int i = 0; i < numChildren; i++){
      clustersCreatedBeforeNode((HierarchicalClusterNode)root.getChildAt(i), node_index, cluster_set);
    }
    
  }//joinSet

  /**
   * Returns the correlation between the children of a <code>HierarchicalClusterNode</code>
   * with the given node index that is in the calculated hierarchical tree for the given dimension.
   */
  public double getChildrensSim (int node_index, int dim){
    EisenClusterNode root = null;
    if(dim == ROWS){
      root = getRowRoot();    
    }else if(dim == COLS){
      root = getColumnRoot();
    }
    EisenClusterNode hcNode = null;
    hcNode = getNodeWithIndex(node_index,root);
    double dist = hcNode.getDistanceBetweenChildren();
    return dist;
  }//getNodeDistance

  /**
   * @return the <code>HCAnimator</code> that displays the Hierarchical-Tree that was last
   * computed, or null if <code>this.createAnimator</code> is false or if the tree has not yet been
   * calculated
   */
  public HCAnimator getHCAnimator (){
    return this.hcAnimator;
  }//getHCAnimator

  /**
   * Returns the number of internal nodes after clustering for the given dimension.
   * If the dimension is not valid, or if no clustering for that dimension has been done, 
   * it returns -1.
   */
  public int getNumInternalNodes (int dim){
    if(dim == ROWS){
      return this.numInternalNodesRows;
    }
    if(dim == COLS){
      return this.numInternalNodesCols;
    }
    return -1; // unknown dimension
  }//getNumInternalNodes
  
  // --------- internal class HierarchicalClusterNode ------- //
  public class HierarchicalClusterNode
    extends EisenClusterNode {
    
    /**
     * The Set of all NodePairs currently in the relevant queue that contain
     * this node.  Null if this node is not an immediate child of the root.
     */
    public transient Set queueParentsSet;

    /**
     * A HierarchicalClusterNode->Double Map containing a key for every other
     * node in the root-parent of this node, with the correlation of this
     * node to that node as the entry value.  Null if this node is not an
     * immediate child of the root.
     */
    public transient Map nodeCorrelationMap;

    /**
     * The correlation between the children of this node.
     */
    public double correlation;

    /**
     * The total number of leaves contained herein.  1 if this is a leaf.
     */
    public int leafCount;

    /**
     * Internal node constructor.
     */
    public HierarchicalClusterNode (
                                    Object node_peer,
                                    int index,
                                    int inputIndex,
                                    double correlation
                                    ) {
      super( node_peer, index, inputIndex );
      queueParentsSet = createQueueParentsSet();
      nodeCorrelationMap = createNodeCorrelationMap();
      this.correlation = correlation;
      leafCount = 0;
    }

    /**
     * Leaf constructor.
     */
    public HierarchicalClusterNode (
                                    Object node_peer,
                                    int index,
                                    int inputIndex
                                    ) {
      super( node_peer, index, inputIndex, true );
      queueParentsSet = createQueueParentsSet();
      nodeCorrelationMap = createNodeCorrelationMap();
      leafCount = 1;
    }

    /**
     * @exception UnsupportedOperationException (always)
     */
    // overrides EisenClusterNode
    public void setDistanceBetweenChildren ( double new_distance ) {
      throw new UnsupportedOperationException( "Unable to set the distanceBetweenChildren "
                                               +"in a HierarchicalClusterNode, "
                                               +"in which this value is computed from the "
                                               +"correlation value." );
    } // setDistanceBetweenChildren( double )
  
    // overrides EisenClusterNode
    public double getDistanceBetweenChildren () {
      //System.out.println("HierarchicalClusterNode::getDistanceBetweenChildren");
      //System.out.flush();
      if( isLeaf() ) {
        return 0.0;
      }
      if( Double.isNaN( correlation ) ) {
        return correlation;
      }
      if(currentMetricCategory == VectorCorrelationCalculator.SIMILARITY_METRIC){
        return ( 1.0 - ( ( correlation + 1.0 ) / 2.0 ) );
      }
      // we are using a distance metric
      //System.out.println("getDistanceBetweenChildren = " + correlation);
      //System.out.flush();
      return correlation;
    } // getDistanceBetweenChildren()

      // TODO REMOVE
    public String toString () {
      return super.toString();// + "@" + correlation;
    }

    /**
     * @return a new Set of ClusterNodes, each of which is a root in the forest
     * generated by setting the forest floor to the given
     * correlation_threshold (which can also be a distance threshold) value.
     */
    public  Set correlationRootSet ( double correlation_threshold ) {
      if( Double.isNaN( correlation_threshold ) ) {
        return new HashSet();
      }
      double distance_threshold;
      if(currentMetricCategory == VectorCorrelationCalculator.SIMILARITY_METRIC){
        distance_threshold =
          ( 1.0 - ( ( correlation_threshold + 1.0 ) / 2.0 ) );
      }else{
        // we are using a distance metric
        distance_threshold = correlation_threshold;
      }
      return rootSet( distance_threshold );
    } // correlationRootSet( double )
    
    public String paramStringHeader () {
      return "Name\tLeft\tRight\tCorrelation";
    } // paramStringHeader()
    
    /**
     * Return a tab-delineated String in the same order given by 
     * {@link #paramStringHeader()}.
     */
    public String paramString () {
      StringBuffer string_buffer = new StringBuffer();
      string_buffer.append( toString() );
      if( isLeaf() ) {
        return string_buffer.toString();
      }
      string_buffer.append( '\t' );
      string_buffer.append( getChildAt( 0 ).toString() );
      string_buffer.append( '\t' );
      string_buffer.append( getChildAt( 1 ).toString() );
      string_buffer.append( '\t' );
      string_buffer.append( correlation );
      return string_buffer.toString();
    } // paramString()

    protected Set createQueueParentsSet () {
      return new HashSet();
    }

    protected Map createNodeCorrelationMap () {
      return new HashMap();
    }

    /**
     * Return a tab-delineated String for use in a TreeView ATR or GTR file.
     */
    // envelops EisenClusterNode
    public String treeViewTreeString ( int dimension ) {
      if( isLeaf() ) {
        return null;
      }
      return ( super.treeViewTreeString( dimension ) + '\t' + correlation );
    } // treeViewTreeString( int )
    
  } // inner class HierarchicalClusterNode

  /**
   * Perform hierarchical clustering on the given data in a separate thread.
   * Delegates to cluster( double[][], Object[], Object[], double[], int,
   * boolean, boolean int ) with the dimension_objects value given as the
   * row_objects and/or the col_objects arguments, depending on
   * cluster_dimensions, and null weights.
   */
  public static HierarchicalClustering cluster (
                                                double[][] input_matrix,
                                                Object[] dimension_objects,
                                                int correlation_metric,
                                                boolean centered,
                                                boolean absolute,
                                                int cluster_dimensions,
                                                boolean use_centroid_method,
                                                int link_method
                                                ) {
   
    return cluster(
                   input_matrix,
                   null,
                   null,
                   ( isRowDimensions( cluster_dimensions ) ?
                     dimension_objects :
                     null
                     ),
                   ( isColumnDimensions( cluster_dimensions ) ?
                     dimension_objects :
                     null
                     ),
                   correlation_metric,
                   centered,
                   absolute,
                   cluster_dimensions,
                   use_centroid_method,
                   link_method
                   );
  } // static cluster( double[][], Object[], int, boolean, boolean, int )

  /**
   * Perform hierarchical clustering on the given data in a separate thread.
   */
  public static HierarchicalClustering cluster (
                                                double[][] input_matrix,
                                                double[] row_weights,
                                                double[] col_weights,
                                                Object[] row_objects,
                                                Object[] col_objects,
                                                int correlation_metric,
                                                boolean centered,
                                                boolean absolute,
                                                int cluster_dimensions,
                                                boolean use_centroid_method,
                                                int link_method
                                                ) {
    final HierarchicalClustering clustering =
      new HierarchicalClustering(
        input_matrix,
        row_weights,
        col_weights,
        row_objects,
        col_objects,
        correlation_metric,
        cluster_dimensions,
        use_centroid_method,
        link_method
      );

    clustering.setCenteredCorrelationsEnabled( centered );
    clustering.setAbsoluteCorrelationsEnabled( absolute );

    SwingWorker worker = new SwingWorker (){
        public Object construct(){
          return clustering.construct();
        }
      };
    worker.start();
    
    // Wait until it is done:
    worker.get();
    return clustering;
  } // static cluster( double[][], double[], Object[], Object[], int, boolean, boolean, int )

  /**
   * Perform hierarchical clustering on the given data in a separate thread.
   * Delegates to cluster( Object[], Object[], double[][], double[][], int )
   * with the dimension_objects value given as the row_objects and/or the
   * col_objects arguments and the similarites value given as the
   * row_correlations and/or col_correlations arguments, depending on the value
   * of cluster_dimensions.
   */
  public static HierarchicalClustering cluster (
                                                Object[] dimension_objects,
                                                double[][] correlations,
                                                int metric_category,
                                                int cluster_dimensions,
                                                int link_method
                                                ) {
    return cluster(
                   ( isRowDimensions( cluster_dimensions ) ?
                     dimension_objects :
                     null
                     ),
                   ( isColumnDimensions( cluster_dimensions ) ?
                     dimension_objects :
                     null
                     ),
                   ( isRowDimensions( cluster_dimensions ) ?
                     correlations :
                     null
                     ),
                   ( isColumnDimensions( cluster_dimensions ) ?
                     correlations :
                     null
                     ),
                   metric_category,
                   cluster_dimensions,
                   link_method
                   );
  } // static cluster( Object[], double[][], int )

  /**
   * Perform hierarchical clustering on the given data in a separate thread.
   */
  public static HierarchicalClustering cluster (
                                                Object[] row_objects,
                                                Object[] col_objects,
                                                double[][] row_correlations,
                                                double[][] col_correlations,
                                                int metric_category,
                                                int cluster_dimensions,
                                                int link_method
                                                ) {
    final HierarchicalClustering clustering =
      new HierarchicalClustering(
                                 row_objects,
                                 col_objects,
                                 row_correlations,
                                 col_correlations,
                                 metric_category,
                                 cluster_dimensions,
                                 link_method
                                 );

    //CodeReporter.reportProgress( clustering );
    //clustering.start();
    SwingWorker worker = new SwingWorker(){
        public Object construct(){
          return clustering.construct();
        }
      };
    worker.start();
    // Wait until it is done:
    worker.get();
    
    return clustering;
  } // static cluster( Object[], Object[], double[][], double[][], int, int )
  
  /**
   * Perform hierarchical clustering on the given data in a separate thread.
   */
  public static HierarchicalClustering cluster (
                                                String input_file_path,
                                                int correlation_metric,
                                                int cluster_dimensions,
                                                boolean use_centroid_method,
                                                int link_method
                                                ) {
    final HierarchicalClustering clustering =
      new HierarchicalClustering(
                                 input_file_path,
                                 correlation_metric,
                                 cluster_dimensions,
                                 use_centroid_method,
                                 link_method
                                 );
    
    SwingWorker worker = new SwingWorker (){
        public Object construct(){
          return clustering.construct();
        }
      };
    worker.start();
    
    // Wait until it is done:
    worker.get();
    
    return clustering;
  } // static cluster( String, int, int )
} // class HierarchicalClustering
