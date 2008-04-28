package org.cytoscape.coreplugin.cpath2.util;

import org.cytoscape.GraphPerspective;
import cytoscape.Cytoscape;
import org.cytoscape.attributes.CyAttributes;

import java.util.Set;
import java.util.HashSet;

import org.mskcc.biopax_plugin.mapping.MapBioPaxToCytoscape;
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
     * @return Set<GraphPerspective>
     */
    public static Set<GraphPerspective> getNetworkSet(int type) {

        // set to return
        Set<GraphPerspective> networkSet = new HashSet<GraphPerspective>();

        // get set of cynetworks
        Set<GraphPerspective> cyNetworks = (Set<GraphPerspective>) Cytoscape.getNetworkSet();
        if (cyNetworks.size() == 0) return cyNetworks;

        CyAttributes networkAttributes = Cytoscape.getNetworkAttributes();
        for (GraphPerspective net : cyNetworks) {
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
