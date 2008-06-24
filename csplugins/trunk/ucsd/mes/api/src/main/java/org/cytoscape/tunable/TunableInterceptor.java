
package org.cytoscape.tunable;

public interface TunableInterceptor { 

	/**
	 * Intercepts an object and should look
	 * for {@link Tunable} annotated {@link Field}s in the command,
	 * process the {@link Tunable}s using the {@link Handler}s
	 * specified in the implementation.
	 */
	public void intercept(Object o);
}
