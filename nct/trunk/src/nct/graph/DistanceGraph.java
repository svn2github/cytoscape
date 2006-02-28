
//============================================================================
// 
//  file: DistanceGraph.java
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



package nct.graph;

/**
 * This interface describes a method that returns the distance between
 * two nodes.  What constitutes the distance is left to the implementer.
 */
public interface DistanceGraph<NodeType extends Comparable<? super NodeType>,
                               WeightType extends Comparable<? super WeightType>> 
	extends Graph<NodeType,WeightType> {

	/**
	 * Returns the minimum distance between the specified nodes.
	 * @param nodeA From node.
	 * @param nodeB To node.
	 * @return The distance between the nodes specified.
	 */
	public byte getDistance(NodeType nodeA, NodeType nodeB);
}
