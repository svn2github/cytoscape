package org.cytoscape.layout.duallayout;

import giny.model.GraphPerspective;
import giny.model.Node;
import giny.view.GraphView;
import giny.view.NodeView;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import cytoscape.view.CyNetworkView;

/**
 * An implementation of Kamada and Kawai's spring embedded layout algorithm.
 */
public class SpringEmbeddedLayouter {

	public static final int DEFAULT_NUM_LAYOUT_PASSES = 2;
	public static final double DEFAULT_AVERAGE_ITERATIONS_PER_NODE = 20.0;

	public static final double[] DEFAULT_NODE_DISTANCE_SPRING_SCALARS = new double[] {
			1.0, 1.0 };
	public static final double DEFAULT_NODE_DISTANCE_STRENGTH_CONSTANT = 15.0;
	public static final double DEFAULT_NODE_DISTANCE_REST_LENGTH_CONSTANT = 200.0;
	public static final double DEFAULT_DISCONNECTED_NODE_DISTANCE_SPRING_STRENGTH =
	// .05;
	0;
	public static final double DEFAULT_DISCONNECTED_NODE_DISTANCE_SPRING_REST_LENGTH = 2500.0;
	public static double HOMOLOGY_MULTIPLIER = 5;
	public static final double[] DEFAULT_ANTICOLLISION_SPRING_SCALARS = new double[] {
			0.0, 1.0 };
	public static final double DEFAULT_ANTICOLLISION_SPRING_STRENGTH = 100.0;
	protected int numLayoutPasses = DEFAULT_NUM_LAYOUT_PASSES;
	protected double averageIterationsPerNode = DEFAULT_AVERAGE_ITERATIONS_PER_NODE;

	protected double[] nodeDistanceSpringScalars = DEFAULT_NODE_DISTANCE_SPRING_SCALARS;
	protected double nodeDistanceStrengthConstant = DEFAULT_NODE_DISTANCE_STRENGTH_CONSTANT;
	protected double nodeDistanceRestLengthConstant = DEFAULT_NODE_DISTANCE_REST_LENGTH_CONSTANT;
	protected double disconnectedNodeDistanceSpringStrength = DEFAULT_DISCONNECTED_NODE_DISTANCE_SPRING_STRENGTH;
	protected double disconnectedNodeDistanceSpringRestLength = DEFAULT_DISCONNECTED_NODE_DISTANCE_SPRING_REST_LENGTH;
	protected double[][] nodeDistanceSpringStrengths;
	protected double[][] nodeDistanceSpringRestLengths;

	protected double[] anticollisionSpringScalars = DEFAULT_ANTICOLLISION_SPRING_SCALARS;
	protected double[][] anticollisionSpringStrength;

	protected GraphView graphView;
	protected int nodeCount;
	protected int edgeCount;
	protected int layoutPass;
	protected Map node2Species;
	protected NodePairSet homologyPairSet;
	protected HashMap node2Index;
	protected Random rnd;

	/**
	 * @param node2Species
	 *            a hashmap which maps from a node to a species number
	 */
	public SpringEmbeddedLayouter(CyNetworkView graph_view, Map node2Species,
			NodePairSet homologyPairSet, boolean isRandom) {
		setGraphView(graph_view);
		this.node2Species = node2Species;
		// initializeSpringEmbeddedLayouter();
		this.homologyPairSet = homologyPairSet;
		if (isRandom)
			this.rnd = new Random();
		else
			this.rnd = new Random(5);
	}

	public void setGraphView(CyNetworkView new_graph_view) {
		graphView = new_graph_view;
	} // setGraphView( GraphView )

	public GraphView getGraphView() {
		return graphView;
	} // getGraphView()

	protected int getNodeViewIndex(NodeView n) {
		GraphPerspective perspective = graphView.getGraphPerspective();
		return ((Integer) node2Index.get(perspective.getNode(n
				.getGraphPerspectiveIndex()))).intValue();
	}

	protected void initializeSpringEmbeddedLayouter() {
		// Do nothing.
		// TODO: Something?
	} // initializeSpringEmbeddedLayouter()

