// $Id: MapCPathToCytoscape.java,v 1.3 2007/04/20 15:48:50 grossb Exp $
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

import cytoscape.util.ProxyHandler;
import ding.view.NodeContextMenuListener;
import org.cytoscape.coreplugin.cpath2.http.HTTPConnectionHandler;
import org.cytoscape.coreplugin.cpath2.http.HTTPEvent;
import org.cytoscape.coreplugin.cpath2.http.HTTPServerListener;
import org.cytoscape.coreplugin.cpath2.util.NetworkUtil;
import org.cytoscape.coreplugin.cpath2.util.NetworkMergeUtil;
import org.cytoscape.coreplugin.cpath2.view.model.NetworkWrapper;
import org.cytoscape.coreplugin.cpath2.web_service.CPathProperties;

import java.net.Proxy;

/**
 * This class listens for requests from cPath instance
 * and maps the requests into Cytoscape tasks.
 *
 * @author Benjamin Gross.
 */
public class MapCPathToCytoscape implements HTTPServerListener {

    /*
      * ref to network listener - for context menus
      */
    NodeContextMenuListener nodeContextMenuListener;

    /**
     * Constructor
     *
     * @param nodeContextMenuListener NodeContextMenuListener
     */
    public MapCPathToCytoscape(NodeContextMenuListener nodeContextMenuListener) {

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
        String cpathRequest = event.getRequest();

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
                    int indexOfWebService = cpathRequest.indexOf(HTTPConnectionHandler.WEB_SERVICE_URL);
                    if (indexOfWebService > -1) {
                        cpathRequest = newURL + cpathRequest.substring(indexOfWebService);
                    }
                }
            }
        }
        // System.out.println("CPATH REQUEST:  " + cpathRequest.toString());
        loadMergeDialog(cpathRequest);
    }

    /**
     * Loads the merge dialog.
     *
     * @param cpathRequest String
     */
    private void loadMergeDialog(String cpathRequest) {
        CPathProperties cPathProperties = CPathProperties.getInstance();
        int downloadMode = cPathProperties.getDownloadMode();
        cPathProperties.setDownloadMode(CPathProperties.DOWNLOAD_FULL_BIOPAX);
        NetworkMergeUtil mergeUtil = new NetworkMergeUtil();
        if (mergeUtil.mergeNetworksExist()) {
            NetworkWrapper networkWrapper = mergeUtil.promptForNetworkToMerge();
            if (networkWrapper != null && networkWrapper.getNetwork() != null) {
                new NetworkUtil(cpathRequest, networkWrapper.getNetwork(),
                        true, nodeContextMenuListener).start();
            } else {
                new NetworkUtil(cpathRequest, null, false, nodeContextMenuListener).start();                
            }
        } else {
            new NetworkUtil(cpathRequest, null, false, nodeContextMenuListener).start();
        }
        cPathProperties.setDownloadMode(downloadMode);
    }
}
