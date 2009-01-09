

package org.cytoscape.algorithm;

import org.cytoscape.algorithm.control.Tunable;
import java.util.List; 

public interface Algorithm {
	
	void execute(List<Tunable> lt);	
}
