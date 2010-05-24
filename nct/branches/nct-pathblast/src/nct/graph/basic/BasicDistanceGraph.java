
//============================================================================
// 
//  file: BasicDistanceGraph.java
// 
//  Copyright (c) 2006, University of California San Diego 
// 
//  This program is free software; you can redistribute it and/or modify it 
//  under the terms of the GNU General Public License as published by the 
//  Free Software Foundation; either version 2 of the License, or (at your 
//  option) any later version.
//  
//  This program is distributed in the hope that it will be useful, but 
//  WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY 
//  or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License 
//  for more details.
//  
//  You should have received a copy of the GNU General Public License along 
//  with this program; if not, write to the Free Software Foundation, Inc., 
//  59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
// 
//============================================================================



package nct.graph.basic;

import java.util.*;
import java.lang.*;

import nct.graph.DistanceGraph;

/**
 * An extension of BasicGraph that calculates the minimum distance
 * between nodes.
 */
public class BasicDistanceGraph<NodeType extends Comparable<? super NodeType>,WeightType extends Comparable<? super WeightType>> extends BasicGraph<NodeType,WeightType>
	implements DistanceGraph<NodeType,WeightType> {

	/**
	 * A mapping of node to node to distance.  Note that this
	 * structure only includes mappings between nodes with
	 * distances lte 2.  Anything greater is not stored and is
	 * assumed be of distance 3.
	 */
	protected Map<NodeType,Map<NodeType,Byte>> distMap; 

	/**
	 * Constructor.
	 */
	public BasicDistanceGraph() {
		this("none");
	}

	/**
	 * Constructor.
	 * @param id The fileName identifying the graph.
	 */
	public BasicDistanceGraph(String id) {
		super(id);
		distMap = new HashMap<NodeType,Map<NodeType,Byte>>();

		// We don't create the maps now because we need to wait for 
		// edges and nodes to be added.
	}

	/**
	 * Returns the minimum distance between the specified nodes.
	 * @param nodeA From node.
	 * @param nodeB To node.
	 * @return The distance between the nodes specified.
	 */
	public byte getDistance(NodeType nodeA, NodeType nodeB) {
		if ( numberOfEdges() > 0 && !assumeGraphFinished )
			createMaps();
		
		// make sure the edges are actually valid
		if ( !weightMap.containsKey(nodeA) || !weightMap.containsKey(nodeB) )
			return -1;

		if (nodeA.equals( nodeB ) ) 
			return 0;

		Byte b = distMap.get(nodeA).get(nodeB);
		if ( b != null )
			return b.byteValue();
		else
			return (byte)3;
	}

	/**
	 * This method actually creates the maps.  The rough algorithm goes like so:
	 * For each node, look at its neighbors.  
	 * Each node to neighbor has a distance of 1.
	 * Each neighbor to neighbor has a distance of 2 (neighbor1 to node to neighbor2).
	 * Anything not recorded is assumed to have a distance of 3.
	 */
	private void createMaps() {
		//Long beginTime = System.currentTimeMillis();
		assumeGraphFinished = true;

		// initialize now to avoid the containsKey() checks later
		for (NodeType node: getNodes()) 
			distMap.put(node, new HashMap<NodeType,Byte>());

		Map<NodeType,Byte> nodeDist = null; 
		Map<NodeType,Byte> neighbor1Dist = null; 
		Map<NodeType,Byte> neighbor2Dist = null; 

		for (NodeType node: getNodes()) {

			boolean distPut = false;
			
			// to minimize the number of gets
			nodeDist = distMap.get(node);

			for (NodeType neighbor1: getNeighbors(node)) {

				// to minimize the number of gets
				neighbor1Dist = distMap.get(neighbor1);

				nodeDist.put(neighbor1,(byte)1);
				neighbor1Dist.put(node,(byte)1);

				for (NodeType neighbor2: getNeighbors(node)) {

					if ( neighbor1 == neighbor2 )
						continue;
			
					// to minimize the number of gets
					neighbor2Dist = distMap.get(neighbor2);

					// only add new dist/prob if it hasn't already been set
					// if it has been set once, we know that it has been set
					// with the min possible value.

					// only check one direction because we always set both
					// at a given time.

					if ( !neighbor1Dist.containsKey(neighbor2) ) {
						neighbor1Dist.put(neighbor2,(byte)2);
						neighbor2Dist.put(neighbor1,(byte)2);
					}	
				}
			}
		}

		//Long totalTime = System.currentTimeMillis() - beginTime;
		//System.out.println("createMap duration: " + totalTime + " for size: " + probMap.size());
	}
}
