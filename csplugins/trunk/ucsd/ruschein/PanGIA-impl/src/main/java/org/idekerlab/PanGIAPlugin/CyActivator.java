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
import java.util.Properties;
import csplugins.jActiveModules.dialogs.ActivePathsParameterPanel;
import csplugins.jActiveModules.data.ActivePathFinderParameters;
import org.cytoscape.util.swing.NetworkSelectorPanel;
import org.cytoscape.task.creation.LoadVisualStylesFromFileFactory;

public class CyActivator extends AbstractCyActivator {
	public CyActivator() {
		super();
	}


	public void start(BundleContext bc) {


	}
}

