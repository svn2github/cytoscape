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
 * @author Iliana Avila-Campillo iavila@systemsbiology.org
 * @author Paul Edlefsen pedlefsen@systemsbiology.org
 * @version %I%, %G%
 */
package common.algorithms.hierarchicalClustering;

import java.lang.String;
import java.lang.Integer;
import java.lang.Double;
import java.lang.Math;
import java.util.Arrays;
import java.awt.Component;
import giny.util.MonitorableTask;
import giny.util.SwingWorker;

/**
 * This class calculates correlations between vectors.
 */

public class VectorCorrelationCalculator implements MonitorableTask {
  
  /**
   * Values of correlation that use a similarity metrics usually
   * are 1 for perfect correlation, 0 for no correlation, and -1 for
   * perfect inverse correlation
   */
  public static final int SIMILARITY_METRIC = 0;
  
  /**
   * Values of correlation that use distance metrics do not usually
   * have limits. Usually, the smaller the value, the greater the 
   * correlation, and the bigger the value, the smaller the correlation
   */
  public static final int DISTANCE_METRIC = 1;

  /**
   * Similarity metric that calculates the dot product between two vecotors.
   */
  static final public int DOT_PRODUCT = 0;

  /**
   * Similarity metric that calculated the cosine of the angle betwen two vectors.
   */
  static final public int COS = 1;

  /**
   * Similarity metric that calculates the inverse of the Euclidean Distance 
   * between two vectors.
   */
  static final public int EUC = 2;

  /**
   * Distance metric that calculates the Manhattan Distance between two vectors.
   */
  static final public int MANHATTAN = 3;

  /**
   * Value stored in the correlations matrix if the correlation
   * between two rows was not calculated (if a filter matrix is used).
   */
  static final public double NOT_CALCULATED = Double.NaN;

  protected double[][] inputMatrix;
  protected double[] weights;
  protected double [][] correlations; 
  protected double maxCorr;
  protected double nonSelfMaxCorr;
  protected double minCorr;
  
  protected int corrMetric;
  protected boolean columnPairs;
  protected boolean centered;
  protected boolean absolute;
  
  /**
   * A matrix of booleans that determine whether the correlation between two rows
   * or columns in the input matrix should be computed, if not given by the user, 
   * all correlations are computed
   */
  protected boolean [][] filterMatrix; 

  // For monitoring:
  protected int lengthOfTask;
  protected int currentProgress;
  protected boolean done, canceled;
  protected String statMessage; 
  protected String taskName;
  
  /**
   * Constructor.
   *
   * @param input_matrix the 2D array of <code>double</code>s that contain the vectors
   * for which correlations will be calculated
   * @param correlation_metric the correlation metric to be used to calculate correlations
   * @param column_pairs if true, then the columns in input_matrix will be the vectors for
   * which correlations will be calculated, if false, then the correlations between the rows
   * will be calculated
   */
  public VectorCorrelationCalculator (
                                      double[][] input_matrix,
                                      int correlation_metric,
                                      boolean column_pairs
                                      ) {
    this(
         input_matrix,
         null,
         correlation_metric,
         column_pairs,
         false,
         false
         );
  }//VectorCorrelationCalculator()
  
  /**
   * Constructor.
   *
   * @param input_matrix the 2D array of <code>int</code>s that contain the vectors
   * for which correlations will be calculated
   * @param correlation_metric the correlation metric to be used to calculate correlations
   * @param column_pairs if true, then the columns in input_matrix will be the vectors for
   * which correlations will be calculated, if false, then the correlations between the rows
   * will be calculated
   */
  public VectorCorrelationCalculator (
                                      int[][] input_matrix,
                                      int correlation_metric,
                                      boolean column_pairs
                                      ) {
    if( input_matrix == null ) {
      throw new IllegalArgumentException( "The input_matrix argument must not be null." );
    }
    int numRows = input_matrix.length;
    int numCols = input_matrix[0].length;
    inputMatrix = new double [numRows][numCols];
    int j;
    for(int i = 0; i < numRows; i++){
      for(j = 0; j < numCols; j++){ 
        if(input_matrix[i][j] == Integer.MAX_VALUE){
          inputMatrix[i][j] = Double.MAX_VALUE;
        }else{
          inputMatrix[i][j] = (double)input_matrix[i][j];
        }
      }// for j
    }// for i 
    
    if( column_pairs ) {
      this.weights = new double[numCols];
    } else {
      this.weights = new double[numRows];
    }
    Arrays.fill( this.weights, 1.0 );
   
    corrMetric = correlation_metric;
    if(getCategory(corrMetric) == DISTANCE_METRIC){
      switch(corrMetric){
      case MANHATTAN:
        taskName = "Calculating Manhattan Distances...";
        break;
      default:
        taskName = "Calculating Unknown Distances...";
      }
    }else{
      taskName = "Calculating Similarities...";
    }
    columnPairs = column_pairs;
    this.centered = false;
    this.absolute = false;
    initializeVectorCorrelationCalculator();
  }//VectorCorrelationCalculator()

