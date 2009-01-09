
package org.cytoscape.algorithm.control;

import java.util.List;

public interface InteractionController {
	// gui calls this
	public List<Tunable> getParameters();

	// this calls createAlgorithm on the AlgorithmFactory
	public void trigger(List<Tunable> lt);

	// gui calls this
	public void close();

	// InteractiveAlgorithm calls this
	public void updateUI(AlgorithmStatus as, List<Tunable> lt);
}
