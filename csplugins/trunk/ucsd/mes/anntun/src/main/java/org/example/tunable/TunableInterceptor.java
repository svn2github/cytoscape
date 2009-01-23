
package org.example.tunable;

public interface TunableInterceptor { 

	/**
	 * Intercepts {@link Object} and should look
	 * for {@link Tunable} annotated {@link Field}s in the object,
	 * process the {@link Tunable}s using the {@link Handler}s
	 * specified in the implementation.
	 */
	public void intercept(Object o);
}
