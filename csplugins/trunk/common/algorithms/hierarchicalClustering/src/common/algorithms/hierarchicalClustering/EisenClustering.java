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

import java.lang.Double;
import java.lang.Integer;
import java.lang.Boolean;
import java.awt.Component;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.Serializable;
import java.io.IOException;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.LineNumberReader;
import java.io.FileNotFoundException;
import java.io.*;
import java.lang.reflect.Array;

import java.util.*;
import giny.util.MonitorableTask;
import giny.util.SwingWorker;

/**
 * @author Iliana Avila-Campillo iavila@systemsbiology.org
 * @author Paul Edlefsen pedlefsen@systemsbiology.org
 * @version %I%, %G%
 */


public abstract class EisenClustering
  implements MonitorableTask, Serializable {

  /**
   * Cluster rows and columns
   */
  public static final int ROWS_COLS = 0; 
    
  /**
   * Cluster only rows
   */
  public static final int ROWS = 1;
    
  /**
   * Cluster only columns
   */
  public static final int COLS = 2;
    
  /**
   * Cluster only rows, but since the columns represent the same objects as in
   * the rows, order the columns in the same order as the rows. More efficient
   * than clustering both rows and columns if they represent the same objects.
   */
  public static final int ROWS_COLS_AS_ROWS = 3;

  /**
   * The default joining condition.  Returns true always.
   */
  public static JoiningCondition JOIN_ALL_JOINING_CONDITION =
    new JoiningCondition() {
      /**
       * @return true (always).
       */
      public boolean join (
                           EisenClusterNode node1,
                           EisenClusterNode node2
                           ) {
        return true;
      }
    };

  /**
   * The default cluster condition. Returns true always.
   */
  public static ClusterCondition ALL_NODES_ARE_CLUSTERS =
    new ClusterCondition() {
      /**
       * @return true (always).
       */
      public boolean isCluster (EisenClusterNode root){
        return true;
      }
    };
  
  /**
   * The default similarity threshold is 0.0 (used when a metric of category
   * VectorCorrelationCalculator.SIMILARITY_METRIC is set).
   */
  public static double DEFAULT_SIMILARITY_THRESHOLD = 0.0;

  /**
   * The default distance threshold is Double.POSITIVE_INFINITY (used when a metric of category
   * VectorCorrelationCalculator.DISTSNCE_METRIC is set).
   */
  public static double DEFAULT_DISTANCE_THRESHOLD = Double.POSITIVE_INFINITY;

  /**
   * By default, leaf reordering is enabled, for XCluster compatability.
   */
  public static boolean DEFAULT_LEAF_REORDERING_ENABLED = true;

  /**
   * By default, don't center correlations around their local means.
   */
  public static boolean DEFAULT_CENTER_CORRELATIONS_ENABLED = false;

  /**
   * By default, don't make all correlation scores absolute.
   */
  public static boolean DEFAULT_ABSOLUTE_CORRELATIONS_ENABLED = false;

  /**
   * The chosen clustering dimensions.
   */
  protected int clusterDimensions;
  
  /**
   * Whether or not a centroid method is used for clustering.
   * A centroid method replaces the two input rows (or columns if clustering columns)
   * of the two joined nodes by a single row (or column). The new row can be obtained by
   * averaging the two corresponding rows, by selecting the minimum of each pair in the row, 
   * or the maximum.
   * If the centroid method is not used, then similarities/distances between clusters are 
   * not the result of using averaged vectors. Instead, the distances among the leaves of 
   * the clusters are used.
   * Both methods result in similar trees. The centroid method makes the calculation of 
   * similarities much faster IF using a joining condition. 
   * The non-centroid method is faster for building the tree.
   */
  protected boolean centroid;

  /**
   * If using the centroid method AND the user chose ROWS_COLS_AS_ROWS for clustering dimension,
   * then for efficiency, keep a mask array to prevent shifting of columns or rows in the input
   * arrays of the EisenClusterNodes.
   */
  protected boolean [] inputMask;
  
  /**
   * In average-link clustering, we consider the distance between one cluster and another cluster 
   * to be equal to the average distance from any member of one cluster to any member of 
   * the other cluster.
   */
  public static int AVERAGE_LINK  = 0;
 
  /**
   * In single-link clustering (also called the connectedness or minimum method), 
   * we consider the distance between one cluster and another cluster to be equal 
   * to the shortest distance from any member of one cluster to any member of the 
   * other cluster.
   */
  public static int SINGLE_LINK = 1;
 
  /**
   * In complete-link clustering (also called the diameter or maximum method), 
   * we consider the distance between one cluster and another cluster to be equal 
   * to the longest distance from any member of one cluster to any member of the 
   * other cluster.
   */  
  public static int COMPLETE_LINK = 2;
  
  /**
   * Which link method the user chose.
   */
  protected int linkMethod;
  
  /**
   * The correlation threshold for clustering nodes.  Two nodes that have a
   * correlation to each other less than the threshold will never be joined.
   */
  protected double rowCorrelationThreshold;
  
  /**
   * The correlation threshold for clustering nodes.  Two nodes that have a
   * correlation to each other less than the threshold will never be joined.
   */
  protected double colCorrelationThreshold;

  /**
   * Whether or not to rearrange leaves of nodes when they get joined, so that
   * the two most similar leaves are adjacent.  If the user is interested in
   * the final ordering, then this variable should be set to true. If the user
   * is only interested in the groups of leaves that result from a correlation
   * threshold or/and a joining condition, then it should be set to false for
   * improved efficiency.
   */
  protected boolean leafReorderingEnabled;

  /**
   * The type of correlation metric to use.
   * @see VectorCorrelationCalculator
   */
  protected int correlationMetric;

  /**
   * Metrics for deciding which nodes to join next can be similarity metrics
   * or distance metrics. For similarity metrics, higher values have higher
   * priority in the queue of nodes to join. For distance metrics, lower values
   * have higher priority in the queue of nodes to join.
   * @see VectorCorrelationCalculator
   */
  protected int currentMetricCategory;
  
  /**
   * True if similarities should be renormalized around local means.
   * @see VectorCorrelationCalculator
   */
  protected boolean centeredSimilaritiesEnabled;

  /**
   * True if similarities should be made absolute.
   * @see VectorCorrelationCalculator
   */
  protected boolean absoluteSimilaritiesEnabled;

  protected int rowCount = -1;
  protected int colCount = -1;

  /**
   * The input matrix for clustering.  Used to calculate the correlation
   * matrices.
   */
  protected double[][] inputMatrix;

  /**
   * The weights used to calculate the rowCorrelations matrix.
   */
  protected double[] rowWeights;

  /**
   * The weights used to calculate the colCorrelations matrix.
   */
  protected double[] colWeights;

  /**
   * The Objects that will be clustered. The order of objects in the array
   * should correspond to the order in the input matrix.
   */
  protected EisenClusterNode[] rowEisenClusterLeaves;

  /**
   * The Objects that will be clustered. The order of objects in the array
   * should correspond to the order in the input matrix.
   */
  protected EisenClusterNode[] colEisenClusterLeaves;
    
  /**
   * A condition for joining nodes.
   */
  protected JoiningCondition rowJoiningCondition;

  /**
   * A condition for joining nodes.
   */
  protected JoiningCondition colJoiningCondition;

  /**
   * A condition for deciding whether or not a node is the root of a cluster.
   */
  protected ClusterCondition rowClusterCondition;

  /**
   * A condition for deciding whether or not a node is the root of a cluster.
   */
  protected ClusterCondition colClusterCondition;
  
  /**
   * The correlation values between all pairs of objects to cluster. Calculated
   * from the input matrix or supplied.
   */
  protected double[][] rowCorrelations;
  
  /**
   * The correlation values between all pairs of objects to cluster. Calculated
   * from the input matrix or supplied.
   */
  protected double[][] colCorrelations;

  /**
   * The root of the Hierarhical Tree in the rows dimension.
   */
  protected EisenClusterNode rowRoot;
  
  /**
   * The root of the Hierarhical Tree in the columns dimension.
   */
  protected EisenClusterNode colRoot;

  // Monitoring
  protected int targetProgress;
  protected int currentProgress;
  protected boolean done;
  protected boolean canceled;
  protected String statMessage;
  protected String taskName = "Eisen Clustering";
   
  // Collected statistics after the clustering.
  
  /**
   * The largest correlation for the rows dimension
   */
  protected double maxRowJValue;
  /**
   * The smallest correlation for the rows dimension 
   */
  protected double minRowJValue; 
  /**
   * The largest correlation for the columns dimension.
   */
  protected double maxColJValue; 
  /**
   * The smallest correlation for the columns dimension.
   */
  protected double minColJValue;
  /**
   * Keeps all the join distances in order of creation.
   */
  protected ArrayList joinDistances;
  /**
   * Keeps the number of clusters that pass the cluster condition at each cluster iteration.
   */
  protected ArrayList numClustersAtEachIt;
  /**
   * The minimum number of clusters that pass the cluster condition among all iterations.
   */
  protected int minNumClusters;
  /**
   * The maximum number of clusters that pass the cluster condition among all iterations.
   */
  protected int maxNumClusters;
  /**
   * The join number for which there is maximum number of clusters that pass the clusterCondition.
   */
  protected int jnMaxClusters;
  
  /**
   *
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
  public EisenClustering (
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
         dimension_objects,
         dimension_objects,
         correlation_metric,
         cluster_dimensions,
         use_centroid_method,
         link_method
         );
  }//<init>( double[][], Object[], int, int )

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
  public EisenClustering (
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
    clusterDimensions = cluster_dimensions;
    linkMethod = link_method;
    centroid = use_centroid_method;
    inputMatrix = input_matrix;
    rowCount = inputMatrix.length;
    colCount = inputMatrix[ 0 ].length;
    correlationMetric = correlation_metric;
    currentMetricCategory = VectorCorrelationCalculator.getCategory(correlation_metric);
    setLeafReorderingEnabled( DEFAULT_LEAF_REORDERING_ENABLED );

    // set default correlation thresholds
    if(this.currentMetricCategory == VectorCorrelationCalculator.SIMILARITY_METRIC){
      rowCorrelationThreshold = DEFAULT_SIMILARITY_THRESHOLD;
      colCorrelationThreshold = DEFAULT_SIMILARITY_THRESHOLD;
    }else{
      // We are using a distance metric
      rowCorrelationThreshold = DEFAULT_DISTANCE_THRESHOLD;
      colCorrelationThreshold = DEFAULT_DISTANCE_THRESHOLD;
    }
    setCenteredCorrelationsEnabled( DEFAULT_CENTER_CORRELATIONS_ENABLED );
    setAbsoluteCorrelationsEnabled( DEFAULT_ABSOLUTE_CORRELATIONS_ENABLED );

    // Use default joining condition that always returns true.
    rowJoiningCondition = JOIN_ALL_JOINING_CONDITION;
    colJoiningCondition = JOIN_ALL_JOINING_CONDITION;

    // Use default cluster condition that always returns true
    rowClusterCondition = ALL_NODES_ARE_CLUSTERS;
    colClusterCondition = ALL_NODES_ARE_CLUSTERS;

    if( row_weights != null ) {
	    if( row_weights.length != rowCount ) {
        throw new IllegalArgumentException( "The row weights array must be the same length as the "+
                                            "input matrix's primary dimension.  Expected length was " 
                                            + rowCount + ".  Actual length was " + row_weights.length 
                                            + "." );
	    }
	    rowWeights = row_weights;
    }
    if( col_weights != null ) {
	    if( col_weights.length != colCount ) {
        throw new IllegalArgumentException( "The column weights array must be the same length as the "+
                                            "input matrix's secondary dimension.  Expected length was " 
                                            + colCount + ".  Actual length was " + col_weights.length 
                                            + "." );
	    }
	    colWeights = col_weights;
    }

    if( row_objects != null ) {
	    rowEisenClusterLeaves = initializeClusterLeaves( row_objects );
    } else if( isRowDimensions( cluster_dimensions ) ) {
	    throw new IllegalArgumentException( "row_objects must not be null." );
    }
    if( col_objects != null ) {
	    colEisenClusterLeaves = initializeClusterLeaves( col_objects );
    } else if( isColumnDimensions( cluster_dimensions ) ) {
	    throw new IllegalArgumentException( "col_objects must not be null." );
    }

    // Initialize the objects to cluster
    if( ( clusterDimensions == ROWS ) ||
        ( clusterDimensions == ROWS_COLS ) ||
        ( clusterDimensions == ROWS_COLS_AS_ROWS )
        ) {
	    if( clusterDimensions == ROWS_COLS_AS_ROWS ) {
        colEisenClusterLeaves = rowEisenClusterLeaves;
        colRoot = rowRoot = createClusterNode( "ROOT", -1, -1 );
        targetProgress = rowCount + (rowCount/2);
	    } else {
        if(clusterDimensions == ROWS_COLS){
          targetProgress = (rowCount + rowCount/2) + (colCount + colCount/2);
        }else{
          targetProgress = rowCount + (rowCount/2);
        }
        rowRoot = createClusterNode( "ROW_ROOT", -1, -1 );
	    }
    }
    if( ( clusterDimensions == COLS ) ||
        ( clusterDimensions == ROWS_COLS )
        ) {
      if(clusterDimensions == COLS){
        targetProgress = colCount + colCount/2;
      }
	    colRoot = createClusterNode( "COL_ROOT", -1, -1 );
    }

    this.maxRowJValue = Double.NaN;
    this.minRowJValue = Double.NaN;
    this.maxColJValue = Double.NaN;
    this.minColJValue = Double.NaN;
    this.minNumClusters = Integer.MAX_VALUE;
    this.maxNumClusters = Integer.MIN_VALUE;
    this.joinDistances = new ArrayList();
    this.numClustersAtEachIt = new ArrayList();
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
  public EisenClustering (
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
  public EisenClustering (
                          Object[] row_objects,
                          Object[] col_objects,
                          double[][] row_correlations,
                          double[][] col_correlations,
                          int metric_category,
                          int cluster_dimensions,
                          int link_method
                          ) {
    currentMetricCategory = metric_category;
    clusterDimensions = cluster_dimensions;
    centroid = false;
    linkMethod = link_method;
    inputMatrix = null;

    setLeafReorderingEnabled( DEFAULT_LEAF_REORDERING_ENABLED );

    // set default correlation thresholds
    if(currentMetricCategory == VectorCorrelationCalculator.SIMILARITY_METRIC){
      rowCorrelationThreshold = DEFAULT_SIMILARITY_THRESHOLD;
      colCorrelationThreshold = DEFAULT_SIMILARITY_THRESHOLD;
    }else{
      // We are using a distance metric
      rowCorrelationThreshold = DEFAULT_DISTANCE_THRESHOLD;
      colCorrelationThreshold = DEFAULT_DISTANCE_THRESHOLD;
    }
    setCenteredCorrelationsEnabled( DEFAULT_CENTER_CORRELATIONS_ENABLED );
    setAbsoluteCorrelationsEnabled( DEFAULT_ABSOLUTE_CORRELATIONS_ENABLED );

    // Use default joining condition that always returns true.
    rowJoiningCondition = JOIN_ALL_JOINING_CONDITION;
    colJoiningCondition = JOIN_ALL_JOINING_CONDITION;

    // Use default cluster condition that always returns true.
    rowClusterCondition = ALL_NODES_ARE_CLUSTERS;
    colClusterCondition = ALL_NODES_ARE_CLUSTERS;
    
    if( row_correlations != null ) {
	    rowCorrelations = row_correlations;
	    rowCount = rowCorrelations.length;
    } else if( isRowDimensions( cluster_dimensions ) ) {
	    throw new IllegalArgumentException( "row_correlations must not be null." );
    }
    if( col_correlations != null ) {
	    colCorrelations = col_correlations;
	    colCount = colCorrelations.length;
    } else if( isColumnDimensions( cluster_dimensions ) ) {
	    throw new IllegalArgumentException( "col_correlations must not be null." );
    }
    if( row_objects != null ) {
      rowEisenClusterLeaves = initializeClusterLeaves( row_objects );
      if( row_correlations == null ) {
        rowCount = row_objects.length;
	    }
    }
    if( col_objects != null ) {
	    colEisenClusterLeaves = initializeClusterLeaves( col_objects );
	    if( col_correlations == null ) {
        colCount = col_objects.length;
	    }
    }

    if( ( clusterDimensions == ROWS ) ||
        ( clusterDimensions == ROWS_COLS ) ||
        ( clusterDimensions == ROWS_COLS_AS_ROWS )
        ) {
	    if( clusterDimensions == ROWS_COLS_AS_ROWS ) {
        targetProgress = rowCount + rowCount/2;
        colCorrelations = rowCorrelations;
        colEisenClusterLeaves = rowEisenClusterLeaves;
        colRoot = rowRoot = createClusterNode( "ROOT", -1, -1 );
	    } else {
        if(clusterDimensions == ROWS_COLS){
          targetProgress = (rowCount + rowCount/2) + (colCount + colCount/2);
        }else{
          targetProgress = rowCount + rowCount/2;
        }
        rowRoot = createClusterNode( "ROW_ROOT", -1, -1 );
	    }
    }
    if( ( clusterDimensions == COLS ) ||
        ( clusterDimensions == ROWS_COLS )
        ) {
      if(clusterDimensions == COLS){
        targetProgress = colCount + colCount/2;
      }
	    colRoot = createClusterNode( "COL_ROOT", -1, -1 );
    }
    this.maxRowJValue = Double.NaN;
    this.minRowJValue = Double.NaN;
    this.maxColJValue = Double.NaN;
    this.minColJValue = Double.NaN;
    this.minNumClusters = Integer.MAX_VALUE;
    this.maxNumClusters = Integer.MIN_VALUE;
    this.joinDistances = new ArrayList();
    this.numClustersAtEachIt = new ArrayList();
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
  public EisenClustering (
                          String input_file_path,
                          int correlation_metric,
                          int cluster_dimensions,
                          boolean use_centroid_method,
                          int link_method
                          ) {
    clusterDimensions = cluster_dimensions;
    if( !readInputFile( input_file_path ) ) {
	    return;
    }
    centroid = use_centroid_method;
    linkMethod = link_method;
    correlationMetric = correlation_metric;
    currentMetricCategory = VectorCorrelationCalculator.getCategory(correlation_metric);

    setLeafReorderingEnabled( DEFAULT_LEAF_REORDERING_ENABLED );

    // set default correlation thresholds
    if(currentMetricCategory == VectorCorrelationCalculator.SIMILARITY_METRIC){
      rowCorrelationThreshold = DEFAULT_SIMILARITY_THRESHOLD;
      colCorrelationThreshold = DEFAULT_SIMILARITY_THRESHOLD;
    }else{
      // We are using a distance metric
      rowCorrelationThreshold = DEFAULT_DISTANCE_THRESHOLD;
      colCorrelationThreshold = DEFAULT_DISTANCE_THRESHOLD;
    }
    setCenteredCorrelationsEnabled( DEFAULT_CENTER_CORRELATIONS_ENABLED );
    setAbsoluteCorrelationsEnabled( DEFAULT_ABSOLUTE_CORRELATIONS_ENABLED );

    // Use default condition that always returns true.
    rowJoiningCondition = JOIN_ALL_JOINING_CONDITION;
    colJoiningCondition = JOIN_ALL_JOINING_CONDITION;
    
    // Use default cluster condition that always returns true.
    rowClusterCondition = ALL_NODES_ARE_CLUSTERS;
    colClusterCondition = ALL_NODES_ARE_CLUSTERS;
    
    if( ( clusterDimensions == ROWS ) ||
        ( clusterDimensions == ROWS_COLS ) ||
        ( clusterDimensions == ROWS_COLS_AS_ROWS )
        ) {
	    if( clusterDimensions == ROWS_COLS_AS_ROWS ) {
        colEisenClusterLeaves = rowEisenClusterLeaves;
        colRoot = rowRoot = createClusterNode( "ROOT", -1, -1 );
        targetProgress = rowCount + rowCount/2;
	    } else {
        if(clusterDimensions == ROWS_COLS){
          targetProgress = (rowCount + rowCount/2) + (colCount + colCount/2);
        }else{
          targetProgress = rowCount + rowCount/2;
        }
        rowRoot = createClusterNode( "ROW_ROOT", -1, -1 );
	    }
    }
    if( ( clusterDimensions == COLS ) ||
        ( clusterDimensions == ROWS_COLS )
        ) {
      if(clusterDimensions == COLS){
        targetProgress = colCount + colCount/2;
      }
	    colRoot = createClusterNode( "COL_ROOT", -1, -1 );
    }
    this.maxRowJValue = Double.NaN;
    this.minRowJValue = Double.NaN;
    this.maxColJValue = Double.NaN;
    this.minColJValue = Double.NaN;
    this.minNumClusters = Integer.MAX_VALUE;
    this.maxNumClusters = Integer.MIN_VALUE;
    this.joinDistances = new ArrayList();
    this.numClustersAtEachIt = new ArrayList();
  } // <init>( String, int, int )
  
  /**
   * @return true if the task is done, false otherwise
   */
  public boolean isDone (){return this.done;}//done
  
  /**
   * @return the current progress of the task
   */
  public int getCurrentProgress () {return this.currentProgress;}//getCurrentProgress
  
  /**
   * @return the total length of the task
   */
  public int getLengthOfTask () {return this.targetProgress;}//getLengthOfTask

  /**
   * @return a status message that describes what is the current status
   */
  public String getCurrentStatusMessage () {return this.statMessage;}//getStatusMessage

  /**
   * @return a description of the task
   */
  public String getTaskDescription () { return this.taskName;}//getTaskName

  /**
   * @return <code>true</code> if the task was canceled before it was done
   * (for example, by calling <code>MonitorableSwingWorker.stop()</code>, 
   * <code>false</code> otherwise
   */
  public boolean wasCanceled() {return this.canceled;}

  /**
   * Stops the task if it is currently running.
   */
  public void stop () {
    //done = true;
    this.canceled = true;
    this.statMessage = null;
  }//stop
  
  /**
   * Starts doing the clustering in a separate thread so that the GUI stays responsive
   *
   * @param return_when_done if <code>true</code>, then this method will return only when
   * the task is done, else, it will return immediately after spawning the thread that
   * performs the task
   */
  public void start (boolean return_when_done) {
    currentProgress = 0;
    final SwingWorker worker = new SwingWorker (){
        public Object construct(){
          return EisenClustering.this.construct();
        }
        
        public void finished () {
        }
      };
    
    worker.start();
    
    if(return_when_done){
      //System.out.println("Waiting for Hierarchical Clustering to be done...");
      worker.get(); // wait to be done
      //System.out.println("...Done waiting (about to set done to true)");
      done = true;
    }// if return_when_done
  
  }//start

  // reInitialize ()
  protected void reInitialize () {
    if( ( clusterDimensions == ROWS ) ||
        ( clusterDimensions == ROWS_COLS ) ||
        ( clusterDimensions == ROWS_COLS_AS_ROWS )
        ) {
	    if( clusterDimensions == ROWS_COLS_AS_ROWS ) {
        colRoot = rowRoot = createClusterNode( "ROOT", -1, -1 );
        targetProgress = rowCount + (rowCount/2);
	    } else {
        if(clusterDimensions == ROWS_COLS){
          targetProgress = (rowCount + rowCount/2) + (colCount + colCount/2);
        }else{
          targetProgress = rowCount + (rowCount/2);
        }
        rowRoot = createClusterNode( "ROW_ROOT", -1, -1 );
	    }
    }
    if( ( clusterDimensions == COLS ) ||
        ( clusterDimensions == ROWS_COLS )
        ) {
      if(clusterDimensions == COLS){
        targetProgress = colCount + colCount/2;
      }
	    colRoot = createClusterNode( "COL_ROOT", -1, -1 );
    }
    
    this.maxRowJValue = Double.NaN;
    this.minRowJValue = Double.NaN;
    this.maxColJValue = Double.NaN;
    this.minColJValue = Double.NaN;
    this.minNumClusters = Integer.MAX_VALUE;
    this.maxNumClusters = Integer.MIN_VALUE;
    this.joinDistances = new ArrayList();
    this.numClustersAtEachIt = new ArrayList();
    
  }//reInitialize
  
  /**
   * Constructs the Hierarchical Tree. Call <code>start()</code> to run this method in
   * separate thread.
   *
   * @return an <code>EisenClusterNode</code> that is the root of the Hierarchical Tree. 
   */
  public Object construct () {
    this.done = false;
    this.canceled = false;
    cluster();
    //done = true;
    if( ( clusterDimensions == ROWS ) ||
        ( clusterDimensions == ROWS_COLS_AS_ROWS ) ) {
	    return getRowRoot();
    } else if( clusterDimensions == COLS ) {
	    return getColumnRoot();
    } else {
	    EisenClusterNode new_root = createClusterNode( "ROOT_PARENT", -2, -2 );
	    new_root.add( getRowRoot() );
	    new_root.add( getColumnRoot() );
	    return new_root;
    }
  } // construct()

    // ------- parameter interface methods ------- //
  
  /**
   * Sets whether or not the leaves of the hierarchical forest should be ordered so
   * that adjacent leaves of two nodes being joined during clustering are the
   * most similar pair. Must be set BEFORE clustering.
   */
  public void setLeafReorderingEnabled ( boolean new_reorder ) {
    leafReorderingEnabled = new_reorder;
  } // setLeafReorderingEnabled( boolean )

  /**
   * @return whether or not the leaves of the hierarchical forest will be ordered so
   * that adjacent leaves of two nodes being joined during clustering are the
   * most similar pair.
   */
  public boolean isLeafReorderingEnabled () {
    return leafReorderingEnabled;
  }//isLeafReorderingEnabled

  /**
   * Sets whether correlations should be centered around their local means.
   */
  public void setCenteredCorrelationsEnabled ( boolean new_centered ) {
    centeredSimilaritiesEnabled = new_centered;
  } // setCenteredCorrelationsEnabled( boolean )

  /**
   * @return whether correlations should be centered around their local means
   */
  public boolean isCenteredCorrelationsEnabled () {
    return centeredSimilaritiesEnabled;
  }//isCenteredCorrelationsEnabled

  /**
   * Sets whether or not negative correlations should be made positive.
   */
  public void setAbsoluteCorrelationsEnabled ( boolean new_absolute ) {
    absoluteSimilaritiesEnabled = new_absolute;
  } // setAbsoluteCorrelationsEnabled( boolean )

  /**
   * @return whether or not negative correlations should be made positive.
   */
  public boolean isAbsoluteCorrelationsEnabled () {
    return absoluteSimilaritiesEnabled;
  }//isAbsoluteCorrelationsEnabled
  
  /**
   * @return the input matrix.
   */
  public double[][] getInputMatrix () {
    return this.inputMatrix;
  } // getInputMatrix

  /**
   * @return the join distances for the latest clustering.
   */
  public double [] getJoinDistances () {
    int numJoins = joinDistances.size();
    if(numJoins == 0){
      return new double[0];
    }
    double [] jdis = new double [numJoins];
    for(int i = 0; i < numJoins; i++){
      jdis[i] = ((Double)joinDistances.get(i)).doubleValue();
    }//for i
    return jdis;
  }//getJoinDistances

  /**
   * @return the number of clusters that pass the cluster condition at each 
   * iteration of the clustering, or null if no clustering has been performed.
   */
  public int [] getNumClustersAtIterations (){
    int numIts = this.numClustersAtEachIt.size();
    if(numIts == 0){
      return new int[0];
    }
    int [] numClusters = new int [numIts];
    for(int i = 0; i < numIts; i++){
      numClusters[i] = ((Integer)this.numClustersAtEachIt.get(i)).intValue();
    }
    return numClusters;
  }//getNumClustersAtIterations

  /**
   * @return the join number of the join that results in the maximum number of clusters 
   * that meet the clusterCondition.
   */
  public int getJoinNumberWithMaxNumClusters () {
    return this.jnMaxClusters;
  }//getJoinNumberWithMaxNumClusters

  /**
   * @return the minimum number of clusters that pass the cluster condition at an iteration.
   */
  public int getMinNumClusters () {return this.minNumClusters;}//getMinNumClusters

  /**
   * @return the maximum number of clusters that pass the cluster condition at an iteration.
   */
  public int getMaxNumClusters () {
    return this.maxNumClusters;}//getMaxNumClusters
  
  /**
   * @return the current correlation metric
   * @see VectorCorrelationCalculator
   */
  public int getCorrelationMetric () {
    return correlationMetric;
  } // getCorrelationMetric()

  /**
   * Sets the joining condition for rows.
   */
  public void setRowJoiningCondition (
                                      JoiningCondition joining_condition
                                      ) {
    rowJoiningCondition = joining_condition;
  } // setRowJoiningCondition(..)

  /**
   * @return the joining condition for the rows.
   */
  public JoiningCondition getRowJoiningCondition () {
    return rowJoiningCondition;
  } // getRowJoiningCondition()
    
  /**
   * Sets the joining condition for columns.
   */
  public void setColumnJoiningCondition (
                                         JoiningCondition joining_condition
                                         ) {
    colJoiningCondition = joining_condition;
  } // setColumnJoiningCondition(..)

  /**
   * @return the joining condition for the columns.
   */
  public JoiningCondition getColumnJoiningCondition () {
    return colJoiningCondition;
  } // getColumnJoiningCondition()
    
  /**
   * Sets the cluster condition for rows.
   */
  public void setRowClusterCondition (ClusterCondition cluster_condition){
    this.rowClusterCondition = cluster_condition;
  }//setRowClusterCondition

  /**
   * @return the cluster condition for rows.
   */
  public ClusterCondition getRowClusterCondition () {
    return this.rowClusterCondition;
  } //getRowClusterCondition

  /**
   * Sets the cluster condition for columns.
   */
  public void setColumnClusterCondition (ClusterCondition cluster_condition){
    this.colClusterCondition = cluster_condition;
  }//setColumnClusterCondition

  /**
   * @return the cluster condition for the columns.
   */
  public ClusterCondition getColumnClusterCondition (){
    return this.colClusterCondition;
  }

  /**
   * It traverses the row's Hierarchical Tree, and it counts
   * the number of clusters (as specified by the <code>rowClusterCondition</code>).
   * It updates the <code>numClustersAtEachIt</code> member variable and returns it.
   *
   * @return an array of number of clusters (a cluster is defined by the 
   * <code>ClusterCondition</code> for rows) at each clustering iteration
   */
  public int [] updateNumClustersAtEachRowItr (){
    this.minNumClusters = Integer.MAX_VALUE;
    this.maxNumClusters = Integer.MIN_VALUE;
    updateIsClusterFields(rowRoot, rowClusterCondition);
    updateNumClustersAtEachItr(rowRoot,rowRoot);
    return getNumClustersAtIterations ();
  }//updateNumClustersAtEachItr

  /**
   * It traverses the columns's tree, and it counts
   * the number of clusters (as specified by the <code>colClusterCondition</code>).
   * It updates the <code>numClustersAtEachIt</code> member variable and returns it.
   *
   * @return the number of clusters (a cluster is defined by the 
   * <code>ClusterCondition</code> for columns) at each clustering iteration
   */
  public int [] updateNumClustersAtEachColItr (){
    this.minNumClusters = Integer.MAX_VALUE;
    this.maxNumClusters = Integer.MIN_VALUE;
    updateIsClusterFields(colRoot, colClusterCondition);
    updateNumClustersAtEachItr(colRoot,colRoot);
    return getNumClustersAtIterations ();
  }//udpateNumClustersAtEachColItr

  /**
   * Re-evaluates whether a cluster is a cluster or not depending on the given 
   * <code>ClusterCondition</code>
   */
  protected void updateIsClusterFields (EisenClusterNode root,ClusterCondition cluster_condition){
    if(root == null){return;}
    int numChildren = root.getChildCount();
    for(int i = 0; i < numChildren; i++){
      updateIsClusterFields((EisenClusterNode)root.getChildAt(i), cluster_condition);
    }
    if(root.index != -1){
      root.isCluster = cluster_condition.isCluster(root);
      //TODO: Remove
      //System.out.println("index=" + root.index 
      //                 + " isLeaf=" + root.isLeaf()
      //                 + " isCluster=" + root.isCluster 
      //                 + " leafCount=" + root.getLeafCount());
    }
  }//updateIsClusterFields

  /**
   * Given a root of a subtree in a Hierarchical Tree and the absolute root of the
   * Hierarchical Tree, it updates the number of clusters at each iteration 
   * for the nodes in the subtree.
   */
  protected void updateNumClustersAtEachItr (EisenClusterNode root, EisenClusterNode tree_root){
    // leaves were not joined at any iteration, so return
    if(root == null || root.isLeaf()){return;}
    
    int numChildren = root.getChildCount();
    EisenClusterNode child;
    
    for(int i = 0; i < numChildren; i++){
      child = (EisenClusterNode)root.getChildAt(i);
      updateNumClustersAtEachItr(child,tree_root);
    }// for
    
    // the root is not the last join, but a dummy node that points to all roots (join condition)
    if(root.index == -1){return;}
    
    Set clusters = new HashSet();
    // TODO: Don't hard code rowRoot
    HierarchicalClustering.clustersCreatedBeforeNode ( 
                                           (HierarchicalClustering.HierarchicalClusterNode)tree_root,
                                           root.index,
                                           clusters);
    Iterator it = clusters.iterator();
    EisenClusterNode cl;
    int numClusters = 0;
    while(it.hasNext()){
      cl = (EisenClusterNode)it.next();
      if(cl.isCluster && !cl.isLeaf()){
        numClusters++;
      }
    }//while
    
    this.numClustersAtEachIt.set(root.index, new Integer(numClusters));
    //TODO: Remove
    //System.out.println(root.index + " " + numClusters);
    // keep the max and the min
    if(this.minNumClusters > numClusters){
      this.minNumClusters = numClusters;
    }
    if(this.maxNumClusters <= numClusters){
      if(this.maxNumClusters == numClusters && 
         this.jnMaxClusters < root.index){
        // always keep the largest join number
        this.jnMaxClusters = root.index;
      }else if (this.maxNumClusters != numClusters){
        this.jnMaxClusters = root.index;
      }
      this.maxNumClusters = numClusters;
      
    }
  }//updateNumClustersAtEachItr
  
    /**
     * Sets the correlation threshold for rows.
     */
  public void setRowCorrelationThreshold ( double correlation ) {
    rowCorrelationThreshold = correlation;
  } // setRowCorrelationThreshold(..)

    /**
     * @return the correlation threshold for the rows.
     */
  public double getRowCorrelationThreshold () {
    return rowCorrelationThreshold;
  } // getRowCorrelationThreshold()

    /**
     * Sets the correlation threshold for columns.
     */
  public void setColumnCorrelationThreshold ( double correlation ) {
    colCorrelationThreshold = correlation;
  } // setColumnCorrelationThreshold(..)

    /**
     * @return the correlation threshold for the columns.
     */
  public double getColumnCorrelationThreshold () {
    return colCorrelationThreshold;
  } // getColumnCorrelationThreshold()

  /**
   * @return the calculated correlations between the vectors being clustered in the given dimension
   */
  public double[][] getCorrelations ( int dimension ) {
    if( dimension == ROWS ) {
	    return getRowCorrelations();
    } else if( dimension == COLS ) {
	    return getColumnCorrelations();
    } else {
	    throw new IllegalArgumentException( "The dimension argument must be either ROWS or COLS.  "
                                          + "The given value, " 
                                          + dimension + ", is not valid." );
    }
  } // getCorrelations( int )

  /**
   * @return the calculated correlations between the vectors being clustered in the rows dimension
   */
  public double[][] getRowCorrelations () {
    return rowCorrelations;
  } // getRowCorrelations()

  /**
   * @return the calculated correlations between the vectors being clustered in the columns dimension
   */
  public double[][] getColumnCorrelations () {
    return colCorrelations;
  } // getColumnCorrelations()

  /**
   * @return the <code>EisenClusterNode</code> object that is the root of the calculated
   * Hierarchical Tree in the given dimension
   */
  public EisenClusterNode getRoot ( int dimension ) {
    if( dimension == ROWS ) {
	    return getRowRoot();
    } else if( dimension == COLS ) {
	    return getColumnRoot();
    } else {
	    throw new IllegalArgumentException( "The dimension argument must be either ROWS or COLS.  "+
                                          "The given value, " 
                                          + dimension + ", is not valid." );
    }
  } // getRoot( int )

  /**
   * @return the <code>EisenClusterNode</code> object that is the root of the calculated
   * Hierarchical Tree in the given rows dimension
   */
  public EisenClusterNode getRowRoot () {
    return rowRoot;
  }//getRoot

  /**
   * @param node_index the node index for which a <code>EisenClusterNode</code> will be returned
   * @param root the root of the tree to search
   * @return the <code>EisenClusterNode</code> with the given (tree) index, or null of the node
   * does not exist
   */
  public EisenClusterNode getNodeWithIndex (int node_index, EisenClusterNode root){
    //System.out.println("getNodeWithIndex(" + node_index + "," + root + ")");
    if(root == null || root.index == node_index){
      return root;
    }
    int numChildren = root.getChildCount();
    EisenClusterNode returnNode = null;
    for(int i = 0; i < numChildren; i++){
      returnNode = getNodeWithIndex(node_index,(EisenClusterNode)root.getChildAt(i));
      if(returnNode != null){
        break;
      }
    }
    return returnNode;
  }//getNodeWithIndex
  
  /**
   * @return the <code>EisenClusterNode</code> object that is the root of the calculated
   * Hierarchical Tree in the given columns dimension
   */
  public EisenClusterNode getColumnRoot () {
    return colRoot;
  }//getColumnRoot

  /**
   * Not sure what this is for.
   * TODO: Find out
   */
  public void setRowCount ( int row_count ) {
    if( rowCount == row_count ) {
	    return;
    }
    if( rowCount >= 0 ) {
	    throw new IllegalStateException( "Unable to set the rowCount when it has already been set or "+
                                       "calculated.  rowCount is "
                                       + rowCount + "." );
    }
    rowCount = row_count;
  } // setRowCount( int )

  /**
   * @return the number of rows in the input matrix 
   */
  public int getRowCount () {
    return rowCount;
  }//getRowCount

  /**
   * ???
   */
  public void setColumnCount ( int col_count ) {
    if( colCount == col_count ) {
	    return;
    }
    if( colCount >= 0 ) {
	    throw new IllegalStateException( "Unable to set the columnCount when it has "
                                       +"already been set or calculated.  "+
                                       "columnCount is " + colCount + "." );
    }
    colCount = col_count;
  } // setColumnCount( int )

  /**
   * @return the number of columns in the input matrix
   */
  public int getColumnCount () {
    return colCount;
  }//getColumnCount

  /**
   * @return an <code>Iterator</code> of the leaves of the Hierarchical Tree for the rows dimension
   */
  public Iterator rowLeafIterator () {
    return leafIterator( ROWS );
  }//rowLeafIterator
  
  /**
   * @return an <code>Iterator</code> of the leaves of the Hierarchical Tree for the columns dimension
   */
  public Iterator colLeafIterator () {
    return leafIterator( COLS );
  }//colLeafIterator
  
  /**
   * @return an <code>Iterator</code> of the leaves of the Hierarchical Tree for the given dimension 
   */
  public Iterator leafIterator ( final int dimension ) {
    if( ( dimension == ROWS ) && ( clusterDimensions == COLS ) ) {
	    if( rowEisenClusterLeaves == null ) {
        if( rowCount < 0 ) {
          throw new IllegalStateException( "Unable to iterate over row leaves because rowCount "+
                                           "is unknown." );
        }
        rowEisenClusterLeaves =
          initializeClusterLeaves( new Object[ rowCount ] );
	    }
	    //return ArrayUtilities.iterator( rowEisenClusterLeaves );
	    return iterator( rowEisenClusterLeaves );
    }
    if( ( dimension == COLS ) &&
        ( clusterDimensions == ROWS ) ) {
	    if( colEisenClusterLeaves == null ) {
        if( colCount < 0 ) {
          throw new IllegalStateException( "Unable to iterate over row leaves because colCount "+
                                           "is unknown." );
        }
        colEisenClusterLeaves =
          initializeClusterLeaves( new Object[ colCount ] );
	    }
	    //return ArrayUtilities.iterator( colEisenClusterLeaves );
	    return iterator( colEisenClusterLeaves );
    }
    if( dimension == ROWS ) {
	    return rowRoot.leafIterator();
    } else if( dimension == COLS ) {
	    return colRoot.leafIterator();
    } else {
	    throw new IllegalArgumentException( "The dimension argument must be either ROWS or COLS.  "+
                                          "The given value, " 
                                          + dimension + ", is not valid." );
    }
  } // leafIterator( int )

    /**
     * @param dimension the dimension for which to return the nodes
     * @return a new array of EisenClusterNodes containing the leaves in
     * leafIterator order.
     */
  public EisenClusterNode[] leafArray ( int dimension ) {
    if( ( dimension == ROWS ) && ( clusterDimensions == COLS ) ) {
	    if( rowEisenClusterLeaves == null ) {
        if( rowCount < 0 ) {
          throw new IllegalStateException( "Unable to return an array of leaves because "+
                                           "rowCount is unknown." );
        }
        rowEisenClusterLeaves =
          initializeClusterLeaves( new Object[ rowCount ] );
	    }
	    return rowEisenClusterLeaves;
    }
    if( ( dimension == COLS ) &&
        ( clusterDimensions == ROWS ) ) {
	    if( colEisenClusterLeaves == null ) {
        if( colCount < 0 ) {
          throw new IllegalStateException( "Unable to return an array of leaves because "+
                                           "colCount is unknown." );
        }
        colEisenClusterLeaves =
          initializeClusterLeaves( new Object[ colCount ] );
	    }
	    return colEisenClusterLeaves;
    }
    
    return (EisenClusterNode[])addAll(new ArrayList(),leafIterator(dimension)).toArray(new EisenClusterNode [0]);
  } // leafArray()

  /**
   * Return the weights of the objects to cluster in the given dimension. The
   * order of the weights in the result array will reflect the order of the
   * given Object array in the constructor.
   */
  public double[] getWeights ( int dimension ) {
    if( dimension == ROWS ) {
	    return getRowWeights();
    } else if( dimension == COLS ) {
	    return getColumnWeights();
    } else {
	    throw new IllegalArgumentException( "The dimension argument must be either ROWS or COLS."+
                                          "  The given value, " 
                                          + dimension + ", is not valid." );
    }
  } // getWeights( int )
  
   /**
    * Return the weights of the row objects for use in calculating the
    * rowCorrelations matrix. The order of the weights in the result array will
    * reflect the order of the given Object array in the constructor.
    */
  public double[] getRowWeights () {
    if( rowWeights == null ) {
	    rowWeights = new double[ rowCount ];
	    Arrays.fill( rowWeights, 1.0 );
    }
    return rowWeights;
  } // getRowWeights()
    
   /**
    * Return the weights of the column objects for use in calculating the
    * columnSimilarities matrix. The order of the weights in the result array
    * will reflect the order of the given Object array in the constructor.
    */
  public double[] getColumnWeights () {
    if( colWeights == null ) {
	    colWeights = new double[ colCount ];
	    Arrays.fill( colWeights, 1.0 );
    }
    return colWeights;
  } // getColumnWeights()
    
  // ------- clustering methods ---------//
  
  /**
   * Clusters the dimensions desired.  Rows always precede columns.  Calls
   * {@link #cluster( int )}.
   */
  public void cluster () {
    this.joinDistances.clear();
   
    // first cluster the rows, if row clustering is desired.
    if( clusterDimensions == ROWS || 
        clusterDimensions == ROWS_COLS || 
        clusterDimensions == ROWS_COLS_AS_ROWS
        ) {
      if(centroid){
         // Initialize the input mask if necessary
        if(clusterDimensions == ROWS_COLS_AS_ROWS){
          inputMask = new boolean [inputMatrix.length];
          for(int i = 0; i < inputMatrix.length; i++){
            inputMask[i] = true;
          }
        }
        setInputVals(ROWS);
      }//if centroid
      cluster( ROWS );
      if( leafReorderingEnabled ) {
        // TODO: REMOVE
        
        System.out.print( "Reordering row leaves.." );
        System.out.flush();
          
        reorderLeaves( ROWS );
        //System.out.println( ".done." );
	    }
      
	    // TODO: REMOVE
	    //printHierarchicalForest( ROWS );
    }
    // cluster columns if column clustering is desired.
    if( clusterDimensions == ROWS_COLS ||
        clusterDimensions == COLS
        ) {
      if(centroid){
        setInputVals(COLS);
      }
	    cluster( COLS );
      
	    if( leafReorderingEnabled ) {
        // TODO: REMOVE
        
        System.out.print( "Reordering column leaves.." );
        System.out.flush();
        
        reorderLeaves( COLS );
        //System.out.println( ".done." );
	    }
      
	    // TODO: REMOVE
	    //printHierarchicalForest( COLS );
    }

    //System.out.println("Exiting method EisenClustering.cluster()");
  } // cluster

  /**
   * Sets the inputVals arrays for each <code>EisenClusterNode</code>
   */
  protected void setInputVals (int dimension){
    EisenClusterNode [] leaves;
    if(dimension == ROWS){
      leaves = rowEisenClusterLeaves;
    }else if(dimension == COLS){
      leaves = colEisenClusterLeaves;
    }else{
      throw new IllegalArgumentException( "The dimension argument must be either ROWS or COLS.  "+
                                          "The given value, " 
                                          + dimension + ", is not valid." );
    }

    EisenClusterNode node;
    for(int l = 0; l < leaves.length; l++){
      node = leaves[l];
      if(dimension == ROWS){
        int row = node.index;
        node.inputVals = new double [inputMatrix[row].length];
        for(int col = 0; col < inputMatrix[row].length; col++){
          node.inputVals[col] = inputMatrix[row][col];
        }
      }else if(dimension == COLS){
        int col = node.index;
        node.inputVals = new double [inputMatrix.length];
        for(int row = 0; row < inputMatrix.length; row++){
          node.inputVals[row] = inputMatrix[row][col];
        } 
      }//COLS
    }// for l
  }//setInputVals
  
  /**
   * Clusters the dimensions desired, and displays a monitor.  Rows always precede columns.  
   */
  //TODO: Fill int
  public void clusterAndMonitor () {
    //CytoscapeProgressMonitor monitor  = new CytoscapeProgressMonitor(this);
    //monitor.startMonitor(true);
  }//clusterAndMonitor

   /**
    * Clusters the dimensions desired, and displays a monitor.  Rows always precede columns.  
    * @param parentComponent the Component with respect to which the progress monitor will 
    * be positioned
    */
  //TODO: Fill in
  public void clusterAndMonitor (Component parentComponent) {
    //CytoscapeProgressMonitor monitor = new CytoscapeProgressMonitor(this,parentComponent);
    //monitor.startMonitor(true);
  }//clusterAndMonitor
  
  /**
   * Perform clustering in the given dimension, abstract method.
   *
   * @param dimension the dimension in which to perform clustering
   */
  protected abstract void cluster ( int dimension );
  
  /**
   * Calculate the correlations in the given dimension if they have not already
   * been calculated (or provided by the user).
   *
   * @param dimension the dimension in which to perform calculate correlations
   */
  protected void calculateCorrelations ( int dimension ) {
    if( dimension == ROWS ) {
	    if( rowCorrelations != null ) {
        // The user provided it.
        return;
	    }
	    if( inputMatrix == null ) {
        throw new IllegalStateException("The user must provide either an inputMatrix or"
                                        +" rowCorrelations "+
                                        "if we are to cluster in the rows dimension." );
	    }
      boolean [][] filter = getFilterFromJC(dimension);
      
	    // we are clustering rows
	    rowCorrelations = VectorCorrelationCalculator.calculateCorrelations(
                                                                   inputMatrix,
                                                                   getWeights( COLS ),
                                                                   correlationMetric,
                                                                   false,
                                                                   centeredSimilaritiesEnabled,
                                                                   absoluteSimilaritiesEnabled,
                                                                   filter
                                                                   );
      // if(rowCorrelations == null){
      //System.out.println("Oh, Oh. rowCorrelations is null!!!!!!!!!!!!!!!!!!!!!!!!!!!!!111");
      //}
	    // TODO: REMOVE
	    //System.err.println( "Finished calculating row similarities." );
	    //System.err.println( "They are " + ArrayUtilities.toString( rowCorrelations ) + "." );
    } else if( dimension == COLS ) {
	    if( colCorrelations != null ) {
        // The user provided it.
        return;
	    }
	    if( inputMatrix == null ) {
        throw new IllegalStateException( "The user must provide either an inputMatrix or "+
                                         "colCorrelations "+
                                         "if we are to cluster in the cols dimension." );
	    }

      boolean [][] filter = getFilterFromJC(dimension);
      
	    colCorrelations = VectorCorrelationCalculator.calculateCorrelations(
                                                                   inputMatrix,
                                                                   getWeights( ROWS ),
                                                                   correlationMetric,
                                                                   true,
                                                                   centeredSimilaritiesEnabled,
                                                                   absoluteSimilaritiesEnabled,
                                                                   filter
                                                                   );
	    // TODO: REMOVE
	    //System.err.println( "Finished calculating column similarities." );
	    //System.err.println( "They are " + ArrayUtilities.toString( colCorrelations ) + "." );
    } else {
	    throw new IllegalArgumentException("The dimension argument must be either ROWS or COLS."+
                                         "  The given value, " 
                                          + dimension + ", is not valid." );
    }
  } // calculateCorrelations( int )

  /**
   * Creates a 2D array of booleans in which filter[i][j] specifies whether or not the 
   * clustering object at index i and the clustering object at index j should be joined or not.
   * The filter is constructed base on the set joining condition for the given dimension.
   *
   * @param dimension the dimension for which the filter is created
   * @return a 2D array of booleans or null if the joining condition is equal to 
   * <code>JOIN_ALL_JOINING_CONDITION</code>
   */
  public boolean [][] getFilterFromJC (int dimension){
    JoiningCondition jc;
    EisenClusterNode [] leafs;
    if(dimension == ROWS){
      jc = this.rowJoiningCondition;
      leafs = rowEisenClusterLeaves;
    }else if(dimension == COLS){
      jc = colJoiningCondition;
      leafs = colEisenClusterLeaves;
    }else{
      throw new IllegalArgumentException( "The dimension argument must be either ROWS or COLS."+
                                          "  The given value, " 
                                          + dimension + ", is not valid." );
    }
    if(jc == JOIN_ALL_JOINING_CONDITION){
      return null;
    }
    
    boolean [][] filter = new boolean [leafs.length][leafs.length];
    for(int i = 0; i < leafs.length; i++){
      for(int j = i; j < leafs.length; j++){
        if(i==j){
          filter[i][j] = true;
        }else if(!jc.join(leafs[i],leafs[j])){
          filter[i][j] = false;
          filter[j][i] = false;
        }else{
          filter[i][j] = true;
          filter[j][i] = true;
        }
      }
    }
    return filter;
  }//getFilterFromJC

  /**
   * Returns the largest value with which two cluster nodes were joined in the 
   * rows dimension.
   */
  public double getMaximumRowJoiningValue () {return this.maxRowJValue;}//getMaximumRowCorrelation
  
  /**
   * Returns the smallest value with which two cluster nodes were joined in the 
   * columns dimension
   */
  public double getMinimumRowJoiningValue () {return this.minRowJValue;}//getMinumumRowJoiningValue

  /**
   * Returns the largest value with which two cluster nodes were joined in the 
   * columns dimension.
   */
  public double getMaximumColJoiningValue () {return this.maxColJValue;}//getMaximumRowCorrelation
  
  /**
   * Returns the smallest value with which two cluster nodes were joined in the 
   * columns dimension
   */
  public double getMinimumColJoiningValue () {return this.minColJValue;}//getMinumumRowJoiningValue
   
  /**
   * Returns an array of EisenClusterNode leaves with the given objects as node
   * peers.
   */
  protected EisenClusterNode[] initializeClusterLeaves (
                                                        Object[] objects
                                                        ) {
    EisenClusterNode[] cluster_leaves = new EisenClusterNode[ objects.length ];
    for( int i = 0; i < objects.length; i++ ) {
	    cluster_leaves[ i ] = createClusterLeaf( objects[ i ], i, i );
    }
    return cluster_leaves;
  } // initializeClusterLeaves
    
  /**
   * Returns the final ordering of rows after clustering
   * as an array of <code>EisenClusterNode</code> leaves.
   */
  public EisenClusterNode[] getFinalRowOrdering () {
    return leafArray( ROWS );
  } // getFinalRowOrdering()
  
  /**
   * Returns the final ordering of columns after clustering 
   * as an array of EisenClusterNode leaves.
   */
  public EisenClusterNode[] getFinalColumnOrdering () {
    return leafArray( COLS );
  } // getFinalColumnOrdering()

  
  // -------- Sorting methods -------- //
  // TODO: Modify to handle non-binary nodes...
  
  /**
   * After the Hierarchical Tree has been constructed for the given dimension, ir reorders
   * the leaves of the tree so that neighboring leaves have the highest correlation.
   *
   * @param dimension the dimension of the tree to reorder
   */
  protected void reorderLeaves ( int dimension ) {

    EisenClusterNode root;
    if( dimension == ROWS ) {
	    root = rowRoot;
    } else if( dimension == COLS ) {
	    root = colRoot;
    } else {
	    throw new IllegalArgumentException( "The dimension argument must be either ROWS or COLS. "+
                                          " The given value, " 
                                          + dimension + ", is not valid." );
    }

    Comparator node_pair_comparator;
    if(currentMetricCategory == VectorCorrelationCalculator.SIMILARITY_METRIC){
      node_pair_comparator =
        new DescendingNodePairCorrelationComparator();
    }else{
      // We are using a distance metric
      node_pair_comparator =
        new AscendingNodePairCorrelationComparator();
    }
    EisenClusterNode node = root;
    EisenClusterNode parent = null;
    EisenClusterNode other_node;
    while( node != null ) {
	    if( node.isLeaf() ) {
        // Ascend until the current node is the left branch in its parent.
        while( ( parent != null ) &&
               parent.getRightBranch() == node ) {

          // Presently, node is the right branch of its parent.
          // Get the other branch.
          other_node = ( EisenClusterNode )parent.getLeftBranch();

          // Don't try to orient them if they're the same.
          if( node != other_node ) {
            // Now orient them.  Start by finding the most similar couples in
            // which each couple contains a member from node and another from
            // other_node.
            NodePair[] candidate_couples =
              candidateCouples( dimension, other_node, node );
            // Sort them to find the couple(s) with most similarity or least distance
            Arrays.sort( candidate_couples, node_pair_comparator );
            
            // How many most-similar/least-distance couples are there?
            int most_similar_couples_count = 1;
            for( int i = 0; i < ( candidate_couples.length - 1 ); i++ ) {
              if( node_pair_comparator.compare(
                                               candidate_couples[ i ],
                                               candidate_couples[ i + 1 ]
                                               ) == 0 ) { 
                // If couples at i and at i + 1 are equidistant
                most_similar_couples_count++;
              } else {
                break;
              }
            }
            // Select all that are most similar or closest.
            parent.getLeftFaces().select(
                                         parent,
                                         nodeOneSet( candidate_couples, most_similar_couples_count )
                                         );
            parent.getRightFaces().select(
                                          parent,
                                          nodeTwoSet( candidate_couples, most_similar_couples_count )
                                          );
          } // End if( node != other_node )

          // Keep ascending.
          node = parent;
          parent = ( EisenClusterNode )node.getParent();
        } // End while node is the last child of its parent

        // Are we done yet?
        if( parent == null ) {
          // Then node is root, so we're done.
          node = null;
          // Set the root node's orientation, arbitrarily.
          root.setOrientation( ClusterNode.LEFT_BRANCH_OUTSIDE );
          return;
        }

        // Descend right.
        node = ( EisenClusterNode )parent.getRightBranch();
	    } else {
        // If it's not a leaf, then we're seeing a new parent node.  We will
        // come back to this after we have processed its children.
        // Descend left.
        parent = node;
        node = ( EisenClusterNode )parent.getLeftBranch();
	    }
    } // End while node != null, keep 'em flipping.

  } // sortLeaves( int )

  /**
   * 
   */
  protected NodePair[] candidateCouples (
                                         int dimension,
                                         EisenClusterNode c,
                                         EisenClusterNode d
                                         ) {
    double[][] similarities;
    if( dimension == ROWS ) {
	    similarities = rowCorrelations;
    } else if( dimension == COLS ) {
	    similarities = colCorrelations;
    } else {
	    throw new IllegalArgumentException( "The dimension argument must be either ROWS or COLS.  "+
                                          "The given value, " 
                                          + dimension + ", is not valid." );
    }
    int c_candidate_count = 0;
    if( c.getOrientation() == EisenClusterNode.LEFT_BRANCH_OUTSIDE ) {
	    c_candidate_count = c.getLeftFaces().size();
    } else if( c.getOrientation() == EisenClusterNode.RIGHT_BRANCH_OUTSIDE ) {
	    c_candidate_count = c.getRightFaces().size();
    } else {
	    c_candidate_count =
        ( c.getLeftFaces().size() + c.getRightFaces().size() );
    }
    int d_candidate_count = 0;
    if( d.getOrientation() == EisenClusterNode.LEFT_BRANCH_OUTSIDE ) {
	    d_candidate_count = d.getLeftFaces().size();
    } else if( d.getOrientation() == EisenClusterNode.RIGHT_BRANCH_OUTSIDE ) {
	    d_candidate_count = d.getRightFaces().size();
    } else {
	    d_candidate_count =
        ( d.getLeftFaces().size() + d.getRightFaces().size() );
    }
    NodePair[] candidate_couples =
	    new NodePair[ c_candidate_count * d_candidate_count ];
    EisenClusterNode c_candidate;
    EisenClusterNode d_candidate;
    double correlation;
    for( int c_candidate_i = 0;
         c_candidate_i < c_candidate_count;
         c_candidate_i++ ) {
	    // Figure out the c part of the candidate pair
	    if( c.getOrientation() == EisenClusterNode.LEFT_BRANCH_OUTSIDE ) {
        c_candidate = ( EisenClusterNode )
          c.getLeftFaces().get( c_candidate_i );
	    } else if( c.getOrientation() == EisenClusterNode.RIGHT_BRANCH_OUTSIDE ) {
        c_candidate = ( EisenClusterNode )
          c.getRightFaces().get( c_candidate_i );
	    } else if( c_candidate_i < c.getLeftFaces().size() ) {
        c_candidate = ( EisenClusterNode )
          c.getLeftFaces().get( c_candidate_i );
	    } else {
        c_candidate = ( EisenClusterNode )
          c.getRightFaces().get( c_candidate_i - c.getLeftFaces().size() );
	    }
	    for( int d_candidate_i = 0;
           d_candidate_i < d_candidate_count;
           d_candidate_i++ ) {
        // Figure out the d part of the candidate pair
        if( d.getOrientation() == EisenClusterNode.LEFT_BRANCH_OUTSIDE ) {
          d_candidate = ( EisenClusterNode )
            d.getLeftFaces().get( d_candidate_i );
        } else if( d.getOrientation() ==
                   EisenClusterNode.RIGHT_BRANCH_OUTSIDE ) {
          d_candidate = ( EisenClusterNode )
            d.getRightFaces().get( d_candidate_i );
        } else if( d_candidate_i < d.getLeftFaces().size() ) {
          d_candidate = ( EisenClusterNode )
            d.getLeftFaces().get( d_candidate_i );
        } else {
          d_candidate = ( EisenClusterNode )
            d.getRightFaces().get( d_candidate_i - d.getLeftFaces().size() );
        }
        correlation = similarities[ c_candidate.index ][ d_candidate.index ];
        // Make the pair.
        candidate_couples[
                          ( c_candidate_i * d_candidate_count ) + d_candidate_i
        ] =
          new NodePair( c_candidate, d_candidate, correlation );
	    } // End for d_candidate_i
    } // End for c_candidate_i
    return candidate_couples;
  } // candidateCouples(..)

  protected static Set nodeOneSet ( NodePair[] node_pairs, int count ) {
    HashSet set = new HashSet();
    for( int i = 0; i < count; i++ ) {
	    set.add( node_pairs[ i ].nodeOne );
    }
    return set;
  } // static nodeOneSet(..)

  protected static Set nodeTwoSet ( NodePair[] node_pairs, int count ) {
    HashSet set = new HashSet();
    for( int i = 0; i < count; i++ ) {
	    set.add( node_pairs[ i ].nodeTwo );
    }
    return set;
  } // static nodeTwoSet(..)

 // --------- I/O methods ---------- //
  
  /**
   * Prints the hierarchical forest for the given dimension to STDOUT.
   */
  public void printHierarchicalForest ( int dimension ) {
    EisenClusterNode root;
    if( dimension == ROWS ) {
	    root = rowRoot;
    } else if( dimension == COLS ) {
	    root = colRoot;
    } else {
	    throw new IllegalArgumentException( "The dimension argument must be either ROWS or COLS."+
                                          "  The given value, " 
                                          + dimension + ", is not valid." );
    }
    Enumeration depth_first_enum = root.depthFirstEnumeration();
    EisenClusterNode node;
    boolean first_element = true;
    while( depth_first_enum.hasMoreElements() ) {
	    node = ( EisenClusterNode )depth_first_enum.nextElement();
	    if( ( node.isLeaf() && ( node.getParent() != root ) ) ||
          ( node == root ) ) {
        continue;
	    }
	    if( first_element ) {
        System.out.println( node.paramStringHeader() );
        first_element = false;
	    }
	    System.out.println( node.paramString() );
    }
  } // printHierarchicalForest( dimension )

  /**
   * Reads an input file that contains the data for the inputMatrix and the
   * objects to cluster.  Even if unsuccessful, the inputMatrix and
   * rowEisenClusterLeaves and colEisenClusterLeaves values may be modified as
   * a side-effect.
   * @return true iff the read was successful.
   */
  protected boolean readInputFile ( String input_file_path ) {
        
    File inputFile = new File(input_file_path);
        
    if(!inputFile.exists()) {
	    System.err.println("Input file " + input_file_path + " does not exist");
	    System.err.flush();
	    return false;
    }
        
    BufferedReader in;
        
    try{
	    in = new BufferedReader(new FileReader(inputFile));
    }catch(FileNotFoundException e) {
	    System.err.println(e);
	    System.err.flush();
	    return false;
    }
        
    LineNumberReader lineReader = new LineNumberReader(in);
    rowCount = 0;
    colCount = 0;   
    boolean hasName = false;
    List inputStrings = new ArrayList(); // list of strings
    String line;
    try{
	    line = lineReader.readLine();
    }catch(IOException e) {
	    System.err.println(e);
	    System.err.flush();
	    return false;
    }

    int firstLineNum = lineReader.getLineNumber();
        
    while(line != null) {
            
	    StringTokenizer tokenizer = new StringTokenizer(line,"\t");
            
	    if(lineReader.getLineNumber() == firstLineNum) {
        int numTokens = tokenizer.countTokens();
        if(numTokens < 3) {
          System.err.println("Wrong file format: " + input_file_path);
          System.err.println(" The first line contains less than 3 fields.");
          System.err.flush();
          return false;
        }
        String field = tokenizer.nextToken();
        if(!field.equals("UID")) {
          System.err.println("Wrong file format: " + input_file_path);
          System.err.println(" The first field must be UID.");
          System.err.flush();
          return false;
        }
                
        field = tokenizer.nextToken();
        colCount = numTokens;
        if(field.equals("NAME")) {
          colCount -= 3;
          hasName = true;
        }else{
          colCount -= 2;
          // if not NAME then it must be GWEIGHT
        }
                
        if(!field.equals("GWEIGHT") && !(tokenizer.nextToken()).equals("GWEIGHT")) {
          System.err.println("Wrong file format: " + input_file_path);
          System.err.println(" There must be a GWEIGHT field.");
          System.err.flush();
          return false;
        }

        // read the column objects
        colEisenClusterLeaves = new EisenClusterNode[colCount];
        colWeights = new double[ colCount ];
        int i = 0;
        while(tokenizer.hasMoreTokens()) {
          String colObj = tokenizer.nextToken();
          colEisenClusterLeaves[ i ] = createClusterLeaf( colObj, i, i );
          colEisenClusterLeaves[ i ].uid = colObj;
          i++;
        }//while more tokens
        
                        
	    }else if(lineReader.getLineNumber() == firstLineNum+1) {
        String gweight = tokenizer.nextToken();
        if(!gweight.equals("EWEIGHT") && !gweight.equals("EWEIGHT ")) {
          System.err.println("Wrong file format: " + input_file_path);
          System.err.println(" The second line must be an EWEIGHT field, read: [" + gweight + "]");
          System.err.flush();
          return false;
        }
                
        // read the column weights
        int i = 0;
        while(tokenizer.hasMoreTokens()) {
          double colWeight = Double.parseDouble(tokenizer.nextToken());
          // TODO: REMOVE
          //System.err.println( "EWEIGHT( " + i + " ): \"" + colWeight + "\"" );
          colWeights[ i ] = colWeight;
          i++;
        }
                
	    }else{
        rowCount++;
        inputStrings.add(line);
	    }

	    try{
        line = lineReader.readLine();
	    }catch(IOException e) {
        System.err.println(e);
        System.err.flush();
        return false;
	    }
    }//while
        
    rowEisenClusterLeaves = new EisenClusterNode[rowCount];
    rowWeights = new double[ rowCount ];
    inputMatrix = new double[rowCount][colCount];
    int i = 0;
    int j = 0;
        
    int input_strings_i = -1;
    try {
	    for( input_strings_i = 0; input_strings_i < inputStrings.size(); input_strings_i++ ) {
        // Note that we are asking it to return delimiters.  This is because some
        // fields may be left blank, and we must keep track of which column we
        // are in.
        StringTokenizer tokenizer =
          new StringTokenizer( ( String )inputStrings.get( input_strings_i ),"\t", true);
        String uid =
          ( tokenizer.hasMoreTokens() ? tokenizer.nextToken() : null );
        // skip delimiter
        if( uid.equals( "\t" ) ) {
          uid = "";
        } else if( tokenizer.hasMoreTokens() ) {
          tokenizer.nextToken();
        }
        // TODO: REMOVE
        //System.err.println( "uid: \"" + uid + "\"" );
        rowEisenClusterLeaves[ i ] = createClusterLeaf( uid, i, i );
        rowEisenClusterLeaves[ i ].uid = uid;
        if(hasName) {
          String name =
            ( tokenizer.hasMoreTokens() ? tokenizer.nextToken() : "" );
          if( name.equals( "\t" ) ) {
            name = "";
          } else if( tokenizer.hasMoreTokens() ) {
            // skip delimiter
            tokenizer.nextToken();
          }
          // TODO: REMOVE
          //System.err.println( "name: \"" + name + "\"" );
          rowEisenClusterLeaves[ i ].name = name;
        }
        String rowWeight =
          ( tokenizer.hasMoreTokens() ? tokenizer.nextToken() : "1.0" );
        // skip delimiter
        if( rowWeight.equals( "\t" ) ) {
          rowWeight = "1.0";
        } else if( tokenizer.hasMoreTokens() ) {
          tokenizer.nextToken();
        }
        // TODO: REMOVE
        //System.err.println( "GWEIGHT: \"" + rowWeight + "\"" );

        rowWeights[ i ] = Double.parseDouble( rowWeight );
        j = 0;
        boolean expecting_tab = false;
        String token;
        while(tokenizer.hasMoreTokens()) {
          token = tokenizer.nextToken();
          if( token.equals( "\t" ) ) {
            if( !expecting_tab ) {
              inputMatrix[i][j++] = Double.NaN;
            }
            expecting_tab = false;
          } else {
            if( token.equals( "" ) ) {
              inputMatrix[i][j++] = Double.NaN;
            } else {
              inputMatrix[i][j++] = Double.parseDouble( token );
            }
            expecting_tab = true;
          }
        } // while hasMoreTokens
        i++;
	    }//while it
    } catch( Throwable e ) {
	    throw new RuntimeException( "Caught " + e + " at line " + ( input_strings_i + 3 ) + ".", e );
    }
        
    return true;
        
  }//readInputFile

  /**
   * Writes a gtr file for TreeView.
   */
  public void writeGTR ( String gtr_file_path ) {
    writeTreeViewTree( ROWS, gtr_file_path );
  } // writeGTR( String )

  /**
   * Writes an atr file for TreeView.
   */
  public void writeATR ( String atr_file_path ) {
    writeTreeViewTree( COLS, atr_file_path );
  } // writeATR( String )

  /**
   * Writes an atr or a gtr file for TreeView, depending on the dimension.
   */
  public void writeTreeViewTree ( int dimension, String file_path ) {
    EisenClusterNode root;
    if( dimension == ROWS ) {
	    root = rowRoot;
    } else if( dimension == COLS ) {
	    root = colRoot;
    } else {
	    throw new IllegalArgumentException( "The dimension argument must be either ROWS or COLS."+
                                          "  The given value, " 
                                          + dimension + ", is not valid." );
    }

    if( root == null ) {
      System.out.println("Can't write GTR/ATR file, did not specify to cluster for rows/columns");
      System.out.flush();
	    return;
    }
    try {
	    File file = new File( file_path );
	    if( file.exists() ) {
        // the file already exists, for now, print a message
        System.err.println( "The file \"" + file_path + "\" already exists. It will be overwritten." );
	    }
	    PrintWriter file_writer =
        new PrintWriter( new FileWriter( file ) );
	    List internal_nodes_list =
        new ArrayList( ( dimension == ROWS ) ? rowCount : colCount );
	    Enumeration depth_first_enum = root.depthFirstEnumeration();
	    EisenClusterNode node;
	    while( depth_first_enum.hasMoreElements() ) {
        node = ( EisenClusterNode )depth_first_enum.nextElement();
        if( ( node.isLeaf() && ( node.getParent() != root ) ) ||
            ( node == root ) ) {
          continue;
        }
        internal_nodes_list.add( node );
	    }
	    EisenClusterNode[] internal_node_array =
        ( EisenClusterNode[] )internal_nodes_list.toArray(
                                                          new EisenClusterNode[ 0 ]
                                                          );
	    // Sort them.
	    Arrays.sort(
                  internal_node_array,
                  ClusterNode.ASCENDING_DISTANCE_BETWEEN_CHILDREN_COMPARATOR
                  );
	    for( int node_i = 0; node_i < internal_node_array.length; node_i++ ) {
        file_writer.println(
                            internal_node_array[ node_i ].treeViewTreeString( dimension )
                            );
	    }
	    file_writer.flush();
	    file_writer.close();
    } catch( IOException e ) {
	    // TODO: Error handling
	    System.err.println( "WARNING: Caught " + e + " while attempting to write to file \"" + 
                          file_path + "\"." );
    }
  } // writeTreeViewTree( String )
    
  /**
   * Writes a cdt file for TreeView.
   */
  public void writeCDT ( String cdt_file_path ) {
    if( inputMatrix == null ) {
	    // Can't do it.
      System.out.println("Input matrix == null, can't write cdt");
      System.out.flush();
	    return;
    }
    EisenClusterNode[] row_leaves = getFinalRowOrdering();
    EisenClusterNode[] col_leaves = getFinalColumnOrdering();

    try {
	    File cdt_file = new File( cdt_file_path );
	    if( cdt_file.exists() ) {
        // the file already exists, for now, print a message
        System.err.println( "WARNING: The file \"" + cdt_file_path + 
                            "\" already exists. It will be overwritten." );
	    }
	    PrintWriter file_writer =
        new PrintWriter( new FileWriter( cdt_file ) );

	    // header line
	    file_writer.print( "GID\tYORF\tNAME\tGWEIGHT" );
	    for( int col_i = 0; col_i < col_leaves.length; col_i++ ) {
        file_writer.print( '\t' + col_leaves[ col_i ].toString() );
	    }
	    file_writer.println();

	    // array id line
	    if( ( clusterDimensions == ROWS_COLS ) ||
          ( clusterDimensions == COLS ) ) {
        // In this case, the second line has to be an AID (array id) row.
        file_writer.print( "AID\t\t" );
        for( int col_i = 0; col_i < col_leaves.length; col_i++ ) {
          file_writer.print( "\tARRY" + col_leaves[ col_i ].index + "X" );
        }
        file_writer.println();
	    }

	    // TODO: Don't we also want an EWEIGHT line?

	    // data lines
	    for( int row_i = 0; row_i < row_leaves.length; row_i++ ) {
        // gene id UID NAME WEIGHT
        file_writer.print(
                          "GENE" + row_leaves[ row_i ].index + "X\t" +
                          row_leaves[ row_i ].uid + '\t' +
                          row_leaves[ row_i ].name + '\t' +
                          ( ( rowWeights == null ) ?
                            "" :
                            String.valueOf( rowWeights[ row_i ] )
                            )
                          );
        for( int col_i = 0; col_i < col_leaves.length; col_i++) {
          file_writer.print(
                            '\t' +
                            (
                             ( Double.isNaN(
                                            inputMatrix[
                                                        row_leaves[ row_i ].index
                                            ][
                                              col_leaves[ col_i ].index
                                            ]
                                            ) 
                               ||
                               inputMatrix[row_leaves[row_i].index][col_leaves[col_i].index] >= Double.MAX_VALUE
                               )
                             ? "10000" :
                             String.valueOf(
                                            inputMatrix[
                                                        row_leaves[ row_i ].index
                                            ][
                                              col_leaves[ col_i ].index
                                            ]
                                            )
                             )
                            );
        }
        file_writer.println();
	    }

	    // done.
	    file_writer.flush();
	    file_writer.close();
    } catch( IOException e ) {
	    // TODO: Error handling
	    System.err.println( "WARNING: Caught " + e + " while attempting to write to file \"" 
                          + cdt_file_path + "\"." );
    }
  } // writeCDT( String )

   
   /**
    * Writes a cdt file for TreeView.
    * If the user provided a correlations matrix instead of an input matrix
    * when creating this instance of EisenClustering, then he can provide the
    * input matrix in this version of writeCDT().
    */
  public void writeCDT ( String cdt_file_path, double [][] input_vals ) {
    this.inputMatrix = input_vals;
    writeCDT(cdt_file_path);
    this.inputMatrix = null; // restore state
  }//writeCDT
  
  /**
   * Writes the files for viewing the clustering in TreeView.
   */
  public void writeTreeViewFiles(String cdtFilePathName, String gtrFilePathName, String atrFilePathName) {
    if(cdtFilePathName != null) {
	    writeCDT(cdtFilePathName); // the clustered input matrix
    }
    if(gtrFilePathName != null) {
	    writeGTR(gtrFilePathName); // the tree file
    }
    if(atrFilePathName != null) {
	    writeATR(atrFilePathName);
    }
  }//writeTreeViewFiles

  /**
   * @return true iff the given dimensions key is equal to ROWS, ROWS_COLS, or ROWS_COLS_AS_ROWS
   */
  public static boolean isRowDimensions ( int dimensions ) {
    switch( dimensions ) {
    case ROWS:
    case ROWS_COLS:
    case ROWS_COLS_AS_ROWS:
	    return true;
    default:
	    return false;
    }
  } // static isRowDimensions( int )

  /**
   * @return true iff the given dimensions key is equal to COLS, ROWS_COLS, or ROWS_COLS_AS_ROWS
   */
  public static boolean isColumnDimensions ( int dimensions ) {
    switch( dimensions ) {
    case COLS:
    case ROWS_COLS:
    case ROWS_COLS_AS_ROWS:
	    return true;
    default:
	    return false;
    }
  } // static isColumnDimensions( int )

  protected EisenClusterNode createClusterNode (
                                                Object node_peer,
                                                int index,
                                                int inputIndex
                                                ) {
    if( node_peer == null ) {
	    node_peer = String.valueOf( index );
    }
    return new EisenClusterNode( node_peer, index, inputIndex );
  }
  protected EisenClusterNode createClusterLeaf (
                                                Object node_peer,
                                                int index,
                                                int inputIndex
                                                ) {
    if( node_peer == null ) {
	    node_peer = String.valueOf( index );
    }
    return new EisenClusterNode( node_peer, index, inputIndex, true );
  }
  // ------------------------------- Inner Classes ----------------------------------------------- //
  public class EisenClusterNode
    extends ClusterNode {

    public String uid = "";   // its unique id (for example ORF)
    public String name = "";  // its name (for example common name and description of 
                              // function in the cell)
    // There are two kinds of indeces for an EisenClusterNode:
    public int index = -1;    // identifies the position of the node in the tree, -1 for root
    public int inputIndex;    // identifies index for this node in the inputVals array,
                              // useful for centroid method. 
                              // If not using centroid method, it is the same as index.
    public double [] inputVals; // the input values for this node (from the input matrix)
    public boolean isCluster; //whether or not this node is a cluster (its leaves are 
                              // members of a cluster rooted at this node)
                              // false by default
    // A Map of EisenClusterNode -> boolean entries. The EisenClusterNode keys
    // are all of the other nodes in the root-parent of this node. The boolean
    // is true if the joining condition between this EisenClusterNode and the key
    // is true, false otherwise.
    public transient Map joinConditionMap;
    
    public EisenClusterNode ( Object node_peer, int index, int inputIndex ) {
	    super( node_peer );
	    this.index = index;
      this.inputIndex = inputIndex;
      this.isCluster = false;
      this.joinConditionMap = new HashMap();
    }

    public EisenClusterNode ( Object node_peer, int index, int inputIndex, boolean is_leaf ) {
	    super( node_peer, is_leaf );
	    this.index = index;
      this.inputIndex = inputIndex;
      this.isCluster = false;
      this.joinConditionMap = new HashMap();
    }
       
    public String toString () {
	    return ( uid.equals( "" ) ? super.toString() : uid );//+ "@" + getDistanceBetweenChildren();
    }

    public String toTreeString () {
	    if( isLeaf() ) {
        return toString();
	    } else {
        StringBuffer string_buffer = 
          new StringBuffer( uid.equals( "" ) ? super.toString() : uid );
        string_buffer.append( "{ " );
        string_buffer.append(
                             ( ( EisenClusterNode )getChildAt( 0 ) ).toTreeString() );
        int child_count = getChildCount();
        for( int child_i = 1; child_i < child_count; child_i++ ) {
          string_buffer.append( ", " );
          string_buffer.append(
                               ( ( EisenClusterNode )getChildAt( child_i ) ).toTreeString()
                               );
        }
        string_buffer.append( " }" );
        return string_buffer.toString();
	    }
    } // toTreeString()

    public String paramStringHeader () {
	    return "Index\tName\tChildren";
    } // paramStringHeader()

    /**
     * Return a tab-delineated String in the same order given by {@link
     * #paramStringHeader()}.
     */
    public String paramString () {
	    StringBuffer string_buffer = new StringBuffer();
	    string_buffer.append( index );
	    string_buffer.append( '\t' );
	    string_buffer.append( toString() );
	    if( isLeaf() ) {
        return string_buffer.toString();
	    }
	    int child_count = getChildCount();
	    for( int child_i = 0; child_i < child_count; child_i++ ) {
        string_buffer.append( '\t' );
        string_buffer.append(
                             getChildAt( child_i ).toString()
                             );
	    }
	    string_buffer.append( '\t' );
	    return string_buffer.toString();
    } // paramString()

    /**
     * Return a tab-delineated String for use in a TreeView ATR or GTR file.
     */
    public String treeViewTreeString ( int dimension ) {
	    if( isLeaf() ) {
        return null;
	    }
	    StringBuffer string_buffer = new StringBuffer();
	    string_buffer.append( toString() );
	    string_buffer.append( '\t' );
	    if( getChildAt( 0 ).isLeaf() ) {
        if( dimension == ROWS ) {
          string_buffer.append( "GENE" );
        } else {
          string_buffer.append( "ARRY" );
        }
        string_buffer.append( ( ( EisenClusterNode )getChildAt( 0 ) ).index );
        string_buffer.append( "X\t" );
	    } else {
        string_buffer.append( getChildAt( 0 ).toString() );
        string_buffer.append( '\t' );
	    }
	    if( getChildAt( 1 ).isLeaf() ) {
        if( dimension == ROWS ) {
          string_buffer.append( "GENE" );
        } else {
          string_buffer.append( "ARRY" );
        }
        string_buffer.append( ( ( EisenClusterNode )getChildAt( 1 ) ).index );
        string_buffer.append( "X" );
	    } else {
        string_buffer.append( getChildAt( 1 ).toString() );
	    }
	    return string_buffer.toString();
    } // treeViewTreeString( int )

    /**
     * @return ( this == other_object )
     */
    public boolean equals ( Object other_object ) {
	    return ( this == other_object );
    } // equals( Object )

    //private void writeObject (ObjectOutputStream out) throws Exception{
    //System.out.println("EC: Writing " + this.toString() + "...");
    //out.defaultWriteObject();
    //}

  } // inner class EisenClusterNode

  protected NodePair createNodePair (
                                     EisenClusterNode node_one,
                                     EisenClusterNode node_two,
                                     double correlation
                                     ) {
    return new NodePair( node_one, node_two, correlation );
  } // createNodePair(..)
  protected static class NodePair implements Serializable{

    public EisenClusterNode nodeOne;
    public EisenClusterNode nodeTwo;
    public double correlation;

    public NodePair (
                     EisenClusterNode node_one,
                     EisenClusterNode node_two,
                     double correlation
                     ) {
      if( node_one == null ) {
        throw new IllegalArgumentException( "node_one must not be null." );
      }
      if( node_two == null ) {
        throw new IllegalArgumentException( "node_two must not be null." );
      }
	    nodeOne = node_one;
	    nodeTwo = node_two;
	    this.correlation = correlation;
    }
        
    public String toString () {
	    return "{" + ( ( nodeOne == null ) ? "null" : nodeOne.toString() ) + "," +
        ( ( nodeTwo == null ) ? "null" : nodeTwo.toString() ) + "}@" + correlation;
    } // toString()

    public boolean equals ( Object obj ) {
	    return ( this == obj );
    }// equals( Object )

  } // static inner class NodePair

    /**
     * Compare NodePairs on descending correlation values.  Note that the result
     * will be 0 if the NodePairs have the same correlation values.
     */
  protected static class DescendingNodePairCorrelationComparator
    implements Comparator {
    public int compare ( Object object1, Object object2 ) {
	    if( object1 == object2 ) {
        return 0;
	    }
	    double difference =
        ( ( ( NodePair )object1 ).correlation -
          ( ( NodePair )object2 ).correlation );
	    if( difference < 0.0 ) {
        return 1; // Descending!
	    } else if( difference > 0.0 ) {
        return -1; // Descending!
	    } else {
        return 0;
	    }
    } // compare( Object, Object )
  } // static inner class DescendingNodePairCorrelationComparator

    /**
     * This comparator is guaranteed to return 0 only if the objects are ==
     * (assuming that no two NodePairs being compared have the same nodes
     * within).
     */
  protected static class AscendingNodePairCorrelationComparator
    implements Comparator, Serializable {
    public int compare ( Object object1, Object object2 ) {
	    if( object1 == object2 ) {
        return 0;
	    }
	    double difference =
        ( ( ( NodePair )object1 ).correlation -
          ( ( NodePair )object2 ).correlation );
	    if( difference < 0.0 ) {
        return -1;
	    } else if( difference > 0.0 ) {
        return 1;
	    } else {
        // The similarities are the same.
        // Since they are not the same object, we can't return 0.
        if( ( ( NodePair )object1 ).nodeOne.isLeaf() ) {
          if( ( ( NodePair )object2 ).nodeOne.isLeaf() ) {
            // resort to index order if they're both leaves.
            difference = (
                          ( ( NodePair )object1 ).nodeOne.index -
                          ( ( NodePair )object2 ).nodeOne.index
                         );
            if( difference == 0 ) {
              return (
                      ( ( NodePair )object1 ).nodeTwo.index -
                      ( ( NodePair )object2 ).nodeTwo.index
                     );
            } else {
              return (int)difference;
            }
          } else {
            // leaves are "less than" internal nodes with identical
            // correlation.
            return -1;
          }
        } else {
          if( ( ( NodePair )object2 ).nodeOne.isLeaf() ) {
            // leaves are "less than" internal nodes with identical
            // correlation.
            return 1;
          } else {
            // resort to index order if they're both internal nodes.
            difference = (
                          ( ( NodePair )object1 ).nodeOne.index -
                          ( ( NodePair )object2 ).nodeOne.index
                         );
            if( difference == 0 ) {
              return (
                      ( ( NodePair )object1 ).nodeTwo.index -
                      ( ( NodePair )object2 ).nodeTwo.index
                     );
            } else {
              return (int)difference;
            }
          }
        }
	    }
    } // compare( Object, Object )
  } // static inner class AscendingNodePairCorrelationComparator

  public interface JoiningCondition extends Serializable{
    /**
     * @return false iff the given nodes must never be joined.
     */
    public boolean join ( EisenClusterNode node1, EisenClusterNode node2 );
  } // inner interface JoiningCondition

  public interface ClusterCondition extends Serializable{
    /**
     * @return false iff the given node is not a root for a cluster.
     */
    public boolean isCluster(EisenClusterNode root);
  }//inner interface ClusterCondition

  // ------------ TEMPORARY METHODS -------------//
  public static Iterator iterator ( final Object array ) {
    if( array == null ) {
	    throw new IllegalArgumentException( "The ( Object ) array argument may not be null." );
    }
    if( !array.getClass().isArray() ) {
	    throw new IllegalArgumentException( "The given Object is not an array." );
    }
    return new Iterator () {
        private int index = 0;
        private int length = Array.getLength( array );

        public boolean hasNext () {
          return ( index < length );
        } // hasNext()
        public Object next () {
          if( !hasNext() ) {
            throw new NoSuchElementException();
          }
          return Array.get( array, index++ );
        } // next()
        public void remove () {
          throw new UnsupportedOperationException();
        }
	    };
  } // static iterator(..)
    
    /**
     * Add all elements in the given iterator to the given List and then return
     * that List.
     */
  public static List addAll ( List target, Iterator source ) {
    while( source.hasNext() ) {
	    target.add( source.next() );
    }
    return target;
  } // static addAll(..)
  
} // class EisenClustering
