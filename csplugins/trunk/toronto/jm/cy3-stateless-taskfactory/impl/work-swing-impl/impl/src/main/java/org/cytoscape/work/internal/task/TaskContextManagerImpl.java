package org.cytoscape.work.internal.task;

import java.util.Map;
import java.util.WeakHashMap;

import org.cytoscape.work.TaskContextManager;
import org.cytoscape.work.TaskFactory;

public class TaskContextManagerImpl implements TaskContextManager {

	Map<TaskFactory<?>, Object> contexts;
	
	public TaskContextManagerImpl() {
		contexts = new WeakHashMap<TaskFactory<?>, Object>();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <C> C getContext(TaskFactory<C> factory) {
		return (C) contexts.get(factory);
	}

	@Override
	public void registerTaskFactory(TaskFactory<?> factory) {
		contexts.put(factory, factory.createTaskContext());
	}

	@Override
	public void unregisterTaskFactory(TaskFactory<?> factory) {
		contexts.remove(factory);
	}

	@Override
	public void reset() {
		contexts.clear();
	}
}
