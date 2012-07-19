package org.cytoscape.neildhruva.chartapp.impl;

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
import org.cytoscape.neildhruva.chartapp.ChartAppFactory;

import org.jfree.chart.ChartPanel;

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
		this.panelComponents = new PanelComponents(tableFactory, cyNetworkTableMgr, cyTableManager);
	}
	
	public JPanel createPanel(CyNetwork currentNetwork, CyTable cyTable) {
		
		System.out.println(currentNetwork.NAME+"------"+cyTable.getTitle());
		JTable table = new JTable(new MyTableModel(cyTable));
		tableColumnCount = table.getColumnCount();
		
		if(tableColumnCount>0) {
			panelComponents.initComponents(cyTable, currentNetwork, table);
			
			//get all components and send them to the panel layout class.
			JComboBox chartTypeComboBox = panelComponents.getComboBox();
			table = panelComponents.getTable();
			JCheckBox[] checkBoxArray = panelComponents.getCheckBoxArray();
			ChartPanel myChartPanel = panelComponents.getChartPanel();
			
			jpanel = panelLayout.initLayout(table, tableColumnCount, checkBoxArray, chartTypeComboBox, myChartPanel);
			
		} else {
			jpanel = panelLayout.nullJPanel();
		}
		
		return jpanel;
	}


}