	private void initializePositions() {
		Iterator viewIt = graphView.getNodeViewsIterator();
		while (viewIt.hasNext()) {
			NodeView v = (NodeView) viewIt.next();
			v.setXPosition(500 * rnd.nextDouble());
			v.setYPosition(500 * rnd.nextDouble());
		}

	}

	public void doLayout() {
		// initialize the layouting.

		nodeCount = graphView.getNodeViewCount();
		edgeCount = graphView.getEdgeViewCount();
		initializePositions();

		// Stop if all nodes are closer together than this euclidean distance.
		// TODO: Why is this an appropriate threshold?
		double euclidean_distance_threshold = (0.5 * (nodeCount + edgeCount));

		// Stop if the potential energy doesn't go down anymore.
		// double potential_energy_percent_change_threshold = .001;

		int num_iterations = (int) ((nodeCount * averageIterationsPerNode) / numLayoutPasses);

		List partials_list = createPartialsList();
		PotentialEnergy potential_energy = new PotentialEnergy();
		Iterator node_views_iterator;
		NodeView node_view;
		PartialDerivatives partials;
		PartialDerivatives furthest_node_partials = null;
		// double current_progress_temp;
		// double setup_progress = 0.0;
		for (layoutPass = 0; layoutPass < numLayoutPasses; layoutPass++) {

			setupForLayoutPass();

			// System.out.println( " DO Layout Pass " );

			// initialize this layout pass
			potential_energy.reset();
			partials_list.clear();

			// Calculate all node distances. Keep track of the furthest.
			node_views_iterator = graphView.getNodeViewsIterator();
			while (node_views_iterator.hasNext()) {
				node_view = (NodeView) node_views_iterator.next();

				// System.out.println(
				// "Calculate Partials for: "+node_view.getGraphPerspectiveIndex()
				// );

				partials = new PartialDerivatives(node_view);
				calculatePartials(partials, null, potential_energy, false);
				partials_list.add(partials);
				if ((furthest_node_partials == null)
						|| (partials.euclideanDistance > furthest_node_partials.euclideanDistance)) {
					// //System.out.println(
					// "P: "+furthest_node_partials.euclideanDistance+" E: "+partials.euclideanDistance
					// );
					furthest_node_partials = partials;
				}
			}

			// Until num_iterations, or the furthest node is not-so-fur, move
			// the
			// furthest node towards where it wants to be.
			for (int iterations_i = 0; ((iterations_i < num_iterations) && (furthest_node_partials.euclideanDistance >= euclidean_distance_threshold)); iterations_i++) {
				// TODO: REMOVE
				// System.out.println( "At iteration " + layoutPass + ":" +
				// iterations_i + ", furthest_node_partials is " +
				// furthest_node_partials + "." );
				furthest_node_partials = moveNode(furthest_node_partials,
						partials_list, potential_energy);
			} // End for each iteration, attempt to minimize the total potential
			// energy by moving the node that is furthest from where it should
			// be.
		} // End for each layout pass
	} // doLayout()

	/**
	 * Called at the beginning of each layoutPass iteration.
	 */
	protected void setupForLayoutPass() {
		setupNodeDistanceSprings();
		setupAntiCollisionSprings();
	} // setupForLayoutPass()

	// called to determine which edges we want ot prevent from overlapping
	protected void setupAntiCollisionSprings() {
		if (layoutPass != 0) {
			return;
		}
		anticollisionSpringStrength = new double[nodeCount][nodeCount];
		List nodeList = graphView.getGraphPerspective().nodesList();
		for (int idx1 = 0, size = nodeList.size(); idx1 < size; idx1++) {
			for (int idx2 = idx1 + 1; idx2 < size; idx2++) {
				if (node2Species.get(nodeList.get(idx1)).equals(
						node2Species.get(nodeList.get(idx2)))) {
					anticollisionSpringStrength[idx1][idx2] = DEFAULT_ANTICOLLISION_SPRING_STRENGTH;
					anticollisionSpringStrength[idx2][idx1] = DEFAULT_ANTICOLLISION_SPRING_STRENGTH;
				} else {
					anticollisionSpringStrength[idx1][idx2] = 0;
					anticollisionSpringStrength[idx2][idx1] = 0;
				}
			}
		}
	}

