
package org.cytoscape.io.write;

import org.cytoscape.model.network.CyNetwork;
import java.util.List;

/**
 * The interface used to write network file types.
 */
public interface CyNetworkWriter extends CyWriter {

	public void setNetworks(List<CyNetwork> networks);
}