  /**
   * Constructor.
   * 
   * @param input_matrix the input matrix that contains the vectors for which
   * correlations will be calculated
   * @param weights the scalar for matrix values from each column (or row).
   * @param correlation_metric the correlation metric to use.
   * @param column_pairs true iff the calculator should calculate the
   * correlations between column pairs instead of row pairs; if this is true
   * then weights should be of rows.
   * @param centered true iff the correlation values should be renormalized
   * about the local mean; the meaning of this value is metric-dependent.
   * @param absolute true iff negative correlations should be made positive.
   */
  public VectorCorrelationCalculator (
                                      double[][] input_matrix,
                                      double[] weights,
                                      int correlation_metric,
                                      boolean column_pairs,
                                      boolean centered,
                                      boolean absolute) {
    if( input_matrix == null ) {
      throw new IllegalArgumentException( "The input_matrix argument must not be null." );
    }
    inputMatrix = input_matrix;
    if( weights == null ) {
      if( column_pairs ) {
        weights = new double[ input_matrix[ 0 ].length ];
      } else {
        weights = new double[ input_matrix.length ];
      }
      Arrays.fill( weights, 1.0 );
    }
    this.weights = weights;
    corrMetric = correlation_metric;
    if(getCategory(corrMetric) == DISTANCE_METRIC){
      taskName = "Calculating Distances...";
    }else{
      taskName = "Calculating Similarities...";
    }
    columnPairs = column_pairs;
    this.centered = centered;
    this.absolute = absolute;
    initializeVectorCorrelationCalculator();
  }//VectorCorrelationCalculator()
  
  /**
   * @return the correlations matrix.
   */
  public double [][] getCorrelations () {return this.correlations;}//getCorrelations
  
  /**
   * Returns the maximum calculated correlation, or Double.NaN if correlations
   * have not been calculated. The returned value is not infinity (Double.MAX_VALUE)
   * since in most cases that case is not informative.
   */
  public double getMaxCorrelation (){return this.maxCorr;}//getMaxSimilarity
  
  /**
   * Returns the maximum calculated correlation that is not the result of
   * comparing a thing to itself. The returned value is not infinity (Double.MAX_VALUE)
   * since in most cases that case is not informative.
   */
  public double getNonSelfMaxCorrelation (){return this.nonSelfMaxCorr;}//getNonSelfMaxSimilarity

  /**
   * Returns the minimum calculated correlation, or Double.NaN if correlations have
   * not been calculated.
   */
  public double getMinCorrelation () {return minCorr;}//getMinSimilarity
  
  /**
   * Called by the constructor. This implementation
   * sets the monitoring variables to their initial values.
   */
  protected void initializeVectorCorrelationCalculator () {
    if(inputMatrix != null){
      int correlation_length =
        ( columnPairs ? inputMatrix[ 0 ].length : inputMatrix.length );
      this.lengthOfTask = 
        (correlation_length*correlation_length) - ( (correlation_length - 1) * correlation_length) / 2;
    }else{
      this.lengthOfTask = 0;
    }
    this.currentProgress = 0;
    this.done = false;
    this.canceled = false;
    this.statMessage = "Completed 0%";
    this.minCorr = Double.NaN;
    this.maxCorr = Double.NaN;
    this.nonSelfMaxCorr = Double.NaN;
  }//initializeVectorCorrelationCalculator()

  /**
   * @return <code>true</code> if the task is done, false otherwise
   */
  public boolean isDone(){
    return this.done;
  }//isDone

  /**
   * @return the current progress
   */
  public int getCurrentProgress(){
    return this.currentProgress;
  }//getCurrentProgress

  /**
   * @return the total length of the task
   */
  public int getLengthOfTask(){
    return this.lengthOfTask;
  }//getLengthOfTask

  /**
   * @return a <code>String</code> describing the task being performed
   */
  public String getTaskDescription(){
    return this.taskName;
  }//getTaskDescription

