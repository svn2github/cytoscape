package org.cytoscape.coreplugin.cpath2.view.model;

import org.cytoscape.GraphPerspective;

public class NetworkWrapper {
    private GraphPerspective network;

    public NetworkWrapper (GraphPerspective network) {
        this.network = network;
    }

    public GraphPerspective getNetwork() {
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
