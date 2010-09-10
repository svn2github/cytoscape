package org.cytoscape.work;


// TODO: Should we replace hasTunables() w/ a getTunableInterceptor() method?

/** Provides access to a TunableInterceptor to all derived classes and a utility method to determine
 *  if an object has been annotated with Tunables.
 */
public abstract class AbstractTaskManager implements TaskManager {
	protected final TunableInterceptor tunableInterceptor;

	public AbstractTaskManager(final TunableInterceptor tunableInterceptor) {
		this.tunableInterceptor = tunableInterceptor;
	}

	final public boolean hasTunables(final Object o) {
		return tunableInterceptor.hasTunables(o);
	}
}
