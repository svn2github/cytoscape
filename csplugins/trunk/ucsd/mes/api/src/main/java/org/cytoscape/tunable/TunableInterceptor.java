
package org.cytoscape.tunable;

/**
 * A TunableInterceptor is meant to examine an {@link Object} and
 * infer a user interface based on {@link java.lang.reflect.Field}s
 * in the {@link Object} that are annotated with the {@link Tunable}
 * annotation.  Once {@link Tunable} fields are identified, the UI
 * that is inferred can, at its discretion, set the values of the
 * fields base on input from the user.
 */
public interface TunableInterceptor { 

	/**
	 * Intercepts an object and should look
	 * for {@link Tunable} annotated {@link java.lang.reflect.Field}s in the object
	 * and process the {@link Tunable}s using the {@link Handler}s
	 * specified in the implementation.
	 */
	public void intercept(Object o);
}
