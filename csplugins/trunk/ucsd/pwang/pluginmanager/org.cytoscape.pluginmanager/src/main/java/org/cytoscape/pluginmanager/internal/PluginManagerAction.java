package org.cytoscape.pluginmanager.internal;

import java.awt.event.ActionEvent;

import org.cytoscape.session.CyNetworkManager;
import cytoscape.view.CySwingApplication;
import cytoscape.view.CytoscapeAction;
//import org.eclipse.equinox.internal.p2.ui.sdk.ProvSDKUIActivator;

/**
 * Internal implementation of our example Spring Bean
 */
public class PluginManagerAction extends CytoscapeAction
{
	
	private final static long serialVersionUID = 12023398790248567L;
	/**
	 * Creates a new PluginManagerAction object.
	 */
	private CySwingApplication desktop;
	private CyNetworkManager netmgr;
	
	public PluginManagerAction(CySwingApplication desktop, CyNetworkManager netmgr ) {
		super("PluginManager",netmgr);
		this.desktop = desktop;
		this.netmgr = netmgr;
		setPreferredMenu("Plugins");
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param e DOCUMENT ME!
	 */
	public void actionPerformed(ActionEvent e) {
		//ProvSDKUIActivator.getInstallNewSoftwareHandler(desktop,netmgr);
	} 
}
