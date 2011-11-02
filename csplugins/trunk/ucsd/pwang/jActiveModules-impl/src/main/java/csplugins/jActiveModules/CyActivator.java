package csplugins.jActiveModules;

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
import org.cytoscape.application.swing.events.CytoPanelComponentSelectedListener;
import org.cytoscape.application.swing.CyAction;

import org.osgi.framework.BundleContext;
import org.cytoscape.service.util.AbstractCyActivator;
import org.cytoscape.task.creation.LoadVisualStyles;

import java.util.Properties;
import csplugins.jActiveModules.dialogs.ActivePathsParameterPanel;
import csplugins.jActiveModules.data.ActivePathFinderParameters;
import org.cytoscape.util.swing.NetworkSelectorPanel;
import org.cytoscape.task.creation.LoadVisualStyles;

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

		LoadVisualStyles loadVizmapFileTaskFactory =  getService(bc,LoadVisualStyles.class);

		//
		
		ActivePathFinderParameters apfParams = new ActivePathFinderParameters(cytoscapePropertiesServiceRef);

		NetworkSelectorPanel networkSelectorPanel = new NetworkSelectorPanel(cyApplicationManagerServiceRef, cyNetworkManagerServiceRef);

		ActivePathsParameterPanel mainPanel = new ActivePathsParameterPanel(apfParams, cySwingApplicationServiceRef,
				cyApplicationManagerServiceRef,cyNetworkManagerServiceRef, networkSelectorPanel);

		ActiveModulesCytoPanelComponent activeModulesCytoPanelComponent = new ActiveModulesCytoPanelComponent(mainPanel);

		ActiveModulesUI activeModulesUI = new ActiveModulesUI(cyApplicationManagerServiceRef,cySwingApplicationServiceRef,
				cytoscapePropertiesServiceRef, cyNetworkManagerServiceRef,cyNetworkViewManagerServiceRef, visualMappingManagerRef, 
				cyNetworkFactoryServiceRef, cyRootNetworkFactory, cyNetworkViewFactoryServiceRef,cyLayoutsServiceRef,
				taskManagerServiceRef,cyEventHelperServiceRef,loadVizmapFileTaskFactory,apfParams, mainPanel);
		
		registerAllServices(bc,mainPanel, new Properties());
		
		registerAllServices(bc,networkSelectorPanel, new Properties());
				
		registerService(bc,activeModulesCytoPanelComponent,CytoPanelComponent.class, new Properties());
		
		registerAllServices(bc, activeModulesUI, new Properties());		
	}
}

