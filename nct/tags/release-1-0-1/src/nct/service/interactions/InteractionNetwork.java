
//============================================================================
// 
//  file: InteractionNetwork.java
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



package nct.service.interactions;

import java.util.*;
import nct.graph.Graph;

/**
 * A generic interface that describes a method used to update a graph based on
 * some sort of interaction network input.  The expected usage if for the user
 * to create a Graph containing no nodes/edges, then create an InteractionNetwork,
 * and then call updateGraph containing the empty graph which will add the edges
 * and nodes found in the InteractionNetwork to the graph.  
<code>
Graph&lt;String,Double&gt; graph = new BasicGraph&lt;String,Double&gt;();
InteractionNetwork intNet = new ConcreteInteractionNetwork( LotOfData );
intNet.updateGraph( graph );
</code>
 * This roundabout way of getting information from a data source into a graph allows 
 * the InteractionNetwork to be constructed in any manner of ways.
 */
public interface InteractionNetwork<NodeType extends Comparable<? super NodeType>,WeightType extends Comparable<? super WeightType>>   {

	/**
	 * Updates the given graph with whatever values are created from
	 * the interaction network.
	 * @param graph The graph to be updated with information (i.e. nodes
	 * and edges) from this interaction network.
	 */
	public void updateGraph(Graph<NodeType,WeightType> graph );
}
