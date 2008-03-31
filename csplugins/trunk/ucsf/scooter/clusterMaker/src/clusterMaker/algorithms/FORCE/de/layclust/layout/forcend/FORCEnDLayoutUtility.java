/*
 * Created on 6. November 2007
 * 
 */
package de.layclust.layout.forcend;

import de.layclust.layout.data.ConnectedComponent;
import de.layclust.layout.data.ICCEdges;
import de.layclust.taskmanaging.TestingUtility;

/**
 * This class is a collection of static methods that are used for the layouting
 * process of FORCEnD.
 * 
 * @author Sita Lange
 * CeBiTec, Universitaet Bielefeld
 * 
 */
public class FORCEnDLayoutUtility {

	/**
	 * Calculates the temperature (cooling factor) for the given iteration.
	 * 
	 * @param iteration
	 *            The current iteration number.
	 * @param node_no
	 *            The number of nodes in the current ConnectedComponent.
	 * @param param
	 *            The parameters object for this layouting instance.
	 * @return The cooling temperature factor for the given iteration.
	 */
	public static double calculateTemperature(int iteration, int node_no,
			FORCEnDParameters param) {
		int k = 2;
		double temp = Math.pow((1.0 / (iteration + 1)), k)
				* param.getTemperature() * node_no;
		return temp;
	}

	/**
	 * Sets all the values in the displacement array to zero.
	 * 
	 * @param allDisplacements
	 *            The displacement array.
	 * @param node_no
	 *            The number of nodes for the current ConnectedComponent.
	 * @param dim
	 *            The current dimensions the layouting is run in.
	 */
	private static void setDisplacementsToZero(double[][] allDisplacements,
			int node_no, int dim) {
		for (int i = 0; i < node_no; i++) {
			for (int d = 0; d < dim; d++) {
				allDisplacements[i][d] = 0.0;
			}
		}
	}

	/**
	 * Calculates the displacement vector for all nodes and saves it in a 2D
	 * double array.
	 * 
	 * @param allDisplacements
	 *            The displacement values for all nodes.
	 * @param cc
	 *            The current ConnectedComponent object.
	 * @param dim
	 *            The current dimensions the layouting is run in.
	 * @param param
	 *            The parameters object for FORCEnD.
	 */
	public static void calculateDisplacementVectors(
			double[][] allDisplacements, ConnectedComponent cc, int dim,
			FORCEnDParameters param) {
		double[][] node_pos = cc.getCCPositions();
		ICCEdges edges = cc.getCCEdges();
		int node_no = cc.getNodeNumber();
		setDisplacementsToZero(allDisplacements, node_no, dim);

		for (int i = 0; i < node_no; i++) {
			/*
			 * only need to calculate the forces for j<i, because force(i,j) =
			 * force(j,i)
			 * TODO if it should at some stage not be the case, then change
			 * this!
			 */
			for (int j = 0; j < i; j++) {
				double distance = calculateEuclideanDistance(node_pos, dim, i,
						j);

				if (distance == 0) {
					distance = FORCEnDLayoutConfig.MIN_DISTANCE;
				}

				/*
				 * calculate attraction or repulsion force 
				 * 
				 * attraction:
				 * 				log(d(i,j)+1) x cost(i,j) x attraction factor
				 * 				--------------------------------------------- 
				 * 	  		   	  number of nodes x influence of graph size
				 * 
				 * repulsion: 
				 * 				cost(i,j) x repulsion factor
				 * --------------------------------------------------------
				 * log(d(i,j)+1) x number of nodes x influence of graph size
				 * 
				 */
				
				float cost = edges.getEdgeCost(i, j); 

				double logDist = Math.log(distance + 1);

				double force = 0;
				if (cost > 0) {
					force = logDist * cost * param.getAttractionFactor();
				} else {
					force = (1 / logDist) * cost * param.getRepulsionFactor();
				}
				force = force
						/ (node_no * param.getInfluenceOfGraphSizeToForces());
				// TODO maybe just normalize with graph size and not
				// additionally with
				// user defined factor InfluenceOfGraphSizeToForces

				/*
				 * for each dimension plane, set the displacement value in the
				 * displacements array
				 */
				double displacement = 0;
				for (int d = 0; d < dim; d++) {
					double differenceInPosForDim = node_pos[j][d]
							- node_pos[i][d];
					if (differenceInPosForDim == 0) {
						differenceInPosForDim = FORCEnDLayoutConfig.MIN_DISTANCE; // TODO
																					// why?
						// is it so that the distance is never 0 between two
						// nodes?
					}
					/*
					 * normalise the distance in one dimension and times it by
					 * the force
					 */
					displacement = (differenceInPosForDim / distance) * force;
					if (displacement > param.getMaximalDisplacement()) {
						displacement = param.getMaximalDisplacement();
					}
					if (displacement < -(param.getMaximalDisplacement())) {
						displacement = -(param.getMaximalDisplacement());
					}
					allDisplacements[i][d] += displacement;
					allDisplacements[j][d] -= displacement; // opposite
															// direction
				}
			}
		}
	}

