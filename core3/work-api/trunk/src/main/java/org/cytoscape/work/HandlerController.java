package org.cytoscape.work;

import java.util.Map;

/**
 * Provide access for the class implementing this interface to the <code>Handlers</code> that will be created for its fields or methods
 * 
 * @author pasteur
 *
 */

public interface HandlerController {
	
	/**
	 * this method gives the object access to the handlers
	 * 
	 * @param tunableMap a map that contained the <code>Handlers</code> identified by their names
	 */
	void controlHandlers(Map<String,Handler> tunableMap);
}
