/*
  File: GraphSetUtils.java

  Copyright (c) 2006, The Cytoscape Consortium (www.cytoscape.org)

  The Cytoscape Consortium is:
  - Institute for Systems Biology
  - University of California San Diego
  - Memorial Sloan-Kettering Cancer Center
  - Institut Pasteur
  - Agilent Technologies

  This library is free software; you can redistribute it and/or modify it
  under the terms of the GNU Lesser General Public License as published
  by the Free Software Foundation; either version 2.1 of the License, or
  any later version.

  This library is distributed in the hope that it will be useful, but
  WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
  MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
  documentation provided hereunder is on an "as is" basis, and the
  Institute for Systems Biology and the Whitehead Institute
  have no obligations to provide maintenance, support,
  updates, enhancements or modifications.  In no event shall the
  Institute for Systems Biology and the Whitehead Institute
  be liable to any party for direct, indirect, special,
  incidental or consequential damages, including lost profits, arising
  out of the use of this software and its documentation, even if the
  Institute for Systems Biology and the Whitehead Institute
  have been advised of the possibility of such damage.  See
  the GNU Lesser General Public License for more details.

  You should have received a copy of the GNU Lesser General Public License
  along with this library; if not, write to the Free Software Foundation,
  Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
*/
package cytoscape.merge;

import cytoscape.Cytoscape;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.view.GraphView;
import org.cytoscape.vizmap.VisualStyle;

import java.util.*;


/**
 * TODO:  This class is completely broken for 3.0.
 *
 */
public class GraphSetUtils {
	/**
	 * The different types of network graph operations
	 */
	protected static final int UNION = 0;
	protected static final int INTERSECTION = 1;
	protected static final int DIFFERENCE = 2;

	/**
	 * Create a new graph which is the union of multiple graphs. The union graph
	 * is created by applying the set union on both the edges and the nodes.
	 *
	 * @param networkList
	 *            A list containing all of the networks.
	 * @param copyView
	 *            Flag indicates whether view information should be copied to
	 *            the new view
	 * @param title
	 *            The title of the new network
	 * @return A cyNetwork which is the union of the input graphs
	 */
	public static CyNetwork createUnionGraph(List networkList, boolean copyView, String title) {
		return performNetworkOperation(networkList, UNION, copyView, title);
	}

	/**
	 * Create a new graph which is the intersection of multiple graphs. The
	 * intersection graph is created by applying the set intersection on both
	 * the edges and the nodes.
	 *
	 * @param networkList
	 *            A list containing all of the networks.
	 * @param copyView
	 *            Flag indicates whether view information should be copied to
	 *            the new view
	 * @param title
	 *            The title of the new network
	 * @return A cyNetwork which is the intersection of the input graphs
	 */
	public static CyNetwork createIntersectionGraph(List networkList, boolean copyView, String title) {
		return performNetworkOperation(networkList, INTERSECTION, copyView, title);
	}

	/**
	 * Create a new graph which is the difference of multiple graphs. Note that
	 * this is not the symmetric difference. The second graph in the list (and
	 * the third and the fourth ... ) are subtracted from the first graph. Here,
	 * we can not directly apply the set difference on the nodes and edge sets
	 * and get a valid graph. Therefore, we apply the difference operation on
	 * the edge sets firsts and add those nodes in the node difference set which
	 * are required for those edges to exist.
	 *
	 * @param networkList
	 *            A list containing all of the networks.
	 * @param copyView
	 *            Flag indicates whether view information should be copied to
	 *            the new view
	 * @param title
	 *            The title of the new network
	 * @return A cyNetwork which is the difference of the input graphs
	 */
	public static CyNetwork createDifferenceGraph(List networkList, boolean copyView, String title) {
		return performNetworkOperation(networkList, DIFFERENCE, copyView, title);
	}