	protected int[][] calculateNodeDistances(List nodesList,
			GraphPerspective network) {
		node2Index = new HashMap();
		int node_count = nodesList.size();
		int[][] result = new int[node_count][node_count];
		/*
		 * Intialization for Floyd Warshall
		 */
		for (int i = 0; i < nodesList.size(); i++) {
			node2Index.put(nodesList.get(i), new Integer(i));
			Arrays.fill(result[i], 20);
		}
		for (int i = 0; i < node_count; i++) {
			Node node = (Node) nodesList.get(i);
			for (Iterator neighborIt = network.neighborsList(node).iterator(); neighborIt
					.hasNext();) {
				Object neighbor = neighborIt.next();
				int j = ((Integer) node2Index.get(neighbor)).intValue();
				result[i][j] = 1;
				result[j][i] = 1;
			}
			result[i][i] = 0;
		}

		for (int k = 0; k < node_count; k++) { /*
												 * k -> is the intermediate
												 * point
												 */
			for (int i = 0; i < node_count; i++) { /* start from i */
				for (int j = 0; j < node_count; j++) { /* reaching j */
					/* if i-->k + k-->j is smaller than the original i-->j */
					if (result[i][k] + result[k][j] < result[i][j]) {
						/* then reduce i-->j distance to the smaller one i->k->j */
						result[i][j] = result[i][k] + result[k][j];
					}
				}
			}
		}

		return result;
	}

