
package org.cytoscape.internal.test.tunables;

import org.cytoscape.work.AbstractTaskFactory;
import org.cytoscape.work.TaskIterator;


public class ScootersTunableTaskFactory extends AbstractTaskFactory {

	public ScootersTunableTaskFactory() { }

	public TaskIterator createTaskIterator() {
		return new TaskIterator(new ScootersTunableTask());
	}

}
