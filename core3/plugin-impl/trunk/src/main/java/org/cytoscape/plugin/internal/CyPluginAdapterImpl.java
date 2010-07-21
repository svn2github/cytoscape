
package org.cytoscape.plugin.internal;

import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.model.CyDataTableFactory;
import org.cytoscape.view.model.CyNetworkViewFactory;
import org.cytoscape.work.TaskManager;
import org.cytoscape.plugin.CyPluginAdapter;


/**
 * A Java-only api providing access to cytoscape functionality.
 */
public class CyPluginAdapterImpl implements CyPluginAdapter {

	CyNetworkFactory netFac;
	CyDataTableFactory tabFac;
	CyNetworkViewFactory viewFac;
	TaskManager tm;

	CyPluginAdapterImpl(CyNetworkFactory netFac, CyDataTableFactory tabFac,
	           CyNetworkViewFactory viewFac, TaskManager tm ) {
		this.netFac = netFac;
		this.tabFac = tabFac;
		this.viewFac = viewFac;
		this.tm = tm;
	}

	public CyNetworkFactory getCyNetworkFactory() {
		return netFac;
	}

	public CyDataTableFactory getCyDataTableFactory(){
		return tabFac;
	} 

	public CyNetworkViewFactory getCyNetworkViewFactory(){
		return viewFac;
	}

	public TaskManager getTaskManager(){
		return tm;
	}
}
