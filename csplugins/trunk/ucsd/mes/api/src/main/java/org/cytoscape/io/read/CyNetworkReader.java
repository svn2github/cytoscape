
package org.cytoscape.io.read;

import org.cytoscape.model.network.CyNetwork;
import java.util.List;

public interface CyNetworkReader extends CyReader {

	public List<CyNetwork> getReadNetworks();
}
