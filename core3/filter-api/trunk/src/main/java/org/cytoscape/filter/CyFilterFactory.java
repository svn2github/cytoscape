package org.cytoscape.filter;

import java.io.Reader;

/**
 * A factory for creating a specific type of <code>CyFilter</code>. 
 */
public interface CyFilterFactory {
	/**
	 * Returns the unique identifier of the type of filter created by this
	 * factory.
	 * @return
	 */
	String getIdentifier();
	
	/**
	 * Returns a new <code>CyFilter</code> instance.
	 * @return
	 */
	CyFilter getFilter();
	
	/**
	 * Returns a new <code>CyFilter</code> instance from its serialized
	 * representation.
	 * @param reader
	 * @return
	 */
	CyFilter getFilter(Reader reader);
}
