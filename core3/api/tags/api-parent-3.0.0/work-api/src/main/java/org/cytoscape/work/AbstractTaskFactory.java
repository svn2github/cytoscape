package org.cytoscape.work;

/**
 * A TaskFactory that is always ready to produce a TaskIterator.
 * @CyAPI.Abstract.Class
 */
public abstract class AbstractTaskFactory implements TaskFactory {
	@Override
	public boolean isReady() {
		return true;
	}
}
