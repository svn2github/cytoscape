package org.cytoscape.neildhruva.chartapp.app;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

import org.cytoscape.application.events.SetCurrentNetworkEvent;
import org.cytoscape.application.events.SetCurrentNetworkListener;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyTable;
import org.cytoscape.neildhruva.chartapp.ChartAppFactory;
import org.cytoscape.neildhruva.chartapp.ChartAppFactory.AxisMode;
import org.cytoscape.neildhruva.chartapp.CytoChart;


public class EventTableAdded implements SetCurrentNetworkListener{

	private MyCytoPanel myCytoPanel; 
	private ChartAppFactory chartAppFactory;
	private JPanel jpanel;
	private MyCytoPanel2 myCytoPanel2;
	private JPanel jpanel2;
	
	public EventTableAdded(MyCytoPanel myCytoPanel, ChartAppFactory chartAppFactory, MyCytoPanel2 myCytoPanel2) {	
		this.myCytoPanel = myCytoPanel;
		this.chartAppFactory = chartAppFactory;
		this.myCytoPanel2 = myCytoPanel2;
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
		CytoChart cytoChart;
		if(chartAppFactory.isChartSaved(cyTable, cyNetwork)) {
			 cytoChart = chartAppFactory.getSavedChart(cyNetwork, cyTable);
		} else {
			List<String> rowNames = new ArrayList<String>();
			rowNames.add("YJR060W");
			rowNames.add("YLR264W");
			rowNames.add("YDR309C");
			//rowNames.add("YJR060W");
			cytoChart = chartAppFactory.createChart(cyNetwork, cyTable, AxisMode.ROWS, 8, 8, rowNames, null);
		}
		
		//TODO allow the user to name their cytochart
		
		this.jpanel = cytoChart.getJPanel();
		myCytoPanel.setJPanel(jpanel);
		
		//cytoChart = chartAppFactory.createChart(cyNetwork, cyTable);
		//this.jpanel2 = cytoChart.getJPanel();
		//my2.setJPanel(jpanel);
		
		
		
	}
}
