//

/** Copyright (c) 2002 Institute for Systems Biology and the Whitehead Institute
 **
 ** This library is free software; you can redistribute it and/or modify it
 ** under the terms of the GNU Lesser General Public License as published
 ** by the Free Software Foundation; either version 2.1 of the License, or
 ** any later version.
 ** 
 ** This library is distributed in the hope that it will be useful, but
 ** WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
 ** MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
 ** documentation provided hereunder is on an "as is" basis, and the
 ** Institute for Systems Biology and the Whitehead Institute 
 ** have no obligations to provide maintenance, support,
 ** updates, enhancements or modifications.  In no event shall the
 ** Institute for Systems Biology and the Whitehead Institute 
 ** be liable to any party for direct, indirect, special,
 ** incidental or consequential damages, including lost profits, arising
 ** out of the use of this software and its documentation, even if the
 ** Institute for Systems Biology and the Whitehead Institute 
 ** have been advised of the possibility of such damage.  See
 ** the GNU Lesser General Public License for more details.
 ** 
 ** You should have received a copy of the GNU Lesser General Public License
 ** along with this library; if not, write to the Free Software Foundation,
 ** Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 **/

// GroupingAlgorithm.java
//
// $Revision$
// $Date$
// $Author$
//


package cytoscape.layout;

import java.util.*;

import y.base.*;
import y.layout.*;


/**
 * An abstract base class providing an interface
 * used by <code>GroupWiseNodeGrouper</code>.
 *
 * GroupingAlgorithm provides the basis for
 * algorithms to partition an input graph into
 * (disjoing) node subsets.
 *
 */
public abstract class GroupingAlgorithm {
    /**
     * The graph
     */
    LayoutGraph iGraph;

    /**
     * Static values for connectedness table
     */
    final static int CONNECTED_TO = 1;
    final static int CONNECTED_FROM = 2;
    final static int CONNECTED_BI = 3;
    final static int NOT_CONNECTED = 0;


    /**
     * <code>Node -> Node</code> hash linking
     * each node in iGraph to its logical
     * parent node, as determined by
     * <code>getNodeGrouping()</code>.
     *
     * Subclasses are responsible for keeping
     * this up to date.
     */
    HashMap iParentMap;



    /**
     * <code>Node -> NodeList</code> hash
     * linking each node in iGraph to its
     * logical child nodes, again as determined
     * by <code>getNodeGrouping()</code>
     */
    HashMap iChildMap;



    /**
     * Default constructor
     */
    public GroupingAlgorithm() {}



    ////
    //
    // abstract public interface methods
    //
    ////

    /**
     * Calculate and return a <em>Grouping</em>.
     * A Grouping is a <code>Subgraph</code> where each node
     * represents a <em>Cluster</em> of nodes in the original.
     *
     * @param groupCount Number of nodes to which
     *   <code>iGraph</code> should be condensed.  i.e. the
     *   the number of groups to partition the graph into.
     * @return The newly created <code>Subgraph</code>.
     */
    public abstract Subgraph getNodeGrouping(int groupCount);

    /**
     * Return the <em>Cluster</em> of nodes associated
     * with the given <code>groupNode</code> in the
     * Grouping pointed at by <code>group</code>
     *
     * @return The <code>Subgraph</code> coresponding
     * to the nodes in the requested Cluster.
     */
    public abstract Subgraph getClusterByNode(Node groupNode, Subgraph group);





    ////
    //
    // over-rideable public interface methods
    //
    ////

    /**
     * Tell the class instance to refer to the given
     * <code>aGraph</code> on subsequent calls
     */
    public void useGraph(LayoutGraph aGraph) {
	// save graph object
	iGraph = aGraph;

	iParentMap = new HashMap();
	iChildMap = new HashMap();
    }

    /**
     * Re-map the node grouping back to the
     * original graph.  Uses <code>Subgraph</code>'s
     * <code>reInsert()</code> method
     */
    public void putNodeGrouping(Subgraph aGroup) {
	aGroup.reInsert();
    }

    /**
     * Re-map the cluster back to the original
     * graph, using <code>Subgraph</code>'s
     * <code>reInsert()/code> method
     */
    public void putClusterByNode(Subgraph aCluster) {
	aCluster.reInsert();
    }




    ////
    //
    // instance specific utility functions
    //
    ////

    /**
     * Utility function: update logical
     * HashMap mappings to show that
     * <code>aChild</code> is now a child
     * of <code>aParent</code>.
     */
    void joinNodes(Node aParent, Node aChild) {
	if (aParent == aChild) return;

	System.out.println("Joining "+aChild+" to "+aParent);

	iParentMap.put(aChild, aParent);

	// get pre-existing children vectors
	NodeList parentsChildren = (NodeList)iChildMap.get(aParent);
	NodeList childrensChildren = (NodeList)iChildMap.get(aChild);

	// create parent's children if necessary
	if (parentsChildren == null) {
	    parentsChildren = new NodeList();
	    iChildMap.put(aParent, parentsChildren);
	}

	// add gobbled element
	parentsChildren.add(aChild);

	// add children of gobbled element
	if (childrensChildren != null)
	    for (int i = 0; i < childrensChildren.size(); i++)
		parentsChildren.add(childrensChildren.elementAt(i));

	// remove child from iChildMap to free memory
	iChildMap.put(aChild, null);
    }