	/**
	 * Calculates the euclidean distance between two nodes.
	 * 
	 * @param node_pos
	 *            The positions array with all node positions.
	 * @param dim
	 *            The dimension that FORCEnD is run in.
	 * @param node_i
	 *            Node i.
	 * @param node_j
	 *            Node j.
	 * @return The euclidean distance between node i and node j.
	 */
	private static double calculateEuclideanDistance(double[][] node_pos,
			int dim, int node_i, int node_j) {

		double distance = 0;

		for (int d = 0; d < dim; d++) {	
			distance += (node_pos[node_i][d] - node_pos[node_j][d])*(node_pos[node_i][d] - node_pos[node_j][d]);
		}
		distance = Math.sqrt(distance);

		return distance;
	}

	/**
	 * Calculates the new node positions. This means the cooling temperature
	 * factor is taken into account, the displacement has to exeed a fixed
	 * minimal length and then the calculated displacement is added to the
	 * previous node position. The method also checks if the new position would
	 * exceed the int boundary (min and max value), and if so it is bounded by
	 * this value.
	 * 
	 * @param allDisplacements
	 *            The double[][] with all calculated force vectors.
	 * @param node_pos
	 *            The previous node positions.
	 * @param node_no
	 *            The number of nodes.
	 * @param dim
	 *            The dimension for this layouting instance.
	 * @param temp
	 *            The cooling temperature factor.
	 */
	public static void moveAllNodesByDisplacement(double[][] allDisplacements,
			double[][] node_pos, int node_no, int dim, double temp) {

		for (int i = 0; i < node_no; i++) {

			/*
			 * the norm of the resulting force vector represents the
			 * displacement distance
			 */
			double norm = calculateNorm(allDisplacements, i, dim);

			/*
			 * only carry out next steps if the norm is greater than the defined
			 * minimal movement
			 */
			if (norm > FORCEnDLayoutConfig.MIN_MOVEMENT) {

				for (int d = 0; d < dim; d++) {

					if (norm > temp) {
						allDisplacements[i][d] = (allDisplacements[i][d] / norm)
								* temp;
					}

					/* the new position for node i in dimension plane d */
					double newPos = node_pos[i][d] + allDisplacements[i][d];

					/*
					 * the boundaries of the layouting space are the maximal and
					 * the minimal values of an int
					 */
					if (newPos > Integer.MAX_VALUE) {
						node_pos[i][d] = Integer.MAX_VALUE;
					} else if (newPos < Integer.MIN_VALUE) {
						node_pos[i][d] = Integer.MIN_VALUE;
					} else {
						node_pos[i][d] = newPos;
					}
				}
			}
		}
	}

	/**
	 * Calculates the norm for a given node in a positions array.
	 * 
	 * @param positions
	 *            All force vectors in one 2D double array.
	 * @param node
	 *            The position in the array (first dim) for which the norm
	 *            should be calculated.
	 * @param dim
	 *            The dimension of the force vector (second dim).
	 * @return The norm of the force vector for the given position.
	 */
	private static double calculateNorm(double[][] positions, int node, int dim) {
		double norm = 0;

		for (int d = 0; d < dim; d++) {
			double pos_i = positions[node][d];
			norm += pos_i * pos_i;
		}
		norm = Math.sqrt(norm);

		return norm;
	}
}
