package org.cytoscape.sample.internal;

import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;

import javax.swing.JCheckBox;
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

public class EventTableAdded implements SetCurrentNetworkListener{

	private MyCytoPanel myCytoPanel; 
	private JTable table;
	private JCheckBox[] checkBoxArray;
	private PanelComponents panelComponents=null;
	private int columnCount;
	private List<Boolean> checkBoxState;
	private List<String> columnNamesList;
	private CyTableFactory tableFactory;
	private CyTableManager cyTableManager;
	private CyNetworkTableManager cyNetworkTableMgr;
	
	EventTableAdded(MyCytoPanel myCytoPanel, 
					CyTableFactory tableFactory,
					CyNetworkTableManager cyNetworkTableMgr,
					CyTableManager cyTableManager) {
		
		this.myCytoPanel = myCytoPanel;
		this.tableFactory = tableFactory;
		this.cyNetworkTableMgr = cyNetworkTableMgr;
		this.cyTableManager = cyTableManager;
		this.panelComponents = new PanelComponents();
	}

	
	@Override
	public void handleEvent(SetCurrentNetworkEvent e) {
		
		if(e.getNetwork() == null) 
			return;
		
		//cytable is the CyTable corresponding to the current node table
		final CyTable cytable = e.getNetwork().getDefaultNodeTable();
		if(cytable==null)
			return;
		
		final Long networkSUID = e.getNetwork().getSUID();
		table = new JTable(new MyTableModel(cytable));
		columnCount = table.getColumnCount();
		
		//myCyTable is the custom CyTable created for this app and associated with each network.
		CyTable myCyTable=null;
		
		CyTable tempCyTable;
		String compareString = "PrintTable "+cytable.getTitle();
		Iterator<CyTable> iterator = cyTableManager.getAllTables(true).iterator();
		while(iterator.hasNext()) {
			tempCyTable = iterator.next();
			if(tempCyTable.getTitle().equals(compareString)){
				myCyTable=tempCyTable;
				break;
			}
		}
		
		if(myCyTable!=null) {
			checkBoxArray = panelComponents.initCheckBoxArray(myCyTable, cytable);
		} else {
			//checkBoxState stores information on whether a given column of a network table is
			//hidden or visible depending on the associated boolean value (true for visible)
			checkBoxState = new ArrayList<Boolean>();
			columnNamesList = new ArrayList<String>();
			for(int i=0; i<columnCount; i++) {
				columnNamesList.add(table.getColumnName(i));
				checkBoxState.add(true);
			}
			
			//if myCyTable is null, create a new CyTable and associate it with the current network.
			myCyTable = tableFactory.createTable("PrintTable "+cytable.getTitle(), CyIdentifiable.SUID, Long.class, true, true);
			myCyTable.createListColumn("Names", String.class, true);
			myCyTable.createListColumn("States", Boolean.class, true);
			
			CyRow cyrow = myCyTable.getRow(networkSUID);
			cyrow.set("Names", columnNamesList);
			cyrow.set("States", checkBoxState);
			
			//associate myCyTable with this network 
			cyNetworkTableMgr.setTable(e.getNetwork(), CyNetwork.class, "PrintTable", myCyTable);
			//add myCyTable to the CyTableManager in order to preserve it across sessions
			cyTableManager.addTable(myCyTable);
			
			checkBoxArray = panelComponents.initCheckBoxArray(myCyTable, cytable);
		}	
		
		table = panelComponents.getTable();
		myCytoPanel.initComponents(table, checkBoxArray, columnCount);		
	}
}
