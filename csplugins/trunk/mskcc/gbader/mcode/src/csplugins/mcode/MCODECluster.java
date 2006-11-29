package csplugins.mcode;

import giny.model.GraphPerspective;
import giny.model.Node;

import java.util.ArrayList;

/**
 * User: Vuk Pavlovic
 * Date: Nov 29, 2006
 * Time: 5:34:46 PM
 */

public class MCODECluster {
    ArrayList alCluster;
    GraphPerspective gpCluster;
    Node seedNode;

    public MCODECluster() {

    }

    public GraphPerspective getGpCluster() {
        return gpCluster;
    }

    public void setGpCluster(GraphPerspective gpCluster) {
        this.gpCluster = gpCluster;
    }

    public ArrayList getAlCluster() {
        return alCluster;
    }

    public void setAlCluster(ArrayList alCluster) {
        this.alCluster = alCluster;
    }

    public Node getSeedNode() {
        return seedNode;
    }

    public void setSeedNode(Node seedNode) {
        this.seedNode = seedNode;
    }
}
