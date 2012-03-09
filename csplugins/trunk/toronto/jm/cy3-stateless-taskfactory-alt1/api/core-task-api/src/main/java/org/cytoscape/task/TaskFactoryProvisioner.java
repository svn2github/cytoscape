package org.cytoscape.task;

import org.cytoscape.work.TaskFactory;

public interface TaskFactoryProvisioner {
	 TaskFactory createFor(NetworkTaskFactory factory);
	 TaskFactory createFor(NetworkViewTaskFactory factory);
	 TaskFactory createFor(NetworkCollectionTaskFactory factory);
	 TaskFactory createFor(NetworkViewCollectionTaskFactory factory);
	 TaskFactory createFor(TableTaskFactory factory);
}
