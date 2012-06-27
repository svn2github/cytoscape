package org.cytoscape.sample.internal;

import java.awt.GridLayout;
import java.util.Collection;

import javax.swing.JLabel;

import org.cytoscape.model.CyColumn;
import org.cytoscape.model.CyTable;
import org.cytoscape.model.events.NetworkAboutToBeDestroyedEvent;
import org.cytoscape.model.events.NetworkAboutToBeDestroyedListener;

public class TableDestroyedEvent implements NetworkAboutToBeDestroyedListener{

	private MyCytoPanel myCytoPanel;
	
	
	TableDestroyedEvent(MyCytoPanel myCytoPanel){
		
		this.myCytoPanel = myCytoPanel;
		
	}
	
	@Override
	public void handleEvent(NetworkAboutToBeDestroyedEvent e) {
		
		long networkSUID = e.getNetwork().getSUID();
		
		//TODO: delete row in cytable 
		
		//removing all the entries from hiddenColumnsColumn and hiddenColumnsIndex Hashmaps
		String columnName;
		CyTable cytable =e.getNetwork().getDefaultNodeTable();
		Collection<CyColumn> columns = cytable.getColumns();
		for(CyColumn column: columns) {
			columnName =column.getName();
			columnName+=networkSUID;
			if(PanelComponents.hiddenColumnsColumn.containsKey(columnName)) {
				PanelComponents.hiddenColumnsColumn.remove(columnName);
				PanelComponents.hiddenColumnsIndex.remove(columnName);
			}
		}
		
		//Clear the Table View Panel
		myCytoPanel.removeAll();
		JLabel label = new JLabel("Please select/import a network");
		myCytoPanel.setLayout(new GridLayout());
		myCytoPanel.add(label);
		myCytoPanel.revalidate();
		
		//Set networkDestroyed to true in order to keep from implementing the code in TableAddedEvent.java
		TableAddedEvent.networkDestroyed = true;
		
	}
}
