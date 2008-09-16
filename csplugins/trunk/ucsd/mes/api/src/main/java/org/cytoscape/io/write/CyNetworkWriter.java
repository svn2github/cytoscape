
package org.cytoscape.io.write;

import org.cytoscape.model.CyNetwork;
import java.util.List;

/**
 * The interface used to write network file types.
 */
public interface CyNetworkWriter extends CyWriter {

	/**
	 * @param networks A non-null list of {@link CyNetwork} objects.
	 */
	public void setNetworks(List<CyNetwork> networks);
}
