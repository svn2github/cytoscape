// $Id: MapPathwayCommonsToCytoscape.java,v 1.3 2007/04/20 15:48:50 grossb Exp $
//------------------------------------------------------------------------------
/** Copyright (c) 2007 Memorial Sloan-Kettering Cancer Center.
 **
 ** Code written by: Ethan Cerami, Benjamin Gross
 ** Authors: Ethan Cerami, Gary Bader, Chris Sander, Benjamin Gross
 **
 ** This library is free software; you can redistribute it and/or modify it
 ** under the terms of the GNU Lesser General Public License as published
 ** by the Free Software Foundation; either version 2.1 of the License, or
 ** any later version.
 **
 ** This library is distributed in the hope that it will be useful, but
 ** WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
 ** MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
 ** documentation provided hereunder is on an "as is" basis, and
 ** Memorial Sloan-Kettering Cancer Center
 ** has no obligations to provide maintenance, support,
 ** updates, enhancements or modifications.  In no event shall
 ** Memorial Sloan-Kettering Cancer Center
 ** be liable to any party for direct, indirect, special,
 ** incidental or consequential damages, including lost profits, arising
 ** out of the use of this software and its documentation, even if
 ** Memorial Sloan-Kettering Cancer Center
 ** has been advised of the possibility of such damage.  See
 ** the GNU Lesser General Public License for more details.
 **
 ** You should have received a copy of the GNU Lesser General Public License
 ** along with this library; if not, write to the Free Software Foundation,
 ** Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 **/
package org.cytoscape.coreplugin.cpath2.mapping;

// imports

import cytoscape.CyNetwork;
import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;
import cytoscape.util.ProxyHandler;
import ding.view.NodeContextMenuListener;
import org.mskcc.biopax_plugin.mapping.MapBioPaxToCytoscape;
import org.cytoscape.coreplugin.cpath2.http.HTTPConnectionHandler;
import org.cytoscape.coreplugin.cpath2.http.HTTPEvent;
import org.cytoscape.coreplugin.cpath2.http.HTTPServerListener;
import org.cytoscape.coreplugin.cpath2.util.PluginProperties;
import org.cytoscape.coreplugin.cpath2.util.NetworkUtil;
import org.mskcc.pathway_commons.view.MergeDialog;

import java.net.Proxy;
import java.util.HashSet;
import java.util.Set;

/**
 * This class listens for requests from patwaycommons.org
 * and maps the requests into Cytoscape tasks.
 *
 * @author Benjamin Gross.
 */
public class MapPathwayCommonsToCytoscape implements HTTPServerListener {

    /*
      * ref to network listener - for context menus
      */
    NodeContextMenuListener nodeContextMenuListener;

    /**
     * Constructor
     *
     * @param nodeContextMenuListener NodeContextMenuListener
     */
    public MapPathwayCommonsToCytoscape(NodeContextMenuListener nodeContextMenuListener) {

        // init member vars
        this.nodeContextMenuListener = nodeContextMenuListener;
    }

    /**
     * Our implementation of HTTPServerListener.
     *
     * @param event HTTPEvent
     */
    public void httpEvent(HTTPEvent event) {

        // get the request/url
        String pathwayCommonsRequest = event.getRequest();

        // swap in proxy server if necessary
        Proxy proxyServer = ProxyHandler.getProxyServer();
        if (proxyServer != null) {
            String proxyAddress = proxyServer.toString();
            if (proxyAddress != null) {
                // parse protocol from ip/port address
                String[] addressComponents = proxyAddress.split("@");
                // do we have valid components ?
                if (addressComponents[0] != null && addressComponents[0].length() > 0 &&
                        addressComponents[1] != null && addressComponents[1].length() > 0) {
                    String newURL = addressComponents[0].trim() + ":/" + addressComponents[1].trim();
                    int indexOfWebService = pathwayCommonsRequest.indexOf(HTTPConnectionHandler.WEB_SERVICE_URL);
                    if (indexOfWebService > -1) {
                        pathwayCommonsRequest = newURL + pathwayCommonsRequest.substring(indexOfWebService);
                    }
                }
            }
        }

        Set<CyNetwork> bpNetworkSet = getBiopaxNetworkSet();

        // if no other networks are loaded, we can just load it up
        if (bpNetworkSet.size() == 0) {
            new NetworkUtil(pathwayCommonsRequest, null, false, nodeContextMenuListener).start();
        }
        // other networks list, give user option to merge
        else {
            loadMergeDialog(pathwayCommonsRequest, bpNetworkSet);
        }
    }

    /**
     * Constructs a set of BioPAX networks.
     *
     * @return Set<CyNetwork>
     */
    private Set<CyNetwork> getBiopaxNetworkSet() {

        // set to return
        Set<CyNetwork> bpNetworkSet = new HashSet<CyNetwork>();

        // get set of cynetworks
        Set<CyNetwork> cyNetworks = (Set<CyNetwork>) Cytoscape.getNetworkSet();
        if (cyNetworks.size() == 0) return cyNetworks;

        CyAttributes networkAttributes = Cytoscape.getNetworkAttributes();
        for (CyNetwork net : cyNetworks) {
            String networkID = net.getIdentifier();

            // is the biopax network attribute true ?
            Boolean b = networkAttributes.getBooleanAttribute(networkID,
                    MapBioPaxToCytoscape.BIOPAX_NETWORK);
            if (b != null && b) {
                bpNetworkSet.add(net);
            }
        }

        // outta here
        return bpNetworkSet;
    }

    /**
     * Loads the merge dialog.
     *
     * @param pathwayCommonsRequest String
     * @param bpNetworkSet          Set<CyNetwork>
     */
    private void loadMergeDialog(String pathwayCommonsRequest, Set<CyNetwork> bpNetworkSet) {

        MergeDialog dialog = new MergeDialog(Cytoscape.getDesktop(),
                PluginProperties.getNameOfCPathInstance() + "Network Merge",
                true,
                pathwayCommonsRequest,
                bpNetworkSet,
                nodeContextMenuListener);
        dialog.setLocationRelativeTo(Cytoscape.getDesktop());
        dialog.setVisible(true);
    }
}