package org.cytoscape.neildhruva.chartapp.app;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

import org.cytoscape.application.events.SetCurrentNetworkEvent;
import org.cytoscape.application.events.SetCurrentNetworkListener;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyTable;
import org.cytoscape.neildhruva.chartapp.ChartAppFactory;
import org.cytoscape.neildhruva.chartapp.CytoChart;


public class EventTableAdded implements SetCurrentNetworkListener{

	private MyCytoPanel myCytoPanel; 
	private ChartAppFactory chartAppFactory;
	private JPanel jpanel;
	
	public EventTableAdded(MyCytoPanel myCytoPanel, ChartAppFactory chartAppFactory) {	
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
		
		CytoChart cytoChart = chartAppFactory.createChart(cyNetwork, cyTable);
		this.jpanel = cytoChart.getJPanel();
		myCytoPanel.setJPanel(jpanel);
		
		/*demo code-----------------------
		List<String> rows = new ArrayList<String>();
		rows.add("YJR060W");
		rows.add("YLR264W");
		rows.add("YDR309C");
		if(jpanel.getComponentCount()>1) {
			panelManager.setRows(rows);
		}
		*/
		
		
	}
}
