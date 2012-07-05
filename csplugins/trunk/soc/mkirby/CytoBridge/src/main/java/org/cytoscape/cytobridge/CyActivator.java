package org.cytoscape.cytobridge;

import org.cytoscape.application.swing.CyAction;
import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.service.util.AbstractCyActivator;
import org.cytoscape.view.model.CyNetworkViewFactory;
import org.cytoscape.view.model.CyNetworkViewManager;
import org.cytoscape.view.model.events.AddedNodeViewsListener;
import org.cytoscape.view.vizmap.VisualMappingManager;
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
		
		CySwingApplication cytoscapeDesktopService = getService(bc,CySwingApplication.class);
		
		//Create instance of NetworkManager with the factories/managers
		NetworkManager myManager = new NetworkManager(netFact, netViewFact, netMan, netViewMan);
		
		//Create and register CytoBridge as a Service
		CytoBridgeAction cytoBridgeAction = new CytoBridgeAction(cytoscapeDesktopService, myManager);
		registerService(bc,cytoBridgeAction,CyAction.class, new Properties());
		
		NodeViewListener listen = new NodeViewListener(visMan);
		registerService(bc,listen,AddedNodeViewsListener.class, new Properties());
	}
}
