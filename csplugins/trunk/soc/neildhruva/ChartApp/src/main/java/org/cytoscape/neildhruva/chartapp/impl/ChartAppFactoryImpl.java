package org.cytoscape.neildhruva.chartapp.impl;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JTable;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNetworkTableManager;
import org.cytoscape.model.CyTable;
import org.cytoscape.model.CyTableFactory;
import org.cytoscape.model.CyTableManager;
import org.cytoscape.neildhruva.chartapp.ChartAppFactory;
import org.cytoscape.neildhruva.chartapp.ChartAppFactory.AxisMode;

import org.jfree.chart.ChartPanel;

public class ChartAppFactoryImpl implements ChartAppFactory {

	private JPanel jpanel;
	private int tableColumnCount;
	private PanelComponents panelComponents;
	private PanelLayout panelLayout;
	
	public ChartAppFactoryImpl(	CyTableFactory tableFactory,
								CyNetworkTableManager cyNetworkTableMgr,
								CyTableManager cyTableManager) {

		this.panelLayout = new PanelLayout();
		this.panelComponents = new PanelComponents(tableFactory, cyNetworkTableMgr, cyTableManager);
		
		if(cyNetworkTableMgr.getNetworkSet().iterator().hasNext())
			System.out.println("YES");
	}
	
	public JPanel createPanel(CyNetwork currentNetwork, CyTable cyTable) {
		return createPanel(currentNetwork, cyTable, AxisMode.COLUMNS);
	}
	
	public JPanel createPanel(CyNetwork currentNetwork, CyTable cyTable, AxisMode mode) {
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
	
	public JPanel createPanel(CyNetwork currentNetwork, CyTable cyTable, AxisMode mode, int height, int width){
		return jpanel;
	}
	
	public JPanel createPanel(CyNetwork currentNetwork, CyTable cyTable, AxisMode mode, int height, int width, String[] rows, String[] columns){
		return jpanel;
		
	}
	
	
}
