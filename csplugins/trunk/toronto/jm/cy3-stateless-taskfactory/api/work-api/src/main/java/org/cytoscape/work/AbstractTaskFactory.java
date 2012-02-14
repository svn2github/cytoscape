package org.cytoscape.work;

/**
 * A convenience class for TaskFactory implementations that don't require
 * a context definition.
 */
public abstract class AbstractTaskFactory implements TaskFactory<Object> {
	@Override
	public Object createTaskContext() {
		return new Object();
	}
}