  /**
   * @return a <code>String</code> status message describing what the task
   * is currently doing (example: "Completed 23% of total.", "Initializing...", etc).
   */
  public String getCurrentStatusMessage (){
    return this.statMessage;
  }//getCurrentStatusMessage
  
  /**
   * Stops the task if it is currently running.
   */
  public void stop(){
    this.canceled = true;
    this.statMessage = null;
  }//stop
  
  /**
   * @return <code>true</code> if the task was canceled before it was done
   * (for example, by calling <code>MonitorableSwingWorker.stop()</code>, 
   * <code>false</code> otherwise
   */
  // TODO: Not sure if needed
  public boolean wasCanceled (){
    return this.canceled;
  }//wasCanceled
  
	/**
   * It calculates the correlations and pops-up a CytoscapeProgressMonitor while it does it.
   * @param parent_component the parent component for the CytoscapeProgressMonitor
   */
 //  public void calculateSimsAndMonitor(Component parent_component){
//     this.done = false;
//     this.canceled = false;
//     this.statMessage = "Completed 0%";
//     this.minCorr = Double.NaN;
//     this.maxCorr = Double.NaN;
//     this.nonSelfMaxCorr = Double.NaN;
//     this.currentProgress = 0;
//     CytoscapeProgressMonitor monitor = new CytoscapeProgressMonitor(this,parent_component);
//     monitor.startMonitor(true);
//   }//calculateSimsAndMonitor
  
  /**
   * Calculates the correlations and returns them.
   *
   * @return the double[][] array of correlations or null if the task was canceled or
   * an error was encountered
   */
  public double[][] calculate () {
    
    initializeVectorCorrelationCalculator();
    int correlation_length = ( columnPairs ? inputMatrix[ 0 ].length : inputMatrix.length ); 
    this.correlations =
      new double[ correlation_length ][ correlation_length ];
    
    double correlation;
    double percent;
    for( int x = 0; x < correlation_length; x++ ) {
      if(this.canceled){break;}
      for( int y = x; y < correlation_length; y++ ){
        if(filterMatrix != null &&
           filterMatrix[x][y] == false){
          correlations[ x ][ y ] = NOT_CALCULATED;
          correlations[ y ][ x ] = NOT_CALCULATED;
          
          this.currentProgress++;
          percent = (this.currentProgress * 100 )/this.lengthOfTask;
          this.statMessage = "Completed " + percent + "%";
          continue;
        }
        correlation = 1.0;
        switch( corrMetric ) {
        case COS:
          if(x != y){
            correlation =
              calculateCosineCorrelation(
                                        inputMatrix,
                                        inputMatrix,
                                        weights,
                                        x,
                                        y,
                                        columnPairs,
                                        centered,
                                        absolute,
                                        null
                                        );
          }
          break;
        case DOT_PRODUCT:
          correlation =
            calculateDotProductCorrelation(
                                          inputMatrix,
                                          inputMatrix,
                                          weights,
                                          x,
                                          y,
                                          columnPairs,
                                          centered,
                                          absolute,
                                          null
                                          );
          break;
        case EUC:
          correlation =
            calculateEuclideanCorrelation(
                                         inputMatrix,
                                         inputMatrix,
                                         weights,
                                         x,
                                         y,
                                         columnPairs,
                                         centered,
                                         absolute,
                                         null
                                         );
          break;
        case MANHATTAN:
          correlation =
            calculateManhattanDistance(
                                       inputMatrix,
                                       inputMatrix,
                                       weights,
                                       x,
                                       y,
                                       columnPairs,
                                       centered,
                                       absolute,
                                       null
                                       );
          break;
        default:
          throw new IllegalArgumentException( "Invalid correlation metric: " + corrMetric + 
                                              ".  It should be COS, DOT_PRODUCT, or EUC." );
          
        }
        correlations[ x ][ y ] = correlation;
        correlations[ y ][ x ] = correlation;
        if(minCorr > correlation){
          minCorr = correlation;
        }
        // maxCorr is always the maximum similarity (or distance) between two nodes
        // that are connected
        if(correlation < Double.MAX_VALUE && maxCorr < correlation){
          maxCorr = correlation;
        }
        if(x != y && correlation < Double.MAX_VALUE && nonSelfMaxCorr < correlation){
          nonSelfMaxCorr = correlation;
        }
        if(this.canceled){
          this.correlations = null;
          this.minCorr = Double.NaN;
          this.maxCorr = Double.NaN;
          this.nonSelfMaxCorr = Double.NaN;
          break;
        }else{
          this.currentProgress++;
          percent = (this.currentProgress * 100 )/this.lengthOfTask;
          this.statMessage = "Completed " + percent + "%";
        }
        //TODO: Remove
        //System.out.println("MD[" + x + "][" + y + "]=" + correlation);
      }
    }
    
    if(!this.canceled){
      this.done = true;
    }
    //System.out.println("...Done calculating sims/dists between vectors.");
    return correlations;
  } //calculate()
  
