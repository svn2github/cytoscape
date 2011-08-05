
package org.cytoscape.taglets.compatibility;

import java.util.Map;

/**
 * A taglet that describes the versioning and upgrade policy for
 * enums.
 */
public class EnumClassTaglet extends AbstractApiTaglet {
    
  	/**
	 * Constructor.
	 */
	public EnumClassTaglet() {
		super("CyAPI.Enum.Class",
		      "Enum",
			  "This class is an enum therefore can't be extended by users. " +
			  "This means that we may add methods or <i>enum values</i> " +
			  "for minor version updates. " +
			  "Methods or enum values will only be removed for major version updates. " 
			  );
	}

	/**
	 * The method that registers this taglet.
	 * @param tagletMap The map used to which this taglet should be added.
	 */
	@SuppressWarnings("unchecked")
	public static void register(Map tagletMap) {
		registerTaglet(tagletMap, new EnumClassTaglet());
	}
}
