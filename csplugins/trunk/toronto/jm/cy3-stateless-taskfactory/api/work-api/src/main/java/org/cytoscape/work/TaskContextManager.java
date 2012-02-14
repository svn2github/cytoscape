package org.cytoscape.work;

public interface TaskContextManager {
	<C> C getContext(TaskFactory<C> factory);
	void registerTaskFactory(TaskFactory<?> factory);
	void unregisterTaskFactory(TaskFactory<?> factory);
	void reset();
}