	protected void setupNodeDistanceSprings() {
		// We only have to do this once.
		if (layoutPass != 0) {
			return;
		}

		nodeDistanceSpringRestLengths = new double[nodeCount][nodeCount];
		nodeDistanceSpringStrengths = new double[nodeCount][nodeCount];

		if (nodeDistanceSpringScalars[layoutPass] == 0.0) {
			return;
		}

		// NodeDistances ind = new NodeDistances(
		// graphView.getGraphPerspective().nodesList(), null,
		// graphView.getGraphPerspective() );
		// int[][] node_distances = ( int[][] )ind.calculate();
		int[][] node_distances = calculateNodeDistances(graphView
				.getGraphPerspective().nodesList(), graphView
				.getGraphPerspective());
		String[] nodeNames = new String[graphView.getGraphPerspective()
				.getNodeCount()];
		{
			int idx = 0;
			for (Iterator nodeIt = graphView.getGraphPerspective()
					.nodesIterator(); nodeIt.hasNext();) {
				// nodeNames[idx++] = (String)
				// Cytoscape.getNodeAttributeValue((Node) nodeIt.next(),
				// Semantics.CANONICAL_NAME);
				nodeNames[idx++] = ((Node) nodeIt.next()).getIdentifier();
			}
		}
		// System.err.print("Nodes");
		// for(int idx=0;idx<nodeNames.length;idx++){
		// System.err.print("\t"+nodeNames[idx]);
		// }
		// System.err.println();
		// for(int idx=0;idx<node_distances.length;idx++){
		// System.err.print(nodeNames[idx]);
		// for(int idy=0;idy<node_distances[idx].length;idy++){
		// System.err.print("\t"+node_distances[idx][idy]);
		// }
		// System.err.println();
		// }

		// TODO: A good strength_constant is the characteristic path length of
		// the
		// graph. For now we'll just use nodeDistanceStrengthConstant.
		double node_distance_strength_constant = nodeDistanceStrengthConstant;

		// TODO: rest_length_constant can be chosen to scale the whole graph.
		// To make it the size of the current view, try
		// rest_length_constant = Math.sqrt( ( ( graphView.getViewRect().width /
		// graphView.getViewRect.height() ) / 4 ) / graphView.getGraphDiameter()
		// );
		// To make it bigger, try
		// rest_length_constant = graphView.averageEdgeLength();
		// To make it smaller, try
		// rest_length_constant = Math.sqrt( ( graphView.getViewRect().width *
		// graphView.getViewRect.height() ) / graphView.getGraphDiameter() );
		// For now we'll just use nodeDistanceRestLengthConstant.
		double node_distance_rest_length_constant = nodeDistanceRestLengthConstant;

		List nodeList = graphView.getGraphPerspective().nodesList();

		// Calculate the rest lengths and strengths based on the node distance
		// data
		for (int node_i = 0; node_i < nodeCount; node_i++) {
			for (int node_j = (node_i + 1); node_j < nodeCount; node_j++) {
				Node i = (Node) nodeList.get(node_i);
				Node j = (Node) nodeList.get(node_j);
				if (!homologyPairSet.contains(i, j)) {
					// there is not a homology edge between these two nodes
					nodeDistanceSpringRestLengths[node_i][node_j] = (node_distance_rest_length_constant * node_distances[node_i][node_j]);
					nodeDistanceSpringStrengths[node_i][node_j] = (node_distance_strength_constant / (node_distances[node_i][node_j] * node_distances[node_i][node_j]));

					if (node_distances[node_i][node_j] == Integer.MAX_VALUE) {
						nodeDistanceSpringRestLengths[node_i][node_j] = disconnectedNodeDistanceSpringRestLength;

					} else {
						nodeDistanceSpringRestLengths[node_i][node_j] = (node_distance_rest_length_constant * node_distances[node_i][node_j]);
					}

					if (node_distances[node_i][node_j] == Integer.MAX_VALUE) {
						nodeDistanceSpringStrengths[node_i][node_j] = disconnectedNodeDistanceSpringStrength;
					} else {
						nodeDistanceSpringStrengths[node_i][node_j] = (node_distance_strength_constant / (node_distances[node_i][node_j] * node_distances[node_i][node_j]));
					}
				} else {
					// there is a homology interaction between them, make a
					// strong homology interaction
					nodeDistanceSpringRestLengths[node_i][node_j] = node_distance_rest_length_constant
							/ HOMOLOGY_MULTIPLIER;
					nodeDistanceSpringStrengths[node_i][node_j] = HOMOLOGY_MULTIPLIER
							* HOMOLOGY_MULTIPLIER
							* node_distance_strength_constant;
				}

				// System.out.println( "APSP: node_i: "+node_i+ " node_j: "+
				// node_j+" == "+node_distances[ node_i ][node_j ] );
				/*
				 * if( node_distances[ node_i ][ node_j ] == Integer.MAX_VALUE )
				 * { nodeDistanceSpringRestLengths[ node_i ][ node_j ] =
				 * disconnectedNodeDistanceSpringRestLength;
				 * //System.out.println(
				 * "disconnectedNodeDistanceSpringRestLength 1: "+
				 * disconnectedNodeDistanceSpringRestLength ); } else {
				 * nodeDistanceSpringRestLengths[ node_i ][ node_j ] = (
				 * node_distance_rest_length_constant * node_distances[ node_i
				 * ][ node_j ] ); //System.out.println(
				 * " ELSE 1: "+nodeDistanceSpringRestLengths[ node_i ][ node_j ]
				 * ); } // Mirror over the diagonal.
				 * nodeDistanceSpringRestLengths[ node_j ][ node_i ] =
				 * nodeDistanceSpringRestLengths[ node_i ][ node_j ];
				 * 
				 * if( node_distances[ node_i ][ node_j ] == Integer.MAX_VALUE )
				 * { nodeDistanceSpringStrengths[ node_i ][ node_j ] =
				 * disconnectedNodeDistanceSpringStrength; } else {
				 * nodeDistanceSpringStrengths[ node_i ][ node_j ] = (
				 * node_distance_strength_constant / ( node_distances[ node_i ][
				 * node_j ] * node_distances[ node_i ][ node_j ] ) ); }
				 */
				nodeDistanceSpringRestLengths[node_j][node_i] = nodeDistanceSpringRestLengths[node_i][node_j];
				nodeDistanceSpringStrengths[node_j][node_i] = nodeDistanceSpringStrengths[node_i][node_j];
			}

		}
		// currentProgress has been increased by ( nodeCount * nodeCount ).

	} // setupNodeDistanceSprings()

