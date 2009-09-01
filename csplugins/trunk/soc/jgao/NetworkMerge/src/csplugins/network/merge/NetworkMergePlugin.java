/* File: NetworkMergePlugin.java

 Copyright (c) 2006, 2007, The Cytoscape Consortium (www.cytoscape.org)

 The Cytoscape Consortium is:
 - Institute for Systems Biology
 - University of California San Diego
 - Memorial Sloan-Kettering Cancer Center
 - Institut Pasteur
 - Agilent Technologies

 This library is free software; you can redistribute it and/or modify it
 under the terms of the GNU Lesser General Public License as published
 by the Free Software Foundation; either version 2.1 of the License, or
 any later version.

 This library is distributed in the hope that it will be useful, but
 WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
 MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
 documentation provided hereunder is on an "as is" basis, and the
 Institute for Systems Biology and the Whitehead Institute
 have no obligations to provide maintenance, support,
 updates, enhancements or modifications.  In no event shall the
 Institute for Systems Biology and the Whitehead Institute
 be liable to any party for direct, indirect, special,
 incidental or consequential damages, including lost profits, arising
 out of the use of this software and its documentation, even if the
 Institute for Systems Biology and the Whitehead Institute
 have been advised of the possibility of such damage.  See
 the GNU Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public License
 along with this library; if not, write to the Free Software Foundation,
 Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 */

package csplugins.network.merge;

import csplugins.network.merge.ui.NetworkMergeFrame;

import cytoscape.plugin.CytoscapePlugin;
import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;
import cytoscape.data.Semantics;
import cytoscape.util.CytoscapeAction;

import giny.model.Node;


import java.awt.event.ActionEvent;

import java.util.List;
import java.util.Arrays;

/**
 * Plugin to merge networks
 * 
 * 
 */
public class NetworkMergePlugin extends CytoscapePlugin {
    public NetworkMergePlugin() {
        Cytoscape.getDesktop().getCyMenus().getOperationsMenu().add(new NetworkMergeAction());
        //Cytoscape.createNetworkFromFile("testData\\data1.cys");
    }
    
    class NetworkMergeAction extends CytoscapeAction {
        public NetworkMergeAction() {
            super("Advanced Network Merge"); //TODO rename
	}

        /**
         * This method is called when the user selects the menu item.
         */
        @Override
        public void actionPerformed(final ActionEvent ae) {
            prepare(); //TODO: remove in Cytoscape3
            
            final NetworkMergeFrame frame = new NetworkMergeFrame();
            frame.setLocationRelativeTo(Cytoscape.getDesktop());
            frame.setVisible(true);
            //frame.setAlwaysOnTop(true);
        }
        
        //TODO: remove in Cytoscape3
        /*
         * Copy node ID to canonicalName if canonicalName does not exist
         * 
         */
        private void prepare() {
            CyAttributes cyAttributes = Cytoscape.getNodeAttributes();
            if (!Arrays.asList(cyAttributes.getAttributeNames()).contains(Semantics.CANONICAL_NAME)) {
                List<Node> nodeList = Cytoscape.getCyNodesList();
                int n = nodeList.size();
                for (int i=0; i<n; i++) {
                    String nodeID = nodeList.get(i).getIdentifier();
                    cyAttributes.setAttribute(nodeID, Semantics.CANONICAL_NAME, nodeID);
                }
            }

            cyAttributes = Cytoscape.getEdgeAttributes();
            if (!Arrays.asList(cyAttributes.getAttributeNames()).contains(Semantics.CANONICAL_NAME)) {
                List<Node> edgeList = Cytoscape.getCyEdgesList();
                int n = edgeList.size();
                for (int i=0; i<n; i++) {
                    String edgeID = edgeList.get(i).getIdentifier();
                    cyAttributes.setAttribute(edgeID, Semantics.CANONICAL_NAME, edgeID);
                }
            }
        }//TODO: remove in Cytoscape3
    }
}


