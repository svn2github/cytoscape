package org.cytoscape.cpath2.internal.util;

/**
 * This class executes code using this bundle's ClassLoader as the current
 * thread's context ClassLoader.  This enables the service discovery mechanism
 * used by some StAX implementations to function properly under OSGi.
 */
public abstract class StaxHack<T> {
	public abstract T runWithHack();
	
	public final T run() {
		Thread thread = Thread.currentThread();
		ClassLoader oldClassLoader = thread.getContextClassLoader();
		try {
			thread.setContextClassLoader(getClass().getClassLoader());
			return runWithHack();
		} finally {
			thread.setContextClassLoader(oldClassLoader);
		}
	}
}