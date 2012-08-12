package org.cytoscape.neildhruva.chartapp.app;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyRow;
import org.cytoscape.model.events.ColumnCreatedEvent;
import org.cytoscape.model.events.ColumnCreatedListener;
import org.cytoscape.model.events.ColumnDeletedEvent;
import org.cytoscape.model.events.ColumnDeletedListener;
import org.cytoscape.model.events.ColumnNameChangedEvent;
import org.cytoscape.model.events.ColumnNameChangedListener;
import org.cytoscape.model.events.RowSetRecord;
import org.cytoscape.model.events.RowsCreatedEvent;
import org.cytoscape.model.events.RowsCreatedListener;
import org.cytoscape.model.events.RowsSetEvent;
import org.cytoscape.model.events.RowsSetListener;
import org.cytoscape.neildhruva.chartapp.CytoChart;

public class SelectedRowsIdentifier implements ColumnCreatedListener, ColumnDeletedListener, 
												ColumnNameChangedListener, RowsSetListener, RowsCreatedListener {
	
	private CytoChart cytoChart = null;
	private List<String> setRowsList;
	
	public SelectedRowsIdentifier() {
		this.setRowsList = new ArrayList<String>();
	}
	
	@Override
	public void handleEvent(RowsSetEvent e) {
		
		if(cytoChart == null)
			return;
		
		Iterator<RowSetRecord> iterator = e.getPayloadCollection().iterator();
		String rowName;
		Boolean value;
		
		while(iterator.hasNext()) {
			CyRow row = iterator.next().getRow();
			rowName = row.get(CyNetwork.NAME, String.class);
			value = row.get(CyNetwork.SELECTED, Boolean.class);
			
			if(rowName!=null && !rowName.contains(" ") && value!=null && value) {
				cytoChart.addRow(rowName);
			}
			if(rowName!=null && !rowName.contains(" ") && value!=null && !value) {
				cytoChart.removeRow(rowName);
			}
		}
		
	}
	
	@Override
	public void handleEvent(RowsCreatedEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void handleEvent(ColumnNameChangedEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void handleEvent(ColumnDeletedEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void handleEvent(ColumnCreatedEvent e) {
		// TODO Auto-generated method stub
		
	}
	
	public void setCytoChart(CytoChart cytoChart) {
		this.cytoChart = cytoChart;
		if(cytoChart!=null) {
			this.setRowsList = cytoChart.getRows();
		} else {
			this.setRowsList = new ArrayList<String>();
		}
	}

}