    ////
    //
    // static utility functions
    //
    ////

    /**
     * Calculate and return a connectedness
     * table for <code>graph</code>.  The table is
     * an array of n x n integers, corresponding
     * to the indeces of the nodes in
     * <code>graph.getNodeArray()</code>.
     *
     * <p>Values in the array at (u,v) take one of
     * the static values defined by this class.  They
     * are:
     * <ul>
     *  <li><code>NOT_CONNECTED</code>:
     *      u and v are not connected
     *  <li><code>CONNECTED_FROM</code>:
     *      v is connected to u
     *  <li><code>CONNECTED_TO</code>:
     *      u is connected to v
     *  <li><code>CONNECTED_BI</code>:
     *      u and v are recipricolly connected
     * </ul>
     */
    static int[][] gaConnected(LayoutGraph graph) {
	Node[] nodeList = graph.getNodeArray();
	int nC = graph.nodeCount();

	// initialize connectedness table
	int[][] connected = new int[nC][nC];
       	for (int i = 0; i < nC; i++)
	    for (int j = 0; j < nC; j++)
		connected[i][j] = NOT_CONNECTED;

	// debug text - owo 2002.03.28
	//for (int i = 0; i < nC; i++)
	//    System.out.println(i + "  " + nodeList[i].toString());

	// iterate over edges to set up connectedness
	for (EdgeCursor edges = graph.edges(); edges.ok(); edges.next()) {
	    int s = 0; int t = 0;

	    for (int i = 0; i < nC; i++) {
		if (nodeList[i] == edges.edge().source())
		    s = i;
		else if (nodeList[i] == edges.edge().target())
		    t = i;
	    }

	    // debug text - owo 2002.03.28
	    //System.out.println("edge " + s + " " + t);

	    // update connectedness considering directionality
	    if ((connected[s][t] == CONNECTED_FROM)||(connected[t][s] == CONNECTED_TO))
		connected[s][t] = connected[t][s] = CONNECTED_BI;
	    else {
		connected[s][t] = CONNECTED_TO;
		connected[t][s] = CONNECTED_FROM;
	    }
	}

	return connected;
    }



    // return a degree list for number of connections per node
    // based on connectedness table
    static int[] gaDegree(LayoutGraph graph, int[][] connected) {
	Node[] nodeList = graph.getNodeArray();
	int nC = graph.nodeCount();

	// initialize degree list (and sorted node list)
	int[] degree = new int[nC];

	// iterate over connectedness to calculate degree
	for (int i = 0; i < nC; i++) {
	    for (int j = 0; j < nC; j++)
		if (connected[i][j] != NOT_CONNECTED)
		    degree[i]++;
	}

	return degree;
    }


    // return matrix of distances between nodes
    static float[][] gaDistanceMatrix(LayoutGraph graph, int[][] connected) {
	// get node list and allocate space for
	// the distance matrix
        Node[] nodeList = graph.getNodeArray();
	int nC = graph.nodeCount();
	float[][] dm = new float[nC][nC];

	System.out.println("Distance matrix: ("+nC+" nodes)");

	// initialize 1st order defaults (from connected)
	for (int i = 0; i < nC; i++)
	    for (int j = 0; j < nC; j++)
		dm[i][j] = (float)(connected[i][j] != NOT_CONNECTED ? 1:0);

	System.out.println("  filling distance matrix...");

	boolean matrixChanged = true;

	for (int c = 0; (c < nC) && (matrixChanged); c++) {
	    // mark the matrix unchanged
	    matrixChanged = false;

	    // iterate over starting nodes
	    for (int sn = 0; sn < nC; sn++) {
		int pct = Math.round((((float) sn) / ((float) nC))*100);
		System.out.print("\r    pass "+c+"/2(?): "+pct+"%   ");

		// iterate over mid-point nodes
		for (int mn = 0; mn < nC; mn++) {

		    // iterate over target nodes
		    if (dm[sn][mn] != 0.0) {
			for (int tn = 0; tn < nC; tn++) {

			    // take the new distance from source
			    // to target, if conditions holding
			    if (dm[mn][tn] != 0.0) {
				float newDist = dm[sn][mn] + dm[mn][tn];

				if ((dm[sn][tn] == 0) ||
				    (newDist < dm[sn][tn])) {
				    dm[sn][tn] = newDist;
				    dm[tn][sn] = newDist;
				    matrixChanged = true;
				}
			    }
			}
		    }
		}
	    }

	    System.out.println();
	}

	// return distance map
	return dm;
    }