  /**
   * Calculate the cosine of the angle between two rows or columns in the input
   * matrix, weighted by the scalars in the given weights vector.  This
   * calculation is also known as the Pearson Correlation.
   * 
   * @param x_input_matrix the input matrix that x is an index into; it must
   * have the same length as y_input_matrix in the minor dimension (or the
   * major dimension, if column_pairs is true).
   * @param y_input_matrix the input matrix that y is an index into; it must
   * have the same length as x_input_matrix in the minor dimension (or the
   * major dimension, if column_pairs is true).
   * @param weights the scalar for matrix values from each column (or row)
   * @param x the index of the first row or column in input_matrix
   * @param y the index of the second row or column in input_matrix
   * @param column_pairs true iff the calculator should calculate the
   * correlations between column pairs instead of row pairs; if this is true
   * then weights should be of rows.
   * @param centered true iff the correlation values should be renormalized
   * about the local mean.
   * @param absolute true iff negative correlations should be made positive.
   * @return the cosine correlation ( 0 - 1 ) between the two vectors */
  // TODO: Modify this to handle enumerated types
  protected static double calculateCosineCorrelation (
                                                     double[][] x_input_matrix,
                                                     double[][] y_input_matrix,
                                                     double[] weights,
                                                     int x,
                                                     int y,
                                                     boolean column_pairs,
                                                     boolean centered,
                                                     boolean absolute,
                                                     boolean [] filter
                                                     ) {
    double x_sum = 0.0;
    double y_sum = 0.0;
    double dot_product = 0.0;
    double x_dot_product = 0.0;
    double y_dot_product = 0.0;
    double count = 0.0;

    // z is perpendicular to x & y.
    int length =
      ( column_pairs ? x_input_matrix.length : x_input_matrix[ x ].length );
    

    for( int z = 0; z < length; z++ ) {
      if(filter != null && !filter[z]){
        continue;
      }
      if( weights[ z ] == 0.0 ) {
        continue;
      }
      
      if( column_pairs ) {
        if( Double.isNaN( x_input_matrix[ z ][ x ] ) ||
            Double.isNaN( y_input_matrix[ z ][ y ] ) ) {
          continue;
        }
        x_sum += ( weights[ z ] * x_input_matrix[ z ][ x ] );
        y_sum += ( weights[ z ] * y_input_matrix[ z ][ y ] );
        dot_product +=
          ( weights[ z ] * x_input_matrix[ z ][ x ] * y_input_matrix[ z ][ y ] );
        x_dot_product +=
          ( weights[ z ] * Math.pow( x_input_matrix[ z ][ x ], 2 ) );
        y_dot_product +=
          ( weights[ z ] * Math.pow( y_input_matrix[ z ][ y ], 2 ) );
        count += weights[ z ];
      } else {
        // not column_pairs
        if( Double.isNaN( x_input_matrix[ x ][ z ] ) ||
            Double.isNaN( y_input_matrix[ y ][ z ] ) ) {
          continue;
        }
        
        x_sum += ( weights[ z ] * x_input_matrix[ x ][ z ] );
        y_sum += ( weights[ z ] * y_input_matrix[ y ][ z ] );
        dot_product +=
          ( weights[ z ] * x_input_matrix[ x ][ z ] * y_input_matrix[ y ][ z ] );
        x_dot_product +=
          ( weights[ z ] * Math.pow( x_input_matrix[ x ][ z ], 2 ) );
        y_dot_product +=
          ( weights[ z ] * Math.pow( y_input_matrix[ y ][ z ], 2 ) );
        count += weights[ z ];
      }
    }

    if( count == 0.0 ) {
      return 0.0;
    }

    double correlation = 0.0;
    if( centered ) {
      double x_mean = x_sum / count;
      double y_mean = y_sum / count;
      double norm =
        Math.sqrt(
                  ( x_dot_product -
                    ( 2 * x_mean * x_sum ) +
                    ( count * Math.pow( x_mean, 2 ) )
                    ) *
                  ( y_dot_product -
                    ( 2 * y_mean * y_sum ) +
                    ( count * Math.pow( y_mean, 2 ) )
                    )
                  );
      if( norm > 0 ) {
        correlation =
          ( ( dot_product -
              ( x_sum * y_mean ) -
              ( y_sum * x_mean ) +
              ( count * x_mean * y_mean )
              ) /
            norm
            );
      }
    } else { // not centered
      double norm = Math.sqrt( x_dot_product * y_dot_product );
      if( norm > 0 ) {
        correlation = ( dot_product / norm );
      }
    }
    if( absolute && ( correlation < 0.0 ) ) {
      return ( 0.0 - correlation );
    } else {
      return correlation;
    }
  } // static calculateCosineCorrelation(..)

