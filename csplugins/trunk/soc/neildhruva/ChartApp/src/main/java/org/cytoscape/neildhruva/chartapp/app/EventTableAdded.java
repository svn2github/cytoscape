package org.cytoscape.neildhruva.chartapp.app;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.swing.JPanel;
import org.cytoscape.application.events.SetCurrentNetworkEvent;
import org.cytoscape.application.events.SetCurrentNetworkListener;
import org.cytoscape.application.events.SetSelectedNetworksEvent;
import org.cytoscape.application.events.SetSelectedNetworksListener;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyTable;
import org.cytoscape.model.CyTableMetadata;
import org.cytoscape.model.events.SetNetworkPointerEvent;
import org.cytoscape.model.events.SetNetworkPointerListener;
import org.cytoscape.model.events.TableAddedEvent;
import org.cytoscape.model.events.TableAddedListener;
import org.cytoscape.model.events.UnsetNetworkPointerEvent;
import org.cytoscape.model.events.UnsetNetworkPointerListener;
import org.cytoscape.neildhruva.chartapp.ChartAppFactory;
import org.cytoscape.neildhruva.chartapp.ChartAppFactory.AxisMode;
import org.cytoscape.neildhruva.chartapp.CytoChart;
import org.cytoscape.session.events.SessionLoadedEvent;
import org.cytoscape.session.events.SessionLoadedListener;


public class EventTableAdded implements SetCurrentNetworkListener, TableAddedListener, SessionLoadedListener, UnsetNetworkPointerListener{

	private MyCytoPanel myCytoPanel; 
	private ChartAppFactory chartAppFactory;
	private JPanel jpanel;
	private SelectedRowsIdentifier selectedRowsIdentifier;
	private CytoChart cytoChart;
	private CyTable cyTable;
	
	public EventTableAdded(MyCytoPanel myCytoPanel, ChartAppFactory chartAppFactory, SelectedRowsIdentifier selectedRowsIdentifier) {	
		this.myCytoPanel = myCytoPanel;
		this.chartAppFactory = chartAppFactory;
		
		this.selectedRowsIdentifier = selectedRowsIdentifier;
		this.cytoChart = null;
	}

	@Override
	public void handleEvent(SetCurrentNetworkEvent e) {
		
		final CyNetwork cyNetwork = e.getNetwork();
		if(cyNetwork == null) 
			return;
		
		//cyTable is the CyTable corresponding to the current node table
		cyTable = e.getNetwork().getDefaultNodeTable();
		if(cyTable==null)
			return;
		
		String chartName = null;
		
		if(chartAppFactory.isChartSaved(chartName, cyTable)) {
			cytoChart = chartAppFactory.getSavedChart(chartName, cyTable);
		} else {
			cytoChart = chartAppFactory.createChart(chartName, cyTable, AxisMode.ROWS);			
		}
		
		//TODO allow the user to name their cytochart
		
		//TODO what if the user installs the app after loading some tables?
		this.jpanel = cytoChart.getJPanel();
		myCytoPanel.setJPanel(jpanel);
		if(!jpanel.getName().equals("NULL")) {
			selectedRowsIdentifier.setCytoChart(cytoChart);
		} else {
			selectedRowsIdentifier.setCytoChart(null);
		}
		
		
	}

	@Override
	public void handleEvent(TableAddedEvent e) {
		//TODO use this in sync with SetCurrentNetworkEvent
		
	}

	@Override
	public void handleEvent(SessionLoadedEvent e) {
		Iterator<CyTableMetadata> iterator = e.getLoadedSession().getTables().iterator();
		while(iterator.hasNext()) {
			System.out.println(iterator.next().getTable().getTitle());
		}
		//TODO use this to make ChartApp for all loaded networks
	}

	@Override
	public void handleEvent(UnsetNetworkPointerEvent e) {
		System.out.println(e.getNetwork().getDefaultNodeTable().getTitle());
		
	}
}
