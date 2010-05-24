
//============================================================================
// 
//  file: HomologyModel.java
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



package nct.service.homology;

import java.util.*;
import nct.graph.SequenceGraph; 

/**
 * An interface that provides access to something that provides
 * expectation values (e.g.Blast or Fasta).
 */
public interface HomologyModel {

	/**
	 * A method that returns a mapping of expectation values between nodes
	 * of the two graphs.
	 * @param sg1 The first SequenceGraph that contains nodes to be compared. 
	 * @param sg2 The second SequenceGraph that contains nodes to be compared. 
	 * @return A mapping of nodes from the first graph to the second graph
	 * and the value of that relationship, in this case the expectation
	 * value.
	 */
	public Map<String,Map<String,Double>> expectationValues(SequenceGraph<String,Double> sg1, SequenceGraph<String,Double> sg2);
}