		/**
		 * Calculate the dot product between two rows or columns in the input
		 * matrix.
     * 
     * @param x_input_matrix the input matrix that x is an index into; it must
		 * have the same length as y_input_matrix in the minor dimension (or the
		 * major dimension, if column_pairs is true).
		 * @param y_input_matrix the input matrix that y is an index into; it must
		 * have the same length as x_input_matrix in the minor dimension (or the
		 * major dimension, if column_pairs is true).
		 * @param weights the scalar for matrix values from each column (or row)
		 * @param x the index of the first row or column in input_matrix
		 * @param y the index of the second row or column in input_matrix
		 * @param column_pairs true iff the calculator should calculate the
		 * correlations between column pairs instead of row pairs; if this is true
		 * then weights should be of rows.
		 * @param centered true iff the correlation values should be renormalized
		 * about the local mean.
		 * @param absolute true iff negative correlations should be made positive.
		 * @return the dot product between the two vectors
		 */
		// TODO: Modify this to handle NaN values
		// TODO: Modify this to handle enumerated types
  protected static double calculateDotProductCorrelation (
                                                         double[][] x_input_matrix,
                                                         double[][] y_input_matrix,
                                                         double[] weights,
                                                         int x,
                                                         int y,
                                                         boolean column_pairs,
                                                         boolean centered,
                                                         boolean absolute,
                                                         boolean [] filter
                                                         ) {
    double dot_product = 0.0;

    // TODO: Modify to use weights, centered, & absolute.

    // z is perpendicular to x & y.
    int length =
      ( column_pairs ? x_input_matrix.length : x_input_matrix[ x ].length );
    for( int z = 0; z < length; z++ ) {
      if(filter != null && !filter[z]){continue;}
      if( column_pairs ) {
        dot_product +=
          ( x_input_matrix[ z ][ x ] * y_input_matrix[ z ][ y ] );
      } else {
        dot_product +=
          ( x_input_matrix[ x ][ z ] * y_input_matrix[ y ][ z ] );
      }
    }
    return dot_product;
  } // static calcualteDotProductCorrelation(..)

		/**
		 * Calculate the inverse of the square root between two rows or columns in
		 * the input matrix.
		 *
     * @param x_input_matrix the input matrix that x is an index into; it must
		 * have the same length as y_input_matrix in the minor dimension (or the
		 * major dimension, if column_pairs is true).
		 * @param y_input_matrix the input matrix that y is an index into; it must
		 * have the same length as x_input_matrix in the minor dimension (or the
		 * major dimension, if column_pairs is true).
		 * @param weights the scalar for matrix values from each column (or row)
		 * @param x the index of the first row or column in input_matrix
		 * @param y the index of the second row or column in input_matrix
		 * @param column_pairs true iff the calculator should calculate the
		 * correlations between column pairs instead of row pairs; if this is true
		 * then weights should be of rows.
		 * @param centered true iff the correlation values should be renormalized
		 * about the local mean.
		 * @param absolute true iff negative correlations should be made positive.
     * @param filter whether or not a pair should be considered for the calculation. Null if all pairs
     * are to be considered.
		 * @return the euclidean correlation ( 0 - 1 ) between the two vectors
		 */
		// TODO: Modify this to handle NaN values
		// TODO: Modify this to handle enumerated types
  protected static double calculateEuclideanCorrelation (
                                                        double[][] x_input_matrix,
                                                        double[][] y_input_matrix,
                                                        double[] weights,
                                                        int x,
                                                        int y,
                                                        boolean column_pairs,
                                                        boolean centered,
                                                        boolean absolute,
                                                        boolean [] filter
                                                        ) {
    double distance = 0.0;

    // TODO: Modify to use weights, centered, & absolute.

    int length =
      ( column_pairs ? x_input_matrix.length : x_input_matrix[ x ].length );
    int divLength = length;
    for( int z = 0; z < length; z++ ) {
      if(filter != null && !filter[z]){
        divLength--;
        continue;
      }
      if( column_pairs ) {
        distance +=
          Math.pow( ( x_input_matrix[ z ][ x ] - y_input_matrix[ z ][ y ] ), 2 );
      } else {
        distance +=
          Math.pow( ( x_input_matrix[ x ][ z ] - y_input_matrix[ y ][ z ] ), 2 );
      }
    }
    if( distance == 0 ){
      return 1.0;
    }
    return ( 1.0 / Math.sqrt( distance / divLength ) );
  } // static calculateEuclideanCorrelation(..)

