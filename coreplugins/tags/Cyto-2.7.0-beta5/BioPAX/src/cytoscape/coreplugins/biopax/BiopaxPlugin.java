/** BioPAX Plugin for Cytoscape
 **
 ** Copyright (c) 2010 University of Toronto (UofT)
 ** and Memorial Sloan-Kettering Cancer Center (MSKCC).
 **
 ** This is free software; you can redistribute it and/or modify it
 ** under the terms of the GNU Lesser General Public License as published
 ** by the Free Software Foundation; either version 2.1 of the License, or
 ** any later version.
 **
 ** This library is distributed in the hope that it will be useful, but
 ** WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
 ** MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
 ** documentation provided hereunder is on an "as is" basis, and
 ** both UofT and MSKCC have no obligations to provide maintenance, 
 ** support, updates, enhancements or modifications.  In no event shall
 ** UofT or MSKCC be liable to any party for direct, indirect, special,
 ** incidental or consequential damages, including lost profits, arising
 ** out of the use of this software and its documentation, even if
 ** UofT or MSKCC have been advised of the possibility of such damage.  
 ** See the GNU Lesser General Public License for more details.
 **
 ** You should have received a copy of the GNU Lesser General Public License
 ** along with this software; if not, write to the Free Software Foundation,
 ** Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA;
 ** or find it at http://www.fsf.org/ or http://www.gnu.org.
 **/
package cytoscape.coreplugins.biopax;


import cytoscape.Cytoscape;
import cytoscape.CytoscapeInit;
import cytoscape.coreplugins.biopax.action.ExportAsBioPAXAction;
import cytoscape.coreplugins.biopax.view.BioPaxContainer;
import cytoscape.data.ImportHandler;
import cytoscape.logger.CyLogger;
import cytoscape.plugin.CytoscapePlugin;
import cytoscape.view.CyMenus;

import java.util.Properties;

/**
 * BioPAX Import PlugIn.
 *
 * @author Ethan Cerami, B. Arman Aksoy, Rex Dwyer, Igor Rodchenkov
 */
public class BiopaxPlugin extends CytoscapePlugin {
	
	protected static final CyLogger log = CyLogger.getLogger(BiopaxPlugin.class);
	
	/**
	 * Version Major Number.
	 */
	public static final int VERSION_MAJOR_NUM = 0;

	/**
	 * Version Minor Number.
	 */
	public static final int VERSION_MINOR_NUM = 7;

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
	public BiopaxPlugin() {
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
		
		// add export to BioPAX menu
		CyMenus cyMenus = Cytoscape.getDesktop().getCyMenus();
		cyMenus.addAction(new ExportAsBioPAXAction());
		
		// to start listening to network events, like a load of a network from a session,
		// we create an instance of a BioPaxContainerClass this contains the network listener
		BioPaxContainer.getInstance();
	}
	
}
