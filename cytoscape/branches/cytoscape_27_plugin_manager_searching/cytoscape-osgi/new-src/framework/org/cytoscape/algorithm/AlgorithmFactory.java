

package org.cytoscape.algorithm;

import java.util.List;
import org.cytoscape.algorithm.control.Tunable;

public interface AlgorithmFactory {
	
	Algorithm createAlgorithm();
	List<Tunable> createParameters();
}
