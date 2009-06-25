package org.cytoscape.coreplugin.cpath2.view.model;

import cytoscape.CyNetwork;

public class NetworkWrapper {
    private CyNetwork network;

    public NetworkWrapper (CyNetwork network) {
        this.network = network;
    }

    public CyNetwork getNetwork() {
        return network;
    }

    public String toString() {
        if (network != null) {
            String title = network.getTitle();
            if (title != null && title.length() > 40) {
                title = title.substring(0, 38) + "...";
            }
            return "Merge with:  " + title;
        } else {
            return "Create New Network";
        }
    }
}
