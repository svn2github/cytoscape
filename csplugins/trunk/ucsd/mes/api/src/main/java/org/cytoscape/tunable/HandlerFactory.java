
package org.cytoscape.tunable;

import java.lang.reflect.Field;

/**
 * A Factory that produces {@link Handler}s based on the
 * type of {@link Field} specified. 
 * Should be implemented in a hidden package and only
 * used by the extension of {@link AbstractTunableInterceptor}.
 */
public interface HandlerFactory<T extends Handler> {

	/**
	 * Should return null if no handler exists for the
	 * given field type.
	 */
	public T getHandler(Field f, Object o, Tunable t);

}
