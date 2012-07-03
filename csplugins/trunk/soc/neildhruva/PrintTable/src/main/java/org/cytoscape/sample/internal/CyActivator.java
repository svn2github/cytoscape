package org.cytoscape.sample.internal;

import org.cytoscape.application.events.SetCurrentNetworkListener;
import org.cytoscape.application.swing.CytoPanelComponent;
import org.cytoscape.model.CyNetworkTableManager;
import org.cytoscape.model.CyTableFactory;
import org.cytoscape.model.events.NetworkAboutToBeDestroyedListener;
import org.cytoscape.service.util.AbstractCyActivator;
import org.osgi.framework.BundleContext;
import java.util.Properties;


public class CyActivator extends AbstractCyActivator {

	public CyActivator() {
		super();
	}

	public void start(BundleContext bc) {
		
		CyTableFactory cyDataTableFactoryServiceRef = getService(bc,CyTableFactory.class);
		CyNetworkTableManager cyNetworkTableManagerServiceRef = getService(bc, CyNetworkTableManager.class);
		
		MyCytoPanel myCytoPanel = new MyCytoPanel();
		EventTableAdded tableAddedEvent = new EventTableAdded(myCytoPanel, cyDataTableFactoryServiceRef, cyNetworkTableManagerServiceRef);
		EventTableDestroyed tableDestroyedEvent = new EventTableDestroyed(myCytoPanel);
		
		registerService(bc,myCytoPanel,CytoPanelComponent.class, new Properties());
		registerService(bc,tableAddedEvent,SetCurrentNetworkListener.class, new Properties());
		registerService(bc,tableDestroyedEvent,NetworkAboutToBeDestroyedListener.class, new Properties());
		
		
	}
}