    /**
     * @return a symmetrical matrix of distances the given nodes.  The
     * diagonal of the returned matrix will be 0s, and all cells i,j such that
     * nodes with "index"es i and j are adjacent will have value 1.
     */
    public static float[][] gaFastDistanceMatrix(LayoutGraph graph, int[][] connected) {

	// get node list and allocate space for
	// the distance matrix
	int nC = graph.nodeCount();
	float[][] dm = new float[nC][nC];
	LinkedList[] lla = new LinkedList[nC];
	System.err.println( "Fast Alg" );

	// We don't have to make new Integers all the time, so we store the index
	// Objects in this array for reuse.
	Integer[] integers = new Integer[ nC ];

	for(int from_node_int=0; from_node_int< nC; from_node_int++) {
	    for(int to_node_int=0; to_node_int< nC; to_node_int++) {
		if (connected[from_node_int][to_node_int] != NOT_CONNECTED) {
		    lla[from_node_int].add(integers[to_node_int]);
		}
	    }
	}

	// TODO: REMOVE
	System.err.println( "Calculating all node distances.." );
	
	
	// Fill the nodes array with the nodes in their proper index locations.
	int index;
	LinkedList from_node_ll;
	
	LinkedList queue = new LinkedList();
	boolean[] completed_nodes = new boolean[ nC ];
	Iterator neighbors;
	LinkedList to_node_ll;
	int neighbor_index;
	int to_node_distance;
	int neighbor_distance;
	for( int from_node_index = 0;
	     from_node_index < nC;
	     from_node_index++ ) {
	    from_node_ll = lla[from_node_index];
	    if( from_node_ll.getFirst() == null ) {
		// Make the distances in this row all Integer.MAX_VALUE.
		Arrays.fill( dm[ from_node_index ], nC+2 );
		continue;
	    }
	    
	    /*
	      // TODO: REMOVE
	      System.err.print( "Calculating node distances from graph node " +
	      from_node );
	      System.err.flush();
	    */
	    
	    // Make the distances row and initialize it.
	    Arrays.fill( dm[ from_node_index ], nC+2 );
	    dm[ from_node_index ][ from_node_index ] = 0;
	    
	    // Reset the completed nodes array.
	    Arrays.fill( completed_nodes, false );
	    
	    // Add the start node to the queue.
	    queue.add( integers[from_node_index] );
	    
	    while( !( queue.isEmpty() ) ) {
		
		index = ( (Integer)queue.removeFirst()).intValue();
		if( completed_nodes[ index ] ) {
		    continue;
		}
		completed_nodes[ index ] = true;
		
		to_node_ll = lla[index];
		to_node_distance = (int)dm[ from_node_index ][ index ];
		
		if( index < from_node_index ) {
		    // Oh boy.  We've already got every distance from/to this node.
		    int distance_through_to_node;
		    for( int i = 0; i < nC; i++ ) {
			if( (int)dm[ index ][ i ] == nC+2 ) {
			    continue;
			}
			distance_through_to_node =
			    to_node_distance + (int)dm[ index ][ i ];
			if( distance_through_to_node <=
			    (int)dm[ from_node_index ][ i ] ) {
			    // Any immediate neighbor of a node that's already been
			    // calculated for that does not already have a shorter path
			    // calculated from from_node never will, and is thus complete.
			    if( (int)dm[ index ][ i ] == 1 ) { 
				completed_nodes[ i ] = true;
			    }
			    dm[ from_node_index ][ i ] =
				(float)distance_through_to_node;
			}
		    } // End for every node, update the distance using the distance from
		    // to_node.
		    // So now we don't need to put any neighbors on the queue or
		    // anything, since they've already been taken care of by the previous
		    // calculation.
		    continue;
		} // End if to_node has already had all of its distances calculated.
		
		neighbors = to_node_ll.listIterator(0);
		
		while( neighbors.hasNext() ) {
		    neighbor_index = ((Integer)neighbors.next()).intValue();
		    
		    if( completed_nodes[ neighbor_index ] ) {
			// We've already done everything we can here.
			continue;
		    }
		    
		    neighbor_distance = (int)dm[ from_node_index ][ neighbor_index ];
		    
		    if( ( to_node_distance != nC+2 ) &&
			( neighbor_distance > ( to_node_distance + 1 ) ) ) {
			dm[ from_node_index ][ neighbor_index ] =
			    (float)( to_node_distance + 1 );
			queue.addLast( integers[neighbor_index] );
		    }
		    
		    // TODO: REMOVE
		    /*
		      System.err.print( "." );
		      System.err.flush();
		    */
		    
		} // For each of the next nodes' neighbors
		// TODO: REMOVE
		/*
		  System.err.print( "|" );
		  System.err.flush();
		*/
	    } // For each to_node, in order of their (present) distances
	    
	    // TODO: REMOVE
	    /*
	      System.err.println( "done." );
	    */
	    
	} // For each from_node
	
	// TODO: REMOVE
	System.err.println( "..Done calculating all node distances." );
	
	return dm; // owoUP
    } // calculateAllNodeDistances(..)




}


