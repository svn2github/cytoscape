
package csplugins.jActiveModules;

import org.cytoscape.work.TaskManager;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.property.CyProperty;
import org.cytoscape.view.model.CyNetworkViewManager;
import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.event.CyEventHelper;
import org.cytoscape.application.swing.CytoPanelComponent;
import org.cytoscape.application.swing.events.CytoPanelComponentSelectedListener;
import org.cytoscape.application.swing.CyAction;

import org.osgi.framework.BundleContext;
import org.cytoscape.service.util.AbstractCyActivator;
import java.util.Properties;
import csplugins.jActiveModules.dialogs.ActivePathsParameterPanel;
import csplugins.jActiveModules.data.ActivePathFinderParameters;


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
		
		CyProperty<?> cytoscapePropertiesServiceRef = getService(bc, CyProperty.class,
        "(cyPropertyName=cytoscape3.props)");
		
		ActivePathFinderParameters apfParams = new ActivePathFinderParameters(cytoscapePropertiesServiceRef);
		ActivePathsParameterPanel mainPanel = new ActivePathsParameterPanel(apfParams);

		ActiveModulesCytoPanelComponent activeModulesCytoPanelComponent = new ActiveModulesCytoPanelComponent(mainPanel);

		ActiveModulesUI activeModulesUI = new ActiveModulesUI(cyApplicationManagerServiceRef,cySwingApplicationServiceRef,
				apfParams, mainPanel);

		ActiveModulesPanelSelectedListener activeModulesPanelSelectedListener = new ActiveModulesPanelSelectedListener(mainPanel);
		
		
		registerService(bc,activeModulesCytoPanelComponent,CytoPanelComponent.class, new Properties());
		
		registerAllServices(bc, activeModulesUI, new Properties());
		
		registerService(bc,activeModulesPanelSelectedListener,CytoPanelComponentSelectedListener.class, new Properties());
	}
}