	/**
	 * If partials_list is given, adjust all partials (bidirectional) for the
	 * current location of the given partials and return the new furthest node's
	 * partials. Otherwise, just adjust the given partials (using the
	 * graphView's nodeViewsIterator), and return it. If reversed is true then
	 * partials_list must be provided and all adjustments made by a non-reversed
	 * call (with the same partials with the same graphNodeView at the same
	 * location) will be undone. Complexity is O( #Nodes ).
	 */
	protected PartialDerivatives calculatePartials(PartialDerivatives partials,
			List partials_list, PotentialEnergy potential_energy,
			boolean reversed) {

		partials.reset();

		NodeView node_view = partials.getNodeView();
		// int node_view_index = node_view.getGraphPerspectiveIndex() - 1;
		int node_view_index = getNodeViewIndex(node_view);
		double node_view_radius = node_view.getWidth();
		double node_view_x = node_view.getXPosition();
		double node_view_y = node_view.getYPosition();

		// System.out.println(
		// "index: "+node_view_index+" x: "+node_view_x+" y:" +node_view_y );

		PartialDerivatives other_node_partials = null;
		NodeView other_node_view;
		int other_node_view_index;
		double other_node_view_radius;

		PartialDerivatives furthest_partials = null;

		Iterator iterator;
		if (partials_list == null) {
			iterator = graphView.getNodeViewsIterator();
		} else {
			iterator = partials_list.iterator();
		}
		double delta_x;
		double delta_y;
		double euclidean_distance;
		double euclidean_distance_cubed;
		double distance_from_rest;
		double distance_from_touching;
		double incremental_change;
		while (iterator.hasNext()) {
			if (partials_list == null) {
				other_node_view = (NodeView) iterator.next();
			} else {
				other_node_partials = (PartialDerivatives) iterator.next();
				other_node_view = other_node_partials.getNodeView();
			}

			// System.out.println( "Node_View: "+
			// (node_view.getGraphPerspectiveIndex() - 1 ));
			// System.out.println( "Other_Node_View: "+
			// (other_node_view.getGraphPerspectiveIndex() - 1 ) );

			if (node_view.getGraphPerspectiveIndex() - 1 == other_node_view
					.getGraphPerspectiveIndex() - 1) {
				// System.out.println( "Nodes are the same. " );
				continue;
			}

			// other_node_view_index =
			// other_node_view.getGraphPerspectiveIndex() - 1;
			other_node_view_index = getNodeViewIndex(other_node_view);
			other_node_view_radius = other_node_view.getWidth();

			delta_x = (node_view_x - other_node_view.getXPosition());
			delta_y = (node_view_y - other_node_view.getYPosition());

			// System.out.println( "Delta's Calculated: "+delta_y+ "  "+delta_x
			// );

			euclidean_distance = Math.sqrt((delta_x * delta_x)
					+ (delta_y * delta_y));
			euclidean_distance_cubed = Math.pow(euclidean_distance, 3);

			// System.out.println(
			// "Euclidean_Distance: "+euclidean_distance+" Euclidean_Distance_Cubed: "+euclidean_distance_cubed
			// );

			distance_from_touching = (euclidean_distance - (node_view_radius + other_node_view_radius));

			// System.out.println(
			// "Distance_From_Touching: "+distance_from_touching );

			incremental_change = (nodeDistanceSpringScalars[layoutPass] * (nodeDistanceSpringStrengths[node_view_index][other_node_view_index] * (delta_x - ((nodeDistanceSpringRestLengths[node_view_index][other_node_view_index] * delta_x) / euclidean_distance))));

			// System.out.println( "Incremental_Change: "+incremental_change );

			if (!reversed) {
				partials.x += incremental_change;
			}
			if (other_node_partials != null) {
				incremental_change = (nodeDistanceSpringScalars[layoutPass] * (nodeDistanceSpringStrengths[other_node_view_index][node_view_index] * (-delta_x - ((nodeDistanceSpringRestLengths[other_node_view_index][node_view_index] * -delta_x) / euclidean_distance))));
				if (reversed) {
					other_node_partials.x -= incremental_change;
				} else {
					other_node_partials.x += incremental_change;
				}
			}
			if (distance_from_touching < 0.0) {
				incremental_change = (anticollisionSpringScalars[layoutPass] * (anticollisionSpringStrength[node_view_index][other_node_view_index] * (delta_x - (((node_view_radius + other_node_view_radius) * delta_x) / euclidean_distance))));
				if (!reversed) {
					partials.x += incremental_change;
				}
				if (other_node_partials != null) {
					incremental_change = (anticollisionSpringScalars[layoutPass] * (anticollisionSpringStrength[other_node_view_index][node_view_index] * (-delta_x - (((node_view_radius + other_node_view_radius) * -delta_x) / euclidean_distance))));
					if (reversed) {
						other_node_partials.x -= incremental_change;
						// System.out.println(
						// "Other_Node_Partials (-): "+other_node_partials.x );
					} else {
						other_node_partials.x += incremental_change;
						// System.out.println(
						// "Other_Node_Partials (+): "+other_node_partials.x );
					}
				}
			}
			incremental_change = (nodeDistanceSpringScalars[layoutPass] * (nodeDistanceSpringStrengths[node_view_index][other_node_view_index] * (delta_y - ((nodeDistanceSpringRestLengths[node_view_index][other_node_view_index] * delta_y) / euclidean_distance))));

			// System.out.println( "Incremental_Change: "+incremental_change );

			if (!reversed) {
				partials.y += incremental_change;
			}
			if (other_node_partials != null) {
				incremental_change = (nodeDistanceSpringScalars[layoutPass] * (nodeDistanceSpringStrengths[other_node_view_index][node_view_index] * (-delta_y - ((nodeDistanceSpringRestLengths[other_node_view_index][node_view_index] * -delta_y) / euclidean_distance))));
				if (reversed) {
					other_node_partials.y -= incremental_change;
				} else {
					other_node_partials.y += incremental_change;
				}
			}
			if (distance_from_touching < 0.0) {
				incremental_change = (anticollisionSpringScalars[layoutPass] * (anticollisionSpringStrength[node_view_index][other_node_view_index] * (delta_y - (((node_view_radius + other_node_view_radius) * delta_y) / euclidean_distance))));
				if (!reversed) {
					partials.y += incremental_change;
				}
				if (other_node_partials != null) {
					incremental_change = (anticollisionSpringScalars[layoutPass] * (anticollisionSpringStrength[other_node_view_index][node_view_index] * (-delta_y - (((node_view_radius + other_node_view_radius) * -delta_y) / euclidean_distance))));
					if (reversed) {
						other_node_partials.y -= incremental_change;
					} else {
						other_node_partials.y += incremental_change;
					}
				}
			}

			incremental_change = (nodeDistanceSpringScalars[layoutPass] * (nodeDistanceSpringStrengths[node_view_index][other_node_view_index] * (1.0 - ((nodeDistanceSpringRestLengths[node_view_index][other_node_view_index] * (delta_y * delta_y)) / euclidean_distance_cubed))));
			// System.out.println( "Incremental_Change: "+incremental_change );

			if (reversed) {
				if (other_node_partials != null) {
					other_node_partials.xx -= incremental_change;
				}
			} else {
				partials.xx += incremental_change;
				if (other_node_partials != null) {
					other_node_partials.xx += incremental_change;
				}
			}
			if (distance_from_touching < 0.0) {
				incremental_change = (anticollisionSpringScalars[layoutPass] * (anticollisionSpringStrength[node_view_index][other_node_view_index] * (1.0 - (((node_view_radius + other_node_view_radius) * (delta_y * delta_y)) / euclidean_distance_cubed))));
				if (reversed) {
					if (other_node_partials != null) {
						other_node_partials.xx -= incremental_change;
					}
				} else {
					partials.xx += incremental_change;
					if (other_node_partials != null) {
						other_node_partials.xx += incremental_change;
					}
				}
			}
			incremental_change = (nodeDistanceSpringScalars[layoutPass] * (nodeDistanceSpringStrengths[node_view_index][other_node_view_index] * (1.0 - ((nodeDistanceSpringRestLengths[node_view_index][other_node_view_index] * (delta_x * delta_x)) / euclidean_distance_cubed))));

			// System.out.println( "Incremental_Change: "+incremental_change );

			if (reversed) {
				if (other_node_partials != null) {
					other_node_partials.yy -= incremental_change;
				}
			} else {
				partials.yy += incremental_change;
				if (other_node_partials != null) {
					other_node_partials.yy += incremental_change;
				}
			}
			if (distance_from_touching < 0.0) {
				incremental_change = (anticollisionSpringScalars[layoutPass] * (anticollisionSpringStrength[node_view_index][other_node_view_index] * (1.0 - (((node_view_radius + other_node_view_radius) * (delta_x * delta_x)) / euclidean_distance_cubed))));
				if (reversed) {
					if (other_node_partials != null) {
						other_node_partials.yy -= incremental_change;
					}
				} else {
					partials.yy += incremental_change;
					if (other_node_partials != null) {
						other_node_partials.yy += incremental_change;
					}
				}
			}
			incremental_change = (nodeDistanceSpringScalars[layoutPass] * (nodeDistanceSpringStrengths[node_view_index][other_node_view_index] * ((nodeDistanceSpringRestLengths[node_view_index][other_node_view_index] * (delta_x * delta_y)) / euclidean_distance_cubed)));

			// System.out.println( "Incremental_Change: "+incremental_change );

			if (reversed) {
				if (other_node_partials != null) {
					other_node_partials.xy -= incremental_change;
				}
			} else {
				partials.xy += incremental_change;
				if (other_node_partials != null) {
					other_node_partials.xy += incremental_change;
				}
			}
			if (distance_from_touching < 0.0) {
				incremental_change = (anticollisionSpringScalars[layoutPass] * (anticollisionSpringStrength[node_view_index][other_node_view_index] * (((node_view_radius + other_node_view_radius) * (delta_x * delta_y)) / euclidean_distance_cubed)));
				if (reversed) {
					if (other_node_partials != null) {
						other_node_partials.xy -= incremental_change;
					}
				} else {
					partials.xy += incremental_change;
					if (other_node_partials != null) {
						other_node_partials.xy += incremental_change;
					}
				}
			}

			distance_from_rest = (euclidean_distance - nodeDistanceSpringRestLengths[node_view_index][other_node_view_index]);
			incremental_change = (nodeDistanceSpringScalars[layoutPass] * ((nodeDistanceSpringStrengths[node_view_index][other_node_view_index] * (distance_from_rest * distance_from_rest)) / 2));

			// System.out.println(
			// "Distance_From_Rest: "+distance_from_rest+" Incremental_Change: "+incremental_change
			// );

			if (reversed) {
				if (other_node_partials != null) {
					potential_energy.totalEnergy -= incremental_change;
				}
			} else {
				potential_energy.totalEnergy += incremental_change;
				if (other_node_partials != null) {
					potential_energy.totalEnergy += incremental_change;
				}
			}
			if (distance_from_touching < 0.0) {
				incremental_change = (anticollisionSpringScalars[layoutPass] * ((anticollisionSpringStrength[node_view_index][other_node_view_index] * (distance_from_touching * distance_from_touching)) / 2));
				if (reversed) {
					if (other_node_partials != null) {
						potential_energy.totalEnergy -= incremental_change;
					}
				} else {
					potential_energy.totalEnergy += incremental_change;
					if (other_node_partials != null) {
						potential_energy.totalEnergy += incremental_change;
					}
				}
			}
			if (other_node_partials != null) {
				other_node_partials.euclideanDistance = Math
						.sqrt((other_node_partials.x * other_node_partials.x)
								+ (other_node_partials.y * other_node_partials.y));
				if ((furthest_partials == null)
						|| (other_node_partials.euclideanDistance > furthest_partials.euclideanDistance)) {
					furthest_partials = other_node_partials;
				}
			}

		}

		if (!reversed) {
			partials.euclideanDistance = Math.sqrt((partials.x * partials.x)
					+ (partials.y * partials.y));
		}

		if ((furthest_partials == null)
				|| (partials.euclideanDistance > furthest_partials.euclideanDistance)) {
			furthest_partials = partials;
		}

		// System.out.println( "Furthest_Partials: "+furthest_partials );

		return furthest_partials;
	} // calculatePartials( PartialDerivatives, List, PotentialEnergy, boolean )

