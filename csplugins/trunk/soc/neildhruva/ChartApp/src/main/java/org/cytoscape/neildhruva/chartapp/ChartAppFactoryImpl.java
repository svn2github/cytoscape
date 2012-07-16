package org.cytoscape.neildhruva.chartapp;

import java.util.ArrayList;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JTable;

import org.cytoscape.model.CyIdentifiable;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNetworkTableManager;
import org.cytoscape.model.CyRow;
import org.cytoscape.model.CyTable;
import org.cytoscape.model.CyTableFactory;
import org.cytoscape.model.CyTableManager;

import org.cytoscape.neildhruva.chartapp.api.ChartAppFactory;

public class ChartAppFactoryImpl implements ChartAppFactory {

	private JPanel jpanel;
	private int tableColumnCount;
	private CyTableFactory tableFactory;
	private CyNetworkTableManager cyNetworkTableMgr;
	private CyTableManager cyTableManager;
	private PanelComponents panelComponents;
	private PanelLayout panelLayout;
	
	public ChartAppFactoryImpl(	CyTableFactory tableFactory,
								CyNetworkTableManager cyNetworkTableMgr,
								CyTableManager cyTableManager) {

		this.tableFactory = tableFactory;
		this.cyNetworkTableMgr = cyNetworkTableMgr;
		this.cyTableManager = cyTableManager;
		this.panelLayout = new PanelLayout();
		this.panelComponents = new PanelComponents(panelLayout);
	}
	
	public JPanel createPanel(CyNetwork currentNetwork, CyTable cyTable) {
		
		final Long networkSUID = currentNetwork.getSUID();
		JTable table = new JTable(new MyTableModel(cyTable));
		tableColumnCount = table.getColumnCount();
			
		//myCyTable is the custom CyTable created for this app and associated with each network.
		CyTable myCyTable=null;
			
		myCyTable = cyNetworkTableMgr.getTable(currentNetwork, CyNetwork.class, "PrintTable "+cyTable.getTitle());
			
		if(myCyTable!=null) {
			panelComponents.initComponents(myCyTable, cyTable);
		} else {
			//checkBoxState stores information on whether a given column of a network table is
			//hidden or visible depending on the associated boolean value (true for visible)
			ArrayList<Boolean> checkBoxState = new ArrayList<Boolean>();
			ArrayList<String> columnNamesList = new ArrayList<String>();
			for(int i=0; i<tableColumnCount; i++) {
				columnNamesList.add(table.getColumnName(i));
				checkBoxState.add(true);
		}
			
			//if myCyTable is null, create a new CyTable and associate it with the current network.
			myCyTable = tableFactory.createTable("PrintTable "+cyTable.getTitle(), CyIdentifiable.SUID, Long.class, true, true);
			myCyTable.createListColumn("Names", String.class, true);
			myCyTable.createListColumn("States", Boolean.class, true);
			myCyTable.createColumn("ChartType", String.class, true);
			
			CyRow cyrow = myCyTable.getRow(networkSUID);
			cyrow.set("Names", columnNamesList);
			cyrow.set("States", checkBoxState);
			cyrow.set("ChartType", "Bar Chart"); //default value is "Bar Chart"
			
			//associate myCyTable with this network 
			cyNetworkTableMgr.setTable(currentNetwork, CyNetwork.class, "PrintTable "+cyTable.getTitle(), myCyTable);
			//add myCyTable to the CyTableManager in order to preserve it across sessions
			cyTableManager.addTable(myCyTable);
			
			panelComponents.initComponents(myCyTable, cyTable);
		}	
		
		JComboBox chartTypeComboBox = panelComponents.getComboBox();
		table = panelComponents.getTable();
		JCheckBox[] checkBoxArray = panelComponents.getCheckBoxArray();
		jpanel = panelLayout.initLayout(table, tableColumnCount, checkBoxArray, chartTypeComboBox);
			
		return jpanel;
	}


}
