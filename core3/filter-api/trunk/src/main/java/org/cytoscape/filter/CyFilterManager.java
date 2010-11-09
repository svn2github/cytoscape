package org.cytoscape.filter;

import java.util.Collection;

/**
 * The central registry for all <code>CyFilter</code>s and their
 * corresponding factories. 
 */
public interface CyFilterManager {
	/**
	 * Returns the <code>CyFilterFactory</code> with the given identifier, or
	 * null, if no such factory was previously registered.
	 * @param identifier
	 * @return
	 */
	CyFilterFactory getFilterFactory(String identifier);
	
	/**
	 * Returns all of the <code>CyFilterFactory</code>s registered with this
	 * object.
	 * @return
	 */
	Collection<CyFilterFactory> getAllFilterFactories();
}
