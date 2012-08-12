package org.cytoscape.neildhruva.chartapp.app;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.swing.JPanel;
import org.cytoscape.application.events.SetCurrentNetworkEvent;
import org.cytoscape.application.events.SetCurrentNetworkListener;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyTable;
import org.cytoscape.model.CyTableMetadata;
import org.cytoscape.model.events.TableAddedEvent;
import org.cytoscape.model.events.TableAddedListener;
import org.cytoscape.neildhruva.chartapp.ChartAppFactory;
import org.cytoscape.neildhruva.chartapp.ChartAppFactory.AxisMode;
import org.cytoscape.neildhruva.chartapp.CytoChart;
import org.cytoscape.session.events.SessionLoadedEvent;
import org.cytoscape.session.events.SessionLoadedListener;


public class EventTableAdded implements SetCurrentNetworkListener, TableAddedListener, SessionLoadedListener{

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
		
		generateCytoChart(null);
	}

	@Override
	public void handleEvent(TableAddedEvent e) {
		//TODO use this in sync with SetCurrentNetworkEvent
	}

	@Override
	public void handleEvent(SessionLoadedEvent e) {
		Set<CyTableMetadata> cyTableMetadata = e.getLoadedSession().getTables();
		generateCytoChart(cyTableMetadata);
	}
	
	/**
	 * Generates CytoChart depending on whether a new CyTable was added or was it loaded in a session.
	 * @param cyTableMetadata The <code>Set</code> of all <code>CyTableMetadata</code> stored across session. 
	 */
	private void generateCytoChart(Set<CyTableMetadata> cyTableMetadata) {
		
		String chartName = "";
		cytoChart = null;
		
		cytoChart = chartAppFactory.getSavedChart(chartName, cyTable, cyTableMetadata);
		if(cytoChart==null) {
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
}
