
package org.cytoscape.taglets.compatibility;

import java.util.Map;

/**
 * A taglet that describes the versioning and upgrade policy for
 * abstract classes. 
 */
public class AbstractClassTaglet extends AbstractApiTaglet {
    
  	/**
	 * Constructor.
	 */
	public AbstractClassTaglet() {
		super("CyAPI.Abstract.Class",
		      "Abstract Class",
			  "This class is abstract and meant to be extended by users. " +
			  "This means that we may add methods for minor version updates. " +
			  "Methods will only be removed for major version updates. " 
			  );
	}

	/**
	 * The method that registers this taglet.
	 * @param tagletMap The map used to which this taglet should be added.
	 */
	@SuppressWarnings("unchecked")
	public static void register(Map tagletMap) {
		registerTaglet(tagletMap, new AbstractClassTaglet());
	}
}
