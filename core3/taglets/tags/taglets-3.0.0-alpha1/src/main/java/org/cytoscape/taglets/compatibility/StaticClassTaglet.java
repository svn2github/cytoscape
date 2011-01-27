
package org.cytoscape.taglets.compatibility;

import java.util.Map;

/**
 * A taglet that describes the versioning and upgrade policy for
 * static classes. 
 */
public class StaticClassTaglet extends AbstractApiTaglet {
    
  	/**
	 * Constructor.
	 */
	public StaticClassTaglet() {
		super("CyAPI.Static.Class",
		      "Static Class",
			  "This class is static and therefore can't be extended by users. " +
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
		registerTaglet(tagletMap, new StaticClassTaglet());
	}
}
