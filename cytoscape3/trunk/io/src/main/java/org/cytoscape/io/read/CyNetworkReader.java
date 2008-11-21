
package org.cytoscape.io.read;

import org.cytoscape.model.CyNetwork;
import java.util.List;

/**
 * Extends the {@link CyReader} interface to support the reading of
 * {@link CyNetwork} objects. 
 */
public interface CyNetworkReader extends CyReader {

	/** 
	 * Once the {@link CyReader#read()} method finishes executing, this 
	 * method should return a {@link CyNetwork} object.
	 * @return A {@link CyNetwork} object.
	 */
	public CyNetwork getReadNetwork();
}


