
package org.cytoscape.taglets.compatibility;

import java.util.Map;

/**
 * A taglet that describes the versioning and upgrade policy for
 * final classes. 
 */
public class FinalClassTaglet extends AbstractApiTaglet {
    
  	/**
	 * Constructor.
	 */
	public FinalClassTaglet() {
		super("CyAPI.Final.Class",
		      "Final Class",
			  "This class is final and therefore can't be extended by users. " +
			  "This means that we may add methods for minor version updates. " +
			  "Methods will only be removed for major version updates."
			  );
	}

	/**
	 * The method that registers this taglet.
	 * @param tagletMap The map used to which this taglet should be added.
	 */
	@SuppressWarnings("unchecked")
	public static void register(Map tagletMap) {
		registerTaglet(tagletMap, new FinalClassTaglet());
	}
}