  /**
   * Calculate the manhattan distance between two rows or columns in
   * the input matrix.
   *
   * @param x_input_matrix the input matrix that x is an index into; it must
   * have the same length as y_input_matrix in the minor dimension (or the
   * major dimension, if column_pairs is true).
   * @param y_input_matrix the input matrix that y is an index into; it must
   * have the same length as x_input_matrix in the minor dimension (or the
   * major dimension, if column_pairs is true).
   * @param weights the scalar for matrix values from each column (or row)
   * @param x the index of the first row or column in input_matrix
   * @param y the index of the second row or column in input_matrix
   * @param column_pairs true iff the calculator should calculate the
   * correlations between column pairs instead of row pairs; if this is true
   * then weights should be of rows.
   * @param centered true iff the correlation values should be renormalized
   * about the local mean (ignored for now)
   * @param absolute true iff negative correlations should be made positive (ignored for now)
   * @param filter whether or not a pair should be considered for the calculation, null
   * if all pairs are to be considered.
   * @return the manhattan distance between the two vectors
   */
  // TODO : Handle absolute and centered
  protected static double calculateManhattanDistance(
                                                       double[][] x_input_matrix,
                                                       double[][] y_input_matrix,
                                                       double[] weights,
                                                       int x,
                                                       int y,
                                                       boolean column_pairs,
                                                       boolean centered,
                                                       boolean absolute,
                                                       boolean [] filter
                                                       ){
    int length =
      ( column_pairs ? x_input_matrix.length : x_input_matrix[ x ].length );
    
    double distance = 0;
    if( column_pairs ){
      for(int z = 0; z < length; z++){
        if(filter != null && !filter[z]){continue;}
        if(x_input_matrix[z][x] == Double.MAX_VALUE &&
             y_input_matrix[z][y] != Double.MAX_VALUE){
          distance = Double.MAX_VALUE;
          break;
        }else if(x_input_matrix[z][x] != Double.MAX_VALUE &&
                  y_input_matrix[z][y] == Double.MAX_VALUE){
          distance = Double.MAX_VALUE;
          break;
        }
        distance +=
          Math.abs( x_input_matrix[ z ][ x ] - y_input_matrix[ z ][ y ] );
      }//for i 
    }else{
      for(int z = 0; z < length; z++){
        if(filter != null && !filter[z]){continue;}
        if(x_input_matrix[x][z] == Double.MAX_VALUE &&
           y_input_matrix[y][z] != Double.MAX_VALUE){
          distance = Double.MAX_VALUE;
          break;
        }else if(x_input_matrix[x][z] != Double.MAX_VALUE &&
                 y_input_matrix[y][z] == Double.MAX_VALUE){
          distance = Double.MAX_VALUE;
          break;
        }
        distance +=
          Math.abs( x_input_matrix[ x ][ z ] - y_input_matrix[ y ][ z ] );
      }//for i  
    }
    // TODO: REMOVE
    // if(distance != Double.MAX_VALUE){
    //System.out.println("manhattan dist = " + distance);
    //System.out.flush();
    //}
    
    return distance;
  }//calculateManhattanDistance

