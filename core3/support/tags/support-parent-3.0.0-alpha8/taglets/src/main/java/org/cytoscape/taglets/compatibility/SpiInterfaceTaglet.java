
package org.cytoscape.taglets.compatibility;

import java.util.Map;

/**
 * A taglet that describes the versioning and upgrade policy for
 * SPI interfaces. 
 */
public class SpiInterfaceTaglet extends AbstractApiTaglet {
    
  	/**
	 * Constructor.
	 */
	public SpiInterfaceTaglet() {
		super("CyAPI.Spi.Interface",
		      "SPI Interface",
			  "We expect that this interface will be implemented. Therefore to " + 
			  "maintain backwards compatibility this interface will only be " + 
			  "modified for major version updates." 
			  );
	}

	/**
	 * The method that registers this taglet.
	 * @param tagletMap The map used to which this taglet should be added.
	 */
	@SuppressWarnings("unchecked")
	public static void register(Map tagletMap) {
		registerTaglet(tagletMap, new SpiInterfaceTaglet());
	}
}
