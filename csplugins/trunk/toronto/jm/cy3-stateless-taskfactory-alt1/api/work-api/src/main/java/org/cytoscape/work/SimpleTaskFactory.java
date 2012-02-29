package org.cytoscape.work;

/**
 * A convenience class for task factories that don't use Tunables.
 */
public abstract class SimpleTaskFactory implements TaskFactory<Object> {
	@Override
	public TaskIterator createTaskIterator(Object tunableContext) {
		return createTaskIterator();
	}
	
	@Override
	public Object createTunableContext() {
		return null;
	}

	protected abstract TaskIterator createTaskIterator();
}