		/**
		 * Calculate the correlations between all pairs of rows (or columns) in the
		 * input matrix.  Delegates to calculateCorrelations( double[][], double[],
		 * int, boolean, boolean, boolean ), with false arguments for centered and
		 * absolute.
     *
		 * @param input_matrix the input matrix
		 * @param weights the scalar for matrix values from each column (or row).
		 * @param correlation_metric the correlation metric to use.
		 * @param column_pairs calculate the correlations between column pairs
		 * instead of row pairs.  If this is true then weights should be of rows.
		 * @return the square correlation matrix.
     */
  public static double[][] calculateCorrelations (
                                                  double[][] input_matrix,
                                                  double[] weights,
                                                  int correlation_metric,
                                                  boolean column_pairs,
                                                  boolean [][] filter
                                                  ) {
    return calculateCorrelations(
                                 input_matrix,
                                 weights,
                                 correlation_metric,
                                 column_pairs,
                                 false,
                                 false,
                                 filter
                                 );
  } // static calculateCorrelations( double[][], double[], int, boolean )

		/**
		 * Calculate the correlations between all pairs of rows (or columns) in the
		 * input matrix.
     *
		 * @param input_matrix the input matrix
		 * @param weights the scalar for matrix values from each column (or row).
		 * @param correlation_metric the correlation metric to use.
		 * @param column_pairs true iff the calculator should calculate the
		 * correlations between column pairs instead of row pairs; if this is true
		 * then weights should be of rows.
		 * @param centered true iff the correlation values should be renormalized
		 * about the local mean; the meaning of this value is metric-dependent.
		 * @param absolute true iff negative correlations should be made positive.
     * @param filter the filter to determine which correlations not to calculate
     * if null, all similairities will be calculated
		 * @return the square correlation matrix.
		 */
  public static double[][] calculateCorrelations (
                                                  double[][] input_matrix,
                                                  double[] weights,
                                                  int correlation_metric,
                                                  boolean column_pairs,
                                                  boolean centered,
                                                  boolean absolute,
                                                  boolean [][]filter
                                                  ) {
    final VectorCorrelationCalculator calculator =
      new VectorCorrelationCalculator(
                               input_matrix,
                               weights,
                               correlation_metric,
                               column_pairs,
                               centered,
                               absolute
                               );
    calculator.useFilter(filter);

    SwingWorker worker = new SwingWorker(){
        public Object construct(){
          return calculator.calculate();
        }
      };
    worker.start();
				
    return ( double[][] )worker.get();
  } // static calculateCorrelations( double[][], double[], int, boolean, boolean, boolean )

		/**
		 * Calculate the correlation between a pair of rows (or columns) in the given
		 * input matrices.
     *
		 * @param x_input_matrix the input matrix that x is an index into; it must
		 * have the same length as y_input_matrix in the minor dimension (or the
		 * major dimension, if column_pairs is true).
		 * @param y_input_matrix the input matrix that y is an index into; it must
		 * have the same length as x_input_matrix in the minor dimension (or the
		 * major dimension, if column_pairs is true).
		 * @param weights the scalar for matrix values from each column (or row)
		 * @param x the index of the first row or column in input_matrix
		 * @param y the index of the second row or column in input_matrix
		 * @param correlation_metric the correlation metric to use.
		 * @param column_pairs true iff the calculator should calculate the
		 * correlations between column pairs instead of row pairs; if this is true
		 * then weights should be of rows.
		 * @param centered true iff the correlation values should be renormalized
		 * about the local mean.
		 * @param absolute true iff negative correlations should be made positive.
		 * @return the cosine correlation ( 0 - 1 ) between the two vectors
		 */
  protected static double calculateCorrelation (
                                               double[][] x_input_matrix,
                                               double[][] y_input_matrix,
                                               double[] weights,
                                               int x,
                                               int y,
                                               int correlation_metric,
                                               boolean column_pairs,
                                               boolean centered,
                                               boolean absolute
                                               ) {
    double correlation;
    switch( correlation_metric ) {
    case COS:
      correlation =
        calculateCosineCorrelation(
                                  x_input_matrix,
                                  y_input_matrix,
                                  weights,
                                  x,
                                  y,
                                  column_pairs,
                                  centered,
                                  absolute,
                                  null
                                  );
      break;
    case DOT_PRODUCT:
      correlation =
        calculateDotProductCorrelation(
                                      x_input_matrix,
                                      y_input_matrix,
                                      weights,
                                      x,
                                      y,
                                      column_pairs,
                                      centered,
                                      absolute,
                                      null
                                      );
      break;
    case EUC:
      correlation =
        calculateEuclideanCorrelation(
                                     x_input_matrix,
                                     y_input_matrix,
                                     weights,
                                     x,
                                     y,
                                     column_pairs,
                                     centered,
                                     absolute,
                                     null
                                     );
      break;
    case MANHATTAN:
      correlation =
        calculateManhattanDistance(
                                   x_input_matrix,
                                   y_input_matrix,
                                   weights,
                                   x,
                                   y,
                                   column_pairs,
                                   centered,
                                   absolute,
                                   null
                                   );
      break;
    default:
      throw new IllegalArgumentException( "Invalid correlation metric: " + correlation_metric + 
                                          ".  It should be COS, DOT_PRODUCT, or EUC." );
    }
    return correlation;
  } // static calculateCorrelation(..)

