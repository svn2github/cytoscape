package cytoscape.util;

import java.util.Set;

import cytoscape.CyNetwork;
import cytoscape.Cytoscape;


public class TestUtil {
	/**
	 * Destroys all networks in the root graph.  Please note that the nodes and edges of said networks are also being destroyed.
	 */
	public static void destroyNetworksEdgesAndNodes() {
		final Set<CyNetwork> networks = Cytoscape.getNetworkSet();
		for (final CyNetwork network : networks) {
			Cytoscape.destroyNetwork(network, /* destroy_unique = */true);
		}
	}

}