	/**
	 * Move the node with the given partials and adjust all partials in the
	 * given List to reflect that move, and adjust the potential energy too.
	 * 
	 * @return the PartialDerivatives of the furthest node after the move.
	 */
	protected PartialDerivatives moveNode(PartialDerivatives partials,
			List partials_list, PotentialEnergy potential_energy) {
		// NodeView node_view = partials.getNodeView();

		PartialDerivatives starting_partials = new PartialDerivatives(partials);
		calculatePartials(partials, partials_list, potential_energy, true);
		simpleMoveNode(starting_partials);
		return calculatePartials(partials, partials_list, potential_energy,
				false);
	} // moveNode( PartialDerivatives, List, PotentialEnergy )

	protected void simpleMoveNode(PartialDerivatives partials) {
		NodeView node_view = partials.getNodeView();
		double denomenator = ((partials.xx * partials.yy) - (partials.xy * partials.xy));
		double delta_x = (((-partials.x * partials.yy) - (-partials.y * partials.xy)) / denomenator);
		double delta_y = (((-partials.y * partials.xx) - (-partials.x * partials.xy)) / denomenator);

		// REMOVE
		// System.out.println( "moving node \"" + node_view + "\" to ( " + (
		// node_view.getXPosition() + delta_x ) + ", " + (
		// node_view.getYPosition() + delta_y ) + " )." );

		// TODO: figure out movement
		// node_view.setXPosition(
		// node_view.getXPosition() + delta_x
		// );
		// node_view.setYPosition(
		// node_view.getYPosition() + delta_y
		// );

		Point2D p = node_view.getOffset();
		node_view.setOffset(p.getX() + delta_x, p.getY() + delta_y);

	} // simpleMoveNode( PartialDerivatives )

