package org.cytoscape.work;

/**
 * A convenience class for task factories that don't use Tunables.
 */
public abstract class SimpleTaskFactory implements TaskFactory {
	@Override
	public boolean isReady() {
		return true;
	}
}
