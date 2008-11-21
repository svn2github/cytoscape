package org.cytoscape.coreplugin.cpath2.util;

import cytoscape.Cytoscape;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.coreplugin.cpath2.view.SearchDetailsPanel;
import org.cytoscape.coreplugin.cpath2.view.model.NetworkWrapper;
import org.cytoscape.coreplugin.cpath2.web_service.CPathProperties;

import javax.swing.*;
import java.net.URL;
import java.util.Set;
import java.util.Vector;

/**
 * Network Merge Utility.
 */
public class NetworkMergeUtil {
    private Vector networkVector;

    /**
     * Constructor.
     */
    public NetworkMergeUtil() {
        CPathProperties cPathProperties = CPathProperties.getInstance();
        int downloadMode = cPathProperties.getDownloadMode();

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

    /**
     * Prompt User for Network to Merge.
     * @return NetworkWrapper Object.
     */
    public NetworkWrapper promptForNetworkToMerge() {
        if (mergeNetworksExist()) {
            NetworkWrapper[] networks = (NetworkWrapper[]) getMergeNetworks().toArray
                (new NetworkWrapper[getMergeNetworks().size()]);
            URL iconURL = SearchDetailsPanel.class.getResource("/images/question.png");
            Icon icon = null;
            if (iconURL != null) {
                icon = new ImageIcon(iconURL);
            }
            NetworkWrapper mergeNetwork = (NetworkWrapper)
                    JOptionPane.showInputDialog(Cytoscape.getDesktop(),
                    "Create new network or merge with existing network?", "Create / Merge",
                    JOptionPane.PLAIN_MESSAGE, icon,
                    networks, networks[0]);
            if (mergeNetwork == null) {
                return null;
            } else {
                return mergeNetwork;
            }
        }
        return null;
    }

    /**
     * Do mergeable network exist?
     * @return true or false.
     */
    public boolean mergeNetworksExist() {
        if (networkVector != null && networkVector.size() >1) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Candidate networks for merging.
     * @return Vector of NetworkWrapper Objects.
     */
    public Vector getMergeNetworks() {
        return networkVector;
    }
}