	/**
	 * Protected helper function that actually does the heavy lifting to perform
	 * the set operations. For explantion of the input parameters, see any of
	 * the public methods.
	 *
	 * @return cyNetwork created from applying this set operation
	 */
	protected static CyNetwork performNetworkOperation(List networkList, int operation,
	                                                   boolean copyView, String title) {
		/*
		 * We require at least one network for this operation This should be
		 * enforced by hte GUI, bujt we will check it here as well
		 */
		if (networkList.size() == 0) {
			throw new IllegalArgumentException("Must have at least one network in the list");
		}

		/*
		 * Just handle each type of operation independently, this will cause
		 * some potential duplications of code, but it should be a little bit
		 * more readable
		 */
		int[] new_nodes = null;
		int[] new_edges = null;

		switch (operation) {
			case UNION:
				new_nodes = GraphSetUtils.unionizeNodes(networkList);
				new_edges = GraphSetUtils.unionizeEdges(networkList);

				break;

			case INTERSECTION:
				new_nodes = GraphSetUtils.intersectNodes(networkList);
				new_edges = GraphSetUtils.intersectEdges(networkList);
				System.err.println("number of intersecting nodes is " + new_nodes.length);

				break;

			case DIFFERENCE:
				new_edges = GraphSetUtils.differenceEdges(networkList);
				new_nodes = GraphSetUtils.differenceNodes(networkList, new_edges);

				break;

			default:
				throw new IllegalArgumentException("Specified invalid graph set operation");
		}

		// create the new network
		CyNetwork newNetwork = Cytoscape.createNetwork(new_nodes, new_edges, title);

		// get the visual style for the first network in the list and try to apply
		// it to the new network.
		CyNetwork firstNetwork = (CyNetwork)networkList.get(0);
		GraphView firstView =  Cytoscape.getNetworkView( firstNetwork.getSUID() );
		if ( firstView != null && firstView != Cytoscape.getNullNetworkView() ) {
			VisualStyle firstVS = Cytoscape.getVisualMappingManager().getVisualStyleForView(firstView);

			GraphView newView = Cytoscape.getNetworkView( newNetwork.getSUID() );
			if ( newView != null && newView != Cytoscape.getNullNetworkView() && firstVS != null ) {
				Cytoscape.getVisualMappingManager().setVisualStyleForView(newView, firstVS);
				Cytoscape.redrawGraph(newView);
			}
		}

		return newNetwork;
	}

	/**
	 * Determine the set of difference edges. This apply a straight set difference
	 * operation to the edge sets.
	 * @param networkList A lists containing cyNetworks
	 * @return an integer array containing the set of edges in the difference
	 */
	protected static int[] differenceEdges(List networkList) {
		List<CyEdge> edges = new Vector<CyEdge>();

		/*
		 * For each node in the first network, chech to make sure that it is not
		 * present in all the other networks, add it to the list if this is the
		 * case
		 */
		CyNetwork firstNetwork = (CyNetwork) networkList.get(0);
EDGE_LOOP: 
		for ( CyEdge currentEdge : firstNetwork.getEdgeList() ) {
			for (int idx = 1; idx < networkList.size(); idx++) {
				CyNetwork currentNetwork = (CyNetwork) networkList.get(idx);

				if (currentNetwork.containsEdge(currentEdge)) {
					continue EDGE_LOOP;
				}
			}

			edges.add(currentEdge);
		}

		int[] result = new int[edges.size()];
		int idx = 0;

		for (Iterator edgeIt = edges.iterator(); edgeIt.hasNext(); idx++) {
			CyEdge currentEdge = (CyEdge) edgeIt.next();
			result[idx] = currentEdge.getRootGraphIndex();
		}

		return result;
	}

	/**
	 * Determine the set of difference nodes. In order to perform this operation
	 * we also have to know the set of edges in the edge difference, so that we
	 * can make sure that any nodes are present that are required by those
	 * edges
	 * @param networkList A lists containing cyNetworks
	 * @param edges The difference set of edges
	 * @return an integer array containing the set of edges in the difference
	 */
	protected static int[] differenceNodes(List networkList, int[] edges) {
		HashSet<CyNode> nodes = new HashSet<CyNode>();

		/*
		 * For each node in the first network, check to see if it is not present
		 * in any of the other networks, add it to the list if this is the case
		 */
		CyNetwork firstNetwork = (CyNetwork) networkList.get(0);
NODE_LOOP: 
		for ( CyNode currentNode : firstNetwork.getNodeList() ) {

			for (int idx = 1; idx < networkList.size(); idx++) {
				CyNetwork currentNetwork = (CyNetwork) networkList.get(idx);

				if (currentNetwork.containsNode(currentNode)) {
					continue NODE_LOOP;
				}
			}

			nodes.add(currentNode);
		}

		/*
		 * Now we need to make sure that any nodes required to be present are
		 * included (if these nodes connect an edge in the difference set)
		 */
		for (int idx = 0; idx < edges.length; idx++) {
			//nodes.add(firstNetwork.getNode(firstNetwork.getEdgeSourceIndex(edges[idx])));
			//nodes.add(firstNetwork.getNode(firstNetwork.getEdgeTargetIndex(edges[idx])));
			nodes.add(firstNetwork.getEdge(edges[idx]).getSource());
			nodes.add(firstNetwork.getEdge(edges[idx]).getTarget());
		}

		int[] result = new int[nodes.size()];
		int idx = 0;

		for (Iterator nodeIt = nodes.iterator(); nodeIt.hasNext(); idx++) {
			CyNode currentNode = (CyNode) nodeIt.next();
			result[idx] = currentNode.getRootGraphIndex();
		}

		return result;
	}

