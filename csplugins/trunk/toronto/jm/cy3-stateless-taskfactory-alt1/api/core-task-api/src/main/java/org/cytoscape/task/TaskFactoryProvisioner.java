package org.cytoscape.task;

import org.cytoscape.work.TaskFactory;

public interface TaskFactoryProvisioner {
	<T> TaskFactory<T> createFor(NetworkTaskFactory<T> factory);
	<T> TaskFactory<T> createFor(NetworkViewTaskFactory<T> factory);
	<T> TaskFactory<T> createFor(NetworkCollectionTaskFactory<T> factory);
	<T> TaskFactory<T> createFor(NetworkViewCollectionTaskFactory<T> factory);
	<T> TaskFactory<T> createFor(TableTaskFactory<T> factory);
}
