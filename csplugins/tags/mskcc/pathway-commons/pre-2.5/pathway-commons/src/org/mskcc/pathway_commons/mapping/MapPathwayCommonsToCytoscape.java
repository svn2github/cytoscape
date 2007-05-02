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
package org.mskcc.pathway_commons.mapping;

// imports
import org.mskcc.pathway_commons.util.NetworkUtil;
import org.mskcc.pathway_commons.http.HTTPEvent;
import org.mskcc.pathway_commons.http.HTTPServerListener;
import org.mskcc.pathway_commons.view.MergeDialog;

import org.mskcc.biopax_plugin.mapping.MapBioPaxToCytoscape;

import cytoscape.Cytoscape;
import cytoscape.CyNetwork;
import cytoscape.data.CyAttributes;

import java.util.Set;
import java.util.HashSet;

/**
 * This class listens for requests from patwaycommons.org
 * and maps the requests into Cytoscape tasks.
 *
 * @author Benjamin Gross.
 */
public class MapPathwayCommonsToCytoscape implements HTTPServerListener {

	/**
	 * Our implementation of HTTPServerListener.
	 *
	 * @param event HTTPEvent
	 */
	public void httpEvent(HTTPEvent event) {

		// get the request/url
		String pathwayCommonsRequest = event.getRequest();
		Set<CyNetwork> bpNetworkSet = getBiopaxNetworkSet();

		// if no other networks are loaded, we can just load it up
		if (bpNetworkSet.size() == 0) {
			new NetworkUtil(pathwayCommonsRequest, null, false).start();
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
		Set<CyNetwork> cyNetworks = (Set<CyNetwork>)Cytoscape.getNetworkSet();
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
	 * @param bpNetworkSet Set<CyNetwork>
	 */
    private void loadMergeDialog(String pathwayCommonsRequest, Set<CyNetwork> bpNetworkSet) {

		MergeDialog dialog = new MergeDialog(Cytoscape.getDesktop(),
											 "Pathway Commons Network Merge",
											 true,
											 pathwayCommonsRequest,
											 bpNetworkSet);
		dialog.setLocationRelativeTo(Cytoscape.getDesktop());
		dialog.setVisible(true);
	}
}