	/**
	 * Apply a simple intersection operation to the node sets
	 * @param networkList A list of cyNetworks
	 * @return an integer array which contains the indices of nodes in the intersection
	 */
	protected static int[] intersectNodes(List networkList) {
		List<CyNode> nodes = new Vector<CyNode>();

		/*
		 * For each node in the first network, check to see if it is present in
		 * all of the other networks, add it to the list if this is the case
		 */
		CyNetwork firstNetwork = (CyNetwork) networkList.get(0);
NODE_LOOP: 
		for ( CyNode currentNode : firstNetwork.getNodeList() ) {

			for (int idx = 1; idx < networkList.size(); idx++) {
				CyNetwork currentNetwork = (CyNetwork) networkList.get(idx);

				if (!currentNetwork.containsNode(currentNode)) {
					continue NODE_LOOP;
				}
			}

			nodes.add(currentNode);
		}

		int[] result = new int[nodes.size()];
		int idx = 0;

		for (Iterator nodeIt = nodes.iterator(); nodeIt.hasNext(); idx++) {
			CyNode currentNode = (CyNode) nodeIt.next();
			result[idx] = currentNode.getRootGraphIndex();
		}

		return result;
	}

	/**
	 * Apply a simple intersection operation to the edge sets
	 * @param networkList A list of cyNetworks
	 * @return an integer array which contains the indices of edges in the intersection
	 */
	protected static int[] intersectEdges(List networkList) {
		List<CyEdge> edges = new Vector<CyEdge>();

		/*
		 * For each node in the first network, check to see if it is present in
		 * all of the other networks, add it to the list if this is the case
		 */
		CyNetwork firstNetwork = (CyNetwork) networkList.get(0);
EDGE_LOOP: 
		for ( CyEdge currentEdge : firstNetwork.getEdgeList() ) {
			for (int idx = 1; idx < networkList.size(); idx++) {
				CyNetwork currentNetwork = (CyNetwork) networkList.get(idx);

				if (!currentNetwork.containsEdge(currentEdge)) {
					continue EDGE_LOOP;
				}
			}

			edges.add(currentEdge);
		}

		int[] result = new int[edges.size()];
		int idx = 0;

		for (Iterator edgeIt = edges.iterator(); edgeIt.hasNext(); idx++) {
			CyEdge currentEdge = (CyEdge) edgeIt.next();
			result[idx] = currentEdge.getRootGraphIndex();
		}

		return result;
	}

	/**
	 * Makes nodes request overtime pay.
	 * @param networkList a list of cyNetworks
	 * @return an integer array containing the indices of nodes in the union
	 */
	protected static int[] unionizeNodes(List networkList) {
		/*
		 * This is the set of nodes that will be in the final merged network
		 */
		Set<Integer> nodes = new HashSet<Integer>();

		for (Iterator it = networkList.iterator(); it.hasNext();) {
			CyNetwork currentNetwork = (CyNetwork) it.next();

			for ( CyNode currentNode : currentNetwork.getNodeList() ) {
				nodes.add(Integer.valueOf(currentNode.getIndex()));
			}
		}

		int[] result = new int[nodes.size()];
		int idx = 0;

		for (Iterator nodeIt = nodes.iterator(); nodeIt.hasNext(); idx++) {
			result[idx] = ((Integer) nodeIt.next()).intValue();
		}

		return result;
	}

	/**
	 * Perform a simple set union on the sets of nodes
	 * @param networkList a list of cyNetworks
	 * @return an integer array containing the indices of edges in the union
	 */
	protected static int[] unionizeEdges(List networkList) {
		/*
		 * This is the set of edges that will be in the final network
		 */
		Set<Integer> edges = new HashSet<Integer>();

		for (Iterator it = networkList.iterator(); it.hasNext();) {
			CyNetwork currentNetwork = (CyNetwork) it.next();
	
			for ( CyEdge edge : currentNetwork.getEdgeList() ) {
				edges.add(Integer.valueOf(edge.getIndex()));
			}
		}

		int[] result = new int[edges.size()];
		int idx = 0;

		for (Iterator edgeIt = edges.iterator(); edgeIt.hasNext(); idx++) {
			result[idx] = ((Integer) edgeIt.next()).intValue();
		}

		return result;
	}
}
