package org.cytoscape.plugin;

import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.model.CyDataTableFactory;
import org.cytoscape.view.model.CyNetworkViewFactory;
import org.cytoscape.work.TaskManager;


/**
 * A Java-only api providing access to Cytoscape functionality.
 */
public interface CyPluginAdapter {

	/**
	 * Returns an instance of {@link CyNetworkFactory}.
	 * @return an instance of {@link CyNetworkFactory}.
	 */
	CyNetworkFactory getCyNetworkFactory(); 

	/**
	 * Returns an instance of {@link CyDataTableFactory}.
	 * @return an instance of {@link CyDataTableFactory}.
	 */
	CyDataTableFactory getCyDataTableFactory(); 

	/**
	 * Returns an instance of {@link CyNetworkViewFactory}.
	 * @return an instance of {@link CyNetworkViewFactory}.
	 */
	CyNetworkViewFactory getCyNetworkViewFactory();

	/**
	 * Returns an instance of {@link TaskManager}.
	 * @return an instance of {@link TaskManager}.
	 */
	TaskManager getTaskManager();
}
