package org.cytoscape.sample.internal;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JCheckBox;
import javax.swing.JTable;

import org.cytoscape.application.events.SetCurrentNetworkEvent;
import org.cytoscape.application.events.SetCurrentNetworkListener;
import org.cytoscape.model.CyRow;
import org.cytoscape.model.CyTable;
import org.cytoscape.model.CyTableFactory;

public class TableAddedEvent implements SetCurrentNetworkListener{

	private MyCytoPanel myCytoPanel; 
	private JTable table;
	private CyTable cytable;
	private JCheckBox[] checkBoxArray;
	private PanelComponents panelComponents;
	private int tableColumnCount;
	public static boolean networkDestroyed = false;
	private List<Boolean> checkBoxState;
	
	TableAddedEvent(MyCytoPanel myCytoPanel, CyTableFactory tableFactory){
		
		this.myCytoPanel = myCytoPanel;
		this.panelComponents = new PanelComponents();
		panelComponents.initCyTable(tableFactory);
	}

	
	@Override
	public void handleEvent(SetCurrentNetworkEvent e) {
		
		//If this method was called immediately following a network destroyed event, which it by default does,
		//then such a current network event should not be implemented because the pointer doesn't point to 
		//a particular network at that time.
		if(networkDestroyed) {
			networkDestroyed = false;
			return;
		}	
			
		//cytable is the CyTable corresponding to the current node table	
		cytable = e.getNetwork().getDefaultNodeTable();
		if(cytable==null)
			return;
		
		Long networkSUID = e.getNetwork().getSUID();
		table = new JTable(new MyTableModel(cytable));
		tableColumnCount = table.getColumnCount();
		
		if(PanelComponents.myCyTable.rowExists(networkSUID)) {	
			checkBoxState = PanelComponents.myCyTable.getRow(networkSUID).getList("States", Boolean.class);
			checkBoxArray = panelComponents.initCheckBoxArray(checkBoxState, networkSUID, table);
		} else {
			checkBoxState = new ArrayList<Boolean>();
			for(int i=0; i<tableColumnCount; i++) {
				checkBoxState.add(true);
			}
			CyRow cyrow = PanelComponents.myCyTable.getRow(networkSUID);
			cyrow.set("States", checkBoxState);
			checkBoxArray = panelComponents.initCheckBoxArray(checkBoxState, networkSUID, table);
		}	
		
		table = panelComponents.getTable();
		myCytoPanel.initComponents(table, checkBoxArray, tableColumnCount);		
	}
}
