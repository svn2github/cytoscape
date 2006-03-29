
//============================================================================
// 
//  file: GraphRandomizer.java
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

package nct.graph.util;

import nct.graph.Graph;
import java.util.Random;

/**
 * A randomization interface.  An implementing class should, in some way, 
 * randomize the input graph. How the randomization occurs and inputs
 * needed for the randomization should be provided in the constructor of
 * the implementing class.
 */
public interface GraphRandomizer<NodeType extends Comparable<? super NodeType>,WeightType extends Comparable<? super WeightType>> {

	/**
	 * The method used to randomize the graph.
	 * @param g The graph to be randomized.
	 */
	public void randomize(Graph<NodeType,WeightType> g); 

}
