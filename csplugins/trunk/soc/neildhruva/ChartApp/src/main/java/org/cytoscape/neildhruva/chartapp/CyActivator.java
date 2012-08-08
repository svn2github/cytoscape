package org.cytoscape.neildhruva.chartapp;

import org.cytoscape.application.swing.CytoPanelComponent;
import org.cytoscape.model.CyTableFactory;
import org.cytoscape.model.CyTableManager;
import org.cytoscape.neildhruva.chartapp.app.EventTableAdded;
import org.cytoscape.neildhruva.chartapp.app.EventTableDestroyed;
import org.cytoscape.neildhruva.chartapp.app.MyCytoPanel;
import org.cytoscape.neildhruva.chartapp.app.SelectedRowsIdentifier;
import org.cytoscape.neildhruva.chartapp.impl.ChartAppFactoryImpl;
import org.cytoscape.service.util.AbstractCyActivator;

import org.osgi.framework.BundleContext;

import java.util.Properties;


public class CyActivator extends AbstractCyActivator {

	public CyActivator() {
		super();
	}

	public void start(BundleContext bc) {
		
		CyTableFactory cyDataTableFactoryServiceRef = getService(bc,CyTableFactory.class);
		CyTableManager cyTableManagerServiceRef = getService(bc,CyTableManager.class);
			
		MyCytoPanel myCytoPanel = new MyCytoPanel();
		
		ChartAppFactoryImpl chartAppFactoryImpl = new ChartAppFactoryImpl(cyDataTableFactoryServiceRef, cyTableManagerServiceRef);
		
		SelectedRowsIdentifier selectedRowsIdentifier = new SelectedRowsIdentifier();
		EventTableDestroyed eventTableDestroyed = new EventTableDestroyed(myCytoPanel, cyTableManagerServiceRef);
		EventTableAdded eventTableAdded = new EventTableAdded(myCytoPanel, chartAppFactoryImpl, selectedRowsIdentifier);
		
		registerService(bc, chartAppFactoryImpl, ChartAppFactory.class, new Properties());
		registerService(bc, myCytoPanel,CytoPanelComponent.class, new Properties());
		registerAllServices(bc, eventTableAdded, new Properties());
		registerAllServices(bc, eventTableDestroyed, new Properties());
		registerAllServices(bc, selectedRowsIdentifier, new Properties());
		
	}
}
