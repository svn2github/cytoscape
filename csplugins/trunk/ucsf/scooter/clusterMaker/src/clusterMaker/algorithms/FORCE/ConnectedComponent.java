/*
 * Created on 25. September 2007
 * 
 */


package clusterMaker.algorithms.FORCE;

import java.util.Vector;

import cern.colt.function.IntIntDoubleFunction;
import cern.colt.matrix.DoubleFactory2D;
import cern.colt.matrix.DoubleMatrix2D;
import cern.colt.matrix.impl.SparseDoubleMatrix2D;



/**
 * This class describes a connected component of a graph.
 * 
 *
 */
public class ConnectedComponent {

         
        
	/* edges of the graph with weights */
	private DoubleMatrix2D ccEdges;

	/* positions of the vertices; not initialised at instance creation */
	private double[][] ccPositions = null;

	
	/* total number of nodes in the component */
	private int node_no = -1;


        /*total number of dimensions*/
        private int dim;

	public ConnectedComponent(DoubleMatrix2D ccEdges, int dimensions) {

		this.ccEdges = ccEdges;
		this.node_no = ccEdges.rows();
		this.dim = dimensions;
		this.ccPositions = new double[node_no][dimensions];

		

	}


         /**
         * This method fills the positions array and sets it in the ConnectedComponent object.
         * The positions are uniformly distributed on the surface of a hypersphere with as
         * many dimensions as in the entire program.
         */
        public void init_hsphere_layout() {



                for(int n=0;n<node_no;n++){
                        double r = 0.0;

                        for (int d=0;d<dim;d++){
                            /* generate N variables from uniform distribution */

                                        ccPositions[n][d] = uniform(-5.0, 5.0);


                            /* compute Euclidean norm of vector x[] */
                            r = r + ccPositions[n][d]*ccPositions[n][d];
                        }
                        r = Math.sqrt(r);
                        for (int d2=0;d2<dim;d2++){
                                /* scale vector with respect to r */
                                ccPositions[n][d2] = ccPositions[n][d2]/r;
                        }
                }
              
        }


        /**
         * Returns a real number uniformly between a and b.
         * @return Uniformly distributed number between a and b.
         */
    public static double uniform(double a, double b) {
        return a + Math.random() * (b-a);
    }


	/**
	 * This class checks whether two positions are equal. NOTE both input arrays
	 * must be of equal size for this method to work correctly, but in the
	 * context of this program they always will be. This method only takes long
	 * if the positions are equal, as soon as one axis-position is unequal it
	 * returns false.
	 * 
	 * @param pos_a
	 *            First position in a double array.
	 * @param pos_b
	 *            Second position in a double array.
	 * @return boolean if it is equal or not.
	 */
	public boolean isPositionEqual(double[] pos_a, double[] pos_b) {
		// if(pos_a.length != pos_b.length) return false;
		for (int i = 0; i < pos_a.length; i++) {
			if (pos_a[i] != pos_b[i]) {
				return false;
			}
		}
		return true;
	}

	

	/**
	 * Gets the array with the node positions, which is a 2-dimensional double
	 * array where each row i represents the position for node i. The size of
	 * the array is no. of nodes x dimension.
	 */
	public double[][] getCCPositions() {
		return ccPositions;
	}

	/**
	 * Gets the  node position of node i. This will be returned as double array where the size is the dimension
	 * @param i Node i
	 * @return double array position
	 */
	public double[] getCCPostions(int i){
		return ccPositions[i];
	}
	
	/**
	 * Sets the array with the node positions, which is a 2-dimensional double
	 * array where each row i represents the position for node i. The size of
	 * the array is no. of nodes x dimension.
	 * 
	 * @param ccPositions
	 *            The array with the node positions.
	 */
	public void setCCPositions(double[][] ccPositions) {
		this.ccPositions = ccPositions;
	}

    /** Gets edge Matrix **/
    public DoubleMatrix2D getCCEdges() {
	return ccEdges;
    }
   
    /**Gets Node Number **/

    public int getNodeNumber(){
	return node_no;
    }





}
