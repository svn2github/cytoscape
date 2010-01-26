package org.cytoscape.coreplugin.cpath2.util;

import cytoscape.CyNetwork;
import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;

import java.util.Set;
import java.util.HashSet;

import cytoscape.coreplugins.biopax.MapBioPaxToCytoscape;
import org.cytoscape.coreplugin.cpath2.web_service.CPathProperties;
import org.cytoscape.coreplugin.cpath2.cytoscape.BinarySifVisualStyleUtil;

/**
 * Utility for Finding Groups of Networks.
 *
 * @author Ethan Cerami.
 */
public class NetworkGroupUtil {

    /**
     * Constructs a set of X networks.
     *
     * @return Set<CyNetwork>
     */
    public static Set<CyNetwork> getNetworkSet(int type) {

        // set to return
        Set<CyNetwork> networkSet = new HashSet<CyNetwork>();

        // get set of cynetworks
        Set<CyNetwork> cyNetworks = (Set<CyNetwork>) Cytoscape.getNetworkSet();
        if (cyNetworks.size() == 0) return cyNetworks;

        CyAttributes networkAttributes = Cytoscape.getNetworkAttributes();
        for (CyNetwork net : cyNetworks) {
            String networkID = net.getIdentifier();

            String attribute = MapBioPaxToCytoscape.BIOPAX_NETWORK;
            if (type == CPathProperties.DOWNLOAD_REDUCED_BINARY_SIF) {
                attribute = BinarySifVisualStyleUtil.BINARY_NETWORK;
            }
            Boolean b = networkAttributes.getBooleanAttribute(networkID, attribute);
            if (b != null && b) {
                networkSet.add(net);
            }
        }

        // outta here
        return networkSet;
    }
}
