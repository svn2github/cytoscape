
package org.cytoscape.algorithm.ui;

import org.cytoscape.algorithm.AlgorithmStatus;
import org.cytoscape.algorithm.control.Tunable;

public interface UI {
	void updateUI(AlgorithmStatus as, List<Tunable> lt);
}


