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
	public Map<String,Map<String,Double>> expectationValues(SequenceGraph sg1, SequenceGraph sg2);
}


