package org.cytoscape.plugin;

import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.model.subnetwork.CyRootNetworkFactory;
import org.cytoscape.model.CyTableFactory;
import org.cytoscape.event.CyEventHelper;
import org.cytoscape.view.model.CyNetworkViewFactory;
import org.cytoscape.view.layout.CyLayouts;
import org.cytoscape.view.presentation.RenderingEngineFactory;
import org.cytoscape.view.vizmap.VisualMappingManager;
import org.cytoscape.session.CyNetworkManager;
import org.cytoscape.session.CySessionManager;
import org.cytoscape.work.TaskManager;


/**
 * A Java-only api providing access to Cytoscape functionality.
 * This class will provide access the various Manager and 
 * Factory interfaces defined in different API jars that are
 * normally made available to plugins as OSGi services. Through
 * these interfaces developers will have access to most management
 * and creational facilities defined in the Cytoscape API.
 * This is a convenience interface intended make plugin development
 * as simple as possible.
 */
public interface CyPluginAdapter {

	//
	// model api
	//

	/**
	 * Returns an instance of {@link CyNetworkFactory}.
	 * @return an instance of {@link CyNetworkFactory}.
	 */
	CyNetworkFactory getCyNetworkFactory(); 

	/**
	 * Returns an instance of {@link CyTableFactory}.
	 * @return an instance of {@link CyTableFactory}.
	 */
	CyTableFactory getCyTableFactory(); 

	/**
	 * Returns an instance of {@link CyRootNetworkFactory}.
	 * @return an instance of {@link CyRootNetworkFactory}.
	 */
	CyRootNetworkFactory getCyRootNetworkFactory(); 

	/**
	 * Returns an instance of {@link CyEventHelper}.
	 * @return an instance of {@link CyEventHelper}.
	 */
	CyEventHelper getCyEventHelper(); 

	//
	// viewmodel api
	//

	/**
	 * Returns an instance of {@link CyNetworkViewFactory}.
	 * @return an instance of {@link CyNetworkViewFactory}.
	 */
	CyNetworkViewFactory getCyNetworkViewFactory();

	//
	// session api
	//

	/**
	 * Returns an instance of {@link CyNetworkManager}.
	 * @return an instance of {@link CyNetworkManager}.
	 */
	CyNetworkManager getCyNetworkManager();

	/**
	 * Returns an instance of {@link CySessionManager}.
	 * @return an instance of {@link CySessionManager}.
	 */
	CySessionManager getCySessionManager();

	//
	// work api
	//

	/**
	 * Returns an instance of {@link TaskManager}.
	 * @return an instance of {@link TaskManager}.
	 */
	TaskManager getTaskManager();

	//
	// presentation api
	//

	/**
	 * Returns an instance of {@link RenderingEngineFactory}.
	 * @return an instance of {@link RenderingEngineFactory}.
	 */
	RenderingEngineFactory getPresentationFactory();

	//
	// vizmap api
	//

	/**
	 * Returns an instance of {@link VisualMappingManager}.
	 * @return an instance of {@link VisualMappingManager}.
	 */
	VisualMappingManager getVisualMappingManager();


	//
	// layout api
	//

	/**
	 * Returns an instance of {@link CyLayouts}.
	 * @return an instance of {@link CyLayouts}.
	 */
	CyLayouts getCyLayouts();

}
