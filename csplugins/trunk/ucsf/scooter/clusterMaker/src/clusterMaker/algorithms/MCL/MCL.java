package clusterMaker.algorithms.MCL;

import java.util.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.event.ActionEvent;


import javax.swing.*;
import javax.swing.JOptionPane;
import javax.swing.JFrame;

//import giny.model.Node;
//import giny.view.NodeView;

import cytoscape.plugin.CytoscapePlugin;
import cytoscape.util.CytoscapeAction;
import cytoscape.Cytoscape;
import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.CyEdge;
import cytoscape.view.CyNetworkView;
import cytoscape.data.Semantics;
import cytoscape.data.CyAttributes;
/*
 */
public class MCL extends CytoscapePlugin {


    public MCL() {
        Cytoscape.getDesktop().getCyMenus().getOperationsMenu().add(new MCLMenu());
    }

}

