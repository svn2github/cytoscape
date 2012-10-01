
package org.cytoscape.taglets.compatibility;

import java.util.Map;

/**
 * A taglet that describes the versioning and upgrade policy for
 * API interfaces.
 */
public class ApiInterfaceTaglet extends AbstractApiTaglet {
    
  	/**
	 * Constructor.
	 */
	public ApiInterfaceTaglet() {
		super("CyAPI.Api.Interface",
		      "API Interface",
			  "We expect that this interface will be used but not implemented by " +
			  "developers using this interface.  As such, we reserve the right to " + 
			  "add methods to the interface as part of minor version upgrades.  We " + 
			  "will not remove methods for any changes other than major version " + 
			  "upgrades."
			  );
	}

	/**
	 * The method that registers this taglet.
	 * @param tagletMap The map used to which this taglet should be added.
	 */
	@SuppressWarnings("unchecked")
	public static void register(Map tagletMap) {
		registerTaglet(tagletMap, new ApiInterfaceTaglet());
	}
}
