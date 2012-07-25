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
import org.cytoscape.neildhruva.chartapp.PanelManager;

import org.jfree.chart.ChartPanel;

public class ChartAppFactoryImpl implements ChartAppFactory {

	private final AxisMode DEFAULT_MODE = AxisMode.COLUMNS;
	private final int DEFAULT_HEIGHT = 600;
	private final int DEFAULT_WIDTH = 800;
	
	private JPanel jpanel;
	private int tableColumnCount;
	private PanelComponents panelComponents;
	private PanelLayout panelLayout;
	
	public ChartAppFactoryImpl(	CyTableFactory tableFactory,
								CyNetworkTableManager cyNetworkTableMgr,
								CyTableManager cyTableManager) {

		this.panelLayout = new PanelLayout();
		this.panelComponents = new PanelComponents(tableFactory, cyNetworkTableMgr, cyTableManager);
	}
	
	public JPanel createPanel(CyNetwork currentNetwork, CyTable cyTable) {
		return createPanel(currentNetwork, cyTable, DEFAULT_MODE, DEFAULT_HEIGHT, DEFAULT_WIDTH, null, null);
	}
	
	public JPanel createPanel(CyNetwork currentNetwork, CyTable cyTable, AxisMode mode) {
		
		return createPanel(currentNetwork, cyTable, mode, DEFAULT_HEIGHT, DEFAULT_WIDTH, null, null);
	}
	
	public JPanel createPanel(CyNetwork currentNetwork, CyTable cyTable, AxisMode mode, int height, int width){
		return createPanel(currentNetwork, cyTable, mode, height, width, null, null);
	}
	
	public JPanel createPanel(CyNetwork currentNetwork, CyTable cyTable, AxisMode mode, int height, int width, String[] rows, String[] columns){
		
		JTable table = new JTable(new MyTableModel(cyTable));
		tableColumnCount = table.getColumnCount();
		
		if(tableColumnCount>0) {
			panelComponents.initComponents(cyTable, currentNetwork, table, mode);
			
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
	
	public PanelManager getPanelManager() {
		return new PanelManagerImpl(panelComponents, panelLayout);
	}
	
}