	protected List createPartialsList() {
		return new ArrayList();
	} // createPartialsList()

	class PartialDerivatives {
		protected NodeView nodeView;
		public double x;
		public double y;
		public double xx;
		public double yy;
		public double xy;
		public double euclideanDistance;

		public PartialDerivatives(NodeView node_view) {
			nodeView = node_view;
		}

		public PartialDerivatives(PartialDerivatives copy_from) {
			nodeView = copy_from.getNodeView();
			copyFrom(copy_from);
		}

		public void reset() {
			x = 0.0;
			y = 0.0;
			xx = 0.0;
			yy = 0.0;
			xy = 0.0;
			euclideanDistance = 0.0;
		} // reset()

		public NodeView getNodeView() {
			return nodeView;
		} // getNodeView()

		public void copyFrom(PartialDerivatives other_partial_derivatives) {
			x = other_partial_derivatives.x;
			y = other_partial_derivatives.y;
			xx = other_partial_derivatives.xx;
			yy = other_partial_derivatives.yy;
			xy = other_partial_derivatives.xy;
			euclideanDistance = other_partial_derivatives.euclideanDistance;
		} // copyFrom( PartialDerivatives )

		public String toString() {
			return "PartialDerivatives( \"" + getNodeView() + "\", x=" + x
					+ ", y=" + y + ", xx=" + xx + ", yy=" + yy + ", xy=" + xy
					+ ", euclideanDistance=" + euclideanDistance + " )";
		} // toString()

	} // inner class PartialDerivatives

	class PotentialEnergy {

		public double totalEnergy = 0.0;

		public void reset() {
			totalEnergy = 0.0;
		} // reset()

	} // class PotentialEnergy

} // class SpringEmbeddedLayouter
