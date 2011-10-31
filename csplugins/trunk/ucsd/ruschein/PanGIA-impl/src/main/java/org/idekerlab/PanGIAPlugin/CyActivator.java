package org.idekerlab.PanGIAPlugin;

import org.cytoscape.work.TaskManager;
import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.model.subnetwork.CyRootNetworkFactory;
import org.cytoscape.property.CyProperty;
import org.cytoscape.view.layout.CyLayoutAlgorithmManager;
import org.cytoscape.view.model.CyNetworkViewFactory;
import org.cytoscape.view.model.CyNetworkViewManager;
import org.cytoscape.view.vizmap.VisualMappingManager;
import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.event.CyEventHelper;
import org.cytoscape.application.swing.CytoPanelComponent;
import org.osgi.framework.BundleContext;
import org.cytoscape.service.util.AbstractCyActivator;
import java.util.Properties;

//import org.cytoscape.task.creation.LoadVisualStylesFromFileFactory;
import org.idekerlab.PanGIAPlugin.ui.SearchPropertyPanel;
import org.idekerlab.PanGIAPlugin.ServicesUtil;

public class CyActivator extends AbstractCyActivator {
	public CyActivator() {
		super();
	}


	public void start(BundleContext bc) {

		CySwingApplication cySwingApplicationServiceRef = getService(bc,CySwingApplication.class);
		CyApplicationManager cyApplicationManagerServiceRef = getService(bc,CyApplicationManager.class);
		CyNetworkViewManager cyNetworkViewManagerServiceRef = getService(bc,CyNetworkViewManager.class);
		CyNetworkManager cyNetworkManagerServiceRef = getService(bc,CyNetworkManager.class);
		CyServiceRegistrar cyServiceRegistrarServiceRef = getService(bc,CyServiceRegistrar.class);
		CyEventHelper cyEventHelperServiceRef = getService(bc,CyEventHelper.class);
		TaskManager taskManagerServiceRef = getService(bc,TaskManager.class);
		
		CyProperty<Properties> cytoscapePropertiesServiceRef = getService(bc, CyProperty.class,
        "(cyPropertyName=cytoscape3.props)");
		VisualMappingManager visualMappingManagerRef = getService(bc,VisualMappingManager.class);
		CyNetworkFactory cyNetworkFactoryServiceRef = getService(bc,CyNetworkFactory.class);

		CyRootNetworkFactory cyRootNetworkFactory = getService(bc,CyRootNetworkFactory.class);
		CyNetworkViewFactory cyNetworkViewFactoryServiceRef = getService(bc,CyNetworkViewFactory.class);
		CyLayoutAlgorithmManager cyLayoutsServiceRef = getService(bc,CyLayoutAlgorithmManager.class);

//		LoadVisualStylesFromFileFactory loadVisualStylesFromFileFactory = getService(bc, LoadVisualStylesFromFileFactory.class);;

		//
		ServicesUtil.cySwingApplicationServiceRef = cySwingApplicationServiceRef;
		ServicesUtil.cyApplicationManagerServiceRef = cyApplicationManagerServiceRef;
		ServicesUtil.cyNetworkViewManagerServiceRef = cyNetworkViewManagerServiceRef;
		ServicesUtil.cyNetworkManagerServiceRef = cyNetworkManagerServiceRef;
		ServicesUtil.cyServiceRegistrarServiceRef = cyServiceRegistrarServiceRef;
		ServicesUtil.cyEventHelperServiceRef = cyEventHelperServiceRef;
		ServicesUtil.taskManagerServiceRef = taskManagerServiceRef;
		ServicesUtil.cytoscapePropertiesServiceRef = cytoscapePropertiesServiceRef;
		ServicesUtil.visualMappingManagerRef = visualMappingManagerRef;
		ServicesUtil.cyNetworkFactoryServiceRef = cyNetworkFactoryServiceRef;
		ServicesUtil.cyRootNetworkFactory = cyRootNetworkFactory;
		ServicesUtil.cyNetworkViewFactoryServiceRef = cyNetworkViewFactoryServiceRef;
		ServicesUtil.cyLayoutsServiceRef = cyLayoutsServiceRef;
		
//		ServicesUtil.loadVisualStylesFromFileFactory = loadVisualStylesFromFileFactory;

		//		
		SearchPropertyPanel searchPanel = new SearchPropertyPanel();
		PanGIACytoPanelComponent panGIACytoPanelComponent = new PanGIACytoPanelComponent(searchPanel);
		PanGIAPlugin panGIAPlugin = new PanGIAPlugin(searchPanel);

//
		registerService(bc,panGIACytoPanelComponent,CytoPanelComponent.class, new Properties());
		registerAllServices(bc,searchPanel, new Properties());
		registerAllServices(bc,panGIAPlugin, new Properties());
	}
}

