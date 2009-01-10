// $Id: BioPaxPlugIn.java,v 1.11 2006/06/15 22:06:02 grossb Exp $
//------------------------------------------------------------------------------
/** Copyright (c) 2006 Memorial Sloan-Kettering Cancer Center.
 **
 ** Code written by: Ethan Cerami
 ** Authors: Ethan Cerami, Gary Bader, Chris Sander
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
package org.mskcc.biopax_plugin.plugin;

import org.mskcc.biopax_plugin.view.BioPaxContainer;

import cytoscape.CytoscapeInit;
import cytoscape.data.ImportHandler;
import cytoscape.plugin.CytoscapePlugin;

import java.util.Properties;

/**
 * BioPAX Import PlugIn.
 *
 * @author Ethan Cerami.
 */
public class BioPaxPlugIn extends CytoscapePlugin {
	/**
	 * Version Major Number.
	 */
	public static final int VERSION_MAJOR_NUM = 0;

	/**
	 * Version Minor Number.
	 */
	public static final int VERSION_MINOR_NUM = 6;

    /**
     * Name of Plugin.
     */
    public static final String PLUGIN_NAME = "BioPAX Plugin";

	/**
	 * Attribute Name for BioPAX Utility Class.
	 */
	public static final String BP_UTIL = "BIO_PAX_UTIL";

	/**
	 * Proxy Host Property Name
	 */
	public static final String PROXY_HOST_PROPERTY = "dataservice.proxy_host";

	/**
	 * Proxy Port Property Name
	 */
	public static final String PROXY_PORT_PROPERTY = "dataservice.proxy_port";

	/**
	 * Constructor.
	 * This method is called by the main Cytoscape Application upon startup.
	 */
	public BioPaxPlugIn() {
		ImportHandler importHandler = new ImportHandler();
		importHandler.addFilter(new BioPaxFilter());

		//  Optionally set up HTTP Proxy
		Properties cytoProps = CytoscapeInit.getProperties();
		String proxyHost = (String) cytoProps.get(PROXY_HOST_PROPERTY);
		String proxyPort = (String) cytoProps.get(PROXY_PORT_PROPERTY);

		if ((proxyHost != null) && (proxyPort != null)) {
			System.getProperties().put("proxySet", "true");
			System.getProperties().put("proxyHost", proxyHost);
			System.getProperties().put("proxyPort", proxyPort);
		}

		// we are now interested in receiving all network events
		// like a load of a network from a session
		// to start listening to network events, we grab an instance of
		// a BioPaxContainerClass - this contains the network listener
		BioPaxContainer bpContainer = BioPaxContainer.getInstance();
	}
}
