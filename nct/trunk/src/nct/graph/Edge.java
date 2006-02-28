
//============================================================================
// 
//  file: Edge.java
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
 * An generic interface describing the basic components of an edge: 
 * source and target nodes, edge weight, and description.
 */
public interface Edge<NodeType extends Comparable<? super NodeType>,
                      WeightType extends Comparable<? super WeightType>> 
	extends Comparable<Edge<NodeType,WeightType>> {

	/**
	 * @return The description of the edge.
	 */
	public String getDescription();

	/**
	 * @param desc The description of the edge.
	 */
	public void setDescription(String desc);

	/**
	 * @return The source node of this edge.
	 */
	public NodeType getSourceNode();

	/**
	 * @return The target node of this edge.
	 */
	public NodeType getTargetNode();

	/**
	 * @return The weight of this edge.
	 */
	public WeightType getWeight();
}