  	/**
		 * Calculate the correlation between a pair of arrays.
		 *
     * @param array1 the first array, must be of the same length as array2
		 * @param array2 the second array, must be of the same length as array1 
		 * @param weights the scalar for values from each column (or row)
     * @param correlation_metric the correlation metric to use.
     * @param centered true iff the correlation values should be renormalized
		 * about the local mean.
		 * @param absolute true iff negative correlations should be made positive.
		 * @return the correlation or distance between the two vectors
		 */
  protected static double calculateCorrelation (    
                                               double[] array1,
                                               double[] array2,
                                               double[] weights,
                                               int correlation_metric,
                                               boolean centered,
                                               boolean absolute,
                                               boolean [] filter
                                               ) {
    double correlation;
    double [][] x_input_matrix = new double[1][];
    x_input_matrix[0] = array1;
    double [][] y_input_matrix = new double [1][];
    y_input_matrix[0] = array2;
    
    switch( correlation_metric ) {
    case COS:
      correlation =
        calculateCosineCorrelation(
                                  x_input_matrix,
                                  y_input_matrix,
                                  weights,
                                  0,
                                  0,
                                  false,
                                  centered,
                                  absolute,
                                  filter
                                  );
      break;
    case DOT_PRODUCT:
      correlation =
        calculateDotProductCorrelation(
                                      x_input_matrix,
                                      y_input_matrix,
                                      weights,
                                      0,
                                      0,
                                      false,
                                      centered,
                                      absolute,
                                      filter
                                      );
      break;
    case EUC:
      correlation =
        calculateEuclideanCorrelation(
                                     x_input_matrix,
                                     y_input_matrix,
                                     weights,
                                     0,
                                     0,
                                     false,
                                     centered,
                                     absolute,
                                     filter
                                     );
      break;
    case MANHATTAN:
      correlation =
        calculateManhattanDistance(
                                   x_input_matrix,
                                   y_input_matrix,
                                   weights,
                                   0,
                                   0,
                                   false,
                                   centered,
                                   absolute,
                                   filter
                                   );
      break;
    default:
      throw new IllegalArgumentException( "Invalid correlation metric: " + correlation_metric + 
                                          ".  It should be COS, DOT_PRODUCT, or EUC." );
    }
    return correlation;
  } // static calculateCorrelation(..)

  /**
   * Given a correlation metric (COS, EUC, etc), it returns
   * SIMILARITY_METRIC, DISTANCE_METRIC, or -1 if the 
   * correlation metric is not known.
   */
  public static int getCategory(int corrMetric){
    switch(corrMetric){
    case COS:
    case DOT_PRODUCT:
    case EUC:
      return SIMILARITY_METRIC;
    case MANHATTAN:
      return DISTANCE_METRIC;
    default:
      System.out.println("Unknown correlation metric " + corrMetric);
      System.out.flush();
      return -1;
    }
  }//getCategory

  /**
   * Call this method before calculating correlations if a filter is to be used.
   * A filter is a simple 2D array of booleans that determine whether or not the 
   * correlation between two rows or columns in the input matrix should be calculated.
   * For example, if filter[0][15] is false, then the correlation between row 0 and row
   * 15 is not calculated and in the correlations matrix, it is set to NOT_CALCULATED.
   */
  public void useFilter (boolean [][] filter){
    this.filterMatrix = filter;
  }//useFilter

  /**
   * Starts doing the task in a separate thread so that the GUI stays responsive
   *
   * @param return_when_done if <code>true</code>, then this method will return only when
   * the task is done, else, it will return immediately after spawning the thread that
   * performs the task
   */
   public void start (boolean return_when_done){
    final SwingWorker worker = new SwingWorker(){
        public Object construct(){
          return VectorCorrelationCalculator.this.calculate();
        }
      };
    worker.start();
    if(return_when_done){
      // Wait for the task to be done
      worker.get();
    }
   }//start
  
} // class VectorCorrelationCalculator
