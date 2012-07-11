package org.cytoscape.cytobridge;

import org.cytoscape.application.swing.CyAction;
import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.model.CyTableFactory;
import org.cytoscape.model.CyTableManager;
import org.cytoscape.property.CyProperty;
import org.cytoscape.service.util.AbstractCyActivator;
import org.cytoscape.view.layout.CyLayoutAlgorithmManager;
import org.cytoscape.view.model.CyNetworkViewFactory;
import org.cytoscape.view.model.CyNetworkViewManager;
import org.cytoscape.view.model.events.AddedNodeViewsListener;
import org.cytoscape.view.vizmap.VisualMappingManager;
import org.cytoscape.work.TaskManager;
import org.osgi.framework.BundleContext;


import java.util.Properties;

/**
 * CytoBridge plugin.
 * A Cytoscape plugin which allows users to manipulate and query networks.
 * @author Michael Kirby
 */
public class CyActivator extends AbstractCyActivator{

    public CyActivator() {
		super();
	}


	public void start(BundleContext bc) {
		
		//Get appropriate factories and managers
		CyNetworkFactory netFact = getService(bc,CyNetworkFactory.class);
		CyNetworkManager netMan = getService(bc,CyNetworkManager.class);
		CyNetworkViewFactory netViewFact = getService(bc,CyNetworkViewFactory.class);
		CyNetworkViewManager netViewMan = getService(bc,CyNetworkViewManager.class);
		VisualMappingManager visMan = getService(bc,VisualMappingManager.class);
		
		CyTableFactory tabFact = getService(bc,CyTableFactory.class);
		CyTableManager tabMan = getService(bc,CyTableManager.class);
		
		CyLayoutAlgorithmManager layMan = getService(bc,CyLayoutAlgorithmManager.class);
		
		CySwingApplication cytoscapeDesktopService = getService(bc,CySwingApplication.class);
		
		//Create instance of NetworkManager with the factories/managers
		NetworkManager myManager = new NetworkManager(netFact, netViewFact, netMan, netViewMan, tabFact, tabMan);
		
		//Create and register CytoBridge as a Service
		CytoBridgeAction cytoBridgeAction = new CytoBridgeAction(cytoscapeDesktopService, myManager);
		registerService(bc,cytoBridgeAction,CyAction.class, new Properties());
		
		TaskManager tm = getService(bc, TaskManager.class);
		CyProperty cyPropertyServiceRef = getService(bc,CyProperty.class,"(cyPropertyName=cytoscape3.props)");
		
		NodeViewListener listen = new NodeViewListener(visMan, layMan, tm, cyPropertyServiceRef);
		registerService(bc,listen,AddedNodeViewsListener.class, new Properties());
	}
}
