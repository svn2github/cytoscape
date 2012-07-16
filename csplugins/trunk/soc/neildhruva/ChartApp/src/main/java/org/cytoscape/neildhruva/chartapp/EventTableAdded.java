package org.cytoscape.neildhruva.chartapp;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JTable;

import org.cytoscape.application.events.SetCurrentNetworkEvent;
import org.cytoscape.application.events.SetCurrentNetworkListener;
import org.cytoscape.model.CyIdentifiable;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNetworkTableManager;
import org.cytoscape.model.CyRow;
import org.cytoscape.model.CyTable;
import org.cytoscape.model.CyTableFactory;
import org.cytoscape.model.CyTableManager;
import org.cytoscape.neildhruva.chartapp.impl.ChartAppFactory;

public class EventTableAdded implements SetCurrentNetworkListener{

	private MyCytoPanel myCytoPanel; 
	private ChartAppFactory chartAppFactory;
	
	EventTableAdded(MyCytoPanel myCytoPanel, ChartAppFactory chartAppFactory) {	
		this.myCytoPanel = myCytoPanel;
		this.chartAppFactory = chartAppFactory;
	}

	@Override
	public void handleEvent(SetCurrentNetworkEvent e) {
		
		final CyNetwork cyNetwork = e.getNetwork();
		if(cyNetwork == null) 
			return;
		
		//cyTable is the CyTable corresponding to the current node table
		final CyTable cyTable = e.getNetwork().getDefaultNodeTable();
		if(cyTable==null)
			return;
		
		JPanel jpanel = chartAppFactory.createPanel(cyNetwork, cyTable);
		myCytoPanel.setJPanel(jpanel);
	}
}
