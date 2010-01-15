// $Id: BiopaxPlugin.java,v 1.11 2006/06/15 22:06:02 grossb Exp $
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
package cytoscape.coreplugins.biopax;


import cytoscape.Cytoscape;
import cytoscape.CytoscapeInit;
import cytoscape.coreplugins.biopax.action.ExportAsBioPAXAction;
import cytoscape.coreplugins.biopax.action.IntegrateBioPAXAction;
import cytoscape.coreplugins.biopax.action.MergeBioPAXAction;
import cytoscape.coreplugins.biopax.util.BioPaxUtil;
import cytoscape.coreplugins.biopax.view.BioPaxContainer;
import cytoscape.data.ImportHandler;
import cytoscape.layout.CyLayoutAlgorithm;
import cytoscape.layout.CyLayouts;
import cytoscape.logger.CyLogger;
import cytoscape.plugin.CytoscapePlugin;
import cytoscape.util.CytoscapeAction;
import cytoscape.view.CyMenus;

import java.awt.event.ActionEvent;
import java.util.Arrays;
import java.util.Comparator;
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
		
		CyMenus cyMenus = Cytoscape.getDesktop().getCyMenus();
		cyMenus.addAction(new ExportAsBioPAXAction());
		cyMenus.addAction(new MergeBioPAXAction());
		cyMenus.addAction(new IntegrateBioPAXAction());
		cyMenus.addAction(new CreateNodesForControlsAction());
		CyLayoutAlgorithm allLayouts[] = CyLayouts.getAllLayouts()
			.toArray(new CyLayoutAlgorithm[1]);
		
		// Put layout names in alphabetical order.
		Arrays.sort(allLayouts, new Comparator<CyLayoutAlgorithm>() {
			public int compare(CyLayoutAlgorithm o1, CyLayoutAlgorithm o2) {
				return o1.getName().compareTo(o2.getName());
			}
		});
		
		// For each layout algorithm, create a menu item to select it as default when reading.
		for (CyLayoutAlgorithm algo : allLayouts) {
			cyMenus.addAction(new SelectDefaultLayoutAction(algo));
		}

		
		// we are now interested in receiving all network events
		// like a load of a network from a session
		// to start listening to network events, we grab an instance of
		// a BioPaxContainerClass - this contains the network listener
		BioPaxContainer.getInstance();
	}
	
	/**
	 * For "Plugins->BioPaX Import->Create Nodes for Controls" menu item.
	 * If checked, the reader creates cytoscape nodes for Controls (Catalysis, etc.) 
	 * in the BioPaX file.  If not checked, the reader represent Controls by edges.	 *
	 * 
	 * TODO does not work
	 * 
	 */
	public class CreateNodesForControlsAction extends CytoscapeAction {
		private static final long serialVersionUID = 1L;
		
		public CreateNodesForControlsAction() {
			super("Create Nodes for Controls");
			this.setPreferredMenu("Plugins.BioPaX Import");
			useCheckBoxMenuItem = true;
		}

		public void actionPerformed(ActionEvent ae) {
			BioPaxGraphReader.setCreateNodesForControls(!BioPaxGraphReader.getCreateNodesForControls());
			if(log.isDebugging()) {
				log.debug("createNodesForControls = " + BioPaxGraphReader.getCreateNodesForControls());
			}
		}
	}

	/**
	 * For "Plugins->BioPaX Import->Default Layout" menu.
	 * One of these actions exists for each known layout.
	 * When the layout name is clicked, it becomes the default initial layout for future biopax reads.
	 */
	public class SelectDefaultLayoutAction extends CytoscapeAction {
		private static final long serialVersionUID = 1L;
		private CyLayoutAlgorithm algo = BioPaxGraphReader.getDefaultLayoutAlgorithm();
		
		public SelectDefaultLayoutAction(CyLayoutAlgorithm algo) {
			super(algo.getName());
			this.algo = algo;
			this.setPreferredMenu("Plugins.BioPaX Import.Default Layout");
		}

		public void actionPerformed(ActionEvent ae) {
			BioPaxGraphReader.setDefaultLayoutAlgorithm(algo);
		}
	}
	
}
