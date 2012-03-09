package org.cytoscape.work;

/**
 * A convenience class for task factories that don't have special
 * provisioning requirements.
 */
public abstract class AbstractTaskFactory implements TaskFactory {
	@Override
	public boolean isReady() {
		return true;
	}
}
