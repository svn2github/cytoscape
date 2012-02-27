
package org.cytoscape.internal.test.tunables;

import org.cytoscape.work.TaskIterator;
import org.cytoscape.work.TaskFactory;


public class ScootersTunableTaskFactory implements TaskFactory {

	public ScootersTunableTaskFactory() { }

	public TaskIterator createTaskIterator() {
		return new TaskIterator(new ScootersTunableTask());
	}

}
