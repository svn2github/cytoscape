package org.cytoscape.sample.internal;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JCheckBox;
import javax.swing.JTable;

import org.cytoscape.application.events.SetCurrentNetworkEvent;
import org.cytoscape.application.events.SetCurrentNetworkListener;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNetworkTableManager;
import org.cytoscape.model.CyRow;
import org.cytoscape.model.CyTable;
import org.cytoscape.model.CyTableFactory;

public class EventTableAdded implements SetCurrentNetworkListener{

	private MyCytoPanel myCytoPanel; 
	private JTable table;
	private CyTable cytable;
	private JCheckBox[] checkBoxArray;
	private PanelComponents panelComponents;
	private int columnCount;
	public static boolean networkDestroyed = false;
	private List<Boolean> checkBoxState;
	private List<String> columnNamesList;
	private CyNetworkTableManager networkTableMgr;
	private CyTableFactory tableFactory;
	
	EventTableAdded(MyCytoPanel myCytoPanel, CyTableFactory tableFactory, CyNetworkTableManager networkTableMgr){
		this.myCytoPanel = myCytoPanel;
		this.networkTableMgr = networkTableMgr;
		this.tableFactory = tableFactory;
		this.panelComponents = new PanelComponents();
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
		columnCount = table.getColumnCount();
		
		CyTable myCyTable = networkTableMgr.getTable(e.getNetwork(), CyNetwork.class, "PrintTable");
		
		if(myCyTable!=null) {	
			checkBoxArray = panelComponents.initCheckBoxArray(myCyTable, networkSUID, cytable);
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
			myCyTable = tableFactory.createTable("PrintTabl", "SUID", Long.class, true, true);
			myCyTable.createListColumn("Names", String.class, true);
			myCyTable.createListColumn("States", Boolean.class, true);
			
			CyRow cyrow = myCyTable.getRow(networkSUID);
			cyrow.set("Names", columnNamesList);
			cyrow.set("States", checkBoxState);
			
			networkTableMgr.setTable(e.getNetwork(), CyNetwork.class, "PrintTable", myCyTable);
			
			checkBoxArray = panelComponents.initCheckBoxArray(myCyTable, networkSUID, cytable);
		}	
		
		table = panelComponents.getTable();
		myCytoPanel.initComponents(table, checkBoxArray, columnCount);		
	}
}
