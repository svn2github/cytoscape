
package org.cytoscape.plugin.internal;

import org.cytoscape.plugin.CyPluginAdapter;

//
// Keep these alphabetical by class name
//
import org.cytoscape.model.CyTableFactory;
import org.cytoscape.event.CyEventHelper;
//import org.cytoscape.view.layout.CyLayouts;
import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.model.subnetwork.CyRootNetworkFactory;
import org.cytoscape.session.CyApplicationManager;
import org.cytoscape.session.CySessionManager;
import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.view.model.CyNetworkViewFactory;
import org.cytoscape.view.model.CyNetworkViewManager;
import org.cytoscape.view.presentation.RenderingEngineFactory;
import org.cytoscape.work.TaskManager;
import org.cytoscape.view.vizmap.VisualMappingManager;


/**
 * An implementation of CyPluginAdapter
 */
public class CyPluginAdapterImpl implements CyPluginAdapter {

	//
	// Since there are a lot of fields, keep them
	// in alphabetical order to maintain some order.
	// Always make the field name same as the class
	// name, but with a lower case first letter.
	//
	private final CyTableFactory cyTableFactory;
	private final CyEventHelper cyEventHelper;
//	private final CyLayouts cyLayouts;
	private final CyNetworkFactory cyNetworkFactory;
	private final CyNetworkManager cyNetworkManager;
	private final CyNetworkViewFactory cyNetworkViewFactory;
	private final CyRootNetworkFactory cyRootNetworkFactory;
	private final CySessionManager cySessionManager;
	private final CySwingApplication cySwingApplication;
	private final RenderingEngineFactory presentationFactory;
	private final TaskManager taskManager;
	private final VisualMappingManager visualMappingManager;
	private final CyNetworkViewManager networkViewManager;
	private final CyApplicationManager applicationManager;

	//
	// Since this is implementation code, there shouldn't be a
	// a problem adding new arguments as needed.  Therefore, to
	// maintain sanity, keep the arguments in alphabetical order.
	//
	CyPluginAdapterImpl( 
	                    CyTableFactory cyTableFactory,
	                    CyEventHelper cyEventHelper,
//	                    CyLayouts cyLayouts,
			    CyNetworkFactory cyNetworkFactory,
			    CyNetworkManager cyNetworkManager,
			    CyNetworkViewFactory cyNetworkViewFactory,
			    CyRootNetworkFactory cyRootNetworkFactory,
			    CySessionManager cySessionManager,
			    CySwingApplication cySwingApplication,
			    RenderingEngineFactory presentationFactory,
			    TaskManager taskManager,
			    VisualMappingManager visualMappingManager,
			    CyNetworkViewManager networkViewManager,
			    CyApplicationManager applicationManager
					    )
	{
		this.cyTableFactory = cyTableFactory;
		this.cyEventHelper = cyEventHelper;
//		this.cyLayouts = cyLayouts;
		this.cyNetworkFactory = cyNetworkFactory;
		this.cyNetworkManager = cyNetworkManager;
		this.cyNetworkViewFactory = cyNetworkViewFactory;
		this.cyRootNetworkFactory = cyRootNetworkFactory;
		this.cySessionManager = cySessionManager;
		this.cySwingApplication = cySwingApplication;
		this.presentationFactory = presentationFactory;
		this.taskManager = taskManager;
		this.visualMappingManager = visualMappingManager;
		this.networkViewManager = networkViewManager;
		this.applicationManager = applicationManager;
	}


	//
	// May as well keep the methods alphabetical too!
	// 

	public CyTableFactory getCyTableFactory() { return cyTableFactory; } 

	public CyEventHelper getCyEventHelper() { return cyEventHelper; } 

//	public CyLayouts getCyLayouts() { return cyLayouts; } 

	public CyNetworkFactory getCyNetworkFactory() { return cyNetworkFactory; }

	public CyNetworkManager getCyNetworkManager() { return cyNetworkManager; } 

	public CyNetworkViewFactory getCyNetworkViewFactory() { return cyNetworkViewFactory; }

	public CyRootNetworkFactory getCyRootNetworkFactory() { return cyRootNetworkFactory; } 

	public CySessionManager getCySessionManager() { return cySessionManager; } 

	public RenderingEngineFactory getPresentationFactory() { return presentationFactory; }

	public TaskManager getTaskManager() { return taskManager; }

	public VisualMappingManager getVisualMappingManager() { return visualMappingManager; }

	public CyNetworkViewManager getCyNetworkViewManager() { return networkViewManager; }

	public CyApplicationManager getCyApplicationManager() { return applicationManager; }

	public CySwingApplication getCySwingApplication() { return cySwingApplication; }
}
