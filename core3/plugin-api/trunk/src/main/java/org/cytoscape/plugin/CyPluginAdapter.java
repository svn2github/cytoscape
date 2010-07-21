package org.cytoscape.plugin;

import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.model.CyDataTableFactory;
import org.cytoscape.view.model.CyNetworkViewFactory;
import org.cytoscape.work.TaskManager;


/**
 * A Java-only api providing access to cytoscape functionality.
 */
public interface CyPluginAdapter {

	/**
	 * Returns an instance of CyNetworkFactory.
	 * @return an instance of CyNetworkFactory.
	 */
	CyNetworkFactory getCyNetworkFactory(); 

	/**
	 * Returns an instance of CyNetworkFactory.
	 * @return an instance of CyNetworkFactory.
	 */
	CyDataTableFactory getCyDataTableFactory(); 

	/**
	 * Returns an instance of CyNetworkFactory.
	 * @return an instance of CyNetworkFactory.
	 */
	CyNetworkViewFactory getCyNetworkViewFactory();

	/**
	 * Returns an instance of CyNetworkFactory.
	 * @return an instance of CyNetworkFactory.
	 */
	TaskManager getTaskManager();
}
