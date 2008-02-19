package org.cytoscape.coreplugin.cpath2.util;

import org.cytoscape.coreplugin.cpath2.web_service.CPathProperties;
import org.cytoscape.coreplugin.cpath2.view.model.NetworkWrapper;
import cytoscape.CyNetwork;

import java.util.Set;
import java.util.Vector;

/**
 * Network Merge Utility.
 */
public class NetworkMergeUtil {
    private Vector networkVector;

    public NetworkMergeUtil() {
        CPathProperties cPathProperties = CPathProperties.getInstance();
        int downloadMode = cPathProperties.getDownloadMode();
        if (downloadMode == CPathProperties.DOWNLOAD_REDUCED_BINARY_SIF) {

            //  Get networks which we could merge with.
            Set<CyNetwork> networkSet = NetworkGroupUtil.getNetworkSet(downloadMode);

            networkVector = new Vector();
            networkVector.add(new NetworkWrapper(null));
            if (networkSet != null && networkSet.size() > 0) {
                for (CyNetwork net : networkSet) {
                    NetworkWrapper netWrapper = new NetworkWrapper (net);
                    networkVector.add(netWrapper);
                }
            }
        }
    }

    public boolean mergeNetworksExist() {
        if (networkVector != null && networkVector.size() >1) {
            return true;
        } else {
            return false;
        }
    }

    public Vector getMergeNetworks() {
        return networkVector;
    }
}
